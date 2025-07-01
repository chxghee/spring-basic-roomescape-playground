# 🚪 방탈출 예약 홈페이지  

## 🚀 1단계 - 로그인

### 📝 기능 요구사항
✅ /login에 email, password 값을 body에 포함하세요.  
✅ 응답에 Cookie에 "token"값으로 토큰이 포함되도록 하세요.

### 💻 구현 전략

1. 로그인 시 jwt 토큰 발급   
   ➡️ access token 유효기간 30분 설정     
   ➡️ 토큰에 id, name, role 정보만 담는다  
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



<br>
