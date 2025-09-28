package com.synclife.reservation.service;

import com.synclife.reservation.dto.ReservationRequestDTO;
import com.synclife.reservation.dto.ReservationResponseDTO;
import com.synclife.reservation.entity.Reservation;
import com.synclife.reservation.entity.Room;
import com.synclife.reservation.enums.Role;
import com.synclife.reservation.repository.ReservationRepository;
import com.synclife.reservation.repository.RoomRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final RoomRepository roomRepository;

    @Transactional
    public ReservationResponseDTO postReservation(Long memberId, ReservationRequestDTO reservationRequestDTO) {
        Room room = roomRepository.findById(reservationRequestDTO.getRoomId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회의실입니다."));

        if (reservationRequestDTO.getStartAt() == null || reservationRequestDTO.getEndAt() == null) {
            throw new IllegalArgumentException("시간을 입력해야 합니다.");
        }
        if (!reservationRequestDTO.getStartAt().isBefore(reservationRequestDTO.getEndAt())) {
            throw new IllegalArgumentException("시작 시간은 종료 시간보다 빨라야 합니다.");
        }

        List<Reservation> reservations = reservationRepository.findReservationsLock(room.getId(), reservationRequestDTO.getStartAt(), reservationRequestDTO.getEndAt());
        if (!reservations.isEmpty()) {
            throw new IllegalArgumentException("이미 해당 시간대에 예약이 있습니다.");
        }

        Reservation reservation = new Reservation(room, memberId, reservationRequestDTO.getStartAt(), reservationRequestDTO.getEndAt());
        Reservation saveReservation = reservationRepository.save(reservation);

        log.info("예약 성공: reservationId={}, memberId={}, roomId={}", saveReservation.getId(), memberId, room.getId());

        return new ReservationResponseDTO(
                saveReservation.getId(),
                saveReservation.getRoom().getId(),
                saveReservation.getMemberId(),
                saveReservation.getStartAt(),
                saveReservation.getEndAt()
        );
    }

    @Transactional
    public void deleteReservation(Long reservationId, Long memberId, Role role) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 예약입니다."));

        // 권한 검증: ADMIN 또는 자기가 예약한것만 취소 가능
        if (role != Role.ADMIN && !reservation.getMemberId().equals(memberId)) {
            throw new SecurityException("본인 예약만 취소할 수 있습니다.");
        }

        reservationRepository.delete(reservation);

        log.info("예약 취소 성공: reservationId={}, memberId={}, role={}", reservationId, memberId, role);
    }

}
