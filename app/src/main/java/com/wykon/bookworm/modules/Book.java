package com.wykon.bookworm.modules;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.widget.Toast;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by WykonGoar on 11-09-2020.
 */

public class Book implements Comparable{

    // Required
    protected Integer mId = -1;
    protected String mAuthorLastName = "";
    protected String mTitle = "";
    protected float mRating = 0.0f;
    protected double mBookNumber = -1.0;

    // Optional
    protected String mAuthorFirstName = null;
    protected String mDescription = null;
    protected String mUrl = null;
    protected Serie mSerie = null;
    protected LinkedList<Genre> mGenres = new LinkedList<>();

    public Book() {
    }

    public Book(Integer id, String mAuthorLastName, String title, float rating, double bookNumber) {
        this.mId = id;
        this.mAuthorLastName = mAuthorLastName;
        this.mTitle = title;
        this.mRating = rating;
        this.mBookNumber = bookNumber;
    }

    public Book(Book toClone){
        this.mId = toClone.getId();
        this.mAuthorLastName = toClone.getAuthorLastName();
        this.mTitle = toClone.getTitle();
        this.mRating = toClone.getRating();
        this.mBookNumber = toClone.getBookNumber();
        this.mAuthorFirstName = toClone.getAuthorFirstName();
        this.mDescription = toClone.getDescription();
        this.mUrl = toClone.getUrl();
        this.mSerie = toClone.getSerie();
        this.mGenres = toClone.getGenres();
    }

    public Integer getId() {
        return mId;
    }

    public String getAuthorLastName() {
        return mAuthorLastName;
    }

    public String getTitle() {
        return mTitle;
    }

    public float getRating() {
        return mRating;
    }

    public double getBookNumber() {
        return mBookNumber;
    }

    public @Nullable String getAuthorFirstName() {
        return mAuthorFirstName;
    }

    public @Nullable String getDescription() {
        return mDescription;
    }

    public @Nullable String getUrl() {
        return mUrl;
    }

    public @Nullable Serie getSerie() {
        return mSerie;
    }

    public LinkedList<Genre> getGenres() {
        return mGenres;
    }

    public String getAuthor() {
        if(this.getAuthorFirstName() == null){
            return this.getAuthorLastName();
        }
        else {
            return String.format("%s %s", this.getAuthorFirstName(), this.getAuthorLastName());
        }
    }

    public void setAuthorLastName(String lastName) {
        this.mAuthorLastName = lastName.trim();
    }

    public void setTitle(String title) {
        this.mTitle = title.trim();
    }

    public void setRating(float rating) {
        this.mRating = rating;
    }

    public void setBookNumber(double bookNumber) {
        this.mBookNumber = bookNumber;
    }

    public void setAuthorFirstName(@Nullable String firstName) {
        if (firstName != null) {
            firstName = firstName.trim();
        }
        this.mAuthorFirstName = firstName;
    }

    public void setDescription(@Nullable String description) {
        if (description != null) {
            description = description.trim();
        }
        this.mDescription = description;
    }

    public void setUrl(@Nullable String url) {
        if (url != null) {
            url = url.trim();
        }
        this.mUrl = url;
    }

    public void setSerie(@Nullable Serie serie) {
        this.mSerie = serie;
    }

    public void addGenre(Genre genre) {
        this.mGenres.add(genre);
    }

    public void addGenres(Collection<Genre> genres) {
        this.mGenres.addAll(genres);
    }

    public void clearGenres() {
        this.mGenres.clear();
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

        updateBookGenres(databaseConnection);
        return true;
    }

    public void delete(DatabaseConnection databaseConnection) {

        SQLiteStatement genre_statement = databaseConnection.getNewStatement(
                "DELETE FROM books_genres WHERE book_id = ?;"
        );
        genre_statement.bindDouble(1, mId);
        databaseConnection.executeUpdateQuery(genre_statement);

        SQLiteStatement book_statement = databaseConnection.getNewStatement(
                "DELETE FROM books WHERE _id = ?;"
        );
        book_statement.bindDouble(1, mId);

        databaseConnection.executeUpdateQuery(book_statement);
    }

    private void insertBook(DatabaseConnection databaseConnection) {
        SQLiteStatement statement = databaseConnection.getNewStatement(
                "INSERT INTO books(" +
                        "title, " + // Index: 1
                        "author_last_name, " + // Index: 2
                        "rating, " + // Index: 3
                        "book_number, " + // Index: 4
                        "author_first_name, " + // Index: 5
                        "serie, " + // Index: 6
                        "description, " + // Index: 7
                        "url" + // Index: 8
                        ")" +
                        "VALUES (?, ?, ?, ?, ?, ?, ?, ?);"
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
                        "url = ? " + // Index: 8
                        "WHERE _id = ?" // Index: 9
        );
        statement.bindDouble(9, mId);
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

        databaseConnection.executeUpdateQuery(statement);
    }

    public void moveToWishList(DatabaseConnection databaseConnection) {
        SQLiteStatement statement = databaseConnection.getNewStatement(
                "UPDATE books SET is_wish = 1 " +
                        "WHERE _id = ?" // Index: 1
        );
        statement.bindDouble(1, mId);

        databaseConnection.executeUpdateQuery(statement);
    }

    public void updateBookGenres(DatabaseConnection databaseConnection) {
        // Remove all genre connections
        SQLiteStatement statement = databaseConnection.getNewStatement(
                "DELETE FROM books_genres WHERE book_id = ?;"
        );
        statement.bindDouble(1, mId);
        databaseConnection.executeUpdateQuery(statement);

        // Insert genre connections
        for (Genre genre : mGenres) {
            ContentValues insertValues = new ContentValues();
            insertValues.put("book_id", mId);
            insertValues.put("genre_id", genre.getId());

            databaseConnection.createConnection();
            databaseConnection.mDatabase.insertWithOnConflict("books_genres", null, insertValues, SQLiteDatabase.CONFLICT_IGNORE);
        }
    }

    @Override
    public int compareTo(Object o) {
        return mTitle.compareTo(((Book) o).getTitle()) ;
    }
}
