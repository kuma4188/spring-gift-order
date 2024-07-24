package gift.service;


import gift.dto.KakaoUserDTO;
import gift.dto.Response.AccessTokenResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;
import java.util.List;

@Service
public class KakaoLoginServiceImpl implements KakaoLoginService {

    @Value("${kakao.client-id}")
    private String clientId;

    @Value("${kakao.redirect-uri}")
    private String redirectUri;

    @Override
    public AccessTokenResponse getAccessToken(String code) {
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
        ResponseEntity<AccessTokenResponse> response = restTemplate.postForEntity("https://kauth.kakao.com/oauth/token", request, AccessTokenResponse.class);

        if (!response.getStatusCode().is2xxSuccessful()) {
            throw new RuntimeException("Failed to get access token: " + response.getStatusCode());
        }

        return response.getBody();
    }

    @Override
    public KakaoUserDTO getUserInfo(String accessToken) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        headers.setAcceptCharset(List.of(StandardCharsets.UTF_8));

        HttpEntity<String> userInfoRequest = new HttpEntity<>(headers);
        ResponseEntity<KakaoUserDTO> userInfoResponse = restTemplate.exchange("https://kapi.kakao.com/v2/user/me", HttpMethod.GET, userInfoRequest, KakaoUserDTO.class);

        if (!userInfoResponse.getStatusCode().is2xxSuccessful()) {
            throw new RuntimeException("Failed to retrieve user information: " + userInfoResponse.getStatusCode());
        }

        return userInfoResponse.getBody();
    }

    @Override
    public String extractNickname(KakaoUserDTO userInfo) {
        if (userInfo == null || userInfo.getProperties() == null) {
            return null;
        }
        return userInfo.getProperties().getNickname();
    }

    @Override
    public void sendMessage(String accessToken, String nickname) {
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
