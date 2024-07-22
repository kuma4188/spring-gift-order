package gift.dto.Response;

public class AddToWishlistResponse {
    private boolean success;

    public AddToWishlistResponse(boolean success) {
        this.success = success;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }
}
