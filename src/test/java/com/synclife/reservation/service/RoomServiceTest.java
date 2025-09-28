package com.synclife.reservation.service;

import com.synclife.reservation.dto.AvailableTimeDTO;
import com.synclife.reservation.dto.ReservationRequestDTO;
import com.synclife.reservation.dto.RoomRequestDTO;
import com.synclife.reservation.dto.RoomResponseDTO;
import com.synclife.reservation.entity.Room;
import com.synclife.reservation.repository.ReservationRepository;
import com.synclife.reservation.repository.RoomRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class RoomServiceTest {

    @Autowired
    private RoomService roomService;

    @Autowired
    private ReservationService reservationService;

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private ReservationRepository reservationRepository;

    private Room room;

    @BeforeEach
    void setUp() {
        reservationRepository.deleteAll();
        roomRepository.deleteAll();
        room = roomRepository.save(new Room("회의실 1", "서울", 10));
    }

    @Test
    void 회의실_등록_성공() {
        RoomRequestDTO roomRequestDTO = new RoomRequestDTO("회의실 2", "인천", 5);
        Long id = roomService.postRoom(roomRequestDTO);

        assertNotNull(id);
    }

    @Test
    void 예약_없을때_가용성조회() {
        LocalDate localDate = LocalDate.of(2025, 9, 27);
        List<RoomResponseDTO> rooms = roomService.getRooms(localDate);

        assertEquals(1, rooms.size());
        RoomResponseDTO roomResponseDTO = rooms.get(0);

        assertEquals(1, roomResponseDTO.getAvailableTimes().size());
        AvailableTimeDTO availableTimeDTO = roomResponseDTO.getAvailableTimes().get(0);
        assertEquals(localDate.atStartOfDay(), availableTimeDTO.getStartAt());
        assertEquals(localDate.plusDays(1).atStartOfDay(), availableTimeDTO.getEndAt());
    }

    @Test
    void 예약_있을때_가용성조회() {
        LocalDate localDate = LocalDate.of(2025, 9, 27);

        ReservationRequestDTO reservationRequestDTO1 = new ReservationRequestDTO(
                room.getId(),
                LocalDateTime.of(2025, 9, 27, 10, 0),
                LocalDateTime.of(2025, 9, 27, 11, 0)
        );
        reservationService.postReservation(1L,reservationRequestDTO1);

        ReservationRequestDTO reservationRequestDTO2 = new ReservationRequestDTO(
                room.getId(),
                LocalDateTime.of(2025, 9, 27, 14, 0),
                LocalDateTime.of(2025, 9, 27, 15, 0)
        );
        reservationService.postReservation(2L,reservationRequestDTO2);

        List<RoomResponseDTO> rooms = roomService.getRooms(localDate);
        RoomResponseDTO roomResponseDTO = rooms.get(0);

        List<AvailableTimeDTO> availableTimes = roomResponseDTO.getAvailableTimes();
        assertEquals(3, availableTimes.size());

        assertEquals(localDate.atStartOfDay(), availableTimes.get(0).getStartAt());
        assertEquals(LocalDateTime.of(2025, 9, 27, 10, 0), availableTimes.get(0).getEndAt());
        assertEquals(LocalDateTime.of(2025, 9, 27, 11, 0), availableTimes.get(1).getStartAt());
        assertEquals(LocalDateTime.of(2025, 9, 27, 14, 0), availableTimes.get(1).getEndAt());
        assertEquals(LocalDateTime.of(2025, 9, 27, 15, 0), availableTimes.get(2).getStartAt());
        assertEquals(localDate.plusDays(1).atStartOfDay(), availableTimes.get(2).getEndAt());
    }
}