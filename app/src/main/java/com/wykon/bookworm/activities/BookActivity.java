package com.wykon.bookworm.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.RatingBar;
import android.widget.SearchView;
import android.widget.TextView;

import com.wykon.bookworm.R;
import com.wykon.bookworm.modules.Book;
import com.wykon.bookworm.modules.DatabaseConnection;
import com.wykon.bookworm.modules.Genre;

public class BookActivity extends AppCompatActivity {

    private Context mContext;
    private DatabaseConnection mDatabaseConnection;
    private Integer mBookId;
    private Book mBook;

    private TextView tvTitle;
    private TextView tvAuthor;
    private TextView tvSerie;
    private CheckBox cbSerieCompleted;
    private TextView tvBookNumber;
    private TextView tvGenre;
    private RatingBar rbRating;
    private TextView tvDescription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book);

        mContext = this;
        mDatabaseConnection = new DatabaseConnection(this);

        tvTitle = findViewById(R.id.tvTitle);
        tvAuthor = findViewById(R.id.tvAuthor);
        tvSerie = findViewById(R.id.tvSerie);
        cbSerieCompleted = findViewById(R.id.cbSerieCompleted);
        tvBookNumber = findViewById(R.id.tvBookNumber);
        tvGenre = findViewById(R.id.tvGenre);
        rbRating = findViewById(R.id.rbRating);
        tvDescription = findViewById(R.id.tvDescription);

        Intent mIntent = getIntent();
        mBookId = mIntent.getIntExtra("id", -1);
        loadBook();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadBook();
    }

    public void loadBook() {
        mBook = new Book();
        if (mBookId != -1)
            mBook = mDatabaseConnection.getBook(mBookId);

        tvTitle.setText(mBook.getTitle());
        tvAuthor.setText(mBook.getAuthor());

        tvSerie.setText("");
        tvBookNumber.setText("");
        cbSerieCompleted.setVisibility(View.GONE);
        if (mBook.getSerie() != null) {
            tvSerie.setText(mBook.getSerie().getName());
            tvBookNumber.setText(String.format("%.1f", mBook.getBookNumber()));
            cbSerieCompleted.setVisibility(View.VISIBLE);
            cbSerieCompleted.setChecked(mBook.getSerie().isCompleted());
        }

        String genres = "";
        for (Genre genre: mBook.getGenres()) {
            genres += String.format("%s; ", genre.getName());
        }
        tvGenre.setText(genres);

        rbRating.setRating(mBook.getRating());

        tvDescription.setText("");
        if (mBook.getDescription() != null) {
            tvDescription.setText(mBook.getDescription());
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_book, menu);

        MenuItem bOpen = menu.findItem(R.id.bOpen);
        if (mBook.getUrl() == null) {
            bOpen.setVisible(false);
        }

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.bEdit) {
            Intent mIntent = new Intent(getApplicationContext(), EditBookActivity.class);
            mIntent.putExtra("id", mBook.getId());

            startActivity(mIntent);
        }
        if (id == R.id.bDelete) {
            deleteBook();
        }

        if (id == R.id.bOpen) {
            String url = mBook.getUrl();
            if (url == null) {
                return super.onOptionsItemSelected(item);
            }

            if (!url.startsWith("http://") && !url.startsWith("https://")) {
                url = "http://" + url;
            }

            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
        }

        return super.onOptionsItemSelected(item);
    }

    public void deleteBook() {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle(R.string.app_name);
        builder.setMessage("Do you want to remove this book?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
                mBook.delete(mDatabaseConnection);
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}