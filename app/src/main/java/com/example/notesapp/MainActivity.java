package com.example.notesapp;

import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.notesapp.R;
import com.example.notesapp.FormAddUpdateActivity;
import com.example.notesapp.adapter.NoteAdapter;
import com.example.notesapp.db.NoteHelper;
import com.example.notesapp.entity.Note;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.LinkedList;

import static com.example.notesapp.FormAddUpdateActivity.REQUEST_UPDATE;

public class MainActivity extends AppCompatActivity
        implements View.OnClickListener{
    RecyclerView rvNotes;
    ProgressBar progressBar;
    FloatingActionButton fabAdd;

    private LinkedList<Note> list;
    private NoteAdapter adapter;
    private NoteHelper noteHelper;

    private SwipeToDeleteCallback swipeToDeleteCallback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getSupportActionBar().setTitle("Notes");

        rvNotes = (RecyclerView)findViewById(R.id.rv_notes);
        rvNotes.setLayoutManager(new LinearLayoutManager(this));
        rvNotes.setHasFixedSize(true);

        progressBar = (ProgressBar)findViewById(R.id.progressbar);
        fabAdd = (FloatingActionButton)findViewById(R.id.fab_add);
        fabAdd.setOnClickListener(this);

        noteHelper = new NoteHelper(this);
        noteHelper.open();

        list = new LinkedList<>();

        adapter = new NoteAdapter(this);
        adapter.setListNotes(list);
        rvNotes.setAdapter(adapter);

        new LoadNoteAsync().execute();
        
        enableSwipeToDeleteAndUndo();

//        ItemTouchHelper.Callback callback=new SwipeHelper(adapter);
//        ItemTouchHelper helper=new ItemTouchHelper(callback);
//        helper.attachToRecyclerView(rvNotes);

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.fab_add){
            Intent intent = new Intent(MainActivity.this, FormAddUpdateActivity.class);
            startActivityForResult(intent, FormAddUpdateActivity.REQUEST_ADD);
        }
    }

    private class LoadNoteAsync extends AsyncTask<Void, Void, ArrayList<Note>>{
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setVisibility(View.VISIBLE);

            if (list.size() > 0){
                list.clear();
            }
        }

        @Override
        protected ArrayList<Note> doInBackground(Void... voids) {
            return noteHelper.query();
        }

        @Override
        protected void onPostExecute(ArrayList<Note> notes) {
            super.onPostExecute(notes);
            progressBar.setVisibility(View.GONE);

            list.addAll(notes);
            adapter.setListNotes(list);
            adapter.notifyDataSetChanged();

            if (list.size() == 0){
                showSnackbarMessage("No data at this time");
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == FormAddUpdateActivity.REQUEST_ADD){
            if (resultCode == FormAddUpdateActivity.RESULT_ADD){
                new LoadNoteAsync().execute();
                showSnackbarMessage("One item successfully added");
                // rvNotes.getLayoutManager().smoothScrollToPosition(rvNotes, new RecyclerView.State(), 0);
            }
        }
        else if (requestCode == REQUEST_UPDATE) {

            if (resultCode == FormAddUpdateActivity.RESULT_UPDATE) {
                new LoadNoteAsync().execute();
                showSnackbarMessage("One item changed successfully");
                 int position = data.getIntExtra(FormAddUpdateActivity.EXTRA_POSITION, 0);
                 rvNotes.getLayoutManager().smoothScrollToPosition(rvNotes, new RecyclerView.State(), position);
            }

            else if (resultCode == FormAddUpdateActivity.RESULT_DELETE) {
                int position = data.getIntExtra(FormAddUpdateActivity.EXTRA_POSITION, 0);
                list.remove(position);
                adapter.setListNotes(list);
                adapter.notifyDataSetChanged();
                showSnackbarMessage("One item was successfully deleted");
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (noteHelper != null){
            noteHelper.close();
        }
    }

    private void showSnackbarMessage(String message){
        Snackbar.make(rvNotes, message, Snackbar.LENGTH_SHORT).show();
    }


    private void enableSwipeToDeleteAndUndo() {
        SwipeToDeleteCallback swipeToDeleteCallback = new SwipeToDeleteCallback(this) {
            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int i) {


                final int position = viewHolder.getAdapterPosition();
                final Note item = adapter.getData().get(position);

                adapter.removeItem(position);


                Snackbar snackbar = Snackbar
                        .make(rvNotes, "Item was removed from the list.", Snackbar.LENGTH_LONG);
                snackbar.setAction("UNDO", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        adapter.restoreItem(item, position);
                        rvNotes.scrollToPosition(position);
                    }
                });

                snackbar.setActionTextColor(Color.YELLOW);
                snackbar.show();

            }
        };

        ItemTouchHelper itemTouchhelper = new ItemTouchHelper(swipeToDeleteCallback);
        itemTouchhelper.attachToRecyclerView(rvNotes);
    }


}