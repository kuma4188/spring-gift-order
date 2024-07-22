package gift.dto.Response;

public class WishlistResponse {
    private boolean success;
    private double totalPrice;

    public WishlistResponse(boolean success) {
        this.success = success;
    }

    public WishlistResponse(boolean success, double totalPrice) {
        this.success = success;
        this.totalPrice = totalPrice;
    }

    public boolean isSuccess() {
        return success;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }
}
