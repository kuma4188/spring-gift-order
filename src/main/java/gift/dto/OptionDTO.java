package gift.dto;

import gift.model.Option;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class OptionDTO {
    @NotNull(message = "ID는 필수 값입니다.")
    private Long id;
    @NotBlank(message = "이름은 필수 값입니다.")
    @Size(max = 50, message = "이름은 최대 50자까지 가능합니다.")
    private String name;
    @Min(value = 1, message = "수량은 최소 1 이상이어야 합니다.")
    @Max(value = 100000000, message = "수량은 최대 1억개 이하이어야 합니다.")
    private int quantity;
    @Min(value = 0, message = "가격은 최소 0 이상이어야 합니다.")
    private int price;
    @NotNull(message = "상품 ID는 필수 값입니다.")
    private Long productId;

    public OptionDTO() {}

    public OptionDTO(Long id, String name, int quantity, int price, Long productId) {
        this.id = id;
        this.name = name;
        this.quantity = quantity;
        this.price = price;
        this.productId = productId;
    }

    // Getters and Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public static OptionDTO convertToDTO(Option option) {
        return new OptionDTO(
            option.getId(),
            option.getName(),
            option.getQuantity(),
            option.getPrice(),
            option.getProduct().getId()
        );
    }
}
