package com.example.gympt.domain.member.service;

import com.example.gympt.domain.member.dto.KakaoMoblieRequestDTO;
import com.example.gympt.domain.member.entity.Member;
import com.example.gympt.domain.member.enums.MemberRole;
import com.example.gympt.domain.member.repository.MemberRepository;
import com.example.gympt.props.SocialProps;
import com.example.gympt.security.MemberAuthDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class KakaoServiceImpl implements KakaoService {

    private final RestTemplate restTemplate;
    private final SocialProps socialProps;
    private final MemberService memberService;
    private final MemberRepository memberRepository;
private final PasswordEncoder passwordEncoder;

    @Override
    public String getKakaoAccessToken(String code) {
        // 인가코드 + 필요한 설정 파일(SocialProps)을 form 데이터 형식으로
        //oauth2에 엑세스 토큰 요청을 하는 과정임
        //String code : 이게 카카오 -> 클라이언트 에게서 받은 인가 코드
        log.info("getKakaoAccessToken", code);

        //oauth2 와 연결되기 위해 필요한 설정값들
        String kakaoTokenURL = socialProps.getKakao().getTokenUri();
        String clientID = socialProps.getKakao().getClientId();
        String clientSecret = socialProps.getKakao().getClientSecret();
        String redirectURI = socialProps.getKakao().getRedirectUri();

        log.info("TokenURL: {}", kakaoTokenURL);
        log.info("ClientID: {}", clientID);
        log.info("RedirectURI: {}", redirectURI);


        HttpHeaders headers = new HttpHeaders();
// oauth2에게 보낼 HttpHeaders 를 생성한다
        headers.add("Content-Type", "application/x-www-form-urlencoded");
//form 데이타 타입으로 보냄 ( 서버 -> oauth2)
        HttpEntity<String> entity = new HttpEntity<>(headers);
//HTTP 요청/응답을 나타내는 클래스


        //카카오Oauth2 에게 보낼 http 요청을 구성하고 요청에 필요한 설정값들을 넣어준다
        UriComponents uriBuilder = UriComponentsBuilder.fromHttpUrl(kakaoTokenURL)
                //kakaoTokenURL : 카카오와 연결되는 서버의 주소
                .queryParam("grant_type", "authorization_code")
                .queryParam("client_id", clientID)
                .queryParam("client_secret", clientSecret)
                .queryParam("redirect_uri", redirectURI)
                .queryParam("code", code)
                .build();
        //UriComponentsBuilder : url 을 구성하는 빌더  클래스
        //queryParam : url 에 쿼리 파라미터를 추가함
        //이렇게 값이 나오게 된다 ..
        //https://kauth.kakao.com/oauth/token?grant_type=authorization_code&client_id=xxx&...


/**
 * 여기서 부턴 카카오에게서 받은 응답을 처리 + 엑세스 토큰만 검출 해내는 과정임
 */
        //LinkedHashMap : 순서가 있는 map 자료구조
        ResponseEntity<LinkedHashMap> response = restTemplate.exchange(uriBuilder.toString(), HttpMethod.POST, entity, LinkedHashMap.class);
        //response 이건 클라에게 보낼 응답이 아니라 카카오에게 받은 응답임 헷깔리지 말기
        //restTemplate.exchange : 카카오 서버에 post 요청을 보낸다
        //카카오 oauth2 에게서 받은 응답이 LinkedHashMap 형식으로 오게 된다
        // 그 응답에는 엑세스 토큰이 포함 되어있음


        log.info("Kakao token response: {}", response);

        LinkedHashMap<String, String> bodyMap = response.getBody();
        //카카오가 보낸 응답의 body 를 문자열로 변환
        log.info("Kakao token bodyMap: {}", bodyMap);
        return bodyMap.get("access_token");
        //바디 내용중 엑세스 토큰만 걸러냄
    }

    @Override
    public MemberAuthDTO getKakaoMember(String accessToken) {
        log.info("getKakaoMember start...");
        String email = this.getEmailFromKakaoAccessToken(accessToken);
        //
        log.info("getKakaoMember email: {}", email);
        //소셜유저 테이블에 회원이 있는지 없는지 조회
        Optional<Member> member = memberRepository.findByEmail(email);

        if (member.isPresent()) {
            Member kakaoMember = member.get();
            return memberService.toAuthDTO(kakaoMember);
        } //회원일 경우 인증 객체로 리턴

        Member socialUser = this.makeKakaoUser(email);
        memberRepository.save(socialUser);
        return memberService.toAuthDTO(socialUser);
        //회원이 아닐경우 회원가입 진행
    }



    private Member makeKakaoUser(String email) {
        String tempPassword = this.makeTempPassword();
        Member user = Member.builder()
                .email(email)
                .password(passwordEncoder.encode(tempPassword))
                .memberRoleList(new ArrayList<>(Arrays.asList(MemberRole.USER)))
                .build();

        return user; // 카카오 계정으로 회원가입
    }

    private String makeTempPassword() {
        //임시비번 발급 메서드
        StringBuffer buffer = new StringBuffer();

        for (int i = 0; i < 10; i++) {
            buffer.append((char) ((int) (Math.random() * 55) + 65));
        }
        return buffer.toString();

    }

    private String getEmailFromKakaoAccessToken(String accessToken) {
        //위에서 kakaoOauth2 에서 받은 엑세스 토큰으로 한번 더 카카오에게 유저의 정보(username , email) 를 요청함
        //보안을 위해 의도적으로 분리된 과정 -> redirect 방식
        log.info("getEmailFromKakaoAccessToken start...");
        String kakaoGetUserURL = socialProps.getKakao().getUserInfoUri();

        if (accessToken == null) {
            throw new RuntimeException("Access Token is null");
        }

        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + accessToken);
        headers.add("Content-Type", "application/x-www-form-urlencoded");

        HttpEntity<String> entity = new HttpEntity<>(headers);

        UriComponents uriBuilder = UriComponentsBuilder.fromHttpUrl(kakaoGetUserURL).build();

        //카카오서버에게 요청 전달 후 받은 response
        ResponseEntity<LinkedHashMap> response
                = restTemplate.exchange(uriBuilder.toString(), HttpMethod.GET, entity, LinkedHashMap.class);

        log.info("response: {}", response);

        //response 에서 유저의 이메일만 검출
        LinkedHashMap<String, LinkedHashMap> bodyMap = response.getBody();
        log.info("bodyMap: {}", bodyMap);

        LinkedHashMap<String, String> kakaoAccount = bodyMap.get("kakao_account");
        log.info("kakaoAccount: {}", kakaoAccount);

        return kakaoAccount.get("email");

    }


//모바일 로그인용 인증객체
    @Override
    public MemberAuthDTO getKakaoMoblieMember(KakaoMoblieRequestDTO loginRequestDTO) {
        Member member = memberRepository.getWithRoles(loginRequestDTO.getEmail()).orElse(null);
//회원이 존재하는지 조회
        if (member == null) {

            //여기서 회원가입 시키고  MemberAuthDTO 로 만들어서 보내기
            Member newKakaoMember = makeKakaoUser(loginRequestDTO.getEmail());
            return memberService.toAuthDTO(newKakaoMember);

        } else {
            return  memberService.toAuthDTO(member);
        }
    }
}
