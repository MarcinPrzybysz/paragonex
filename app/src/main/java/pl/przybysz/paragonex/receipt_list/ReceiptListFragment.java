package pl.przybysz.paragonex.receipt_list;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.fragment.app.Fragment;

import java.util.ArrayList;

import pl.przybysz.paragonex.R;
import pl.przybysz.paragonex.dto.Receipt;

public class ReceiptListFragment extends Fragment {

    ArrayList<Receipt> peopleList = new ArrayList<>();


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

        ListView listView = (ListView) root.findViewById(R.id.lv_receipts);

        ArrayList<Receipt> peopleList = getMock();
        ReceiptListAdapter adapter = new ReceiptListAdapter(getActivity(), R.layout.receipt_list_adapter, peopleList);

        listView.setAdapter(adapter);

        //todo obsługa dodania nowego elementu i przejscie do listy(?)
//        final TextView message = root.findViewById(R.id.displayMessage);
//
//        if(getArguments() != null){
//            displayMessage = getArguments().getString("message");
//        }
//        message.setText(displayMessage);

        return root;
    }


    private ArrayList<Receipt> getMock() {

        Receipt r1 = new Receipt("Spożywcze", "Biedronka", "2020-01-01");
        Receipt r2 = new Receipt("Drogeria", "Rossman", "2020-01-01");
        Receipt r3 = new Receipt("Spożywcze", "Żuczek", "2020-01-01");
        Receipt r4 = new Receipt("Restauracje", "Pierogi u Zosi", "2020-01-01");
        Receipt r5 = new Receipt("Drogeria", "Aptek elo", "2020-01-01");
        Receipt r6 = new Receipt("Spożywcze", "Aldi", "2020-01-01");
        Receipt r7 = new Receipt("Restauracje", "El pierro", "2020-01-01");
        Receipt r8 = new Receipt("Spożywcze", "Lidl", "2020-01-01");
        Receipt r9 = new Receipt("Spożywcze", "Top Market", "2020-01-01");


        peopleList.add(r1);
        peopleList.add(r2);
        peopleList.add(r3);
        peopleList.add(r4);
        peopleList.add(r5);
        peopleList.add(r6);
        peopleList.add(r7);
        peopleList.add(r8);
        peopleList.add(r9);
        peopleList.add(r1);
        peopleList.add(r2);
        peopleList.add(r3);
        peopleList.add(r4);
        peopleList.add(r5);
        peopleList.add(r6);
        return peopleList;
    }


}