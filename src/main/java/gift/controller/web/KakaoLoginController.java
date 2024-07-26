package gift.controller.web;

import gift.dto.KakaoUserDTO;
import gift.dto.Response.AccessTokenResponse;
import gift.model.SiteUser;
import gift.repository.UserRepository;
import gift.service.KakaoLoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.util.UriComponentsBuilder;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.IOException;
import java.util.Collections;
import java.util.Optional;

@Controller
public class KakaoLoginController {

    private static final Logger logger = LoggerFactory.getLogger(KakaoLoginController.class);

    @Value("${kakao.client-id}")
    private String clientId;

    @Value("${kakao.redirect-uri}")
    private String redirectUri;


    @Autowired
    private KakaoLoginService kakaoLoginService;

    @Autowired
    private UserRepository userRepository;


    @RequestMapping("/login/kakao")
    public void redirectToKakao(HttpServletResponse response) throws IOException {
        String redirectUrl = UriComponentsBuilder.fromHttpUrl("https://kauth.kakao.com/oauth/authorize")
            .queryParam("client_id", clientId)
            .queryParam("redirect_uri", redirectUri)
            .queryParam("response_type", "code")
            .queryParam("scope", "profile_nickname,talk_message")
            .toUriString();
        response.sendRedirect(redirectUrl);
    }

    @GetMapping("/")
    public String kakaoCallback(@RequestParam(required = false) String code, Model model, HttpSession session) {
        if (code == null) {
            model.addAttribute("error", "Authorization code is missing");
            return "error";
        }

        try {
            AccessTokenResponse tokenResponse = kakaoLoginService.getAccessToken(code);
            KakaoUserDTO userInfo = kakaoLoginService.getUserInfo(tokenResponse.getAccess_token());
            String nickname = kakaoLoginService.extractNickname(userInfo);
            session.setAttribute("kakaoUserDTO", userInfo);
            System.out.println("로그인 할떄 발급 받은 토큰 : " +tokenResponse.getAccess_token() );
            session.setAttribute("accessToken", tokenResponse.getAccess_token());

            if (nickname != null) {
                session.setAttribute("nickname", nickname);
                kakaoLoginService.sendMessage(tokenResponse.getAccess_token(), nickname);

                Authentication auth = new UsernamePasswordAuthenticationToken(nickname, null, Collections.singleton(new SimpleGrantedAuthority("ROLE_USER")));
                SecurityContextHolder.getContext().setAuthentication(auth);
                session.setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, SecurityContextHolder.getContext());

                Optional<SiteUser> userOptional = userRepository.findByUsername(nickname);
                if (userOptional.isEmpty()) {
                    SiteUser newUser = new SiteUser();
                    newUser.setUsername(nickname);
                    newUser.setPassword("");
                    newUser.setEmail("");
                    userRepository.save(newUser);
                }

                return "redirect:/web/products/list";
            } else {
                model.addAttribute("error", "Failed to retrieve user information. User Info: " + userInfo);
                return "error";
            }
        } catch (Exception e) {
            model.addAttribute("error", "An error occurred: " + e.getMessage());
            logger.error("An error occurred during Kakao login callback", e);
            return "error";
        }
    }
}
