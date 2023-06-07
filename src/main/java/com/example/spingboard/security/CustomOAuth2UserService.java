package com.example.spingboard.security;

import com.example.spingboard.domain.Member;
import com.example.spingboard.domain.MemberRole;
import com.example.spingboard.repository.MemberRepository;
import com.example.spingboard.security.dto.MemberSecurityDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;


//카카오 서비스 로그인까지 성공해도 게시물 작성에 문제
// 문제 처리를 위해 UserDetailsService 인터페이스를 구현하듯
// OAuth2UserService 인터페이스를 구현해야 함.
// OAuth2UserService 인터페이스를 구현 할 수도 있지만
//하위클래스인 DefaultOAuth2UserService를 상속해서 구현하는 방식이 더 간단
@Log4j2
@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException{

        log.info("userRequest...");
        log.info(userRequest);

        log.info("oauth2 user ...");

        ClientRegistration clientRegistration = userRequest.getClientRegistration();
        String clientName = clientRegistration.getClientName();

        log.info("NAME:" + clientName);

        OAuth2User oAuth2User = super.loadUser(userRequest);
        Map<String, Object> parameMap = oAuth2User.getAttributes();

        parameMap.forEach((k, v) -> {
            log.info("-----------");
            log.info(k + ":" + v);
        });

        String email = null;
        switch (clientName){
            case "kakao":
                email = getKakaoEmail(parameMap);
                break;
        }
        log.info("=============");
        log.info(email);
        log.info("=============");

        return generateDTO(email, parameMap);
    }

    private String getKakaoEmail(Map<String, Object> parameMap){
        log.info("KAKAO---------------");

        Object value = parameMap.get("kakao_account");
        log.info(value);

        LinkedHashMap accountMap = (LinkedHashMap) value;
        String email = (String) accountMap.get("email");

        log.info("email..." + email);
        return email;
    }

    private MemberSecurityDTO generateDTO(String email, Map<String, Object> params) {
        Optional<Member> result = memberRepository.findByEmail(email);

        //데이터 베이스에 해당 이메일의 사용자가 없다면
        if (result.isEmpty()) {
            //회원추가 -- mid는 이메일 주소 / 패스워드는 1234
            Member member = Member.builder()
                    .mid(email)
                    .mpw(passwordEncoder.encode("1234"))
                    .email(email)
                    .social(true)
                    .build();
            member.addRole(MemberRole.USER);
            memberRepository.save(member);

            //MemberSecurityDTO 구성 및 반환
            MemberSecurityDTO memberSecurityDTO =
                    new MemberSecurityDTO(email, "1234", email, false, true,
                            Arrays.asList(new SimpleGrantedAuthority("ROLE_USER")));
            memberSecurityDTO.setProps(params);

            return memberSecurityDTO;
        } else {
            Member member = result.get();
            MemberSecurityDTO memberSecurityDTO =
                    new MemberSecurityDTO(
                            member.getMid(),
                            member.getMpw(),
                            member.getEmail(),
                            member.isDel(),
                            member.isSocial(),
                            member.getRoleSet().stream().map(
                                    memberRole -> new SimpleGrantedAuthority("ROLE_" + memberRole.name())
                            ).collect(Collectors.toList())
                    );
            return memberSecurityDTO;
        }
    }

}
