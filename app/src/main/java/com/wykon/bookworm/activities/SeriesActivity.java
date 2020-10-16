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
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.wykon.bookworm.R;
import com.wykon.bookworm.modules.Book;
import com.wykon.bookworm.modules.BookListAdapter;
import com.wykon.bookworm.modules.DatabaseConnection;
import com.wykon.bookworm.modules.Serie;
import com.wykon.bookworm.modules.SerieListAdapter;

import java.util.LinkedList;

public class SeriesActivity extends AppCompatActivity implements SearchView.OnQueryTextListener {

    private LinkedList<Serie> mSeries = new LinkedList<>();

    private Context mContext;
    private DatabaseConnection mDatabaseConnection;
    private SerieListAdapter mSerieListAdapter;

    private ListView mSerieListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_series);

        mContext = this;
        mDatabaseConnection = new DatabaseConnection(this);

        FloatingActionButton fab = findViewById(R.id.fabAdd);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               createNewSerieDialog(null);
            }
        });

        mSerieListView = findViewById(R.id.lvSeries);
        mSerieListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                Serie serie = (Serie) mSerieListAdapter.getItem(position);
                createNewSerieDialog(serie);
                return false;
            }
        });

        loadSeries();
    }

    public void loadSeries() {
        mSeries = mDatabaseConnection.getSeries();

        mSerieListAdapter = new SerieListAdapter(this, mSeries);
        mSerieListView.setAdapter(mSerieListAdapter);
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

    public void createNewSerieDialog(final Serie serie) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        // I'm using fragment here so I'm using getView() to provide ViewGroup
        // but you can provide here any other instance of ViewGroup from your Fragment / Activity
        LayoutInflater layoutInflater = LayoutInflater.from(mContext);
        View myView = layoutInflater.inflate(R.layout.dialog_new_serie, null);
        builder.setView(myView);

        // Set up the buttons
        String title = "New serie";
        String positiveText = "Create";
        if (serie != null) {
            title = "Edit serie";
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

        if (serie != null) {
            builder.setNeutralButton("Delete", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (serie.delete(mDatabaseConnection)) {
                        mSeries.remove(serie);
                        mSerieListAdapter.notifyDataSetChanged();
                    }
                    else {
                        Toast.makeText(mContext, "Serie still in use", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }

        final AlertDialog dialog = builder.create();
        dialog.show();

        final EditText etNewSerie = ((AlertDialog) dialog).findViewById(R.id.etNewSerie);
        final CheckBox cbCompleted = ((AlertDialog) dialog).findViewById(R.id.cbCompleted);
        if(serie != null) {
            etNewSerie.setText(serie.getName());
            cbCompleted.setChecked(serie.isCompleted());
        }

        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Boolean newSerieCreated = updateSerie(etNewSerie.getText().toString(), cbCompleted.isChecked(), serie);
                if(newSerieCreated)
                    dialog.dismiss();
            }
        });

    }

    public Boolean updateSerie(String name, Boolean completed, Serie serie) {
        boolean newSerie = false;
        if (serie == null) {
            serie = new Serie();
            newSerie = true;
        }
        serie.setName(name);
        serie.setComplete(completed);
        if(!serie.save(mContext, mDatabaseConnection)) {
            return false;
        }

        if (newSerie) {
            mSeries.add(serie);
        }
        mSerieListAdapter.notifyDataSetChanged();
        return true;
    }

    @Override
    public boolean onQueryTextSubmit(String s) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        mSerieListAdapter.getFilter().filter(newText);
        return false;
    }
}