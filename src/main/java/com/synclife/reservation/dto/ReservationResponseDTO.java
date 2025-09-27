package com.synclife.reservation.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class ReservationResponseDTO {
    private Long id;
    private Long roomId;
    private Long memberId;
    private LocalDateTime startAt;
    private LocalDateTime endAt;
}
