package pl.przybysz.paragonex.dto;

import androidx.annotation.NonNull;

public enum ReceiptCategory {
    EMPTY("-"),
    GROCERY("Spożywcze"),
    SPORT("Sportowe"),
    RESTAURANT("Restauracje"),
    ELECTRONICS("Elektronika"),
    DRUGSTORE("Drogeria");

    private String categroyLabel;

    ReceiptCategory(String categroyLabel) {
        this.categroyLabel = categroyLabel;
    }


    @NonNull
    @Override
    public String toString() {
        return categroyLabel;
    }

    public static ReceiptCategory getEnumForLabel(String label) {
        for (ReceiptCategory category : values()) {
            if (category.toString().toLowerCase().equals(label.toLowerCase())) {
                return category;
            }
        }
        throw new java.lang.UnsupportedOperationException("Unsupported category enum: " + label);
    }

}
