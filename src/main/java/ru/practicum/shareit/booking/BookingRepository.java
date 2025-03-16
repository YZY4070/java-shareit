package ru.practicum.shareit.booking;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    Collection<Booking> findBookingsByBookerIdAndStatus(Long bookerId, BookingStatus status);

    Collection<Booking> findBookingsByBookerId(Long bookerId);

    Collection<Booking> findBookingsByItem_Owner_Id(Long itemOwnerId);

    Collection<Booking> findBookingsByItem_Owner_IdAndStatus(Long itemOwnerId, BookingStatus status);

    @Query(value = """
            SELECT * FROM bookings
            WHERE item_id = :itemId AND end_date < CURRENT_TIMESTAMP
            ORDER BY end_date DESC
            LIMIT 1
            """, nativeQuery = true)
    Booking findLastBookingByItemId(@Param("itemId") Long itemId);


    @Query(value = """
            SELECT * FROM bookings
            WHERE item_id = :itemId
            AND start_date > CURRENT_TIMESTAMP
            ORDER BY start_date ASC
            LIMIT 1
            """, nativeQuery = true)
    Booking findNextBookingByItemId(@Param("itemId") Long itemId);

    @Query("""
            SELECT b
            FROM Booking b
            WHERE b.item.owner.id = :ownerId
                AND (
                (b.endDate = (SELECT MAX(b1.endDate)
                    FROM Booking b1
                    WHERE b1.item.id = b.item.id
                    AND b1.endDate < CURRENT_TIMESTAMP))
                OR
                (b.startDate = (SELECT MIN(b2.startDate)
                    FROM Booking b2
                    WHERE b2.item.id = b.item.id
                    AND b2.startDate > CURRENT_TIMESTAMP))
                )
                ORDER BY b.item.id, b.endDate DESC, b.startDate ASC
            """)
    List<Booking> findLastAndNextBookingsByOwnerId(@Param("ownerId") Long ownerId);
}
