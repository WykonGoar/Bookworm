package com.wykon.bookworm.activities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.wykon.bookworm.R;
import com.wykon.bookworm.modules.Book;
import com.wykon.bookworm.modules.DatabaseConnection;
import com.wykon.bookworm.modules.Genre;
import com.wykon.bookworm.modules.WishBook;

public class WishBookActivity extends AppCompatActivity {

    private Context mContext;
    private DatabaseConnection mDatabaseConnection;
    private Integer mWishBookId;
    private WishBook mWishBook;

    private TextView tvTitle;
    private TextView tvAuthor;
    private TextView tvSerie;
    private CheckBox cbSerieCompleted;
    private TextView tvBookNumber;
    private TextView tvGenre;
    private TextView tvReleaseDate;
    private TextView tvDescription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wish_book);

        mContext = this;
        mDatabaseConnection = new DatabaseConnection(this);

        tvTitle = findViewById(R.id.tvTitle);
        tvAuthor = findViewById(R.id.tvAuthor);
        tvSerie = findViewById(R.id.tvSerie);
        cbSerieCompleted = findViewById(R.id.cbSerieCompleted);
        tvBookNumber = findViewById(R.id.tvBookNumber);
        tvGenre = findViewById(R.id.tvGenre);
        tvReleaseDate = findViewById(R.id.tvReleaseDate);
        tvDescription = findViewById(R.id.tvDescription);

        Intent mIntent = getIntent();
        mWishBookId = mIntent.getIntExtra("id", -1);
        loadWishBook();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadWishBook();
    }

    public void loadWishBook() {
        mWishBook = new WishBook();
        if (mWishBookId != -1)
            mWishBook = mDatabaseConnection.getWishBook(mWishBookId);

        tvTitle.setText(mWishBook.getTitle());
        tvAuthor.setText(mWishBook.getAuthor());

        tvSerie.setText("");
        tvBookNumber.setText("");
        cbSerieCompleted.setVisibility(View.GONE);
        if (mWishBook.getSerie() != null) {
            tvSerie.setText(mWishBook.getSerie().getName());

            if (mWishBook.getBookNumber() != -1) {
                tvBookNumber.setText(String.format("%.1f", mWishBook.getBookNumber()));
            }
            cbSerieCompleted.setVisibility(View.VISIBLE);
            cbSerieCompleted.setChecked(mWishBook.getSerie().isCompleted());
        }

        String genres = "";
        for (Genre genre: mWishBook.getGenres()) {
            genres += String.format("%s; ", genre.getName());
        }
        tvGenre.setText(genres);

        tvReleaseDate.setText(mWishBook.getRenderedReleaseDate());

        tvDescription.setText("");
        if (mWishBook.getDescription() != null) {
            tvDescription.setText(mWishBook.getDescription());
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_book, menu);

        MenuItem bOpen = menu.findItem(R.id.bOpen);
        if (mWishBook.getUrl() == null) {
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
            Intent mIntent = new Intent(getApplicationContext(), EditWishBookActivity.class);
            mIntent.putExtra("id", mWishBook.getId());

            startActivity(mIntent);
        }
        if (id == R.id.bDelete) {
            deleteBook();
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
                mWishBook.delete(mDatabaseConnection);
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