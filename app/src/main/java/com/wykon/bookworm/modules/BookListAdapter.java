package com.wykon.bookworm.modules;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.wykon.bookworm.R;

import java.util.LinkedList;

/**
 * Created by Wouter on 11-09-2020.
 */
public class BookListAdapter extends BaseAdapter implements Filterable {

    private Context mContext;
    private LinkedList<Book> mBooks;
    private LinkedList<Book> mbookFilterList;
    private ValueFilter valueFilter;

    public BookListAdapter(Context context, LinkedList<Book> books) {
        mContext = context;
        mBooks = books;
        mbookFilterList = books;
    }


    @Override
    public int getCount() {
        return mBooks.size();
    }

    @Override
    public Object getItem(int position) {
        return mBooks.get(position);
    }

    @Override
    public long getItemId(int position) {
        return mBooks.indexOf(getItem(position));
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater mLayoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = mLayoutInflater.inflate(R.layout.book_row, parent, false);

        TextView tvTitle = rowView.findViewById(R.id.tvTitle);
        TextView tvAuthor = rowView.findViewById(R.id.tvAuthor);
        TextView tvSerie = rowView.findViewById(R.id.tvSerie);
        TextView tvBookNumber = rowView.findViewById(R.id.tvBookNumber);

        Book book = mBooks.get(position);

        tvTitle.setText(book.getTitle());
        tvAuthor.setText(book.getAuthor());

        if (book.getSerie() != null) {
            tvSerie.setText(book.getSerie().getName());

            if (book.getBookNumber() != -1.0) {
                tvBookNumber.setText(String.format("%.1f", book.getBookNumber()));
            }
        }
        else {
            tvSerie.setText("");
            tvBookNumber.setText("");
        }
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
                LinkedList<Book> filterList = new LinkedList<Book>();
                for (int i = 0; i < mbookFilterList.size(); i++) {
                    Book list_item = mbookFilterList.get(i);

                    String upperConstraint = constraint.toString().toUpperCase();
                    if (list_item.getTitle().toUpperCase().contains(upperConstraint)){
                        filterList.add(new Book(list_item));
                    }
                    else if (list_item.getAuthor().toUpperCase().contains(upperConstraint)){
                        filterList.add(new Book(list_item));
                    }
                    else if (list_item.getSerie() != null
                            && list_item.getSerie().getName().toUpperCase().contains(upperConstraint)){
                        filterList.add(new Book(list_item));
                    }
                }
                results.count = filterList.size();
                results.values = filterList;
            } else {
                results.count = mbookFilterList.size();
                results.values = mbookFilterList;
            }
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            mBooks = (LinkedList<Book>) results.values;
            notifyDataSetChanged();
        }
    }
}
