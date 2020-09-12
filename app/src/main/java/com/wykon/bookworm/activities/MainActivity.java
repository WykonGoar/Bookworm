package com.wykon.bookworm.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SearchView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.wykon.bookworm.R;
import com.wykon.bookworm.modules.Book;
import com.wykon.bookworm.modules.BookListAdapter;
import com.wykon.bookworm.modules.DatabaseConnection;

import java.util.LinkedList;

/**
 * Created by WykonGoar on 11-09-2020.
 */

public class MainActivity extends AppCompatActivity implements SearchView.OnQueryTextListener {

    private LinkedList<Book> mBooks = new LinkedList<>();

    private DatabaseConnection mDatabaseConnection;
    private BookListAdapter mBookListAdapter;

    private ListView mBookListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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

        //noinspection SimplifiableIfStatement
//        if (id == R.id.export) {
//            return true;
//        }

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
}