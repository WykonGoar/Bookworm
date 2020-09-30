package com.wykon.bookworm.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.wykon.bookworm.R;
import com.wykon.bookworm.modules.DatabaseConnection;
import com.wykon.bookworm.modules.Genre;
import com.wykon.bookworm.modules.GenreListAdapter;

import java.util.LinkedList;

public class GenresActivity extends AppCompatActivity implements SearchView.OnQueryTextListener {

    private LinkedList<Genre> mGenres = new LinkedList<>();

    private Context mContext;
    private DatabaseConnection mDatabaseConnection;
    private GenreListAdapter mGenreListAdapter;

    private ListView mGenreListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_genres);
        mContext = this;
        mDatabaseConnection = new DatabaseConnection(this);

        FloatingActionButton fab = findViewById(R.id.fabAdd);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createNewGenreDialog(null);
            }
        });

        mGenreListView = findViewById(R.id.lvGenres);
        mGenreListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                Genre genre = (Genre) mGenreListAdapter.getItem(position);
                createNewGenreDialog(genre);
                return false;
            }
        });

        loadGenres();
    }

    public void loadGenres() {
        mGenres = mDatabaseConnection.getGenres();

        mGenreListAdapter = new GenreListAdapter(this, mGenres);
        mGenreListView.setAdapter(mGenreListAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_search, menu);

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

    public void createNewGenreDialog(final Genre genre) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        // I'm using fragment here so I'm using getView() to provide ViewGroup
        // but you can provide here any other instance of ViewGroup from your Fragment / Activity
        LayoutInflater layoutInflater = LayoutInflater.from(mContext);
        View myView = layoutInflater.inflate(R.layout.dialog_new_genre, null);
        builder.setView(myView);

        // Set up the buttons
        String title = "New genre";
        String positiveText = "Create";
        if (genre != null) {
            title = "Edit genre";
            positiveText = "Save";
        }

        builder.setTitle(title);

        builder.setPositiveButton(positiveText, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        if (genre != null) {
            builder.setNeutralButton("Delete", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (genre.delete(mDatabaseConnection)) {
                        mGenres.remove(genre);
                        mGenreListAdapter.notifyDataSetChanged();
                    }
                    else {
                        Toast.makeText(mContext, "Genre still in use", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }

        final AlertDialog dialog = builder.create();
        dialog.show();

        final EditText etNewGenre = ((AlertDialog) dialog).findViewById(R.id.etNewGenre);
        if (genre != null) {
            etNewGenre.setText(genre.getName());
        }

        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Boolean newGenreCreated = updateGenre(etNewGenre.getText().toString(), genre);
                if(newGenreCreated)
                    dialog.dismiss();
            }
        });

    }

    public Boolean updateGenre(String name, Genre genre) {
        boolean newGenre = false;
        if (genre == null) {
            genre = new Genre();
            newGenre = true;
        }
        genre.setName(name);
        if(!genre.save(mContext, mDatabaseConnection)) {
            return false;
        }

        if (newGenre) {
            mGenres.add(genre);
        }
        mGenreListAdapter.notifyDataSetChanged();
        return true;
    }

    @Override
    public boolean onQueryTextSubmit(String s) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        mGenreListAdapter.getFilter().filter(newText);
        return false;
    }
}