package com.synclife.reservation.service;

import com.synclife.reservation.dto.ReservationRequestDTO;
import com.synclife.reservation.entity.Room;
import com.synclife.reservation.repository.ReservationRepository;
import com.synclife.reservation.repository.RoomRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class ReservationConcurrencyTest {

    @Autowired
    private ReservationService reservationService;

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private ReservationRepository reservationRepository;

    private Room room;

    @BeforeEach
    void setUp() {
        reservationRepository.deleteAll();
        roomRepository.deleteAll();
        room = roomRepository.save(new Room("회의실", "서울", 10));
    }

    @Test
    void 예약_동시10건_1건만성공() throws InterruptedException {

        int numThreads = 10;
        ExecutorService executorService = Executors.newFixedThreadPool(numThreads);
        CountDownLatch countDownLatch = new CountDownLatch(numThreads);

        AtomicInteger successCount = new AtomicInteger();
        AtomicInteger failCount = new AtomicInteger();

        ReservationRequestDTO reservationRequestDTO = new ReservationRequestDTO(
                room.getId(),
                LocalDateTime.of(2025, 9, 27, 10, 0),
                LocalDateTime.of(2025, 9, 27, 11, 0)
        );

        for (int i = 0; i < numThreads; i++) {
            long memberId = i + 1;
            executorService.submit(() -> {
                try {
                    reservationService.postReservation(memberId, reservationRequestDTO);
                    successCount.incrementAndGet();
                }catch (Exception e) {
                    failCount.incrementAndGet();
                } finally {
                    countDownLatch.countDown();
                }
            });
        }

        countDownLatch.await();
        executorService.shutdown();

        assertEquals(1, successCount.get());
        assertEquals(9, failCount.get());

    }

}
