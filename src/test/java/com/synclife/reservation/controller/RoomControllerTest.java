package com.synclife.reservation.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.synclife.reservation.dto.RoomRequestDTO;
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

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class RoomControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private Room room;

    @BeforeEach
    void setUp() {
        reservationRepository.deleteAll();
        roomRepository.deleteAll();
        room = roomRepository.save(new Room("회의실", "서울", 10));
    }

    @Test
    void 회의실등록() throws Exception {
        RoomRequestDTO roomRequestDTO = new RoomRequestDTO("회의실 2", "서울", 5);

        mockMvc.perform(post("/rooms")
                        .header("Authorization", "admin-token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(roomRequestDTO)))
                .andExpect(status().isOk());
    }

    @Test
    void 가용성조회() throws Exception {
        String localDate = LocalDate.of(2025, 9, 27).toString();

        mockMvc.perform(get("/rooms")
                .param("date", localDate))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(room.getId()))
                .andExpect(jsonPath("$[0].name").value(room.getName()));
    }

}