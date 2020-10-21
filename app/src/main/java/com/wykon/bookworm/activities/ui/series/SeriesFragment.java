package com.wykon.bookworm.activities.ui.series;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.wykon.bookworm.R;
import com.wykon.bookworm.modules.DatabaseConnection;
import com.wykon.bookworm.modules.Serie;
import com.wykon.bookworm.modules.SerieListAdapter;

import java.util.LinkedList;

public class SeriesFragment extends Fragment implements SearchView.OnQueryTextListener {

    private LinkedList<Serie> mSeries = new LinkedList<>();

    private Context mContext;
    private DatabaseConnection mDatabaseConnection;
    private SerieListAdapter mSerieListAdapter;

    private ListView mSerieListView;

    private View root;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_series, container, false);

        mContext = getActivity().getApplicationContext();;
        mDatabaseConnection = new DatabaseConnection(mContext);

        FloatingActionButton fab = root.findViewById(R.id.fabAdd);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createNewSerieDialog(null);
            }
        });

        mSerieListView = root.findViewById(R.id.lvSeries);
        mSerieListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                Serie serie = (Serie) mSerieListAdapter.getItem(position);
                createNewSerieDialog(serie);
                return false;
            }
        });

        return root;
    }

    public void loadSeries() {
        mSeries = mDatabaseConnection.getSeries();

        mSerieListAdapter = new SerieListAdapter(mContext, mSeries);
        mSerieListView.setAdapter(mSerieListAdapter);
    }

    @Override
    public void onResume() {
        super.onResume();
        loadSeries();
    }

    public void createNewSerieDialog(final Serie serie) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // I'm using fragment here so I'm using getView() to provide ViewGroup
        // but you can provide here any other instance of ViewGroup from your Fragment / Activity
        LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
        View myView = layoutInflater.inflate(R.layout.dialog_new_serie, (ViewGroup) root, false);
        builder.setView(myView);

        // Set up the buttons
        String title = "New serie";
        String positiveText = "Create";
        if (serie != null) {
            title = "Edit serie";
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

        if (serie != null) {
            builder.setNeutralButton("Delete", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (serie.delete(mDatabaseConnection)) {
                        mSeries.remove(serie);
                        mSerieListAdapter.notifyDataSetChanged();
                    }
                    else {
                        Toast.makeText(mContext, "Serie still in use", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }

        final AlertDialog dialog = builder.create();
        dialog.show();

        final EditText etNewSerie = ((AlertDialog) dialog).findViewById(R.id.etNewSerie);
        final CheckBox cbCompleted = ((AlertDialog) dialog).findViewById(R.id.cbCompleted);
        if(serie != null) {
            etNewSerie.setText(serie.getName());
            cbCompleted.setChecked(serie.isCompleted());
        }

        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Boolean newSerieCreated = updateSerie(etNewSerie.getText().toString(), cbCompleted.isChecked(), serie);
                if(newSerieCreated)
                    dialog.dismiss();
            }
        });

    }

    public Boolean updateSerie(String name, Boolean completed, Serie serie) {
        boolean newSerie = false;
        if (serie == null) {
            serie = new Serie();
            newSerie = true;
        }
        serie.setName(name);
        serie.setComplete(completed);
        if(!serie.save(mContext, mDatabaseConnection)) {
            return false;
        }

        if (newSerie) {
            mSeries.add(serie);
        }
        mSerieListAdapter.notifyDataSetChanged();
        return true;
    }

    @Override
    public boolean onQueryTextSubmit(String s) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        mSerieListAdapter.getFilter().filter(newText);
        return false;
    }
}