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
import android.widget.ImageButton;
import android.widget.Spinner;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.Arrays;
import java.util.List;

import pl.przybysz.paragonex.ICommunicator;
import pl.przybysz.paragonex.R;
import pl.przybysz.paragonex.dto.Receipt;
import pl.przybysz.paragonex.dto.ReceiptCategory;
import pl.przybysz.paragonex.firebase.ReceiptService;

public class ReceiptFragment extends Fragment {

    private ICommunicator communicator;

    FloatingActionButton addBtn;
    ImageButton deleteBtn;
    EditText shop;
    EditText price;
    EditText description;
    Spinner category;
    DatePicker datePicker;
    ReceiptService service;

    Receipt originalModel;


    final String RECEIPT = "paragonex.receipt";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        service = new ReceiptService();
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
        addBtn = view.findViewById(R.id.floating_button_add);
        deleteBtn = view.findViewById(R.id.delete_button);
        datePicker = view.findViewById(R.id.datePicker);
        category = view.findViewById(R.id.spinner_category);

        category.setAdapter(new ArrayAdapter(getActivity(), android.R.layout.simple_list_item_1, ReceiptCategory.values()));

        addBtn.setOnClickListener(view1 -> communicator.passDataToReceiptList());

        addBtn.setOnClickListener(view1 -> {
            service.upsertReceipt(getDtoFromEditors());
            communicator.passDataToReceiptList(); //todo zmienić
        });

        deleteBtn.setOnClickListener(view1 -> {
            service.deleteReceipt(originalModel.getId());
            communicator.passDataToReceiptList(); //todo zmienić
        });


        if (getArguments() != null) {
            originalModel = getArguments().getParcelable(RECEIPT);
            loadFromDto(originalModel);
            deleteBtn.setVisibility(View.VISIBLE);
        }else{
            originalModel = new Receipt();
        }

        return view;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void loadFromDto(Receipt receipt) {
        shop.setText(receipt.getShop() != null ? receipt.getShop() : "");
        price.setText(receipt.getPrice() != null ? receipt.getPrice().toString() : "0");
        description.setText(receipt.getDescription() != null ? receipt.getDescription() : "");
        if (receipt.getDate() != null) {
            LocalDate date =  Instant.ofEpochMilli(receipt.getDate()).atZone(ZoneId.systemDefault()).toLocalDate();
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
    private Receipt getDtoFromEditors() {
        Receipt receipt = new Receipt();
        receipt.setId(originalModel.getId());
        receipt.setShop(shop.getText().toString());
        receipt.setDescription(description.getText().toString());
        receipt.setPrice(Double.valueOf(price.getText().toString()));

        //todo: to jest tragiczne, poprawić!

        LocalDate localDate =LocalDate.of(datePicker.getYear(), datePicker.getMonth(), datePicker.getDayOfMonth());
        Long dateInMillis = localDate.atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli();
        receipt.setDate(dateInMillis);

        receipt.setCategory(category.getSelectedItem().toString());

        return receipt;
    }

}