package com.example.gympt.security;

import com.example.gympt.domain.member.entity.Member;
import com.example.gympt.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class CustomUserDetailService implements UserDetailsService {

    private final MemberRepository memberRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        log.info("loadUserByUsername: username: {}", username);

        Member member = memberRepository.getWithRoles(username)
                .orElseThrow(() -> new UsernameNotFoundException("미존재하는 사용자 email: " + username));

        MemberAuthDTO memberDTO = new MemberAuthDTO(
                member.getEmail(),
                member.getPassword(),
                member.getName(),
                member.getMemberRoleList().stream().map(Enum::name).toList());
//트레이너 권한 조건 걸기
        log.info("loadUserByUsername result memberDTO: {}", memberDTO);

        return memberDTO;
    }
}
