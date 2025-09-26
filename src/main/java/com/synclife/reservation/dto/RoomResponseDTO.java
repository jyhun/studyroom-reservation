package com.synclife.reservation.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class RoomResponseDTO {
    private Long id;
    private String name;
    private String location;
    private int capacity;
}
