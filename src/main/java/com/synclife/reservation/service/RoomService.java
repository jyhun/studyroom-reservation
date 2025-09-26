package com.synclife.reservation.service;

import com.synclife.reservation.repository.RoomRepository;
import com.synclife.reservation.dto.RoomRequestDTO;
import com.synclife.reservation.dto.RoomResponseDTO;
import com.synclife.reservation.entity.Room;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RoomService {

    private final RoomRepository roomRepository;

    @Transactional
    public RoomResponseDTO postRoom(RoomRequestDTO roomRequestDTO) {
        Room room = new Room(roomRequestDTO.getName(), roomRequestDTO.getLocation(), roomRequestDTO.getCapacity());
        Room saveRoom = roomRepository.save(room);

        return new RoomResponseDTO(saveRoom.getId(), saveRoom.getName(), saveRoom.getLocation(), saveRoom.getCapacity());
    }

}
