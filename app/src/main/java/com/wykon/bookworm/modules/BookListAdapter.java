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
public class BookListAdapter extends BaseAdapter implements Filterable {

    private Context mContext;
    private LinkedList<Book> mBooks;
    private LinkedList<Book> mbookFilterList;
    private ValueFilter valueFilter;

    private SortOption mSortOption = SortOption.TITLE;
    private SortOrder mSortOrder = SortOrder.INCREASE;

    public BookListAdapter(Context context, LinkedList<Book> books) {
        mContext = context;
        mBooks = books;
        mbookFilterList = books;
    }

    public void setSort(SortOption option, SortOrder order) {
        mSortOption = option;
        mSortOrder = order;

        notifyDataSetChanged();
    }

    public SortOption getSortOption() {
        return mSortOption;
    }

    public SortOrder getSortOrder() {
        return mSortOrder;
    }

    public void removeItem(int position) {
        mBooks.remove(position);
        notifyDataSetChanged();
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
        TextView tvRating = rowView.findViewById(R.id.tvRating);
        TextView tvAuthor = rowView.findViewById(R.id.tvAuthor);
        CheckBox cbCompleted = rowView.findViewById(R.id.cbCompleted);
        TextView tvSerie = rowView.findViewById(R.id.tvSerie);
        TextView tvBookNumber = rowView.findViewById(R.id.tvBookNumber);

        Book book = mBooks.get(position);

        tvTitle.setText(book.getTitle());
        tvRating.setText(String.format("%.1f", book.getRating()));
        tvAuthor.setText(book.getAuthor());

        if (book.getSerie() != null) {
            cbCompleted.setChecked(book.getSerie().isCompleted());
            tvSerie.setText(book.getSerie().getName());

            tvBookNumber.setText("");
            if (book.getBookNumber() != -1.0) {
                tvBookNumber.setText(String.format("%.1f", book.getBookNumber()));
            }
        }
        else {
            cbCompleted.setVisibility(View.INVISIBLE);
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

    @Override
    public void notifyDataSetChanged() {
        mBooks.sort(new Comparator<Book>() {
            @Override
            public int compare(Book lhs, Book rhs) {
                Book left = lhs;
                Book right = rhs;

                if (SortOrder.DECREASE == mSortOrder) {
                    left = rhs;
                    right = lhs;
                }

                Integer result;
                switch (mSortOption) {
                    case AUTHOR:
                        String authorLeft = left.getAuthorLastName().toLowerCase();
                        String authorRight = right.getAuthorLastName().toLowerCase();
                        result = authorLeft.compareTo(authorRight);
                        break;
                    case SERIE:
                        Serie serieLeft = left.getSerie();
                        Serie serieRight = right.getSerie();

                        if (serieLeft == null && serieRight == null) {
                            result = 0;
                        }
                        else if (serieLeft == null) {
                            result = -1;
                        }
                        else if (serieRight == null) {
                            result = 1;
                        }
                        else {
                            String serieNameLeft = serieLeft.getName().toLowerCase();
                            String serieNameRight = serieLeft.getName().toLowerCase();
                            result = serieNameLeft.compareTo(serieNameRight);
                        }

                        if(0 == result) {
                            double bookNumberLeft = left.getBookNumber();
                            double bookNumberRight = right.getBookNumber();

                            if (bookNumberLeft == -1 && bookNumberRight == -1) {
                                result = 0;
                            }
                            else if (bookNumberLeft == -1) {
                                result = -1;
                            }
                            else if (bookNumberRight == -1) {
                                result = 1;
                            }
                            else {
                                result = Double.compare(bookNumberLeft, bookNumberRight);
                            }

                        }

                        break;
                    case RATING:
                        float ratingLeft = left.getRating();
                        float ratingright = right.getRating();

                        if (ratingLeft == ratingright) {
                            result = 0;
                        }
                        else if (ratingLeft > ratingright) {
                            result = 1;
                        }
                        else {
                            result = -1;
                        }
                        break;

                    case TITLE:
                    default:
                        String titleLeft = left.getTitle().toLowerCase();
                        String titleRight = right.getTitle().toLowerCase();
                        result = titleLeft.compareTo(titleRight);
                }

                return result;
            }
        });

        super.notifyDataSetChanged();
    }
}
