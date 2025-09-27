package com.synclife.reservation.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class RoomResponseDTO {
    private Long id;
    private String name;
    private String location;
    private int capacity;
    private List<ReservationResponseDTO> reservations; // 예약 현황
    private List<AvailableTimeDTO> availableTimes; // 빈 시간대
}
