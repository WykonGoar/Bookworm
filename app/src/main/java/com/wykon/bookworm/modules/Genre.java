package com.wykon.bookworm.modules;

import android.content.Context;
import android.database.sqlite.SQLiteStatement;
import android.widget.Toast;

/**
 * Created by WykonGoar on 11-09-2020.
 */

public class Genre {

    // Required
    private Integer mId = -1;
    private String mName = "";

    public Genre() {}

    public Genre(Integer id, String name) {
        this.mId = id;
        this.mName = name;
    }

    public Genre(Genre toClone) {
        this.mId = toClone.getId();
        this.mName = toClone.getName();
    }

    public Integer getId() {
        return mId;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        name = name.replace("\n", " ").replace("\r", " ");
        this.mName = name.trim();
    }

    public Boolean save(Context context, DatabaseConnection databaseConnection) {
        if (databaseConnection.genreExists(mName, mId)) {
            Toast.makeText(context, "Genre already exists", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (mId == -1) {
            insertGenre(databaseConnection);
        }
        else {
            updateGenre(databaseConnection);
        }
        return true;
    }

    public boolean delete(DatabaseConnection databaseConnection) {
        if (databaseConnection.isGenreUsed(mId)) {
            return false;
        }

        SQLiteStatement statement = databaseConnection.getNewStatement(
                "DELETE FROM genres WHERE _id = ?;"
        );
        statement.bindDouble(1, mId);

        databaseConnection.executeUpdateQuery(statement);
        return true;
    }

    private void insertGenre(DatabaseConnection databaseConnection) {
        SQLiteStatement statement = databaseConnection.getNewStatement(
                "INSERT INTO genres(" +
                        "name " + // Index: 1
                        ")" +
                        "VALUES (?);"
        );
        statement.bindString(1, mName);
        mId = databaseConnection.executeInsertQuery(statement);
    }

    private void updateGenre(DatabaseConnection databaseConnection) {
        SQLiteStatement statement = databaseConnection.getNewStatement(
                "UPDATE genres SET " +
                        "name = ? " + // Index: 1
                        "WHERE _id = ?" // Index: 2
        );
        statement.bindDouble(2, mId);
        statement.bindString(1, mName);

        databaseConnection.executeUpdateQuery(statement);
    }
}
