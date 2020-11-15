package pl.przybysz.paragonex.dto;


import java.time.LocalDateTime;

public class Receipt {

    private String category;
    private String shop;
    private String description;
    private Double price;
    private LocalDateTime date;

    public Receipt() {
    }

    public Receipt(String category, String shop, String description) {
        this.category = category;
        this.shop = shop;
        this.description = description;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getShop() {
        return shop;
    }

    public void setShop(String shop) {
        this.shop = shop;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }
}
