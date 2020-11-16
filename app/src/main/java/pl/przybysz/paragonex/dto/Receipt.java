package pl.przybysz.paragonex.dto;


import android.os.Parcel;
import android.os.Parcelable;

import java.time.LocalDate;

public class Receipt implements Parcelable {

    private String category;
    private String shop;
    private String description;
    private Double price;
    private LocalDate date;

    public Receipt() {
    }

    public Receipt(String category, String shop, String description, LocalDate date) {
        this.category = category;
        this.shop = shop;
        this.description = description;
        this.date = date;
    }

    protected Receipt(Parcel in) {
        category = in.readString();
        shop = in.readString();
        description = in.readString();
        if (in.readByte() == 0) {
            price = null;
        } else {
            price = in.readDouble();
        }
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

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(category);
        parcel.writeString(shop);
        parcel.writeString(description);
        if (price == null) {
            parcel.writeByte((byte) 0);
        } else {
            parcel.writeByte((byte) 1);
            parcel.writeDouble(price);
        }
    }


    public static final Creator<Receipt> CREATOR = new Creator<Receipt>() {
        @Override
        public Receipt createFromParcel(Parcel in) {
            return new Receipt(in);
        }

        @Override
        public Receipt[] newArray(int size) {
            return new Receipt[size];
        }
    };
}
