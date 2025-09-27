package com.synclife.reservation.controller;

import com.synclife.reservation.dto.RoomRequestDTO;
import com.synclife.reservation.dto.RoomResponseDTO;
import com.synclife.reservation.enums.Role;
import com.synclife.reservation.service.RoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/rooms")
public class RoomController {

    private final RoomService roomService;

    @PostMapping
    public ResponseEntity<?> postRoom(@RequestBody RoomRequestDTO roomRequestDTO, @RequestAttribute("role") Role role) {

        if(role != Role.ADMIN) {
            throw new SecurityException("권한이 없습니다.");
        }

        RoomResponseDTO roomResponseDTO = roomService.postRoom(roomRequestDTO);
        return ResponseEntity.ok(roomResponseDTO);
    }

}
