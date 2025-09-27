package com.synclife.reservation.service;

import com.synclife.reservation.dto.AvailableTimeDTO;
import com.synclife.reservation.dto.ReservationResponseDTO;
import com.synclife.reservation.entity.Reservation;
import com.synclife.reservation.repository.ReservationRepository;
import com.synclife.reservation.repository.RoomRepository;
import com.synclife.reservation.dto.RoomRequestDTO;
import com.synclife.reservation.dto.RoomResponseDTO;
import com.synclife.reservation.entity.Room;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RoomService {

    private final RoomRepository roomRepository;
    private final ReservationRepository reservationRepository;

    @Transactional
    public Long postRoom(RoomRequestDTO roomRequestDTO) {
        Room room = new Room(roomRequestDTO.getName(), roomRequestDTO.getLocation(), roomRequestDTO.getCapacity());
        Room saveRoom = roomRepository.save(room);

        return saveRoom.getId();
    }

    public List<RoomResponseDTO> getRooms(LocalDate localDate) {
        LocalDateTime startOfDay = localDate.atStartOfDay();
        LocalDateTime endOfDay = localDate.plusDays(1).atStartOfDay();

        List<Room> rooms = roomRepository.findAll();
        List<RoomResponseDTO> roomResponseDTOList = new ArrayList<>();

        for (Room room : rooms) {
            List<Reservation> reservations = reservationRepository.findReservationsAvailability(room.getId(), startOfDay, endOfDay);

            // 예약된 시간대 조회
            List<ReservationResponseDTO> reservationResponseDTOList = new ArrayList<>();
            for (Reservation reservation : reservations) {
                ReservationResponseDTO reservationResponseDTO = new ReservationResponseDTO(
                        reservation.getId(),
                        reservation.getRoom().getId(),
                        reservation.getMemberId(),
                        reservation.getStartAt(),
                        reservation.getEndAt()
                );
                reservationResponseDTOList.add(reservationResponseDTO);
            }

            // 빈 시간대 조회
            List<AvailableTimeDTO> availableTimeDTOList = new ArrayList<>();
            LocalDateTime current = startOfDay;

            for (Reservation reservation : reservations) {
                if(current.isBefore(reservation.getStartAt())) {
                    availableTimeDTOList.add(new AvailableTimeDTO(current, reservation.getStartAt()));
                }
                current = reservation.getEndAt();
            }
            if(current.isBefore(endOfDay)) {
                availableTimeDTOList.add(new AvailableTimeDTO(current, endOfDay));
            }

            roomResponseDTOList.add(new RoomResponseDTO(
                    room.getId(),
                    room.getName(),
                    room.getLocation(),
                    room.getCapacity(),
                    reservationResponseDTOList,
                    availableTimeDTOList
            ));
        }

        return roomResponseDTOList;
    }
}
