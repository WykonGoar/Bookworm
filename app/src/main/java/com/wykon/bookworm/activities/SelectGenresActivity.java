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
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;

import com.wykon.bookworm.R;
import com.wykon.bookworm.modules.DatabaseConnection;
import com.wykon.bookworm.modules.Genre;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class SelectGenresActivity extends AppCompatActivity {

    private Context mContext;
    private DatabaseConnection mDatabaseConnection;

    private ListView mSelectedListView;
    private ListView mUnselectedListView;
    private ImageButton ibSelected;
    private ImageButton ibUnselected;
    private ImageButton ibAddNew;

    private List<String> mSelectedGenreValues;
    private List<String> mUnselectedGenreValues;
    private ArrayList<Integer> mSelectedGenresIds;
    private ArrayAdapter<String> mSelectedAdapter;
    private ArrayAdapter<String> mUnselectedAdapter;

    private HashMap<String, Genre> mGenres;

    private String mSelectedInSelected = "";
    private String mSelectedInUnselected = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_genres);

        mContext = this;
        mDatabaseConnection = new DatabaseConnection(this);

        mGenres = new HashMap<>();
        for (Genre genre : mDatabaseConnection.getGenres()) {
            mGenres.put(genre.getName(), genre);
        }

        mUnselectedListView = findViewById(R.id.lvUnselected);
        mSelectedListView = findViewById(R.id.lvSelected);
        ibSelected = findViewById(R.id.ibSelect);
        ibUnselected = findViewById(R.id.ibUnselect);
        ibAddNew = findViewById(R.id.ibAddNew);

        mUnselectedListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mSelectedInUnselected = mUnselectedGenreValues.get(position);
            }
        });
        mSelectedListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mSelectedInSelected = mSelectedGenreValues.get(position);
            }
        });
        ibSelected.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                moveToSelected();
            }
        });
        ibUnselected.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                moveToUnselected();
            }
        });
        ibAddNew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createNewGenreDialog();
            }
        });

        Intent mIntent = getIntent();
        mSelectedGenresIds = mIntent.getIntegerArrayListExtra("selectedGenresIds");

        loadListValues();
    }

    private void loadListValues() {
        mSelectedGenreValues = new ArrayList<>();
        mUnselectedGenreValues = new ArrayList<>();

        for (Genre genre : mGenres.values()) {
            if (mSelectedGenresIds.contains(genre.getId())) {
                mSelectedGenreValues.add(genre.getName());
            }
            else {
                mUnselectedGenreValues.add(genre.getName());
            }
        }
        Collections.sort(mSelectedGenreValues);
        Collections.sort(mUnselectedGenreValues);

        mSelectedAdapter = new ArrayAdapter<>(mContext, R.layout.simple_spinner_item, mSelectedGenreValues);
        mSelectedAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSelectedListView.setAdapter(mSelectedAdapter);

        mUnselectedAdapter = new ArrayAdapter<>(mContext, R.layout.simple_spinner_item, mUnselectedGenreValues);
        mUnselectedAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mUnselectedListView.setAdapter(mUnselectedAdapter);
    }

    private void moveToSelected() {
        if (mSelectedInUnselected.isEmpty()) {
            return;
        }

        int currentIndex = mUnselectedGenreValues.indexOf(mSelectedInUnselected);
        mUnselectedGenreValues.remove(mSelectedInUnselected);
        mSelectedGenreValues.add(mSelectedInUnselected);
        Collections.sort(mSelectedGenreValues);

        mUnselectedAdapter.notifyDataSetChanged();
        mSelectedAdapter.notifyDataSetChanged();

        if (currentIndex <= mUnselectedGenreValues.size() -1) {
            mSelectedInUnselected = mUnselectedGenreValues.get(currentIndex);
        }
        else {
            mSelectedInUnselected = "";
        }
    }

    private void moveToUnselected() {
        if (mSelectedInSelected.isEmpty()) {
            return;
        }

        int currentIndex = mSelectedGenreValues.indexOf(mSelectedInSelected);
        mSelectedGenreValues.remove(mSelectedInSelected);
        mUnselectedGenreValues.add(mSelectedInSelected);
        Collections.sort(mUnselectedGenreValues);

        mUnselectedAdapter.notifyDataSetChanged();
        mSelectedAdapter.notifyDataSetChanged();

        if (currentIndex <= mSelectedGenreValues.size() -1) {
            mSelectedInSelected = mSelectedGenreValues.get(currentIndex);
        }
        else {
            mSelectedInSelected = "";
        }
    }

    public void save() {
        LinkedList<Integer> selectedIds = new LinkedList<>();

        for (String selected : mSelectedGenreValues) {
            selectedIds.add(mGenres.get(selected).getId());
        }

        Intent intent = new Intent();
        intent.putExtra("selectedGenresIds", selectedIds);
        setResult(RESULT_OK, intent);
        finish();
    }

    public void createNewGenreDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle("New genre");
        // I'm using fragment here so I'm using getView() to provide ViewGroup
        // but you can provide here any other instance of ViewGroup from your Fragment / Activity
        LayoutInflater layoutInflater = LayoutInflater.from(mContext);
        View myView = layoutInflater.inflate(R.layout.dialog_new_genre, null);
        builder.setView(myView);

        // Set up the buttons
        builder.setPositiveButton("Create", new DialogInterface.OnClickListener() {
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

        final AlertDialog dialog = builder.create();
        dialog.show();
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                EditText etNewGenre = ((AlertDialog) dialog).findViewById(R.id.etNewGenre);

                Boolean newGenreCreated = createNewGenre(etNewGenre.getText().toString());
                if(newGenreCreated)
                    dialog.dismiss();
            }
        });

    }

    public Boolean createNewGenre(String name) {
        Genre newGenre = new Genre();
        newGenre.setName(name);
        if(!newGenre.save(mContext, mDatabaseConnection)) {
            return false;
        }

        mGenres.put(newGenre.getName(), newGenre);
        mUnselectedGenreValues.add(newGenre.getName());
        Collections.sort(mUnselectedGenreValues);
        mUnselectedAdapter.notifyDataSetChanged();
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_book_edit, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.bSave) {
            save();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        setResult(RESULT_CANCELED, intent);
        finish();
    }
}