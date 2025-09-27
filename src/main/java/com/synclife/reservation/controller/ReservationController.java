package com.synclife.reservation.controller;

import com.synclife.reservation.dto.ReservationRequestDTO;
import com.synclife.reservation.dto.ReservationResponseDTO;
import com.synclife.reservation.enums.Role;
import com.synclife.reservation.service.ReservationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/reservations")
@RequiredArgsConstructor
public class ReservationController {

    private final ReservationService reservationService;

    @PostMapping
    public ResponseEntity<ReservationResponseDTO> postReservation(
            @RequestBody ReservationRequestDTO reservationRequestDTO,
            @RequestAttribute("memberId") Long memberId,
            @RequestAttribute("role") Role role) {

        if (role != Role.USER) {
            throw new SecurityException("권한이 없습니다.");
        }

        ReservationResponseDTO reservationResponseDTO = reservationService.postReservation(memberId, reservationRequestDTO);
        return ResponseEntity.ok(reservationResponseDTO);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteReservation(
            @PathVariable Long id,
            @RequestAttribute("memberId") Long memberId,
            @RequestAttribute("role") Role role) {

        reservationService.deleteReservation(id, memberId, role);
        return ResponseEntity.ok("예약이 취소되었습니다.");

    }

}
