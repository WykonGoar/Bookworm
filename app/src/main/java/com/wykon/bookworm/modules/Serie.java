package com.wykon.bookworm.modules;

import android.content.Context;
import android.database.sqlite.SQLiteStatement;
import android.widget.Toast;

/**
 * Created by WykonGoar on 11-09-2020.
 */

public class Serie {

    // Required
    private Integer mId = -1;
    private String mName = "";
    private Boolean mComplete = false;

    public Serie() {}

    public Serie(Integer id, String name) {
        this.mId = id;
        this.mName = name;
    }

    public Integer getId() {
        return mId;
    }

    public String getName() {
        return mName;
    }

    public Boolean isCompleted() {
        return mComplete;
    }

    public void setName(String name) {
        name = name.replace("\n", " ").replace("\r", " ");
        this.mName = name.trim();
    }

    public void setComplete(Boolean complete) {
        this.mComplete = complete;
    }

    public Boolean save(Context context, DatabaseConnection databaseConnection) {
        if (databaseConnection.serieExists(mName, mId)) {
            Toast.makeText(context, "Serie already exists", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (mId == -1) {
            insertSerie(databaseConnection);
        }
        else {
            updateSerie(databaseConnection);
        }
        return true;
    }

    public boolean delete(DatabaseConnection databaseConnection) {
        if (databaseConnection.isSerieUsed(mId)) {
            return false;
        }

        SQLiteStatement statement = databaseConnection.getNewStatement(
                "DELETE FROM series WHERE _id = ?;"
        );
        statement.bindDouble(1, mId);

        databaseConnection.executeUpdateQuery(statement);
        return true;
    }

    private void insertSerie(DatabaseConnection databaseConnection) {
        SQLiteStatement statement = databaseConnection.getNewStatement(
                "INSERT INTO series(" +
                        "name, " + // Index: 1
                        "complete " + // Index: 2
                        ")" +
                        "VALUES (?, ?);"
        );
        statement.bindString(1, mName);

        statement.bindDouble(2, 0);
        if (mComplete){
            statement.bindDouble(2, 1);
        }

        mId = databaseConnection.executeInsertQuery(statement);
    }

    private void updateSerie(DatabaseConnection databaseConnection) {
        SQLiteStatement statement = databaseConnection.getNewStatement(
                "UPDATE series SET " +
                        "name = ?, " + // Index: 1
                        "complete = ?, " + // Index: 2
                        "WHERE _id = ?" // Index: 3
        );
        statement.bindDouble(3, mId);
        statement.bindString(1, mName);

        statement.bindDouble(2, 0);
        if (mComplete){
            statement.bindDouble(2, 1);
        }

        databaseConnection.executeUpdateQuery(statement);
    }
}
