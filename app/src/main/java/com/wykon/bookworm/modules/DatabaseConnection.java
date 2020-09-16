package com.wykon.bookworm.modules;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteStatement;

import androidx.annotation.Nullable;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by WykonGoar on 11-09-2020.
 */
public class DatabaseConnection extends Activity {

    SQLiteDatabase mDatabase;
    Context mContext;

    public DatabaseConnection(Context context){
        mContext = context;

        Cursor cursor = null;
        try {
            String check = "SELECT version FROM db_version";

            executeReturn(check);

        }
        catch (SQLiteException ex) {
            updateDatabase(0);
            return;
        }

        String check = "SELECT version FROM db_version";

        cursor = executeReturn(check);

        cursor.moveToFirst();

        if(cursor.isAfterLast()) {
            updateDatabase(0);
        }
        int version = cursor.getInt(cursor.getColumnIndex("version"));

        updateDatabase(version);
    }

    private void updateDatabase(int lastVersion) {
        List<String> filesNames;
        try {
            filesNames = Arrays.asList(mContext.getAssets().list("database"));
        }
        catch (IOException ex) {
            throw new Error(ex.getMessage());
        }

        HashMap<Integer, List<String>> sqlStatements = new HashMap<>();
        int versionCounter = lastVersion + 1;
        while (filesNames.contains(String.format("v%d.sql", versionCounter))) {
            sqlStatements.put(versionCounter, readSQLFile(String.format("v%d.sql", versionCounter)));
            versionCounter ++;
        }

        int updateVersion = lastVersion + 1;
        while (sqlStatements.containsKey(updateVersion)) {
            List<String> statements = sqlStatements.get(updateVersion);

            for (int i = 0; i < statements.size(); i++) {
                String statement = statements.get(i);

                System.out.println("Execute: " + statement);

                if (statement.startsWith("INSERT")) {
                    executeInsertQuery(getNewStatement(statement));
                } else {
                    executeNonReturn(statement);
                }
            }

            //Update version
            if (1 == updateVersion) {
                executeInsertQuery(getNewStatement(
                    String.format("INSERT INTO db_version VALUES (%d);", updateVersion)
                ));
            }
            else {
                executeNonReturn(String.format("UPDATE db_version SET version = %d;", updateVersion));
            }

            updateVersion ++;
        }
    }

    private List<String> readSQLFile(String fileName) {
        String fileContent = null;

        try {
            InputStream mInputStream = mContext.getAssets().open("database/" + fileName);

            BufferedReader reader = new BufferedReader(new InputStreamReader(mInputStream));

            String line;
            while ((line = reader.readLine()) != null) {
                line = line.replaceAll("\n", "");
                line = line.replaceAll("\r", "");

                if (fileContent == null) {
                    fileContent = line;
                }
                else {
                    fileContent += " " + line;
                }
            }
        } catch (IOException ex){
            throw new Error(ex.toString());
        }

        ArrayList<String> sqlStatements = new ArrayList<>();
        if (fileContent == null) {
            return sqlStatements;
        }

        for (String query : fileContent.split(";")) {
            sqlStatements.add(query + ";");
        }

        return sqlStatements;
    }

    private void createConnection(){
        mDatabase = mContext.openOrCreateDatabase("bookworm", MODE_PRIVATE, null);
    }

    public SQLiteStatement getNewStatement(String query){
        createConnection();
        return mDatabase.compileStatement(query);
    }

    public void executeNonReturn(String query) throws SQLiteException {
        createConnection();
        mDatabase.execSQL(query);
        mDatabase.close();
    }

    public void executeNonReturn(SQLiteStatement statement){
        statement.execute();
        mDatabase.close();
    }

    public int executeInsertQuery(SQLiteStatement statement){
        int result = (int) statement.executeInsert();
        mDatabase.close();
        return result;
    }

    public int executeUpdateQuery(SQLiteStatement statement){
        int result = statement.executeUpdateDelete();
        mDatabase.close();
        return result;
    }

    public Cursor executeReturn(String query) throws SQLiteException {
        createConnection();
        Cursor mCursor = mDatabase.rawQuery(query, null);
        return mCursor;
    }

    public LinkedList<Genre> getGenres(){
        Cursor mCursor = null;
        try {
            mCursor = executeReturn("SELECT * FROM genres ORDER BY name;");
        } catch (SQLiteException e) {
            e.printStackTrace();
            return new LinkedList<>();
        }

        mCursor.moveToFirst();

        LinkedList<Genre> genres = new LinkedList<>();

        while(!mCursor.isAfterLast()){
            int id = mCursor.getInt(mCursor.getColumnIndex("_id"));
            String name = mCursor.getString(mCursor.getColumnIndex("name"));

            Genre genre = new Genre(id, name);
            genres.add(genre);

            mCursor.moveToNext();
        }

        mDatabase.close();
        return genres;
    }

    public Boolean genreExists(String name, int currentId) {
        String query = String.format("SELECT COUNT(_id) AS count FROM genres WHERE LOWER(name) = '%s';", name.toLowerCase());
        if (-1 != currentId) {
            query = String.format("SELECT COUNT(_id) AS count FROM genres WHERE LOWER(name) = '%s' AND _id != %d; ", name.toLowerCase(), currentId);
        }

        Cursor mCursor = null;
        try {
            mCursor = executeReturn(query);
        } catch (SQLiteException e) {
            e.printStackTrace();
            return false;
        }

        mCursor.moveToFirst();
        int count = mCursor.getInt(mCursor.getColumnIndex("count"));

        return count != 0;
    }

    public Boolean isGenreUsed(int genreId) {
        Cursor mCursor = null;
        try {
            mCursor = executeReturn(String.format("SELECT COUNT(_id) AS count FROM books where genre = %d;", genreId));
        } catch (SQLiteException e) {
            e.printStackTrace();
            return false;
        }

        mCursor.moveToFirst();
        int count = mCursor.getInt(mCursor.getColumnIndex("count"));

        return count != 0;
    }

    public LinkedList<Serie> getSeries(){
        Cursor mCursor = null;
        try {
            mCursor = executeReturn("SELECT * FROM series ORDER BY name;");
        } catch (SQLiteException e) {
            e.printStackTrace();
            return new LinkedList<>();
        }

        mCursor.moveToFirst();

        LinkedList<Serie> series = new LinkedList<>();

        while(!mCursor.isAfterLast()){
            int id = mCursor.getInt(mCursor.getColumnIndex("_id"));
            String name = mCursor.getString(mCursor.getColumnIndex("name"));
            int iComplete = mCursor.getInt(mCursor.getColumnIndex("complete"));

            Serie serie = new Serie(id, name);
            serie.setComplete(iComplete == 1);
            series.add(serie);

            mCursor.moveToNext();
        }

        mDatabase.close();
        return series;
    }

    public Boolean serieExists(String name, int currentId) {
        String query = String.format("SELECT COUNT(_id) AS count FROM series WHERE LOWER(name) = '%s';", name.toLowerCase());
        if (-1 != currentId) {
            query = String.format("SELECT COUNT(_id) AS count FROM series WHERE LOWER(name) = '%s' AND _id != %d; ", name.toLowerCase(), currentId);
        }

        Cursor mCursor = null;
        try {
            mCursor = executeReturn(query);
        } catch (SQLiteException e) {
            e.printStackTrace();
            return false;
        }

        mCursor.moveToFirst();
        int count = mCursor.getInt(mCursor.getColumnIndex("count"));

        return count != 0;
    }

    public Boolean isSerieUsed(int serieId) {
        Cursor mCursor = null;
        try {
            mCursor = executeReturn(String.format("SELECT COUNT(_id) AS count FROM books where serie = %d;", serieId));
        } catch (SQLiteException e) {
            e.printStackTrace();
            return false;
        }

        mCursor.moveToFirst();
        int count = mCursor.getInt(mCursor.getColumnIndex("count"));

        return count != 0;
    }

    public LinkedList<Book> getBooks() {
        return _getBooks(-1);
    }

    public @Nullable Book getBook(int bookId) {
        LinkedList<Book> books = _getBooks(bookId);

        if(books.size() == 0) {
            throw new IndexOutOfBoundsException(String.format("Book with id '%d' not found.", bookId));
        }

        return books.getFirst();
    }

    public LinkedList<Book> _getBooks(int bookId) {
        Cursor mCursor = null;

        String query = "SELECT * FROM books ORDER BY title";
        if (bookId != -1) {
            query = String.format("SELECT * FROM books WHERE _id = %d ORDER BY LOWER(title)", bookId);
        }

        try {
            mCursor = executeReturn(query);
        } catch (SQLiteException e) {
            e.printStackTrace();
            return new LinkedList<>();
        }

        Map<Integer, Genre> mappedGenres = new HashMap<>();
        for (Genre genre : getGenres()){
            mappedGenres.put(genre.getId(), genre);
        }
        Map<Integer, Serie> mappedSeries = new HashMap<>();
        for (Serie serie : getSeries()){
            mappedSeries.put(serie.getId(), serie);
        }

        mCursor.moveToFirst();

        LinkedList<Book> books = new LinkedList<>();

        while(!mCursor.isAfterLast()){
            int id = mCursor.getInt(mCursor.getColumnIndex("_id"));
            String author_last_name = mCursor.getString(mCursor.getColumnIndex("author_last_name"));
            String title = mCursor.getString(mCursor.getColumnIndex("title"));
            float rating = mCursor.getFloat(mCursor.getColumnIndex("rating"));
            double bookNumber = mCursor.getDouble(mCursor.getColumnIndex("book_number"));

            Book book = new Book(id, author_last_name, title, rating, bookNumber);

            if (!mCursor.isNull(mCursor.getColumnIndex("author_first_name"))) {
                book.setAuthorFirstName(mCursor.getString(mCursor.getColumnIndex("author_first_name")));
            }
            if (!mCursor.isNull(mCursor.getColumnIndex("description"))) {
                book.setDescription(mCursor.getString(mCursor.getColumnIndex("description")));
            }
            if (!mCursor.isNull(mCursor.getColumnIndex("url"))) {
                book.setUrl(mCursor.getString(mCursor.getColumnIndex("url")));
            }
            if (!mCursor.isNull(mCursor.getColumnIndex("serie"))) {
                int serieId = mCursor.getInt(mCursor.getColumnIndex("serie"));
                book.setSerie(mappedSeries.get(serieId));
            }
            if (!mCursor.isNull(mCursor.getColumnIndex("genre"))) {
                int genreId = mCursor.getInt(mCursor.getColumnIndex("genre"));
                book.setGenre(mappedGenres.get(genreId));
            }

            books.add(book);
            mCursor.moveToNext();
        }

        mDatabase.close();
        return books;
    }

    public Boolean bookExists(String name, String authorLastName, int currentId) {
        String query = String.format("SELECT COUNT(_id) AS count FROM books WHERE LOWER(title) = '%s' AND LOWER(author_last_name) = '%s';", name.toLowerCase(), authorLastName.toLowerCase());
        if (-1 != currentId) {
            query = String.format("SELECT COUNT(_id) AS count FROM books WHERE LOWER(title) = '%s' AND LOWER(author_last_name) = '%s' AND _id != %d; ", name.toLowerCase(), authorLastName.toLowerCase(), currentId);
        }

        Cursor mCursor = null;
        try {
            mCursor = executeReturn(query);
        } catch (SQLiteException e) {
            e.printStackTrace();
            return false;
        }

        mCursor.moveToFirst();
        int count = mCursor.getInt(mCursor.getColumnIndex("count"));

        return count != 0;
    }
}

