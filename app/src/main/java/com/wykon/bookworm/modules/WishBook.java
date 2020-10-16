package com.wykon.bookworm.modules;

import android.content.Context;
import android.database.sqlite.SQLiteStatement;
import android.widget.Toast;

import androidx.annotation.Nullable;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class WishBook extends Book {

    protected Date mReleaseDate = null;

    public WishBook() {

    }

    public WishBook(Integer id, String mAuthorLastName, String title, float rating, double bookNumber) {
        super(id, mAuthorLastName, title, rating, bookNumber);
    }

    public WishBook(WishBook toClone) {
        super(toClone.getId(), toClone.getAuthorLastName(), toClone.getTitle(), toClone.getRating(),
                toClone.getBookNumber()
        );

        this.mAuthorFirstName = toClone.getAuthorFirstName();
        this.mDescription = toClone.getDescription();
        this.mSerie = toClone.getSerie();
        this.mGenres = toClone.getGenres();

        this.mReleaseDate = toClone.getReleaseDate();
    }

    public static SimpleDateFormat getDateFormat() {
        return new SimpleDateFormat("yyyy-MM-dd");
    }

    public void setReleaseDate(@Nullable Date releaseDate) {
        this.mReleaseDate = releaseDate;
    }

    public void setReleaseDate(@Nullable String sReleaseDate) {
        if(sReleaseDate == null) {
            this.mReleaseDate = null;
            return;
        }

        try {
            this.mReleaseDate = WishBook.getDateFormat().parse(sReleaseDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public @Nullable Date getReleaseDate() {
        return mReleaseDate;
    }

    public @Nullable String getRenderedReleaseDate() {
        if (mReleaseDate == null) {
            return "";
        }
        return WishBook.getDateFormat().format(mReleaseDate);
    }

    public boolean save(Context context, DatabaseConnection databaseConnection) {
        if (databaseConnection.bookExists(mTitle, mAuthorLastName, mId)) {
            Toast.makeText(context, "Book of author already exists", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (mId == -1) {
            insertBook(databaseConnection);
        }
        else {
            updateBook(databaseConnection);
        }

        this.updateBookGenres(databaseConnection);
        return true;
    }

    public void delete(DatabaseConnection databaseConnection) {
        SQLiteStatement statement = databaseConnection.getNewStatement(
                "DELETE FROM books WHERE _id = ? AND is_wish = 1;"
        );
        statement.bindDouble(1, mId);

        databaseConnection.executeUpdateQuery(statement);
    }

    private void insertBook(DatabaseConnection databaseConnection) {
        SQLiteStatement statement = databaseConnection.getNewStatement(
                "INSERT INTO books(" +
                        "is_wish, " +
                        "title, " + // Index: 1
                        "author_last_name, " + // Index: 2
                        "rating, " + // Index: 3
                        "book_number, " + // Index: 4
                        "author_first_name, " + // Index: 5
                        "serie, " + // Index: 6
                        "description, " + // Index: 7
                        "url," + // Index: 8
                        "release_date" + // Index: 9
                        ")" +
                        "VALUES (1, ?, ?, ?, ?, ?, ?, ?, ?, ?);"
        );
        statement.bindString(1, mTitle);
        statement.bindString(2, mAuthorLastName);
        statement.bindDouble(3, mRating);
        statement.bindDouble(4, mBookNumber);

        if (mAuthorFirstName == null){
            statement.bindNull(5);
        }
        else {
            statement.bindString(5, mAuthorFirstName);
        }

        if (mSerie == null){
            statement.bindNull(6);
        }
        else {
            statement.bindDouble(6, mSerie.getId());
        }

        if (mDescription == null){
            statement.bindNull(7);
        }
        else {
            statement.bindString(7, mDescription);
        }

        if (mUrl == null){
            statement.bindNull(8);
        }
        else {
            statement.bindString(8, mUrl);
        }

        if (mReleaseDate == null){
            statement.bindNull(9);
        }
        else {
            statement.bindString(9, this.getRenderedReleaseDate());
        }

        mId = databaseConnection.executeInsertQuery(statement);
    }

    private void updateBook(DatabaseConnection databaseConnection) {
        SQLiteStatement statement = databaseConnection.getNewStatement(
                "UPDATE books SET " +
                        "title = ?, " + // Index: 1
                        "author_last_name = ?, " + // Index: 2
                        "rating = ?, " + // Index: 3
                        "book_number = ?, " + // Index: 4
                        "author_first_name = ?, " + // Index: 5
                        "serie = ?, " + // Index: 6
                        "description = ?, " + // Index: 7
                        "url = ?, " + // Index: 8
                        "release_date = ? " + // Index: 9
                        "WHERE _id = ?" // Index: 10
        );
        statement.bindDouble(10, mId);
        statement.bindString(1, mTitle);
        statement.bindString(2, mAuthorLastName);
        statement.bindDouble(3, mRating);
        statement.bindDouble(4, mBookNumber);

        if (mAuthorFirstName == null){
            statement.bindNull(5);
        }
        else {
            statement.bindString(5, mAuthorFirstName);
        }

        if (mSerie == null){
            statement.bindNull(6);
        }
        else {
            statement.bindDouble(6, mSerie.getId());
        }

        if (mDescription == null){
            statement.bindNull(7);
        }
        else {
            statement.bindString(7, mDescription);
        }

        if (mUrl == null){
            statement.bindNull(8);
        }
        else {
            statement.bindString(8, mUrl);
        }

        if (mReleaseDate == null){
            statement.bindNull(9);
        }
        else {
            statement.bindString(9, this.getRenderedReleaseDate());
        }

        databaseConnection.executeUpdateQuery(statement);
    }
}
