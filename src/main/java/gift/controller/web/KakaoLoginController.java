package gift.controller.web;

import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.bind.annotation.RequestMapping;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Map;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

@Controller
public class KakaoLoginController {

    @Value("${kakao.client-id}")
    private String clientId;

    @Value("${kakao.redirect-uri}")
    private String redirectUri;

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

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.setAcceptCharset(List.of(StandardCharsets.UTF_8));

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "authorization_code");
        params.add("client_id", clientId);
        params.add("redirect_uri", redirectUri);
        params.add("code", code);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);

        ResponseEntity<Map> response = restTemplate.postForEntity("https://kauth.kakao.com/oauth/token", request, Map.class);

        if (!response.getStatusCode().is2xxSuccessful()) {
            model.addAttribute("error", "Failed to get access token: " + response.getStatusCode());
            return "error";
        }

        String accessToken = (String) response.getBody().get("access_token");

        headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        headers.setAcceptCharset(List.of(StandardCharsets.UTF_8));

        HttpEntity<String> userInfoRequest = new HttpEntity<>(headers);
        ResponseEntity<Map> userInfoResponse = restTemplate.exchange("https://kapi.kakao.com/v2/user/me", HttpMethod.GET, userInfoRequest, Map.class);

        if (!userInfoResponse.getStatusCode().is2xxSuccessful()) {
            model.addAttribute("error", "Failed to retrieve user information: " + userInfoResponse.getStatusCode());
            return "error";
        }

        Map<String, Object> userInfo = userInfoResponse.getBody();
        String nickname = extractNickname(userInfo);

        if (nickname != null) {
            session.setAttribute("nickname", nickname);
            System.out.println("닉네임 세션에 저장: " + nickname);  // 닉네임 저장 확인 로그
            sendMessage(accessToken, nickname);

            // 사용자 인증 설정
            Authentication auth = new UsernamePasswordAuthenticationToken(nickname, null, Collections.singleton(new SimpleGrantedAuthority("ROLE_USER")));
            SecurityContextHolder.getContext().setAuthentication(auth);

            // 아래 코드 추가: 세션을 스프링 시큐리티의 인증 세션으로 설정
            SecurityContextHolder.getContext().setAuthentication(auth);
            session.setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, SecurityContextHolder.getContext());

            return "redirect:/web/products/list";
        } else {
            model.addAttribute("error", "Failed to retrieve user information. User Info: " + userInfo);
            return "error";
        }
    }

    private String extractNickname(Map<String, Object> userInfo) {
        if (userInfo == null) {
            return null;
        }
        if (userInfo.containsKey("properties")) {
            Map<String, Object> properties = (Map<String, Object>) userInfo.get("properties");
            if (properties != null && properties.containsKey("nickname")) {
                return (String) properties.get("nickname");
            }
        }
        return null;
    }

    private void sendMessage(String accessToken, String nickname) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.setBearerAuth(accessToken);
        headers.setAcceptCharset(List.of(StandardCharsets.UTF_8));

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("template_object", String.format("{\"object_type\":\"text\",\"text\":\"%s님이 Spring-gift-order에 로그인했습니다.\",\"link\":{\"web_url\":\"http://localhost:8080\",\"mobile_web_url\":\"http://localhost:8080\"}}", nickname));

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);

        ResponseEntity<String> response = restTemplate.postForEntity("https://kapi.kakao.com/v2/api/talk/memo/default/send", request, String.class);
        if (response.getStatusCode().is2xxSuccessful()) {
            System.out.println("Message sent successfully");
        } else {
            System.out.println("Failed to send message: " + response.getStatusCode());
        }
    }
}
