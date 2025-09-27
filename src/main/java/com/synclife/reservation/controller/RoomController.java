package com.synclife.reservation.controller;

import com.synclife.reservation.dto.RoomRequestDTO;
import com.synclife.reservation.dto.RoomResponseDTO;
import com.synclife.reservation.enums.Role;
import com.synclife.reservation.service.RoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/rooms")
public class RoomController {

    private final RoomService roomService;

    @PostMapping
    public ResponseEntity<Long> postRoom(@RequestBody RoomRequestDTO roomRequestDTO, @RequestAttribute("role") Role role) {

        if(role != Role.ADMIN) {
            throw new SecurityException("권한이 없습니다.");
        }

        Long id = roomService.postRoom(roomRequestDTO);
        return ResponseEntity.ok(id);
    }

    @GetMapping
    public ResponseEntity<List<RoomResponseDTO>> getRooms(@RequestParam String date) {
        LocalDate localDate = LocalDate.parse(date);

        List<RoomResponseDTO> rooms = roomService.getRooms(localDate);
        return ResponseEntity.ok(rooms);
    }

}
