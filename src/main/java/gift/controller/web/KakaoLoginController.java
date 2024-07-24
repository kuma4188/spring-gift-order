package gift.controller.web;


import gift.dto.KakaoUserDTO;
import gift.dto.Response.AccessTokenResponse;
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
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Collections;

@Controller
public class KakaoLoginController {

    @Value("${kakao.client-id}")
    private String clientId;

    @Value("${kakao.redirect-uri}")
    private String redirectUri;

    @Autowired
    private KakaoLoginService kakaoLoginService;

    @RequestMapping("/login/kakao")
    public void redirectToKakao(HttpServletResponse response) throws IOException {
        String redirectUrl = String.format(
            "https://kauth.kakao.com/oauth/authorize?client_id=%s&redirect_uri=%s&response_type=code&scope=profile_nickname,talk_message",
            clientId, redirectUri);
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

            if (nickname != null) {
                session.setAttribute("nickname", nickname);
                kakaoLoginService.sendMessage(tokenResponse.getAccess_token(), nickname);

                Authentication auth = new UsernamePasswordAuthenticationToken(nickname, null, Collections.singleton(new SimpleGrantedAuthority("ROLE_USER")));
                SecurityContextHolder.getContext().setAuthentication(auth);
                session.setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, SecurityContextHolder.getContext());

                return "redirect:/web/products/list";
            } else {
                model.addAttribute("error", "Failed to retrieve user information. User Info: " + userInfo);
                return "error";
            }
        } catch (Exception e) {
            model.addAttribute("error", "An error occurred: " + e.getMessage());
            return "error";
        }
    }
}
