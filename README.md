# spring-gift-order

<step3 과제>

1. 구현 내용
- Swagger 을 사용해서 API 문서를 작성했습니다.
- 먼저 config 파일을 만들고 springdoc 의존성을 추가 한다음
프로그램을 돌려서 http://localhost:8080/swagger-ui/index.htm
여기에 접속했더니 , 자동으로 HTTP통신 목록이 나열되어있었습니다.
- 가독성을 높이기 위해서 controller에는 @operation을 추가하고
DTO에는 @schema를 추가했습니다.
