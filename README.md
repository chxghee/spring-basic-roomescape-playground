# 🚪 방탈출 예약 홈페이지  

## 🚀 1단계 - 로그인

### 📝 기능 요구사항
✅ /login에 email, password 값을 body에 포함하세요.  
✅ 응답에 Cookie에 "token"값으로 토큰이 포함되도록 하세요.

### 💻 구현 전략

1. 로그인 시 jwt 토큰 발급   
   ➡️ access token 유효기간 30분 설정     
   ➡️ 토큰에 id 정보만 담는다  
   ➡️ 발급한 토큰을 쿠키에 저장한다


2. 인증 정보 조회 api 구현  
   ➡️ 요청 쿠키에 저장된 access 토큰을 읽어 사용자의 이름을 응답한다 

<br>

---


## 🚀 2단계 - 로그인 리팩터링

### 📝 기능 요구사항
✅ Cookie에 담긴 인증 정보를 이용해서 멤버 객체를 만드는 로직을 분리합니다.  
✅ 예약 생성 시 ReservationReqeust의 name이 없는 경우 Cookie에 담긴 정보로 예약을 생성하세요.

### 💻 구현 전략

1. 멤버 객체 생성 로직 구현    
   ➡️ `LoginMemberArgumentResolver`: HandlerMethodArgumentResolver의 구현체를 만들어 토큰의 회원 정보를 추출해 회원정보 컨트롤러 메서드에 주입      
   ➡️ `AuthenticatedMember` 어노테이션을 통해 메서드에 명시적으로 주입


2. 예약 api 구현  
   ➡️ 예약 request에 name 필드가 존재하면 해당 정보로 예약을 진행  
   ➡️ 예약 request에 name 필드가 없다면 로그인 회원 정보로 예약을 진행

<br>


---


## 🚀 3단계 - 관리자 기능

### 📝 기능 요구사항
✅ 어드민 페이지 진입은 admin권한이 있는 사람만 할 수 있도록 제한하세요.  
✅ HandlerInterceptor를 활용하여 권한이 없는 경우 401코드를 응답하세요.

### 💻 구현 전략

1. Interceptor 구현    
   ➡️ `AdminAuthInterceptor`: 관리자 요청의 경우 해당 인터셉터에서 권한 검증      


2. 어드민만 요청해야 하는 API의 권한 체크  
  ➡️ `Webconfig`의 `addInterceptors`에 설정할 수 있는 uri는 http method 구분 없이 설정된다  
  ➡️ `/times/**`, `/themes/**`에 대해 get 요청은 모두 가능하지만 post, delete는 어드민만 가능한 상황    
  ➡️ 인터셉터 내부에 화이트 리스트를 두어 해당 리스트에 존재하는 조회 요청은 인터셉터를 pass 하도록 구현  
  ➡️ 추가로, 인터셉터에서 토큰을 추출하여 사용자 정보를 `loginMember` 객체에 저장하고, `ArgumentResolver`에서는 이 객체를 활용하도록 구성함으로써, 매 요청마다 토큰을 반복해서 파싱하는 중복 로직을 제거   



<br>


---
## 🚀 4단계 - JPA 전환

### 📝 기능 요구사항
✅ JPA를 활용하여 데이터베이스에 접근하도록 수정하세요.

### 💻 구현 전략

1. 각 도메인 별 JPA 적용  
   ➡️ 엔티티 연관관계 설정(Reservation)  
   ➡️ DAO -> Repository 변경 (엔티티 매니저를 이용한 쿼리 -1차) -> JpaRepository 변경 (2차)    


<br>

---

## 🚀 5단계 - 내 예약 목록 조회

### 📝 기능 요구사항
✅ 내 예약 목록을 조회하는 API를 구현하세요.

### 💻 구현 전략

1. Reservation 과 Member 연관관계 설정

2. 내 예약 목록 조회    
   ➡️ waitingOrder 컬럼을 추가해 예약 요청 순서 유지    
   ➡️ 가장 순서가 높은 것이 예약 상태 나머지 상태는 예약 대기     



<br>

---
## 🚀 6단계 - 예약 대기 기능

### 📝 기능 요구사항
✅ 예약 대기 요청 기능을 구현하세요.  
✅ 예약 대기 취소 기능도 함께 구현하세요.  
✅ 내 예약 목록 조회 시 예약 대기 목록도 함께 포함하세요.  
✅ 중복 예약이 불가능 하도록 구현하세요.  



### 💻 구현 전략

1. Waiting 엔티티 생성     
   ➡️ 예약 대기 테이블 별도로 생성하여 예약 대기 요청을 관리  
   ➡️ order 컬럼을 추가하여 예약 대기 순서 상태를 유지  
   (예약 취소 시 취소된 예약대기보다 후순위의 대기를 앞당기는 로직 추가)  


2. 동시성 문제 (비관적 락)  
   ➡️ 예약 대기 동시 요청시 대기 순번 중복 문제발생  
   ➡️ 해당 시간대와 테마별로 마지막 대기 순번을 관리하는 `WaitingOrderCounter` 테이블을 별도로 설계  
   ➡️ 예약 대기 요청의 순번 조회시 `WaitingOrderCounter`의 레코드에 락을 걸어 동시 요청을 직렬화해 순번 충돌을 방지   



<br>

---
## 🚀 7단계 - @Configuration

### 📝 기능 요구사항
✅ JWT 관련 로직을 roomescape와 같은 계층의 auth 패키지의 클래스로 분리하세요.  
✅ 불필요한 DB 접근을 최소화 하세요.  



### 💻 구현 전략

1. jwt 모듈 분리     
   ➡️ Jwt 토큰을 발급, 파싱했던 `JwtTokenProvider`와    
   ➡️ Jwt관련 환경변수를 담은 `JwtProperties` 클래스를 외부 jwt 패키지로 분리하고  
   ➡️ 해당 객체들을 `JwtConfig`를 통해 빈으로 등록

이렇게 분리된 설정을 Application 클래스에 `@Import`




<br>

---
## 🚀 8단계 - Profile과 Resource

### 📝 기능 요구사항
✅ schema.sql 대신 데이터베이스를 초기화 해주기 위해 실행하는 클래스를 만드세요.    
✅ token 생성에 필요한 비밀키값을 외부 파일로 분리하세요  



### 💻 구현 전략

1. DB 초기화     
   ➡️ Production용과 DataLoader    
   ➡️ Test용 TestDataLoader를 따로 만들어 DB 초기화

2. prod 환경과 test 환경 분리  
   ➡️ test 에 관한 설정은 test 패키지 하위 application.yml 파일 생성





<br>

---
## 🚀 9단계 - 배포 스크립트

### 📝 기능 요구사항
✅ ec2나 서버에서 배포를 할 수 있게 배포 스크립트를 작성하세요.    



### 💻 구현 전략

1. EC2 인스턴스 생성       



2. 인스턴스에 ssh 접속 후 메모리 스왑 설정  
   ➡️ 부족한 인스턴스의 메모리를 디스크를 활용해 메모리의 내용을 스왑함으로써, 사용가능한 메모리 상한 올림  


3. github 에 코드를 pull


4. 프로젝트 빌드


5. 빌드된 jar 파일 백 그라운드로 실행
