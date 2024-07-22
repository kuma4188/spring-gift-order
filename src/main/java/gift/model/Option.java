package gift.model;

import jakarta.persistence.*;

@Entity
@Table(name = "options")
public class Option {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 50)
    private String name;

    @Column(nullable = false )
    private int quantity;

    @Column(nullable = false)
    private int price;

    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;


    public Option() {
    }

    public Option(String name, int quantity, int price, Product product) {
        this.name = name;
        this.quantity = quantity;
        this.price = price;
        this.product = product;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getQuantity() {
        return quantity;
    }

    public int getPrice() {
        return price;
    }

    public Product getProduct() {
        return product;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public void setProduct(Product product) {
        this.product = product;
    }
}
