package pl.przybysz.paragonex;

import pl.przybysz.paragonex.dto.Receipt;

public interface ICommunicator {

    void passDataToReceiptList();
    void passDataToReceipt(Receipt receipt);
}
