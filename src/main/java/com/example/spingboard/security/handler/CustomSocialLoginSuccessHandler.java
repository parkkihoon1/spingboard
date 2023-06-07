package com.example.spingboard.security.handler;

import com.example.spingboard.security.dto.MemberSecurityDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Log4j2
@RequiredArgsConstructor
public class CustomSocialLoginSuccessHandler implements AuthenticationSuccessHandler {
    private final PasswordEncoder passwordEncoder;


    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {

            log.info("--------------------------");
            log.info("CusetomLoginSuccessHandler onAuthenticationSuccess.......");
            log.info(authentication.getPrincipal());

        MemberSecurityDTO memberSecurityDTO = (MemberSecurityDTO) authentication.getPrincipal();

        String encodePw = memberSecurityDTO.getMpw();

        //소셜 로그인이고 회원의 패스워드가 1234 라면
        if(memberSecurityDTO.isSocial() && (memberSecurityDTO.getMpw().equals("1234")
        || passwordEncoder.matches("1234", memberSecurityDTO.getMpw()))){
            log.info("Should Change Password");

            log.info("Redirect to Member Modify");
            response.sendRedirect("/member/modify");

            return;
        } else {
            response.sendRedirect("/board/list");
        }

    }
}
