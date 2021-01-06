package com.wykon.bookworm.activities.ui.genres;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.wykon.bookworm.R;
import com.wykon.bookworm.modules.DatabaseConnection;
import com.wykon.bookworm.modules.Genre;
import com.wykon.bookworm.modules.GenreListAdapter;
import com.wykon.bookworm.modules.Serie;
import com.wykon.bookworm.modules.SerieListAdapter;

import java.util.LinkedList;

public class GenresFragment extends Fragment implements SearchView.OnQueryTextListener {

    private LinkedList<Genre> mGenres = new LinkedList<>();

    private Context mContext;
    private DatabaseConnection mDatabaseConnection;
    private GenreListAdapter mGenreListAdapter;

    private ListView mGenreListView;

    private View root;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_genres, container, false);

        mContext = getActivity().getApplicationContext();;
        mDatabaseConnection = new DatabaseConnection(mContext);

        FloatingActionButton fab = root.findViewById(R.id.fabAdd);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createNewGenreDialog(null);
            }
        });

        mGenreListView = root.findViewById(R.id.lvGenres);
        mGenreListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                Genre genre = (Genre) mGenreListAdapter.getItem(position);
                createNewGenreDialog(genre);
                return false;
            }
        });

        return root;
    }

    public void loadGenres() {
        mGenres = mDatabaseConnection.getGenres();

        mGenreListAdapter = new GenreListAdapter(mContext, mGenres);
        mGenreListView.setAdapter(mGenreListAdapter);
    }

    @Override
    public void onResume() {
        super.onResume();
        loadGenres();
    }

    public void createNewGenreDialog(final Genre genre) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // I'm using fragment here so I'm using getView() to provide ViewGroup
        // but you can provide here any other instance of ViewGroup from your Fragment / Activity
        LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
        View myView = layoutInflater.inflate(R.layout.dialog_new_genre, (ViewGroup) root, false);
        builder.setView(myView);

        // Set up the buttons
        String title = "New genre";
        String positiveText = "Create";
        if (genre != null) {
            title = "Edit genre";
            positiveText = "Save";
        }

        builder.setTitle(title);

        builder.setPositiveButton(positiveText, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        if (genre != null) {
            builder.setNeutralButton("Delete", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (genre.delete(mDatabaseConnection)) {
                        mGenres.remove(genre);
                        mGenreListAdapter.notifyDataSetChanged();
                    }
                    else {
                        Toast.makeText(mContext, "Genre still in use", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }

        final AlertDialog dialog = builder.create();
        dialog.show();

        final EditText etNewGenre = ((AlertDialog) dialog).findViewById(R.id.etNewGenre);
        if (genre != null) {
            etNewGenre.setText(genre.getName());
        }

        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Boolean newGenreCreated = updateGenre(etNewGenre.getText().toString(), genre);
                if(newGenreCreated)
                    dialog.dismiss();
            }
        });

    }

    public Boolean updateGenre(String name, Genre genre) {
        boolean newGenre = false;
        if (genre == null) {
            genre = new Genre();
            newGenre = true;
        }
        genre.setName(name);

        if (genre.getName().isEmpty()) {
            Toast.makeText(mContext, "Name can not be empty", Toast.LENGTH_SHORT).show();
            return false;
        }

        if(!genre.save(mContext, mDatabaseConnection)) {
            return false;
        }

        if (newGenre) {
            mGenres.add(genre);
        }
        mGenreListAdapter.notifyDataSetChanged();
        return true;
    }

    @Override
    public boolean onQueryTextSubmit(String s) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        mGenreListAdapter.getFilter().filter(newText);
        return false;
    }
}