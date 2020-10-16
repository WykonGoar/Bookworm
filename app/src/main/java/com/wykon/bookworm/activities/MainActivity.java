package com.wykon.bookworm.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SearchView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.wykon.bookworm.R;
import com.wykon.bookworm.modules.Book;
import com.wykon.bookworm.modules.BookListAdapter;
import com.wykon.bookworm.modules.DatabaseConnection;
import com.wykon.bookworm.modules.SortOption;
import com.wykon.bookworm.modules.SortOrder;

import java.util.LinkedList;

/**
 * Created by WykonGoar on 11-09-2020.
 */

public class MainActivity extends AppCompatActivity implements SearchView.OnQueryTextListener {

    private LinkedList<Book> mBooks = new LinkedList<>();

    private Context mContext;
    private DatabaseConnection mDatabaseConnection;
    private BookListAdapter mBookListAdapter;

    private ListView mBookListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mContext = this;
        mDatabaseConnection = new DatabaseConnection(this);

        FloatingActionButton fab = findViewById(R.id.fabAdd);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mIntent = new Intent(getApplicationContext(), EditBookActivity.class);
                startActivity(mIntent);
            }
        });

        mBookListView = findViewById(R.id.lvBooks);
        mBookListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Book book = (Book) mBookListAdapter.getItem(position);

                Intent mIntent = new Intent(getApplicationContext(), BookActivity.class);
                mIntent.putExtra("id", book.getId());
                startActivity(mIntent);
            }
        });

        loadBooks();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadBooks();
    }

    private void loadBooks(){
        mBooks = mDatabaseConnection.getBooks();

        mBookListAdapter = new BookListAdapter(this, mBooks);
        mBookListView.setAdapter(mBookListAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);

        MenuItem searchItem = menu.findItem(R.id.bSearch);
        SearchView searchView = null;
        if (searchItem != null) {
            searchView = (SearchView) searchItem.getActionView();
        }
        if (searchView != null) {
            searchView.setOnQueryTextListener(this);
        }

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        Intent mIntent = null;
        switch (id) {
            case R.id.bOrder:
                createSortDialog();
                break;
            case R.id.bSeries:
                mIntent = new Intent(getApplicationContext(), SeriesActivity.class);
                startActivity(mIntent);
                break;
            case R.id.bGenres:
                mIntent = new Intent(getApplicationContext(), GenresActivity.class);
                startActivity(mIntent);
                break;
        }

        //noinspection SimplifiableIfStatement
//        if (id == R.id.export) {
//            return true;
//        }

        return super.onOptionsItemSelected(item);
    }

    public void createSortDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle("Sort books by:");
        // I'm using fragment here so I'm using getView() to provide ViewGroup
        // but you can provide here any other instance of ViewGroup from your Fragment / Activity
        LayoutInflater layoutInflater = LayoutInflater.from(mContext);
        View myView = layoutInflater.inflate(R.layout.dialog_sort_books, null);
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

    @Override
    public boolean onQueryTextSubmit(String s) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        mBookListAdapter.getFilter().filter(newText);
        return false;
    }
}