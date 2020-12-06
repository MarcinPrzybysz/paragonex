package pl.przybysz.paragonex.receipt_list;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.List;

import pl.przybysz.paragonex.ICommunicator;
import pl.przybysz.paragonex.R;
import pl.przybysz.paragonex.dto.Receipt;
import pl.przybysz.paragonex.firebase.ReceiptService;

public class ReceiptListFragment extends Fragment {

    private ICommunicator communicator;
    final String RECEIPT_LIST = "paragonex.receipt_list";

    public ReceiptListFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_receipt_list, container, false);

        ListView listView = root.findViewById(R.id.lv_receipts);

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

        return root;
    }


}