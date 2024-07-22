package gift.dto;

import gift.model.Product;
import gift.service.CategoryService;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class ProductDTO {

    private Long id;
    @NotBlank(message = "이름은 필수 값입니다.")
    @Size(max = 20, message = "이름은 최대 20자까지 가능합니다.")
    private String name;
    @Min(value = 0, message = "가격은 최소 0 이상이어야 합니다.")
    private int price;
    @Size(max = 255, message = "이미지 URL은 최대 255자까지 가능합니다.")
    private String imageUrl;
    @NotNull(message = "카테고리는 필수 값입니다.")
    private int category;
    @NotNull(message = "카테고리 이름은 필수 값입니다.")
    private String categoryName;

    public ProductDTO() {
    }

    public ProductDTO(Long id, String name, int price, String imageUrl, int category, String categoryName) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.imageUrl = imageUrl;
        this.category = category;
        this.categoryName = categoryName;
    }

    // Getters and Setters

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getPrice() {
        return price;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public int getCategory() {
        return category;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public void setCategory(int category) {
        this.category = category;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public static ProductDTO convertToDTO(Product product, CategoryService categoryService) {
        String categoryName = categoryService.getCategoryById(product.getCategory()).getName();
        return new ProductDTO(product.getId(), product.getName(), product.getPrice(), product.getImageUrl(), product.getCategory(), categoryName);
    }
}
