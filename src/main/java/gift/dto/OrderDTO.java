package gift.dto;

import gift.model.Order;
import java.time.LocalDateTime;

public class OrderDTO {
    private Long id;
    private Long optionId;
    private int quantity;
    private LocalDateTime orderDateTime;

    public OrderDTO() {
    }

    public OrderDTO(Long id, Long optionId, int quantity, LocalDateTime orderDateTime) {
        this.id = id;
        this.optionId = optionId;
        this.quantity = quantity;
        this.orderDateTime = orderDateTime;
    }

    public static OrderDTO from(Order order) {
        return new OrderDTO(
            order.getId(),
            order.getOption().getId(),
            order.getQuantity(),
            order.getOrderDateTime()
        );
    }

    public Long getId() {
        return id;
    }

    public Long getOptionId() {
        return optionId;
    }

    public int getQuantity() {
        return quantity;
    }

    public LocalDateTime getOrderDateTime() {
        return orderDateTime;
    }
}
