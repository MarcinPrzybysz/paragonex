package pl.przybysz.paragonex.receipt_list;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

import pl.przybysz.paragonex.R;
import pl.przybysz.paragonex.dto.Receipt;
import pl.przybysz.paragonex.dto.ReceiptCategory;

public class ReceiptListAdapter extends ArrayAdapter<Receipt> {

    private static final String TAG = "PersonListAdapter";

    private Context mContext;
    int mResource;


    public ReceiptListAdapter(@NonNull Context context, int resource, @NonNull List<Receipt> objects) {
        super(context, resource, objects);
        mContext = context;
        mResource = resource;
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        String category = ReceiptCategory.EMPTY.toString();
        String shop = "";
        LocalDate date = null;
        if (getItem(position) != null) {
            category = getItem(position).getCategory();
            shop = getItem(position).getShop();
            date = getItem(position).getDate() != null ? Instant.ofEpochMilli(getItem(position).getDate()).atZone(ZoneId.systemDefault()).toLocalDate() : null;
        }


        LayoutInflater inflater = LayoutInflater.from(mContext);
        convertView = inflater.inflate(mResource, parent, false);

        TextView tvShop = (TextView) convertView.findViewById(R.id.textViewShop);
        TextView tvDate = (TextView) convertView.findViewById(R.id.textViewDate);
        LinearLayout categoryView = convertView.findViewById(R.id.category_layout);


        categoryView.setBackground(ContextCompat.getDrawable(mContext, ReceiptCategory.getEnumForLabel(category).getIcon()));


        tvShop.setText(shop);

        tvDate.setText(date != null ? date.toString() : null);

        Paint paint;
        paint = new Paint();
        paint.setColor(Color.GRAY);


        return convertView;
    }


}
