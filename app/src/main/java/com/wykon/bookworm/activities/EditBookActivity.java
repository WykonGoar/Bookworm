package com.wykon.bookworm.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
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

        mBook = new Book();

        Intent mIntent = getIntent();
        int bookId = mIntent.getIntExtra("id", -1);
        if (bookId != -1)
            mBook = mDatabaseConnection.getBook(bookId);
            loadBook();
    }

    public void loadSpinners() {
        mSerieValues = new ArrayList<String>();
        mSerieValues.add(NOT_SELECTED);
        for(Serie serie : mSeries.values()) {
            mSerieValues.add(serie.getName());
        }
//        mSerieValues.add(CREATE_NEW);

        ArrayAdapter<String> seriesAdapter = new ArrayAdapter<>(mContext, R.layout.simple_spinner_item, mSerieValues);
        seriesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sSerie.setAdapter(seriesAdapter);

        mGenreValues = new ArrayList<>();
        mGenreValues.add(NOT_SELECTED);
        for(Genre genre : mGenres.values()) {
            mGenreValues.add(genre.getName());
        }
//        mGenreValues.add(CREATE_NEW);

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

        sSerie.setSelection(mSerieValues.indexOf(NOT_SELECTED));
        cbSerieCompleted.setVisibility(View.GONE);
        if (mBook.getSerie() != null) {
            sSerie.setSelection(mSerieValues.indexOf(mBook.getSerie().getName()));

            etBookNumber.setText(String.format("%.1f", mBook.getBookNumber()));
            cbSerieCompleted.setVisibility(View.VISIBLE);
            cbSerieCompleted.setChecked(mBook.getSerie().isCompleted());
        }

        sGenre.setSelection(mGenreValues.indexOf(NOT_SELECTED));
        if (mBook.getGenre() != null) {
            sGenre.setSelection(mGenreValues.indexOf(mBook.getGenre().getName()));
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

        String selected_serie = (String) sSerie.getSelectedItem();
        if(NOT_SELECTED.equals(selected_serie)) {
            mBook.setSerie(null);
        }
        else {
            mBook.setSerie(mSeries.get(selected_serie));
        }

        String selected_genre = (String) sGenre.getSelectedItem();
        if(NOT_SELECTED.equals(selected_genre)) {
            mBook.setGenre(null);
        }
        else {
            mBook.setGenre(mGenres.get(selected_genre));
        }

        mBook.save(mDatabaseConnection);
        finish();
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
}