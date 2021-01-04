package pl.przybysz.paragonex.receipt_list;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

import pl.przybysz.paragonex.R;
import pl.przybysz.paragonex.dto.Receipt;
import pl.przybysz.paragonex.dto.ReceiptCategory;


public class ReceiptListAdapter extends ArrayAdapter<Receipt> {

    private List<Receipt> objects;
    private Context context;
    private Filter filter;
    private int resourceId;

    public ReceiptListAdapter(Context context, int resourceId, List<Receipt> objects) {
        super(context, resourceId, objects);
        this.context = context;
        this.objects = objects;
        this.resourceId = resourceId;
    }

    @Override
    public int getCount() {
        return objects.size();
    }

    @Override
    public Receipt getItem(int position) {
        return objects.get(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        String category = ReceiptCategory.EMPTY.toString();
        String shop = "";
        LocalDate date = null;
        if (getItem(position) != null) {
            category = getItem(position).getCategory();
            shop = getItem(position).getShop();
            date = getItem(position).getDate() != null ? Instant.ofEpochMilli(getItem(position).getDate()).atZone(ZoneId.systemDefault()).toLocalDate() : null;
        }


        LayoutInflater inflater = LayoutInflater.from(context);
        convertView = inflater.inflate(resourceId, parent, false);

        TextView tvShop = convertView.findViewById(R.id.textViewShop);
        TextView tvDate = convertView.findViewById(R.id.textViewDate);
        LinearLayout categoryView = convertView.findViewById(R.id.category_layout);


        categoryView.setBackground(ContextCompat.getDrawable(context, ReceiptCategory.getEnumForLabel(category).getIcon()));


        tvShop.setText(shop);

        tvDate.setText(date != null ? date.toString() : null);

        Paint paint;
        paint = new Paint();
        paint.setColor(Color.GRAY);


        return convertView;
    }

    @Override
    public Filter getFilter() {
        if (filter == null)
            filter = new AppFilter<Receipt>(objects);
        return filter;
    }

    public void refreshFilterSourceObjects() {
        filter = new AppFilter<Receipt>(objects);
    }


    private class AppFilter<T> extends Filter {

        private ArrayList<T> sourceObjects;

        public AppFilter(List<T> objects) {
            sourceObjects = new ArrayList<T>();
            synchronized (this) {
                sourceObjects.addAll(objects);
            }
        }

        @Override
        protected FilterResults performFiltering(CharSequence category) {
            String filterSeq = category.toString().toLowerCase();
            FilterResults result = new FilterResults();
            if (!filterSeq.equals(ReceiptCategory.EMPTY.toString()) && filterSeq != null && filterSeq.length() > 0) {
                ArrayList<Receipt> filter = new ArrayList<Receipt>();

                for (T object : sourceObjects) {
                    Receipt r = (Receipt) object;
                    if (r.getCategory().equals(category))
                        filter.add((Receipt) object);
                }
                result.count = filter.size();
                result.values = filter;
            } else {
                synchronized (this) {
                    result.values = sourceObjects;
                    result.count = sourceObjects.size();
                }
            }
            return result;
        }

        @SuppressWarnings("unchecked")
        @Override
        protected void publishResults(CharSequence constraint,
                                      FilterResults results) {
            ArrayList<T> filtered = (ArrayList<T>) results.values;
            notifyDataSetChanged();
            clear();
            for (int i = 0, l = filtered.size(); i < l; i++)
                add((Receipt) filtered.get(i));
            notifyDataSetInvalidated();
        }
    }

}