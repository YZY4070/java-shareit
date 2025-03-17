package ru.practicum.shareit.booking;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    Collection<Booking> findBookingsByBookerId(Long bookerId, Sort sort);

    Collection<Booking> findBookingsByBookerIdAndStartDateIsBeforeAndEndDateIsAfter(Long bookerId, LocalDateTime start, LocalDateTime end, Sort sort);

    Collection<Booking> findBookingsByBookerIdAndEndDateIsBefore(Long bookerId, LocalDateTime end, Sort sort);

    Collection<Booking> findBookingsByBookerIdAndStartDateIsAfter(Long bookerId, LocalDateTime start, Sort sort);

    Collection<Booking> findBookingsByBookerIdAndStatus(Long bookerId, BookingStatus status, Sort sort);

    Collection<Booking> findBookingsByItem_Owner_Id(Long itemOwnerId, Sort sort);

    Collection<Booking> findByItem_Owner_IdAndStartDateIsBeforeAndEndDateIsAfter(Long itemOwnerId, LocalDateTime start, LocalDateTime end, Sort sort);

    Collection<Booking> findByItem_Owner_IdAndEndDateIsBefore(Long itemOwnerId, LocalDateTime end, Sort sort);

    Collection<Booking> findByItem_Owner_IdAndStartDateIsAfter(Long itemOwnerId, LocalDateTime start, Sort sort);

    Collection<Booking> findBookingsByItem_Owner_IdAndStatus(Long itemOwnerId, BookingStatus status, Sort sort);


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
