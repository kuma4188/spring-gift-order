package gift.controller.api;

import gift.dto.OrderDTO;
import gift.dto.KakaoUserDTO;
import gift.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpSession;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    @Autowired
    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping("/order/{wishlistId}")
    public ResponseEntity<OrderDTO> placeOrder(@PathVariable("wishlistId") Long wishlistId, HttpSession session) {
        KakaoUserDTO kakaoUserDTO = (KakaoUserDTO) session.getAttribute("kakaoUserDTO");
        String accessToken = (String) session.getAttribute("accessToken");

        if (kakaoUserDTO == null || accessToken == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        System.out.println("OrderController 실행됨");
        System.out.println("액세스 토큰: " + accessToken);

        OrderDTO orderDTO = orderService.placeOrder(kakaoUserDTO, wishlistId, accessToken);

        return new ResponseEntity<>(orderDTO, HttpStatus.CREATED);
    }
}
