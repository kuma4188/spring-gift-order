# spring-gift-order

<step2 과제>

1. 추가한 파일
- Order
- OrderDTO
- OrderController
- OrderRepository
- OrderService
- OrderServiceImpl

2. 구현 내용
- 최대 옵션 수량 추가 (OptionDTO)
- 위시리스트에 주문하기 버튼 생성
-> 위시리스트에서만 주문이 가능하도록( 코드 복잡성을 줄이기 위해서)
- 주문하기 버튼 클릭시 위시리스트에서 상품이 사라지도록 동작
- 주문하기 버튼 클릭시 주문한 옵션 수량만큼 최대옵션수량에서 차감
- 카카오톡 메시지 전송 (로그인 시 / 주문 시 )
- 메시지를 보낼때는 토큰과 보낼 메시지가 담겨있어 인증된 이용자만 메시지를 보낼수 있도록 구현

