package pl.przybysz.paragonex.dto;

import pl.przybysz.paragonex.R;

public enum ReceiptCategory {
    EMPTY("-", R.drawable.file),
    GROCERY("Spożywcze", R.drawable.groceries),
    SPORT("Sportowe", R.drawable.weights),
    RESTAURANT("Restauracje", R.drawable.tray),
    ELECTRONICS("Elektronika", R.drawable.iphone),
    DRUGSTORE("Drogeria", R.drawable.shampoo),
    PHARMACY("Leczenie", R.drawable.first_aid_kit),
    CLOTHING("Ubrania", R.drawable.polo_shirt),
    BOOKSTORE("Księgarnia", R.drawable.open_book);

    private String categroyLabel;
    private int icon;

    ReceiptCategory(String categroyLabel, int icon) {
        this.categroyLabel = categroyLabel;
        this.icon = icon;
    }

    public int getIcon() {
        return icon;
    }

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
