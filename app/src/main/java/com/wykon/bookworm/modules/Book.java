package com.wykon.bookworm.modules;

import android.database.sqlite.SQLiteStatement;

import androidx.annotation.Nullable;

/**
 * Created by WykonGoar on 11-09-2020.
 */

public class Book {

    // Required
    private Integer mId = -1;
    private String mAuthorLastName = "";
    private String mTitle = "";
    private float mRating = 0.0f;
    private double mBookNumber = -1.0;

    // Optional
    private String mAuthorFirstName = null;
    private String mDescription = null;
    private String mUrl = null;
    private Serie mSerie = null;
    private Genre mGenre = null;

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
        this.mGenre = toClone.getGenre();
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

    public @Nullable Genre getGenre() {
        return mGenre;
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
        this.mAuthorLastName = lastName;
    }

    public void setTitle(String title) {
        this.mTitle = title;
    }

    public void setRating(float rating) {
        this.mRating = rating;
    }

    public void setBookNumber(double bookNumber) {
        this.mBookNumber = bookNumber;
    }

    public void setAuthorFirstName(@Nullable String firstName) {
        this.mAuthorFirstName = firstName;
    }

    public void setDescription(@Nullable String description) {
        this.mDescription = description;
    }

    public void setUrl(@Nullable String url) {
        this.mUrl = url;
    }

    public void setSerie(@Nullable Serie serie) {
        this.mSerie = serie;
    }

    public void setGenre(@Nullable Genre genre) {
        this.mGenre = genre;
    }

    public void save(DatabaseConnection databaseConnection) {
        if (mId == -1) {
            insertBook(databaseConnection);
        }
        else {
            updateBook(databaseConnection);
        }
    }

    public void delete(DatabaseConnection databaseConnection) {
        SQLiteStatement statement = databaseConnection.getNewStatement(
                "DELETE FROM books WHERE _id = ?;"
        );
        statement.bindDouble(1, mId);

        databaseConnection.executeUpdateQuery(statement);
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
                        "genre, " + // Index: 8
                        "url" + // Index: 9
                        ")" +
                        "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?);"
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

        if (mGenre == null){
            statement.bindNull(8);
        }
        else {
            statement.bindDouble(8, mGenre.getId());
        }

        if (mUrl == null){
            statement.bindNull(9);
        }
        else {
            statement.bindString(9, mUrl);
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
                        "genre = ?, " + // Index: 8
                        "url = ? " + // Index: 9
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

        if (mGenre == null){
            statement.bindNull(8);
        }
        else {
            statement.bindDouble(8, mGenre.getId());
        }

        if (mUrl == null){
            statement.bindNull(9);
        }
        else {
            statement.bindString(9, mUrl);
        }

        databaseConnection.executeUpdateQuery(statement);
    }
}
