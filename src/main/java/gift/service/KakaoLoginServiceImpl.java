package gift.service;

import gift.dto.KakaoUserDTO;
import gift.dto.Response.AccessTokenResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Service
public class KakaoLoginServiceImpl implements KakaoLoginService {

    private static final Logger logger = LoggerFactory.getLogger(KakaoLoginServiceImpl.class);

    @Value("${kakao.client-id}")
    private String clientId;

    @Value("${kakao.redirect-uri}")
    private String redirectUri;

    private static final String KAKAO_TOKEN_URL = "https://kauth.kakao.com/oauth/token";
    private static final String KAKAO_USER_URL = "https://kapi.kakao.com/v2/user/me";
    private static final String KAKAO_MESSAGE_URL = "https://kapi.kakao.com/v2/api/talk/memo/default/send";

    @Bean
    public RestTemplate restTemplate() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(5000);
        factory.setReadTimeout(5000);
        return new RestTemplate(factory);
    }

    @Override
    public AccessTokenResponse getAccessToken(String code) {
        RestTemplate restTemplate = restTemplate();
        HttpHeaders headers = createHeaders(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> params = createTokenRequestParams(code);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);
        ResponseEntity<AccessTokenResponse> response = restTemplate.postForEntity(KAKAO_TOKEN_URL, request, AccessTokenResponse.class);

        if (!response.getStatusCode().is2xxSuccessful()) {
            logger.error("Failed to get access token: {}", response.getStatusCode());
            throw new RuntimeException("Failed to get access token: " + response.getStatusCode());
        }

        return response.getBody();
    }

    @Override
    public KakaoUserDTO getUserInfo(String accessToken) {
        RestTemplate restTemplate = restTemplate();
        HttpHeaders headers = createHeadersWithBearerAuth(accessToken);

        HttpEntity<String> userInfoRequest = new HttpEntity<>(headers);
        ResponseEntity<KakaoUserDTO> userInfoResponse = restTemplate.exchange(KAKAO_USER_URL, HttpMethod.GET, userInfoRequest, KakaoUserDTO.class);

        if (!userInfoResponse.getStatusCode().is2xxSuccessful()) {
            logger.error("Failed to retrieve user information: {}", userInfoResponse.getStatusCode());
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
        RestTemplate restTemplate = restTemplate();
        HttpHeaders headers = createHeadersWithBearerAuth(accessToken);

        MultiValueMap<String, String> params = createMessageRequestParams(nickname);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);
        ResponseEntity<String> response = restTemplate.postForEntity(KAKAO_MESSAGE_URL, request, String.class);
        if (response.getStatusCode().is2xxSuccessful()) {
            logger.info("Message sent successfully");
        } else {
            logger.error("Failed to send message: {}", response.getStatusCode());
        }
    }

    private HttpHeaders createHeaders(MediaType contentType) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(contentType);
        headers.setAcceptCharset(List.of(StandardCharsets.UTF_8));
        return headers;
    }

    private HttpHeaders createHeadersWithBearerAuth(String accessToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        headers.setAcceptCharset(List.of(StandardCharsets.UTF_8));
        return headers;
    }

    private MultiValueMap<String, String> createTokenRequestParams(String code) {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "authorization_code");
        params.add("client_id", clientId);
        params.add("redirect_uri", redirectUri);
        params.add("code", code);
        return params;
    }

    private MultiValueMap<String, String> createMessageRequestParams(String nickname) {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("template_object", String.format("{\"object_type\":\"text\",\"text\":\"%s님이 Spring-gift-order에 로그인했습니다.\",\"link\":{\"web_url\":\"http://localhost:8080\",\"mobile_web_url\":\"http://localhost:8080\"}}", nickname));
        return params;
    }
}
