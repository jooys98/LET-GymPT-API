💪Let-gymPT API

<img width="487" alt="스크린샷 2025-03-21 오후 6 36 23" src="https://github.com/user-attachments/assets/02ae867b-e91f-4ab1-99ed-7b8244b6835c" />





Let-gymPT는 지역별 헬스장과 트레이너를 쉽게 찾고, 합리적인 금액으로 PT를 받을 수 있는 역경매 시스템을 제공하는 헬스 플랫폼입니다. 
트레이너와 일반 회원이 함께 소통할 수 있는 커뮤니티를 통해 헬스 문화를 활성화하고, 실시간 알림과 채팅, 리뷰 시스템으로 편리한 사용자 경험을 제공합니다.


🔍 프로젝트 소개

Let-gymPT는 트레이너와 회원을 연결하는 종합 헬스 플랫폼입니다. 
사용자는 지역별 헬스장과 트레이너를 검색하고 예약할 수 있으며, 
역경매 시스템을 통해 자신에게 맞는 조건의 PT를 합리적인 가격에 받을 수 있습니다. 
WebSocket 기반의 실시간 소통과 AI 기반 리뷰 필터링 및 요약 기능으로 신뢰성 있는 헬스 커뮤니티를 구축합니다.





🔍서버 아키텍처 (모놀리식 아키텍처)

<img width="1020" alt="GymPT_아키텍처구성도" src="https://github.com/user-attachments/assets/1be6ab56-2fa1-4dde-bdf8-33b348997499" />



🔍배포 아키텍처

<img width="1059" alt="스크린샷 2025-03-25 오후 6 38 07" src="https://github.com/user-attachments/assets/ba58766c-a29d-4fa1-bbb8-c4944387db89" />






🔍swagger


:http://localhost:8080/swagger-ui/index.html






<img width="1277" alt="스크린샷 2025-03-25 오후 6 59 34" src="https://github.com/user-attachments/assets/b859d864-fb14-431e-b5cf-ce249f9ff8c9" />











🛠️ 기술 스택

#백엔드

Spring Boot (JPA): 메인 백엔드 프레임워크

WebSocket (STOMP): 실시간 채팅 및 역경매 기능 구현

JWT: 토큰 기반 인증/인가 처리

QueryDSL: 복잡한 데이터 조회 최적화

Mono: AI 리뷰 필터링 비동기 처리



#데이터베이스

MySQL: 관계형 데이터베이스로 핵심 데이터 관리 및 트랜잭션 처리

MongoDB: 채팅 내역 저장 및 조회 서비스

Amazon S3: 이미지 파일 저장 및 관리


#외부 연동

Kakao OAuth2: 소셜 로그인 구현

FCM: 푸시 알림 서비스

OpenAI GPT-3.5: 리뷰 필터링 및 요약 서비스


#기타 도구

Swagger API 3.0.0: API 문서화 

Apache POI: 엑셀 데이터 자바 객체 변환 처리

Figma: UI/UX 디자인




📖ERD 



<img width="929" alt="스크린샷 2025-03-20 오후 4 05 54" src="https://github.com/user-attachments/assets/826fd4b2-1209-4ad6-a5e8-9367aab671ba" />




🌟Figma


https://www.figma.com/design/mcWNBEhzZCT0j2n9lMutWp/Untitled?node-id=0-1&t=SBiyAAzXPIRNruMA-1









🗃️ 주요 기능


#사용자별 기능


👤 일반 회원 (Member)

로그인/회원가입/로그아웃 (카카오 소셜 로그인 지원)

다양한 조건으로 헬스장/트레이너 검색 및 조회

헬스장/트레이너 좋아요 기능

PT 역경매 참여

커뮤니티 글 작성, 수정, 삭제, 검색

트레이너와 1:1 채팅 상담

예약시 예약 당일 아침 알림 서비스 


👨‍🏫 트레이너 (Trainer)

트레이너 권한 신청 및 승인 프로세스

역경매 입찰 참여

자신의 받은 좋아요 수 확인

본인에게 달린 리뷰 확인

커뮤니티 활동



👨‍💼 관리자 (Admin)

트레이너 권한 승인/거부

트레이너 신청 목록 관리

지역별 헬스장 추가, 수정, 삭제

서비스 전반 관리





#핵심 서비스



🔄 PT 역경매 시스템

사용자의 역경매 요청 등록 (희망 지역, 트레이너 성별, 신체 질환 정보, 수업 스타일 등)

해당 지역 트레이너들에게 실시간 알림

트레이너들의 수업 제안 (가격, 프로필, 수업 내용)

사용자에게 트레이너 제안 실시간 알림

사용자의 트레이너 선택

최종 매칭 완료



💬 실시간 알림 및 채팅

WebSocket을 활용한 실시간 양방향 통신

신규 역경매 요청, 새로운 트레이너 입찰, 매칭 완료 등 주요 이벤트 알림

트레이너와 사용자간 1:1 채팅 서비스



✍️ 리뷰 시스템

예약 후 1일 뒤 리뷰 작성 가능

AI 기반 악의적 리뷰 필터링, 필터링 완료 후 푸쉬 알림 전송

헬스장 당 3개 이상 리뷰 누적 시 AI 리뷰 요약 제공



🏋️ 헬스 커뮤니티

헬스 관련 정보 및 일상 공유 커뮤니티

댓글 , 조회수를 기반으로 한 인기 게시글 조회 

회원: 글쓰기, 삭제, 댓글 기능

비회원: 조회만 가능







📊 기술적 특징


대용량 데이터 처리

Apache POI를 활용한 엑셀 데이터 자바 객체 변환 및 대량 데이터 삽입



보안 및 인증

JWT 토큰 기반 인증/인가 시스템

카카오 OAuth2 소셜 로그인 구현



실시간 통신

WebSocket과 STOMP 프로토콜을 활용한 양방향 실시간 통신



AI 활용

GPT-3.5 기반 리뷰 필터링 및 요약 서비스

Mono를 활용한 비동기 처리



데이터베이스 최적화

MySQL: 관계형 데이터베이스 활용 (정규화, 자가참조, 무한 depth 엔티티)

MongoDB: 문서형 데이터베이스로 채팅 내역 고속 처리

QueryDSL: 복잡한 데이터 조회 최적화



📝 디렉토리 구조


<img width="513" alt="스크린샷 2025-03-20 오후 4 13 52" src="https://github.com/user-attachments/assets/3381acb2-c72e-4985-bcb3-bb349fe94f14" />




🌟 향후 계획

모바일 앱 제작 

결제 시스템 연동

트레이너 평가 시스템 고도화

사용자 맞춤형 PT 프로그램 추천 기능

redis 를 활용한 지역별 인기 헬스장 / 트레이너 캐싱 




