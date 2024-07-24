package gift.dto;

public class KakaoUserDTO {
    private Long id;
    private Properties properties;

    public static class Properties {
        private String nickname;

        public String getNickname() {
            return nickname;
        }
        public void setNickname(String nickname) {
            this.nickname = nickname;
        }
    }

    public Long getId() {
        return id;
    }
    public Properties getProperties() {
        return properties;
    }

    public void setId(Long id) {
        this.id = id;
    }
    public void setProperties(Properties properties) {
        this.properties = properties;
    }
}
