package com.synclife.reservation.service;

import com.synclife.reservation.dto.ReservationRequestDTO;
import com.synclife.reservation.dto.ReservationResponseDTO;
import com.synclife.reservation.entity.Room;
import com.synclife.reservation.enums.Role;
import com.synclife.reservation.repository.ReservationRepository;
import com.synclife.reservation.repository.RoomRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class ReservationServiceTest {

    @Autowired
    private ReservationService reservationService;

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private RoomRepository roomRepository;

    private Room room;

    @BeforeEach
    void setUp() {
        reservationRepository.deleteAll();
        roomRepository.deleteAll();
        room = roomRepository.save(new Room("회의실 1", "서울", 10));
    }

    @Test
    void 예약_성공() {
        ReservationRequestDTO reservationRequestDTO = new ReservationRequestDTO(
                room.getId(),
                LocalDateTime.of(2025, 9, 27, 10, 0),
                LocalDateTime.of(2025, 9, 27, 11, 0)
        );

        ReservationResponseDTO reservationResponseDTO = reservationService.postReservation(1L, reservationRequestDTO);

        assertNotNull(reservationResponseDTO);
        assertEquals(1L, reservationResponseDTO.getMemberId());
        assertEquals(reservationResponseDTO.getRoomId(), room.getId());
    }

    @Test
    void 예약_시간없음_실패() {
        ReservationRequestDTO reservationRequestDTO = new ReservationRequestDTO(room.getId(), null, null);

        assertThrows(IllegalArgumentException.class, () -> reservationService.postReservation(1L, reservationRequestDTO));
    }

    @Test
    void 예약_시작시간이_종료시간이후면_실패() {
        ReservationRequestDTO reservationRequestDTO = new ReservationRequestDTO(
                room.getId(),
                LocalDateTime.of(2025, 9, 27, 11, 0),
                LocalDateTime.of(2025, 9, 27, 10, 0)
        );

        assertThrows(IllegalArgumentException.class, () -> reservationService.postReservation(1L, reservationRequestDTO));
    }

    @Test
    void 예약_겹치면_실패() {
        ReservationRequestDTO reservationRequestDTO1 = new ReservationRequestDTO(
                room.getId(),
                LocalDateTime.of(2025,9,27,10,0),
                LocalDateTime.of(2025,9,27,11,0)
        );
        reservationService.postReservation(1L, reservationRequestDTO1);

        ReservationRequestDTO reservationRequestDTO2 = new ReservationRequestDTO(
                room.getId(),
                LocalDateTime.of(2025,9,27,10,0),
                LocalDateTime.of(2025,9,27,11,0)
        );

        assertThrows(IllegalArgumentException.class, () -> reservationService.postReservation(1L, reservationRequestDTO2));
    }

    @Test
    void 예약취소_OWNER_성공() {
        ReservationRequestDTO reservationRequestDTO = new ReservationRequestDTO(
                room.getId(),
                LocalDateTime.of(2025,9,27,10,0),
                LocalDateTime.of(2025,9,27,11,0)
        );
        ReservationResponseDTO reservationResponseDTO = reservationService.postReservation(1L, reservationRequestDTO);

        reservationService.deleteReservation(reservationResponseDTO.getId(), 1L, Role.USER);

        assertTrue(reservationRepository.findById(reservationResponseDTO.getId()).isEmpty());
    }

    @Test
    void 예약취소_ADMIN_성공() {
        ReservationRequestDTO reservationRequestDTO = new ReservationRequestDTO(
                room.getId(),
                LocalDateTime.of(2025, 9, 27,10,0),
                LocalDateTime.of(2025, 9, 27,11,0)
        );
        ReservationResponseDTO reservationResponseDTO = reservationService.postReservation(1L, reservationRequestDTO);

        reservationService.deleteReservation(reservationResponseDTO.getId(), null, Role.ADMIN);

        assertTrue(reservationRepository.findById(reservationResponseDTO.getId()).isEmpty());
    }

    @Test
    void 예약취소_권한없음_실패() {
        ReservationRequestDTO reservationRequestDTO = new ReservationRequestDTO(
                room.getId(),
                LocalDateTime.of(2025,9,27,10,0),
                LocalDateTime.of(2025,9,27,11,0)
        );
        ReservationResponseDTO reservationResponseDTO = reservationService.postReservation(1L, reservationRequestDTO);

        assertThrows(SecurityException.class, () -> reservationService.deleteReservation(reservationResponseDTO.getId(), 2L, Role.USER));
    }

    @Test
    void 예약취소_없는예약_실패() {
        assertThrows(IllegalArgumentException.class, () -> reservationService.deleteReservation(999L, 1L, Role.ADMIN));
    }

}