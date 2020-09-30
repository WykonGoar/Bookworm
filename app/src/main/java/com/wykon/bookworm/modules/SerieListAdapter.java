package com.wykon.bookworm.modules;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.wykon.bookworm.R;

import java.util.Comparator;
import java.util.LinkedList;

/**
 * Created by Wouter on 11-09-2020.
 */
public class SerieListAdapter extends BaseAdapter implements Filterable {

    private Context mContext;
    private LinkedList<Serie> mSeries;
    private LinkedList<Serie> mSerieFilterList;
    private ValueFilter valueFilter;

    public SerieListAdapter(Context context, LinkedList<Serie> series) {
        mContext = context;
        mSeries = series;
        mSerieFilterList = series;
    }

    @Override
    public int getCount() {
        return mSeries.size();
    }

    @Override
    public Object getItem(int position) {
        return mSeries.get(position);
    }

    @Override
    public long getItemId(int position) {
        return mSeries.indexOf(getItem(position));
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater mLayoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = mLayoutInflater.inflate(R.layout.series_row, parent, false);

        TextView tvName = rowView.findViewById(R.id.tvName);
        CheckBox cbCompleted = rowView.findViewById(R.id.cbCompleted);

        Serie serie = mSeries.get(position);

        tvName.setText(serie.getName());
        cbCompleted.setChecked(serie.isCompleted());

        return  rowView;
    }

    @Override
    public Filter getFilter() {
        if (valueFilter == null) {
            valueFilter = new ValueFilter();
        }
        return valueFilter;
    }

    private class ValueFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults results = new FilterResults();

            if (constraint != null && constraint.length() > 0) {
                LinkedList<Serie> filterList = new LinkedList<>();
                for (int i = 0; i < mSerieFilterList.size(); i++) {
                    Serie list_item = mSerieFilterList.get(i);

                    String upperConstraint = constraint.toString().toUpperCase();
                    if (list_item.getName().toUpperCase().contains(upperConstraint)){
                        filterList.add(new Serie(list_item));
                    }
                }
                results.count = filterList.size();
                results.values = filterList;
            } else {
                results.count = mSerieFilterList.size();
                results.values = mSerieFilterList;
            }
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            mSeries = (LinkedList<Serie>) results.values;
            notifyDataSetChanged();
        }
    }

    @Override
    public void notifyDataSetChanged() {
        mSeries.sort(new Comparator<Serie>() {
            @Override
            public int compare(Serie lhs, Serie rhs) {
                String left = lhs.getName().toLowerCase();
                String right = rhs.getName().toLowerCase();
                return left.compareTo(right);
            }
        });
        super.notifyDataSetChanged();
    }
}
