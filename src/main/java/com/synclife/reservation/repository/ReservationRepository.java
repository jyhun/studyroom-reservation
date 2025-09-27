package com.synclife.reservation.repository;

import com.synclife.reservation.entity.Reservation;
import com.synclife.reservation.entity.Room;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    // 같은 회의실에서 시간이 겹치는 예약을 비관적으로 조회함
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT r FROM Reservation r " +
            "WHERE r.room.id = :roomId " +
            "AND r.startAt < :endAt " +
            "AND r.endAt > :startAt")
    List<Reservation> findReservationsLock(@Param("roomId") Long roomId,
                                           @Param("startAt") LocalDateTime startAt,
                                           @Param("endAt") LocalDateTime endAt);
}
