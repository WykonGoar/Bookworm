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

import androidx.annotation.Nullable;

import com.wykon.bookworm.R;

import java.util.Comparator;
import java.util.Date;
import java.util.LinkedList;

/**
 * Created by Wouter on 11-09-2020.
 */
public class WishListAdapter extends BaseAdapter implements Filterable {

    private Context mContext;
    private LinkedList<WishBook> mWishBooks;
    private LinkedList<WishBook> mWishBookFilterList;
    private ValueFilter valueFilter;

    private SortOption mSortOption = SortOption.DATE;
    private SortOrder mSortOrder = SortOrder.INCREASE;

    public WishListAdapter(Context context, LinkedList<WishBook> wishBooks) {
        mContext = context;
        mWishBooks = wishBooks;
        mWishBookFilterList = wishBooks;
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
        mWishBooks.remove(position);
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mWishBooks.size();
    }

    @Override
    public Object getItem(int position) {
        return mWishBooks.get(position);
    }

    @Override
    public long getItemId(int position) {
        return mWishBooks.indexOf(getItem(position));
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater mLayoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = mLayoutInflater.inflate(R.layout.wish_book_row, parent, false);

        TextView tvTitle = rowView.findViewById(R.id.tvTitle);
        TextView tvAuthor = rowView.findViewById(R.id.tvAuthor);
        CheckBox cbCompleted = rowView.findViewById(R.id.cbCompleted);
        TextView tvSerie = rowView.findViewById(R.id.tvSerie);
        TextView tvBookNumber = rowView.findViewById(R.id.tvBookNumber);
        TextView tvReleaseDate = rowView.findViewById(R.id.tvReleaseDate);

        WishBook wishBook = mWishBooks.get(position);

        tvTitle.setText(wishBook.getTitle());
        tvAuthor.setText(wishBook.getAuthor());
        tvReleaseDate.setText(wishBook.getRenderedReleaseDate());

        if (wishBook.getSerie() != null) {
            cbCompleted.setChecked(wishBook.getSerie().isCompleted());
            tvSerie.setText(wishBook.getSerie().getName());

            tvBookNumber.setText("");
            if (wishBook.getBookNumber() != -1.0) {
                tvBookNumber.setText(String.format("%.1f", wishBook.getBookNumber()));
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
                LinkedList<WishBook> filterList = new LinkedList<WishBook>();
                for (int i = 0; i < mWishBookFilterList.size(); i++) {
                    WishBook list_item = mWishBookFilterList.get(i);

                    String upperConstraint = constraint.toString().toUpperCase();
                    if (list_item.getTitle().toUpperCase().contains(upperConstraint)){
                        filterList.add(new WishBook(list_item));
                    }
                    else if (list_item.getAuthor().toUpperCase().contains(upperConstraint)){
                        filterList.add(new WishBook(list_item));
                    }
                    else if (list_item.getSerie() != null
                            && list_item.getSerie().getName().toUpperCase().contains(upperConstraint)){
                        filterList.add(new WishBook(list_item));
                    }
                }
                results.count = filterList.size();
                results.values = filterList;
            } else {
                results.count = mWishBookFilterList.size();
                results.values = mWishBookFilterList;
            }
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            mWishBooks = (LinkedList<WishBook>) results.values;
            notifyDataSetChanged();
        }
    }

    @Override
    public void notifyDataSetChanged() {
        mWishBooks.sort(new Comparator<WishBook>() {
            @Override
            public int compare(@Nullable WishBook lhs, @Nullable WishBook rhs) {
                WishBook left = lhs;
                WishBook right = rhs;

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
                    case DATE:
                        Date dateLeft = left.getReleaseDate();
                        Date dateRight = right.getReleaseDate();
                        if (dateLeft == null && dateRight == null) {
                            result = 0;
                        }
                        else if (dateLeft == null) {
                            result = 1;
                        }
                        else if (dateRight == null) {
                            result = -1;
                        }
                        else {
                            result = dateLeft.compareTo(dateRight);
                        }
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
