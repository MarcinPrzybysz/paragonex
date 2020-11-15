package pl.przybysz.paragonex.add_new;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import pl.przybysz.paragonex.ICommunicator;
import pl.przybysz.paragonex.R;
import pl.przybysz.paragonex.dto.Receipt;

public class AddNewFragment extends Fragment {

    private ICommunicator communicator;
    Receipt receipt;

    Button sendBtn;
    EditText shop;
    EditText price;
    EditText description;
    Spinner category;

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

        //sending date to other Fragment
//        communicator = (ICommunicator) getActivity();
//        sendBtn = view.findViewById(R.id.button_add);
//        final EditText message = view.findViewById(R.id.tv_shop);
//
//        sendBtn.setOnClickListener(new View.OnClickListener() {
//
//            @Override
//            public void onClick(View view) {
//                communicator.passDataCom(message.getText().toString());
//            }
//        });


        Receipt receipt = new Receipt();
        receipt.setShop("Biedra");
        receipt.setDescription("ddd");
        receipt.setPrice(53.22);

        loadFromDto(receipt);


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