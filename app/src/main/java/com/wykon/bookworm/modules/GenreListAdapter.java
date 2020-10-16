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
public class GenreListAdapter extends BaseAdapter implements Filterable {

    private Context mContext;
    private LinkedList<Genre> mGenres;
    private LinkedList<Genre> mGenresFilterList;
    private ValueFilter valueFilter;

    public GenreListAdapter(Context context, LinkedList<Genre> genres) {
        mContext = context;
        mGenres = genres;
        mGenresFilterList = genres;
    }

    @Override
    public int getCount() {
        return mGenres.size();
    }

    @Override
    public Object getItem(int position) {
        return mGenres.get(position);
    }

    @Override
    public long getItemId(int position) {
        return mGenres.indexOf(getItem(position));
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater mLayoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = mLayoutInflater.inflate(R.layout.genres_row, parent, false);

        TextView tvName = rowView.findViewById(R.id.tvName);

        Genre genre = mGenres.get(position);

        tvName.setText(genre.getName());

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
                LinkedList<Genre> filterList = new LinkedList<>();
                for (int i = 0; i < mGenresFilterList.size(); i++) {
                    Genre list_item = mGenresFilterList.get(i);

                    String upperConstraint = constraint.toString().toUpperCase();
                    if (list_item.getName().toUpperCase().contains(upperConstraint)){
                        filterList.add(new Genre(list_item));
                    }
                }
                results.count = filterList.size();
                results.values = filterList;
            } else {
                results.count = mGenresFilterList.size();
                results.values = mGenresFilterList;
            }
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            mGenres = (LinkedList<Genre>) results.values;
            notifyDataSetChanged();
        }
    }

    @Override
    public void notifyDataSetChanged() {
        mGenres.sort(new Comparator<Genre>() {
            @Override
            public int compare(Genre lhs, Genre rhs) {
                String left = lhs.getName().toLowerCase();
                String right = rhs.getName().toLowerCase();
                return left.compareTo(right);
            }
        });
        super.notifyDataSetChanged();
    }
}
