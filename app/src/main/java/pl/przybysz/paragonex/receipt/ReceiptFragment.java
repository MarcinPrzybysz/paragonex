package pl.przybysz.paragonex.receipt;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.fragment.app.Fragment;

import pl.przybysz.paragonex.ICommunicator;
import pl.przybysz.paragonex.R;
import pl.przybysz.paragonex.dto.Receipt;

public class ReceiptFragment extends Fragment {

    private ICommunicator communicator;

    Button addBtn;
    EditText shop;
    EditText price;
    EditText description;
    Spinner category;

    final String RECEIPT = "paragonex.receipt";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_new, container, false);

        shop = view.findViewById(R.id.tv_shop);
        price = view.findViewById(R.id.tv_price);
        description = view.findViewById(R.id.tv_description);
        addBtn = view.findViewById(R.id.button_add);

        communicator = (ICommunicator) getActivity();
        addBtn.setOnClickListener(view1 -> communicator.passDataToReceiptList(loadToDto()));

        if (getArguments() != null) {
            Receipt receipt = getArguments().getParcelable(RECEIPT);
            loadFromDto(receipt);
        }


        return view;
    }

    private void loadFromDto(Receipt receipt) {
        shop.setText("" + receipt.getShop());
        price.setText("" + receipt.getPrice());
        description.setText("" + receipt.getDescription());
    }

    private Receipt loadToDto() {
        Receipt receipt = new Receipt();
        receipt.setShop(shop.getText().toString());
        receipt.setDescription(description.getText().toString());
        receipt.setPrice(Double.valueOf(price.getText().toString()));

        return receipt;
    }

}