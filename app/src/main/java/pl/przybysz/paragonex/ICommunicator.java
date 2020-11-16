package pl.przybysz.paragonex;

import pl.przybysz.paragonex.dto.Receipt;

public interface ICommunicator {

    void passDataToReceiptList(Receipt receipt);
    void passDataToReceipt(Receipt receipt);
}
