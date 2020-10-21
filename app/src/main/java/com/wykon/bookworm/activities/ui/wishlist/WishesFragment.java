package com.wykon.bookworm.activities.ui.wishlist;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.wykon.bookworm.R;
import com.wykon.bookworm.activities.BookActivity;
import com.wykon.bookworm.activities.EditBookActivity;
import com.wykon.bookworm.activities.EditWishBookActivity;
import com.wykon.bookworm.activities.WishBookActivity;
import com.wykon.bookworm.modules.DatabaseConnection;
import com.wykon.bookworm.modules.SortOption;
import com.wykon.bookworm.modules.SortOrder;
import com.wykon.bookworm.modules.WishBook;
import com.wykon.bookworm.modules.WishListAdapter;

import java.util.LinkedList;

public class WishesFragment extends Fragment  implements SearchView.OnQueryTextListener {

    private LinkedList<WishBook> mWishBooks = new LinkedList<>();

    private View root;
    private Context mContext;
    private DatabaseConnection mDatabaseConnection;
    private WishListAdapter mWishBookListAdapter;

    private ListView mWishBookListView;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        root = inflater.inflate(R.layout.fragment_wish_list, container, false);

        setHasOptionsMenu(true);

        mContext = getActivity().getApplicationContext();
        mDatabaseConnection = new DatabaseConnection(mContext);

        FloatingActionButton fab = root.findViewById(R.id.fabAdd);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mIntent = new Intent(mContext, EditWishBookActivity.class);
                startActivity(mIntent);
            }
        });

        mWishBookListView = root.findViewById(R.id.lvWishes);
        mWishBookListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                WishBook book = (WishBook) mWishBookListAdapter.getItem(position);

                Intent mIntent = new Intent(mContext.getApplicationContext(), WishBookActivity.class);
                mIntent.putExtra("id", book.getId());
                startActivity(mIntent);
            }
        });

        return root;
    }

    private void loadWishBooks(){
        mWishBooks = mDatabaseConnection.getWishBooks();

        mWishBookListAdapter = new WishListAdapter(mContext, mWishBooks);
        mWishBookListView.setAdapter(mWishBookListAdapter);
    }

    @Override
    public void onResume() {
        super.onResume();
        loadWishBooks();
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.menu_main, menu);

        MenuItem searchItem = menu.findItem(R.id.bSearch);
        SearchView searchView = null;
        if (searchItem != null) {
            searchView = (SearchView) searchItem.getActionView();
        }
        if (searchView != null) {
            searchView.setOnQueryTextListener(this);
        }

        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        Intent mIntent = null;
        if (R.id.bOrder == id) {
            createSortDialog();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onQueryTextSubmit(String s) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        mWishBookListAdapter.getFilter().filter(newText);
        return false;
    }

    public void createSortDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Sort books by:");
        // I'm using fragment here so I'm using getView() to provide ViewGroup
        // but you can provide here any other instance of ViewGroup from your Fragment / Activity
        LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
        View myView = layoutInflater.inflate(R.layout.dialog_sort_wish_books, (ViewGroup) root, false);
        builder.setView(myView);

        // Set up the buttons
        builder.setPositiveButton("Sort", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        final AlertDialog dialog = builder.create();
        dialog.show();

        RadioButton rbReleaseDateNext = dialog.findViewById(R.id.rbReleaseDateNext);
        RadioButton rbReleaseDatePrevious = dialog.findViewById(R.id.rbReleaseDatePrevious);
        RadioButton rbTitleAZ = dialog.findViewById(R.id.rbTitleAZ);
        RadioButton rbTitleZA = dialog.findViewById(R.id.rbTitleZA);
        RadioButton rbAuthorAZ = dialog.findViewById(R.id.rbAuthorAZ);
        RadioButton rbAuthorZA = dialog.findViewById(R.id.rbAuthorZA);
        RadioButton rbSerieAZ = dialog.findViewById(R.id.rbSerieAZ);
        RadioButton rbSerieZA = dialog.findViewById(R.id.rbSerieZA);

        switch (mWishBookListAdapter.getSortOption()) {
            case AUTHOR:
                if(SortOrder.INCREASE == mWishBookListAdapter.getSortOrder()) {
                    rbAuthorAZ.setChecked(true);
                }
                else {
                    rbAuthorZA.setChecked(true);
                }
                break;
            case SERIE:
                if(SortOrder.INCREASE == mWishBookListAdapter.getSortOrder()) {
                    rbSerieAZ.setChecked(true);
                }
                else {
                    rbSerieZA.setChecked(true);
                }
                break;
            case DATE:
                if(SortOrder.INCREASE == mWishBookListAdapter.getSortOrder()) {
                    rbReleaseDateNext.setChecked(true);
                }
                else {
                    rbReleaseDatePrevious.setChecked(true);
                }
                break;
            default:
                if(SortOrder.INCREASE == mWishBookListAdapter.getSortOrder()) {
                    rbTitleAZ.setChecked(true);
                }
                else {
                    rbTitleZA.setChecked(true);
                }
        }

        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                RadioGroup rbgSortGroup = dialog.findViewById(R.id.rbgOrder);
                switch (rbgSortGroup.getCheckedRadioButtonId()) {
                    case R.id.rbTitleZA:
                        mWishBookListAdapter.setSort(SortOption.TITLE, SortOrder.DECREASE);
                        break;
                    case R.id.rbAuthorAZ:
                        mWishBookListAdapter.setSort(SortOption.AUTHOR, SortOrder.INCREASE);
                        break;
                    case R.id.rbAuthorZA:
                        mWishBookListAdapter.setSort(SortOption.AUTHOR, SortOrder.DECREASE);
                        break;
                    case R.id.rbSerieAZ:
                        mWishBookListAdapter.setSort(SortOption.SERIE, SortOrder.INCREASE);
                        break;
                    case R.id.rbSerieZA:
                        mWishBookListAdapter.setSort(SortOption.SERIE, SortOrder.DECREASE);
                        break;
                    case R.id.rbReleaseDateNext:
                        mWishBookListAdapter.setSort(SortOption.DATE, SortOrder.INCREASE);
                        break;
                    case R.id.rbReleaseDatePrevious:
                        mWishBookListAdapter.setSort(SortOption.DATE, SortOrder.DECREASE);
                        break;
                    default:
                        mWishBookListAdapter.setSort(SortOption.TITLE, SortOrder.INCREASE);
                }

                dialog.dismiss();
            }
        });

    }
}