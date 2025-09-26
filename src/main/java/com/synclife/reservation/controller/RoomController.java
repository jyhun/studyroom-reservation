package com.synclife.reservation.controller;

import com.synclife.reservation.dto.RoomRequestDTO;
import com.synclife.reservation.dto.RoomResponseDTO;
import com.synclife.reservation.enums.Role;
import com.synclife.reservation.service.RoomService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/rooms")
public class RoomController {

    private final RoomService roomService;

    @PostMapping
    public ResponseEntity<?> postRoom(@RequestBody RoomRequestDTO roomRequestDTO, HttpServletRequest httpServletRequest) {

        Role role = (Role) httpServletRequest.getAttribute("role");

        if(role != Role.ADMIN) {
            return ResponseEntity.status(403).body("권한이 없습니다.");
        }

        RoomResponseDTO roomResponseDTO = roomService.postRoom(roomRequestDTO);
        return ResponseEntity.ok(roomResponseDTO);
    }

}
