package gsshop.mobile.v2.user;

import android.content.Context;
import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.TextView;

import java.util.ArrayList;

import gsshop.mobile.v2.R;
import roboguice.util.Ln;

public class DomainFilterAdapter extends ArrayAdapter<String> {
    private ArrayList<String> items;
    private ArrayList<String> itemsAll;
    private ArrayList<String> suggestions;
    private int viewResourceId;

    protected final ForegroundColorSpan emailColorSpan = new ForegroundColorSpan(Color.parseColor("#00a4b3"));

    public DomainFilterAdapter(Context context, int viewResourceId, ArrayList<String> items) {
        super(context, viewResourceId, items);
        this.items = items;
        this.itemsAll = (ArrayList<String>) items.clone();
        this.suggestions = new ArrayList<String>();
        this.viewResourceId = viewResourceId;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        if (v == null) {
            LayoutInflater vi = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = vi.inflate(viewResourceId, null);
        }
        String customer = items.get(position);
        if (customer != null) {
            TextView customerNameLabel = (TextView)v.findViewById(R.id.email_domain);
            if (customerNameLabel != null) {
                final SpannableStringBuilder priceStringBuilder = new SpannableStringBuilder();
                priceStringBuilder.append(customer);
                try {
                    int colorIndex = customer.indexOf("@");
                    priceStringBuilder.setSpan(emailColorSpan, colorIndex + 1, customer.length(),
                            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                }catch(Exception e){
                    Ln.e(e);
                }
                customerNameLabel.setText(priceStringBuilder);
            }
        }
        return v;
    }

    @Override
    public Filter getFilter() {
        return nameFilter;
    }

    Filter nameFilter = new Filter() {
        public String convertResultToString(Object resultValue) {
            String str = (String)resultValue;
            return str;
        }
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            if (constraint != null){
                String palabra = constraint.toString();
                if(palabra != null && palabra.indexOf("@") != -1) {
                    String palabra2 = palabra.substring(palabra.indexOf("@"));
                    String antesArroba;
                    try{
                        antesArroba = palabra.substring(0, palabra.indexOf("@"));
                    }catch (Exception ex)
                    {
                        antesArroba ="";
                    }
                    suggestions.clear();
                    for (String customer : itemsAll) {
                        if(customer.toLowerCase().startsWith(palabra2.toString().toLowerCase())){
                            suggestions.add(antesArroba+customer);
                        }
                    }
                    FilterResults filterResults = new FilterResults();
                    filterResults.values = suggestions;
                    filterResults.count = suggestions.size();
                    return filterResults;
                } else {
                    return new FilterResults();
                }
            }else {
                return new FilterResults();
            }
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            ArrayList<String> filteredList = (ArrayList<String>) results.values;
            if(results != null && results.count > 0) {
                clear();
                for (String c : filteredList) {
                    add(c);
                }
                notifyDataSetChanged();
            }
        }
    };

}