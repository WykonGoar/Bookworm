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
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.wykon.bookworm.R;
import com.wykon.bookworm.modules.Book;
import com.wykon.bookworm.modules.DatabaseConnection;
import com.wykon.bookworm.modules.Genre;
import com.wykon.bookworm.modules.Serie;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EditBookActivity extends AppCompatActivity {

    private Context mContext;
    private DatabaseConnection mDatabaseConnection;
    private Book mBook;
    private Map<String, Serie> mSeries;
    private Map<String, Genre> mGenres;

    private List<String> mSerieValues;
    private List<String> mGenreValues;

    private EditText etTitle;
    private EditText etAuthorFirstName;
    private EditText etAuthorLastName;
    private Spinner sSerie;
    private CheckBox cbSerieCompleted;
    private EditText etBookNumber;
    private Spinner sGenre;
    private RatingBar rbRating;
    private EditText etUrl;
    private EditText etDescription;

    private String lastSelectedSerie;
    private String lastSelectedGenre;

    private final String NOT_SELECTED = "Not selected";
    private final String CREATE_NEW = "Create new";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_book);

        mContext = this;
        mDatabaseConnection = new DatabaseConnection(this);

        mSeries = new HashMap<>();
        for (Serie serie : mDatabaseConnection.getSeries()) {
            mSeries.put(serie.getName(), serie);
        }
        mGenres = new HashMap<>();
        for (Genre genre : mDatabaseConnection.getGenres()) {
            mGenres.put(genre.getName(), genre);
        }

        etTitle = findViewById(R.id.etTitle);
        etAuthorFirstName = findViewById(R.id.etAuthorFirstName);
        etAuthorLastName = findViewById(R.id.etAuthorLastName);
        sSerie = findViewById(R.id.sSerie);
        cbSerieCompleted = findViewById(R.id.cbSerieCompleted);
        etBookNumber = findViewById(R.id.etBookNumber);
        sGenre = findViewById(R.id.sGenre);
        rbRating = findViewById(R.id.rbRating);
        etUrl = findViewById(R.id.etUrl);
        etDescription = findViewById(R.id.etDescription);
        loadSpinners();

        sSerie.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedSerie = (String) sSerie.getItemAtPosition(position);
                if(!CREATE_NEW.equals(selectedSerie)) {
                    lastSelectedSerie = selectedSerie;

                    if(NOT_SELECTED.equals(selectedSerie)) {
                        cbSerieCompleted.setVisibility(View.GONE);
                    }
                    else {
                        Serie serie = mSeries.get(selectedSerie);
                        cbSerieCompleted.setVisibility(View.VISIBLE);
                        cbSerieCompleted.setChecked(serie.isCompleted());
                    }
                    return;
                }

                createNewSerieDialog();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        sGenre.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedGenre = (String) sGenre.getItemAtPosition(position);
                if(!CREATE_NEW.equals(selectedGenre)) {
                    lastSelectedGenre = selectedGenre;
                    return;
                }

                createNewGenreDialog();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        mBook = new Book();

        Intent mIntent = getIntent();
        int bookId = mIntent.getIntExtra("id", -1);
        if (bookId != -1)
            mBook = mDatabaseConnection.getBook(bookId);
            loadBook();
    }

    public void loadSpinners() {
        mSerieValues = new ArrayList<String>();
        for(Serie serie : mSeries.values()) {
            mSerieValues.add(serie.getName());
        }
        Collections.sort(mSerieValues);
        mSerieValues.add(0, NOT_SELECTED);
        mSerieValues.add(CREATE_NEW);

        ArrayAdapter<String> seriesAdapter = new ArrayAdapter<>(mContext, R.layout.simple_spinner_item, mSerieValues);
        seriesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sSerie.setAdapter(seriesAdapter);

        mGenreValues = new ArrayList<>();
        for(Genre genre : mGenres.values()) {
            mGenreValues.add(genre.getName());
        }
        Collections.sort(mGenreValues);
        mGenreValues.add(0, NOT_SELECTED);
        mGenreValues.add(CREATE_NEW);

        ArrayAdapter<String> genreAdapter = new ArrayAdapter<>(mContext, R.layout.simple_spinner_item, mGenreValues);
        genreAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sGenre.setAdapter(genreAdapter);
    }

    public void loadBook() {
        etTitle.setText(mBook.getTitle());
        etAuthorLastName.setText(mBook.getAuthorLastName());
        rbRating.setRating(mBook.getRating());

        etAuthorFirstName.setText("");
        if (mBook.getAuthorFirstName() != null) {
            etAuthorFirstName.setText(mBook.getAuthorFirstName());
        }

        lastSelectedSerie = NOT_SELECTED;
        sSerie.setSelection(mSerieValues.indexOf(lastSelectedSerie));
        cbSerieCompleted.setVisibility(View.GONE);
        if (mBook.getSerie() != null) {
            lastSelectedSerie = mBook.getSerie().getName();
            sSerie.setSelection(mSerieValues.indexOf(lastSelectedSerie));

            etBookNumber.setText(String.format("%.1f", mBook.getBookNumber()));
            cbSerieCompleted.setVisibility(View.VISIBLE);
            cbSerieCompleted.setChecked(mBook.getSerie().isCompleted());
        }

        lastSelectedGenre = NOT_SELECTED;
        sGenre.setSelection(mGenreValues.indexOf(lastSelectedGenre));
        if (mBook.getGenre() != null) {
            lastSelectedGenre = mBook.getGenre().getName();
            sGenre.setSelection(mGenreValues.indexOf(lastSelectedGenre));
        }

        etUrl.setText("");
        if (mBook.getUrl() != null) {
            etUrl.setText(mBook.getUrl());
        }

        etDescription.setText("");
        if (mBook.getDescription() != null) {
            etDescription.setText(mBook.getDescription());
        }
    }

    public boolean validate() {
        if (etTitle.getText().toString().isEmpty()) {
            Toast.makeText(mContext, "Title can not be empty", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (etAuthorLastName.getText().toString().isEmpty()) {
            Toast.makeText(mContext, "Author last name can not be empty", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    public void save() {
        if (!validate()) {
            return;
        }

        mBook.setTitle(etTitle.getText().toString());
        mBook.setAuthorLastName(etAuthorLastName.getText().toString());
        mBook.setRating(rbRating.getRating());

        mBook.setBookNumber(-1.0);
        if(!etBookNumber.getText().toString().isEmpty()) {
            String sBookNumber = etBookNumber.getText().toString();
            mBook.setBookNumber(Float.parseFloat(sBookNumber));
        }

        mBook.setAuthorFirstName(null);
        if(!etAuthorFirstName.getText().toString().isEmpty()) {
            mBook.setAuthorFirstName(etAuthorFirstName.getText().toString());
        }

        mBook.setDescription(null);
        if(!etDescription.getText().toString().isEmpty()) {
            mBook.setDescription(etDescription.getText().toString());
        }

        mBook.setUrl(null);
        if(!etUrl.getText().toString().isEmpty()) {
            mBook.setUrl(etUrl.getText().toString());
        }

        String selectedSerie = (String) sSerie.getSelectedItem();
        if(NOT_SELECTED.equals(selectedSerie)) {
            mBook.setSerie(null);
        }
        else {
            mBook.setSerie(mSeries.get(selectedSerie));
        }

        String selected_genre = (String) sGenre.getSelectedItem();
        if(NOT_SELECTED.equals(selected_genre)) {
            mBook.setGenre(null);
        }
        else {
            mBook.setGenre(mGenres.get(selected_genre));
        }

        if(mBook.save(mContext, mDatabaseConnection)) {
            finish();
        }
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
        confirmCancel();
    }

    public void confirmCancel() {
        String error_message = "Do you want stop creating this book?";
        if (mBook.getId() != -1) {
            error_message = "Do you want stop updating this book?";
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle(R.string.app_name);
        builder.setMessage(error_message);
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
                finish();
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
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
                sGenre.setSelection(mGenreValues.indexOf(lastSelectedGenre));
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
        loadSpinners();
        sGenre.setSelection(mGenreValues.indexOf(newGenre.getName()));
        return true;
    }

    public void createNewSerieDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle("New serie");
        // I'm using fragment here so I'm using getView() to provide ViewGroup
        // but you can provide here any other instance of ViewGroup from your Fragment / Activity
        LayoutInflater layoutInflater = LayoutInflater.from(mContext);
        View myView = layoutInflater.inflate(R.layout.dialog_new_serie, null);
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
                sSerie.setSelection(mSerieValues.indexOf(lastSelectedSerie));
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
                EditText etNewSerie = ((AlertDialog) dialog).findViewById(R.id.etNewSerie);
                CheckBox cbCompleted = ((AlertDialog) dialog).findViewById(R.id.cbCompleted);

                Boolean newSerieCreated = createNewSerie(etNewSerie.getText().toString(), cbCompleted.isChecked());
                if(newSerieCreated)
                    dialog.dismiss();
            }
        });

    }

    public Boolean createNewSerie(String name, Boolean completed) {
        Serie newSerie = new Serie();
        newSerie.setName(name);
        newSerie.setComplete(completed);
        if(!newSerie.save(mContext, mDatabaseConnection)) {
            return false;
        }

        mSeries.put(newSerie.getName(), newSerie);
        loadSpinners();
        sSerie.setSelection(mSerieValues.indexOf(newSerie.getName()));
        return true;
    }
}
