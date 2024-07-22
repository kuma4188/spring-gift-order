package gift.dto.Request;

import java.util.List;

public class AddToWishlistRequest {
    private Long productId;
    private int quantity;
    private List<OptionRequest> options;

    public Long getProductId() {
        return productId;
    }

    public int getQuantity() {
        return quantity;
    }

    public List<OptionRequest> getOptions() {
        return options;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public void setOptions(List<OptionRequest> options) {
        this.options = options;
    }
}
