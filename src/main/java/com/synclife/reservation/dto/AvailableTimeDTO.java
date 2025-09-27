package com.synclife.reservation.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

/**
 * 빈 시간대 조회하기 위한 DTO
 */
@Getter
@AllArgsConstructor
public class AvailableTimeDTO {
    private LocalDateTime startAt;
    private LocalDateTime endAt;
}
