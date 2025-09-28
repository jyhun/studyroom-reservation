# 싱크라이프 백엔드 신입 개발자 과제 — 스터디룸 예약

## 실행 방법

```bash
git clone https://github.com/jyhun/studyroom-reservation.git
cd studyroom-reservation
docker compose up
```

- Docker Compose 실행 시 애플리케이션과 DB가 함께 실행됩니다.

---

## 주요 기능

- 회의실 등록 (ADMIN 전용)
- 가용성 조회 (특정 날짜 기준 예약 현황/빈 시간대 확인)
- 예약 생성 (USER 전용, 시간 겹침 불가)
- 예약 취소 (OWNER 또는 ADMIN 가능)

---

## API 예시

### 1. 회의실 등록 (ADMIN 전용)
```
POST /rooms
Authorization: admin-token
```
요청
```json
{ "name": "회의실", "location": "서울", "capacity": 10 }
```
응답
```json
1
```
---
### 2. 가용성 조회
```
GET /rooms?date=2025-09-28
Authorization: user-token-1
```
응답
```json
[
    {
        "id": 1,
        "name": "회의실",
        "location": "서울",
        "capacity": 10,
        "reservations": [],
        "availableTimes": [
            {
                "startAt": "2025-09-28T00:00:00",
                "endAt": "2025-09-29T00:00:00"
            }
        ]
    }
]
```
---
### 3. 예약 생성
```
POST /reservations
Authorization: user-token-1
```
요청
```json
{
  "roomId": 1,
  "startAt": "2025-09-28T10:00:00",
  "endAt": "2025-09-28T11:00:00"
}
```
응답
```json
{
    "id": 1,
    "roomId": 1,
    "memberId": 1,
    "startAt": "2025-09-28T10:00:00",
    "endAt": "2025-09-28T11:00:00"
}
```
---
### 4. 예약 취소
```
DELETE /reservations/{id}
Authorization: user-token-1
```
응답
```json
"예약이 취소되었습니다."
```

---

## DB 구조

### 테이블

- **Room**
  - id (PK)
  - name
  - location
  - capacity

- **Reservation**
  - id (PK)
  - room_id (FK → Room.id)
  - member_id  
  - start_at
  - end_at

### 관계
- Room 1 : N Reservation

---

## ADR

### 1. 예약 중복 방지
- 동시 요청 시 같은 시간대에 여러 예약이 들어올 수 있는 문제가 있음
- DB 제약 조건을 쓰는 방법(PostgreSQL EXCLUDE)도 있지만, 이번 과제에서는 JPA 비관적 락으로 처리
- 트랜잭션 안에서 겹치는 예약을 확인하고, 동시에 들어온 요청 중 하나만 성공하도록 보장

### 2. 권한 처리
- ADMIN과 USER 권한을 구분해야 했음
- JWT 같은 복잡한 방식을 쓰기보다는 간단한 토큰(`admin-token`, `user-token-<id>`)으로 구분
- ADMIN은 모든 예약을 취소할 수 있고, USER는 본인 예약만 취소 가능

## LLM 사용 내역
- 엔티티 설계나 예외 처리 구조를 확인할 때 참고했습니다.
- 예약 겹침 방지와 동시성 제어 방법에 대한 아이디어를 얻었습니다.
- 테스트 시나리오 구성에 대해 조언을 받았습니다.
- README 같은 문서 작성 형식은 가이드 정도만 참고했습니다.
- 코드는 직접 작성하고 검증했으며, 복사와 붙여넣기는 하지 않았습니다.
