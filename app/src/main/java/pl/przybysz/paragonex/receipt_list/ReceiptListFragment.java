package pl.przybysz.paragonex.receipt_list;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;

import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.List;

import pl.przybysz.paragonex.ICommunicator;
import pl.przybysz.paragonex.R;
import pl.przybysz.paragonex.dto.Receipt;
import pl.przybysz.paragonex.dto.ReceiptCategory;
import pl.przybysz.paragonex.firebase.ReceiptService;

public class ReceiptListFragment extends Fragment {

    private ICommunicator communicator;
    Spinner category;
    Spinner month;
    Spinner year;

    public ReceiptListFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_receipt_list, container, false);

        ListView listView = view.findViewById(R.id.lv_receipts);
        category = view.findViewById(R.id.spinner_category);

        category.setAdapter(new ArrayAdapter(getActivity(), android.R.layout.simple_list_item_1, ReceiptCategory.values()));


        List<Receipt> receipts = new ArrayList<>();
        ReceiptService service = new ReceiptService();
        ReceiptListAdapter adapter = new ReceiptListAdapter(getActivity(), R.layout.receipt_list_adapter, receipts);
        service.readAllReceipt(receipts, adapter);

        listView.setAdapter(adapter);

        communicator = (ICommunicator) getActivity();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Receipt receipt = receipts.get(i);
                communicator.passDataToReceipt(receipt);
            }
        });


        AdapterView.OnItemSelectedListener filterChangedListener = new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String selectedCategory = category.getSelectedItem().toString();
                adapter.getFilter().filter(selectedCategory);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        };

        category.setOnItemSelectedListener(filterChangedListener);
        year.setOnItemSelectedListener(filterChangedListener);
        month.setOnItemSelectedListener(filterChangedListener);

        return view;
    }


}