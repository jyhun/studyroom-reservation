package com.synclife.reservation.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id", nullable = false)
    private Room room;

    private Long memberId;

    private LocalDateTime startAt;

    private LocalDateTime endAt;

    public Reservation(Room room, Long memberId, LocalDateTime startAt, LocalDateTime endAt) {
        this.room = room;
        this.memberId = memberId;
        this.startAt = startAt;
        this.endAt = endAt;
    }
}
