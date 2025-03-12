package com.example.gympt.domain.member.service;

import com.example.gympt.domain.member.dto.JoinRequestDTO;
import com.example.gympt.domain.member.dto.MemberRequestDTO;
import com.example.gympt.domain.member.dto.MemberResponseDTO;
import com.example.gympt.domain.member.entity.Member;
import com.example.gympt.domain.member.enums.MemberRole;
import com.example.gympt.domain.member.repository.MemberRepository;
import com.example.gympt.props.JWTProps;
import com.example.gympt.security.CustomUserDetailService;
import com.example.gympt.security.MemberAuthDTO;
import com.example.gympt.util.JWTUtil;
import com.example.gympt.util.s3.CustomFileUtil;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@Transactional
@Service
public class MemberServiceImpl implements MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final CustomUserDetailService customUserDetailService;
    private final JWTProps jwtProps;
    private final JWTUtil jwtUtil;
    private final CustomFileUtil customFileUtil;


    @Transactional
    @Override
    public void join(JoinRequestDTO request) {

        memberRepository.findByEmail(request.getEmail())
                .ifPresent(member -> {
                    throw new IllegalArgumentException("이미 존재하는 회원입니다");
                });
// joinRequestDTO -> Member Entity
        LocalDate birthday = null;
        String birthdayStr = request.getBirthday(); // "1990.5.15" 형식
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy.M.d");
            Date date = dateFormat.parse(birthdayStr);
            birthday = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        } catch (ParseException e) {
            // 예외 처리
        }
        Member member = Member.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .name(request.getName())
                .address(request.getAddress())
                .localName(request.getLocalName())
                .birthday(birthday)
                .phone(request.getPhone())
                .build();
        member.addGender(request.getGender());

        String role = request.getRole();
        if (role.equals("TRAINER")) {
            member.addRole(MemberRole.PREPARATION_TRAINER);
        } else if (role.equals("USER")) {
            member.addRole(MemberRole.USER);
        } else if (role.equals("ADMIN")) {
            member.addRole(MemberRole.ADMIN);
        }
//TODO : 타입 불일치 주의
        log.info("member: {}", member);
        //권한 업데이트
        memberRepository.save(member);
    }

    @Override
    public Map<String, Object> login(String email, String password) {

        MemberAuthDTO memberAuthDTO = (MemberAuthDTO) customUserDetailService.loadUserByUsername(email);
        //인증객체 생성
        log.info("memberAuthDTO: {}", memberAuthDTO);

        if (!passwordEncoder.matches(password, memberAuthDTO.getPassword())) {
            throw new IllegalArgumentException("비밀번호가 틀렸습니다");
        }
        Map<String, Object> claims = memberAuthDTO.getClaims();
        String accessToken = jwtUtil.generateToken(claims, jwtProps.getAccessTokenExpirationPeriod());
        String refreshToken = jwtUtil.generateToken(claims, jwtProps.getRefreshTokenExpirationPeriod());
//인증객체 정보를 토대로 토큰 발급
        claims.put("accessToken", accessToken);
        claims.put("refreshToken", refreshToken);

        return claims;

    }


    @Override
    public Boolean checkedEmail(String email) {
        Boolean result = memberRepository.existsByEmail(email);
        return result;
    }

    @Override
    public Map<String, Object> getSosialClaim(MemberAuthDTO memberAuthDTO) {
        //앱소셜로그인을 위한 토큰 발급
        Map<String, Object> claim = memberAuthDTO.getClaims();

        String email = claim.get("email").toString();
        String userRole = claim.get("role").toString();
        String jwtAccessToken = jwtUtil.generateToken(claim, (int) (60 * 60 * 1000L));
        String jwtRefreshToken = jwtUtil.generateToken(claim, jwtProps.getRefreshTokenExpirationPeriod());
        claim.put("email", email);
        claim.put("role", userRole);
        claim.put("accessToken", jwtAccessToken);
        claim.put("refreshToken", jwtRefreshToken);
        return claim;

    }

    @Override
    public MemberResponseDTO updateMember(String email, MemberRequestDTO memberRequestDTO) {
        Member member = getMember(email);

        if (memberRequestDTO.getName() != null && !memberRequestDTO.getName().isEmpty()) {
            member.updateName(memberRequestDTO.getName());
        }

        if (memberRequestDTO.getPhone() != null && !memberRequestDTO.getPhone().isEmpty()) {
            member.updatePhone(memberRequestDTO.getPhone());
        }

        // 기존의 파일
        String oldImageName = member.getProfileImage();

        // 새로 업로드해야 하는 파일
        // 화면에서 변화 없이 계속 유지될 파일
        String uploadedImageName = oldImageName;

        // 새로 업로드된 파일이 있으면(이미지를 바꿧으면)
        if (memberRequestDTO.getProfileImage() != null && !memberRequestDTO.getProfileImage().isEmpty()) {
            uploadedImageName = customFileUtil.uploadS3File(memberRequestDTO.getProfileImage());
            // 기존 파일 삭제
//            fileUtil.deleteS3File(oldImageName);
        }

        member.updateProfileImage(uploadedImageName);
        member.updatePhone(memberRequestDTO.getPhone());
        member.updateAddress(memberRequestDTO.getAddress());
        member.updateLocalName(memberRequestDTO.getLocalName());

        return entityToDTO(memberRepository.save(member));
    }


    @Override
    public MemberResponseDTO getMemberDetail(String email) {
        Member member = getMember(email);
        return this.entityToDTO(member);
    }

    @Transactional
    @Override
    public void updateFCMToken(String targetEmail, String fcmToken) {
        Member member = memberRepository.findByEmail(targetEmail)
                .orElseThrow(() -> new IllegalArgumentException("해당 회원이 존재하지 않습니다. email: " + targetEmail));

        member.updateFcmToken(fcmToken);
    }

    private Member getMember(String email) {
        return memberRepository.getWithRoles(email).orElseThrow(() -> new EntityNotFoundException("존재하지 않는 회원입니다"));
    }


}
