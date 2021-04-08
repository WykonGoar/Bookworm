package com.wykon.bookworm.activities.ui.transfer;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.opencsv.CSVReader;
import com.wykon.bookworm.R;
import com.wykon.bookworm.modules.DatabaseConnection;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

public class ImportFragment extends Fragment {

    // A request code's purpose is to match the result of a "startActivityForResult" with
    // the type of the original request.  Choose any value.
    private static final int READ_REQUEST_CODE = 1337;

    private Button bImport;
    private TextView tvLog;

    private View root;
    private Context mContext;
    private DatabaseConnection mDatabaseConnection;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_import, container, false);

        setHasOptionsMenu(true);

        mContext = getActivity().getApplicationContext();
        mDatabaseConnection = new DatabaseConnection(mContext);

        bImport = root.findViewById(R.id.bImport);
        bImport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // BEGIN_INCLUDE (use_open_document_intent)
                // ACTION_OPEN_DOCUMENT is the intent to choose a file via the system's file browser.
                Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);

                // Filter to only show results that can be "opened", such as a file (as opposed to a list
                // of contacts or timezones)
                intent.addCategory(Intent.CATEGORY_OPENABLE);

                // Filter to show only images, using the image MIME data type.
                // If one wanted to search for ogg vorbis files, the type would be "audio/ogg".
                // To search for all documents available via installed storage providers, it would be
                // "*/*".
                intent.setType("*/*");

                startActivityForResult(Intent.createChooser(intent, "Open CSV"), READ_REQUEST_CODE);
                // END_INCLUDE (use_open_document_intent)
            }
        });

        tvLog = root.findViewById(R.id.tvLog);

        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent resultData) {
        System.out.println("Received an \"Activity Result\"");
        // BEGIN_INCLUDE (parse_open_document_response)
        // The ACTION_OPEN_DOCUMENT intent was sent with the request code READ_REQUEST_CODE.
        // If the request code seen here doesn't match, it's the response to some other intent,
        // and the below code shouldn't run at all.

        if (requestCode == READ_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            // The document selected by the user won't be returned in the intent.
            // Instead, a URI to that document will be contained in the return intent
            // provided to this method as a parameter.  Pull that uri using "resultData.getData()"
            Uri uri = null;
            if (resultData != null) {
                uri = resultData.getData();
                String path = uri.getPath();
                tvLog.append("Uri: " + uri.toString() + "\n");
                tvLog.append("Path: " + path + "\n");

                // When internal storage is used.
                String newPath = path.substring(path.indexOf(":") + 1);
                tvLog.append("newPath: " + newPath + "\n");

                readFile(new File(newPath));
            }
            // END_INCLUDE (parse_open_document_response)
        }
    }

    private void readFile(File csvFile) {
        List<String[]> entries;

        try {
            CSVReader reader = new CSVReader(new FileReader(csvFile));
            entries = reader.readAll();
            reader.close();
        } catch (IOException e) {
            tvLog.append(e.getMessage() + "\n");
            return;
        }

        for (String[] entry: entries) {
            tvLog.append(entry.toString() + "\n");
        }
    }
}
