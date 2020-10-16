package com.wykon.bookworm.activities;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.wykon.bookworm.R;
import com.wykon.bookworm.modules.DatabaseConnection;
import com.wykon.bookworm.modules.Genre;
import com.wykon.bookworm.modules.Serie;
import com.wykon.bookworm.modules.WishBook;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EditWishBookActivity extends AppCompatActivity {

    private Context mContext;
    private DatabaseConnection mDatabaseConnection;
    private WishBook mWishBook;
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
    private TextView tvGenres;
    private ImageButton ibEditGenres;
    private EditText etReleaseDate;
    private ImageButton ibRemoveReleaseDate;
    private EditText etUrl;
    private EditText etDescription;

    private String lastSelectedSerie;
    private String lastSelectedGenre;
    private Date selectedReleaseDate = null;

    private final String NOT_SELECTED = "Not selected";
    private final String CREATE_NEW = "Create new";

    private static int EDIT_GENRES = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_wish_book);

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
        tvGenres = findViewById(R.id.tvGenres);
        ibEditGenres = findViewById(R.id.ibEditGenres);
        etUrl = findViewById(R.id.etUrl);
        etDescription = findViewById(R.id.etDescription);
        loadSpinners();

        ibRemoveReleaseDate = findViewById(R.id.ibRemoveReleaseDate);
        ibRemoveReleaseDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                etReleaseDate.setText("");
                selectedReleaseDate = null;
            }
        });

        etReleaseDate = findViewById(R.id.etReleaseDate);
        etReleaseDate.setInputType(InputType.TYPE_NULL);
        etReleaseDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar cldr = Calendar.getInstance();
                if (selectedReleaseDate != null) {
                    cldr.setTime(selectedReleaseDate);
                }

                int day = cldr.get(Calendar.DAY_OF_MONTH);
                int month = cldr.get(Calendar.MONTH);
                int year = cldr.get(Calendar.YEAR);

                // date picker dialog
                DatePickerDialog picker = new DatePickerDialog(mContext, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        cldr.set(year, month,dayOfMonth);
                        selectedReleaseDate = cldr.getTime();

                        etReleaseDate.setText(WishBook.getDateFormat().format(selectedReleaseDate));
                    }
                }, year, month, day);
                picker.show();
            }
        });

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

        mWishBook = new WishBook();

        Intent mIntent = getIntent();
        int bookId = mIntent.getIntExtra("id", -1);
        if (bookId != -1)
            mWishBook = mDatabaseConnection.getWishBook(bookId);
            loadBook();

        ibEditGenres.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mIntent = new Intent(getApplicationContext(), SelectGenresActivity.class);
                ArrayList<Integer> genreIds = new ArrayList<>();
                for (Genre genre : mWishBook.getGenres()) {
                    genreIds.add(genre.getId());
                }

                mIntent.putExtra("selectedGenresIds", genreIds);
                startActivityForResult(mIntent, EDIT_GENRES);
            }
        });
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
    }

    public void loadBook() {
        etTitle.setText(mWishBook.getTitle());
        etAuthorLastName.setText(mWishBook.getAuthorLastName());
        etReleaseDate.setText(mWishBook.getRenderedReleaseDate());
        selectedReleaseDate = mWishBook.getReleaseDate();

        etAuthorFirstName.setText("");
        if (mWishBook.getAuthorFirstName() != null) {
            etAuthorFirstName.setText(mWishBook.getAuthorFirstName());
        }

        lastSelectedSerie = NOT_SELECTED;
        sSerie.setSelection(mSerieValues.indexOf(lastSelectedSerie));
        cbSerieCompleted.setVisibility(View.GONE);
        if (mWishBook.getSerie() != null) {
            lastSelectedSerie = mWishBook.getSerie().getName();
            sSerie.setSelection(mSerieValues.indexOf(lastSelectedSerie));

            if (mWishBook.getBookNumber() != -1) {
                etBookNumber.setText(String.format("%.1f", mWishBook.getBookNumber()));
            }
            cbSerieCompleted.setVisibility(View.VISIBLE);
            cbSerieCompleted.setChecked(mWishBook.getSerie().isCompleted());
        }

        String genres = "";
        for (Genre genre: mWishBook.getGenres()) {
            genres += String.format("%s; ", genre.getName());
        }
        tvGenres.setText(genres);

        etDescription.setText("");
        if (mWishBook.getDescription() != null) {
            etDescription.setText(mWishBook.getDescription());
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

        mWishBook.setTitle(etTitle.getText().toString());
        mWishBook.setAuthorLastName(etAuthorLastName.getText().toString());
        mWishBook.setReleaseDate(selectedReleaseDate);

        mWishBook.setBookNumber(-1.0);
        if(!etBookNumber.getText().toString().isEmpty()) {
            String sBookNumber = etBookNumber.getText().toString();
            sBookNumber = sBookNumber.replace(",", ".");
            mWishBook.setBookNumber(Double.parseDouble(sBookNumber));
        }

        mWishBook.setAuthorFirstName(null);
        if(!etAuthorFirstName.getText().toString().isEmpty()) {
            mWishBook.setAuthorFirstName(etAuthorFirstName.getText().toString());
        }

        mWishBook.setDescription(null);
        if(!etDescription.getText().toString().isEmpty()) {
            mWishBook.setDescription(etDescription.getText().toString());
        }

        String selectedSerie = (String) sSerie.getSelectedItem();
        if(NOT_SELECTED.equals(selectedSerie)) {
            mWishBook.setSerie(null);
        }
        else {
            mWishBook.setSerie(mSeries.get(selectedSerie));
        }

        if(mWishBook.save(mContext, mDatabaseConnection)) {
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
        if (mWishBook.getId() != -1) {
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (RESULT_OK != resultCode) {
            return;
        }

        if(EDIT_GENRES == requestCode) {
            ArrayList<Integer> selectedGenresIds = data.getIntegerArrayListExtra("selectedGenresIds");

            mWishBook.clearGenres();

            mGenres = new HashMap<>();
            for (Genre genre : mDatabaseConnection.getGenres()) {
                mGenres.put(genre.getName(), genre);

                if (selectedGenresIds.contains(genre.getId())) {
                    mWishBook.addGenre(genre);
                }
            }

            loadBook();
        }

    }
}
