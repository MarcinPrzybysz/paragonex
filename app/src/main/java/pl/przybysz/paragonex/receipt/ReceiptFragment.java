package pl.przybysz.paragonex.receipt;

import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import pl.przybysz.paragonex.ICommunicator;
import pl.przybysz.paragonex.R;
import pl.przybysz.paragonex.dto.Receipt;
import pl.przybysz.paragonex.dto.ReceiptCategory;

public class ReceiptFragment extends Fragment {

    private ICommunicator communicator;

    Button addBtn;
    EditText shop;
    EditText price;
    EditText description;
    Spinner category;
    DatePicker datePicker;

    final String RECEIPT = "paragonex.receipt";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        //todo: czy to nie powinno być w on create?
        View view = inflater.inflate(R.layout.fragment_receipt, container, false);
        communicator = (ICommunicator) getActivity();

        shop = view.findViewById(R.id.tv_shop);
        price = view.findViewById(R.id.tv_price);
        description = view.findViewById(R.id.tv_description);
        addBtn = view.findViewById(R.id.button_add);
        datePicker = view.findViewById(R.id.datePicker);
        category = view.findViewById(R.id.spinner_category);

        category.setAdapter(new ArrayAdapter(getActivity(), android.R.layout.simple_list_item_1, ReceiptCategory.values()));

        addBtn.setOnClickListener(view1 -> communicator.passDataToReceiptList(loadToDto()));
        if (getArguments() != null) {
            Receipt receipt = getArguments().getParcelable(RECEIPT);
            loadFromDto(receipt);
        }

        return view;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void loadFromDto(Receipt receipt) {
        shop.setText(receipt.getShop() != null ? receipt.getShop() : "");
        price.setText(receipt.getPrice() != null ? receipt.getPrice().toString() : "0");
        description.setText(receipt.getDescription() != null ? receipt.getDescription() : "");
        if (receipt.getDate() != null) {
            LocalDate date = receipt.getDate();
            datePicker.updateDate(date.getYear(), date.getMonth().getValue(), date.getDayOfMonth());
        }


        List categories = Arrays.asList(ReceiptCategory.values());
        try{
            int index = categories.indexOf(ReceiptCategory.getEnumForLabel(receipt.getCategory()));
            category.setSelection(index);
        }catch (UnsupportedOperationException ex){
            category.setSelection(categories.indexOf(ReceiptCategory.EMPTY));
        }



    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private Receipt loadToDto() {
        Receipt receipt = new Receipt();
        receipt.setShop(shop.getText().toString());
        receipt.setDescription(description.getText().toString());
        receipt.setPrice(Double.valueOf(price.getText().toString()));
        receipt.setDate(LocalDate.of(datePicker.getYear(), datePicker.getMonth(), datePicker.getDayOfMonth()));

        receipt.setCategory(category.getSelectedItem().toString());

        return receipt;
    }

}