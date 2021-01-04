package pl.przybysz.paragonex;

import pl.przybysz.paragonex.dto.ParcelableString;
import pl.przybysz.paragonex.dto.Receipt;

public interface ICommunicator {

    void openReceiptList();
    void openReceipt(Receipt receipt);
    void openPhotoView(ParcelableString photoPath);
}
