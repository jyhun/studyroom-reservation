package com.synclife.reservation.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.synclife.reservation.dto.ReservationRequestDTO;
import com.synclife.reservation.entity.Room;
import com.synclife.reservation.enums.Role;
import com.synclife.reservation.repository.ReservationRepository;
import com.synclife.reservation.repository.RoomRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@SpringBootTest
@AutoConfigureMockMvc
class ReservationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private Room room;
    @Autowired
    private ReservationRepository reservationRepository;

    @BeforeEach
    void setUp() {
        reservationRepository.deleteAll();
        roomRepository.deleteAll();
        room = roomRepository.save(new Room("회의실", "서울", 10));
    }

    @Test
    void 예약생성() throws Exception {
        ReservationRequestDTO reservationRequestDTO = new ReservationRequestDTO(
                room.getId(),
                LocalDateTime.of(2025, 9, 27, 10, 0),
                LocalDateTime.of(2025, 9, 27, 11, 0)
        );

        mockMvc.perform(post("/reservations")
                        .header("Authorization", "user-token-1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reservationRequestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.roomId").value(room.getId()))
                .andExpect(jsonPath("$.memberId").value(1));
    }

    @Test
    void 예약취소() throws Exception {
        ReservationRequestDTO reservationRequestDTO = new ReservationRequestDTO(
                room.getId(),
                LocalDateTime.of(2025, 9, 27, 10, 0),
                LocalDateTime.of(2025, 9, 27, 11, 0)
        );

        String response = mockMvc.perform(post("/reservations")
                        .header("Authorization", "user-token-1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reservationRequestDTO)))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        long id = objectMapper.readTree(response).get("id").asLong();

        mockMvc.perform(delete("/reservations/" + id)
                        .header("Authorization", "user-token-1"))
                .andExpect(status().isOk())
                .andExpect(content().string("예약이 취소되었습니다."));
    }


}