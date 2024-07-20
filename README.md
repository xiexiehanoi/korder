# 예약 시스템 프로젝트 
:date: 2024년 6월 ~ 2024년 7월
 
![Java](https://img.shields.io/badge/Java-17-orange)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.3.1-brightgreen)
![Spring Cloud](https://img.shields.io/badge/Spring%20Cloud-%20-green)
![Redis](https://img.shields.io/badge/Redis-%20-red)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-%20-blue)
![Docker](https://img.shields.io/badge/Docker-%20-blue)
![QueryDSL](https://img.shields.io/badge/QueryDSL-%20-yellow)
![JPA](https://img.shields.io/badge/JPA-%20-lightgrey)
![JWT](https://img.shields.io/badge/JWT-%20-yellowgreen)

## 프로젝트 소개
Redis를 활용한 캐싱 전략, 대규모 티켓 예약 처리

## 프로젝트 주요 목표
1. 마이크로서비스 아키텍처 구현
2. 대규모 동시 접속 처리

🚀 시작 가이드
📥설치
```
$ git clone --branch local-deploy https://github.com/jsjune/E-commerce.git](https://github.com/xiexiehanoi/korder
$ cd korder
```

▶️실행(Rdis)
```
$ ./gradlew docker
$ docker-compose -f redis-compose.yml up -d
```

## 주요 기능
- 사용자 인증 및 관리
- 이벤트 생성 및 관리
- 실시간 티켓 예약 처리
- 티켓 재고 관리

## 기능 및 기술적 구현
1. **초기 모놀리식 아키텍처**
   - 단일 애플리케이션으로 시작하여 모든 기능을 하나의 코드베이스에서 관리

2. **마이크로서비스 아키텍처(MSA)로 전환**
   - 서비스들을 독립적으로 배포 및 확장할 수 있도록 분리

3. **과부하 및 동시 접속 처리**
   - 예약 시스템이 과부하 및 동시 접속을 처리할 수 있도록 설계

4. **락(Optimistic/Pessimistic) 사용**
   - 초기에는 낙관적/비관적 락을 사용하여 동시성 문제를 해결
   - 낙관적 락
   - 비관적 락

5. **Redis 캐싱 및 메시지 큐 활용**
   - Redis를 사용한 캐싱 및 메시지 큐를 통한 비동기 처리로 성능 최적화

## 트러블슈팅 사례

 

## 시퀀스 다이어그램
```mermaid
sequenceDiagram
    participant User
    participant ApiGateway
    participant ReservationService
    participant EventService
    participant UserService
    participant Database
    participant Redis
    participant MessageQueue

    User->>ApiGateway: 예약 요청
    ApiGateway->>ReservationService: 예약 요청 전달
    ReservationService->>Redis: 캐시 확인 및 업데이트
    ReservationService->>MessageQueue: 예약 요청 추가
    MessageQueue->>ReservationService: 예약 요청 처리
    ReservationService->>EventService: 이벤트 재고 확인 및 업데이트
    EventService->>Database: 이벤트 재고 업데이트 (락 사용)
    ReservationService->>Database: 예약 정보 저장
    ReservationService->>Redis: 캐시 업데이트
    ReservationService->>User: 예약 완료 응답
```

## ERD
![image](https://github.com/user-attachments/assets/0fd38778-4d6b-433a-8ef3-e29f9e563262)

