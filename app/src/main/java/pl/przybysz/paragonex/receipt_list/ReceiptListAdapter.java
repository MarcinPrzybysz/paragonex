package pl.przybysz.paragonex.receipt_list;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

import pl.przybysz.paragonex.R;
import pl.przybysz.paragonex.dto.Receipt;

public class ReceiptListAdapter extends ArrayAdapter<Receipt> {


    private static final String TAG = "PersonListAdapter";

    private Context mContext;
    int mResource;

    public ReceiptListAdapter(@NonNull Context context, int resource, @NonNull List<Receipt> objects) {
        super(context, resource, objects);
        mContext = context;
        mResource = resource;
    }


    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        String category = getItem(position).getCategory();
        String shop = getItem(position).getShop();
//        String date = getItem(position).getDate();


        LayoutInflater inflater = LayoutInflater.from(mContext);
        convertView = inflater.inflate(mResource, parent, false);

        TextView tvCategory = (TextView) convertView.findViewById(R.id.textViewCategory);
        TextView tvShop = (TextView) convertView.findViewById(R.id.textViewShop);
        TextView tvDate = (TextView) convertView.findViewById(R.id.textViewDate);

        tvCategory.setText(category);
        tvShop.setText(shop);
//        tvDate.setText(date);

        return convertView;
    }
}
