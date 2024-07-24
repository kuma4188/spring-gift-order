package gift.service;

import gift.dto.Response.AccessTokenResponse;
import gift.dto.KakaoUserDTO;

public interface KakaoLoginService {
    AccessTokenResponse getAccessToken(String code);
    KakaoUserDTO getUserInfo(String accessToken);
    String extractNickname(KakaoUserDTO userInfo);
    void sendMessage(String accessToken, String nickname);
}
