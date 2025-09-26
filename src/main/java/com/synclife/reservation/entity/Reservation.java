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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    private LocalDateTime startAt;

    private LocalDateTime endAt;

    public Reservation(Room room, Member member, LocalDateTime startAt, LocalDateTime endAt) {
        this.room = room;
        this.member = member;
        this.startAt = startAt;
        this.endAt = endAt;
    }
}
