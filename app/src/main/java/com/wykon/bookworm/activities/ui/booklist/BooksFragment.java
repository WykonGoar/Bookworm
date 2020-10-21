package com.wykon.bookworm.activities.ui.booklist;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.ContextMenu;
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
import com.wykon.bookworm.modules.Book;
import com.wykon.bookworm.modules.BookListAdapter;
import com.wykon.bookworm.modules.DatabaseConnection;
import com.wykon.bookworm.modules.SortOption;
import com.wykon.bookworm.modules.SortOrder;

import java.util.LinkedList;

public class BooksFragment extends Fragment implements SearchView.OnQueryTextListener {

    private LinkedList<Book> mBooks = new LinkedList<>();

    private View root;
    private Context mContext;
    private DatabaseConnection mDatabaseConnection;
    private BookListAdapter mBookListAdapter;

    private ListView mBookListView;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_book_list, container, false);

        setHasOptionsMenu(true);

        mContext = getActivity().getApplicationContext();
        mDatabaseConnection = new DatabaseConnection(mContext);

        FloatingActionButton fab = root.findViewById(R.id.fabAdd);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mIntent = new Intent(mContext, EditBookActivity.class);
                startActivity(mIntent);
            }
        });

        mBookListView = root.findViewById(R.id.lvBooks);
        mBookListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Book book = (Book) mBookListAdapter.getItem(position);

                Intent mIntent = new Intent(mContext.getApplicationContext(), BookActivity.class);
                mIntent.putExtra("id", book.getId());
                startActivity(mIntent);
            }
        });

        return root;
    }

    private void loadBooks(){
        mBooks = mDatabaseConnection.getBooks();

        mBookListAdapter = new BookListAdapter(mContext, mBooks);
        mBookListView.setAdapter(mBookListAdapter);
    }

    @Override
    public void onResume() {
        super.onResume();
        loadBooks();
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
        mBookListAdapter.getFilter().filter(newText);
        return false;
    }

    public void createSortDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Sort books by:");
        // I'm using fragment here so I'm using getView() to provide ViewGroup
        // but you can provide here any other instance of ViewGroup from your Fragment / Activity
        LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
        View myView = layoutInflater.inflate(R.layout.dialog_sort_books, (ViewGroup) root, false);
        builder.setView(myView);

        // Set up the buttons
        builder.setPositiveButton("Sort", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        final AlertDialog dialog = builder.create();
        dialog.show();

        RadioButton rbTitleAZ = dialog.findViewById(R.id.rbTitleAZ);
        RadioButton rbTitleZA = dialog.findViewById(R.id.rbTitleZA);
        RadioButton rbAuthorAZ = dialog.findViewById(R.id.rbAuthorAZ);
        RadioButton rbAuthorZA = dialog.findViewById(R.id.rbAuthorZA);
        RadioButton rbSerieAZ = dialog.findViewById(R.id.rbSerieAZ);
        RadioButton rbSerieZA = dialog.findViewById(R.id.rbSerieZA);
        RadioButton rbRating05 = dialog.findViewById(R.id.rbRating05);
        RadioButton rbRating50 = dialog.findViewById(R.id.rbRating50);

        switch (mBookListAdapter.getSortOption()) {
            case AUTHOR:
                if(SortOrder.INCREASE == mBookListAdapter.getSortOrder()) {
                    rbAuthorAZ.setChecked(true);
                }
                else {
                    rbAuthorZA.setChecked(true);
                }
                break;
            case SERIE:
                if(SortOrder.INCREASE == mBookListAdapter.getSortOrder()) {
                    rbSerieAZ.setChecked(true);
                }
                else {
                    rbSerieZA.setChecked(true);
                }
                break;
            case RATING:
                if(SortOrder.INCREASE == mBookListAdapter.getSortOrder()) {
                    rbRating05.setChecked(true);
                }
                else {
                    rbRating50.setChecked(true);
                }
                break;
            default:
                if(SortOrder.INCREASE == mBookListAdapter.getSortOrder()) {
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
                        mBookListAdapter.setSort(SortOption.TITLE, SortOrder.DECREASE);
                        break;
                    case R.id.rbAuthorAZ:
                        mBookListAdapter.setSort(SortOption.AUTHOR, SortOrder.INCREASE);
                        break;
                    case R.id.rbAuthorZA:
                        mBookListAdapter.setSort(SortOption.AUTHOR, SortOrder.DECREASE);
                        break;
                    case R.id.rbSerieAZ:
                        mBookListAdapter.setSort(SortOption.SERIE, SortOrder.INCREASE);
                        break;
                    case R.id.rbSerieZA:
                        mBookListAdapter.setSort(SortOption.SERIE, SortOrder.DECREASE);
                        break;
                    case R.id.rbRating05:
                        mBookListAdapter.setSort(SortOption.RATING, SortOrder.INCREASE);
                        break;
                    case R.id.rbRating50:
                        mBookListAdapter.setSort(SortOption.RATING, SortOrder.DECREASE);
                        break;
                    default:
                        mBookListAdapter.setSort(SortOption.TITLE, SortOrder.INCREASE);
                }

                dialog.dismiss();
            }
        });

    }
}