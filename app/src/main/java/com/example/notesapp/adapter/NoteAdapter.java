package com.example.notesapp.adapter;

import android.app.Activity;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.notesapp.CustomOnItemClickListener;
import com.example.notesapp.R;
import com.example.notesapp.entity.Note;
import com.example.notesapp.FormAddUpdateActivity;


import java.util.LinkedList;


public class NoteAdapter extends RecyclerView.Adapter<NoteAdapter.NoteViewholder> {
    private LinkedList<Note> listNotes;
    private Activity activity;

    public NoteAdapter(Activity activity) {
        this.activity = activity;
    }

    public LinkedList<Note> getListNotes() {
        return listNotes;
    }

    public void setListNotes(LinkedList<Note> listNotes) {
        this.listNotes = listNotes;
    }

    @Override
    public NoteViewholder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_note, parent, false);
        return new NoteViewholder(view);
    }

    @Override
    public void onBindViewHolder(NoteViewholder holder, int position) {
        holder.tvTitle.setText(getListNotes().get(position).getTitle());
        holder.tvDate.setText(getListNotes().get(position).getDate());
        holder.tvDescription.setText(getListNotes().get(position).getDescription());
        holder.cvNote.setOnClickListener(new CustomOnItemClickListener(position, new CustomOnItemClickListener.OnItemClickCallback() {
            @Override
            public void onItemClicked(View view, int position) {
                Intent intent = new Intent(activity, FormAddUpdateActivity.class);
                intent.putExtra(FormAddUpdateActivity.EXTRA_POSITION, position);
                intent.putExtra(FormAddUpdateActivity.EXTRA_NOTE, getListNotes().get(position));
                activity.startActivityForResult(intent, FormAddUpdateActivity.REQUEST_UPDATE);
            }
        }));
    }

    @Override
    public int getItemCount() {
        return getListNotes().size();
    }

//    public void restoreItem(Note item, int position) {
//        listNotes.add(position, item);
//        notifyItemInserted(position);
//    }
//
//    public void removeItem(int position) {
//        listNotes.remove(position);
//        notifyItemRemoved(position);
//    }

    public LinkedList<Note> getData() {
        return listNotes;
    }

    public void restoreItem(Note item, int position) {
        listNotes.add(position, item);
        notifyItemInserted(position);
    }

    public void removeItem(int position) {
        listNotes.remove(position);
        notifyItemRemoved(position);
    }


    public class NoteViewholder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvDescription, tvDate;
        CardView cvNote;

        public NoteViewholder(View itemView) {
            super(itemView);
            tvTitle = (TextView) itemView.findViewById(R.id.tv_item_title);
            tvDescription = (TextView) itemView.findViewById(R.id.tv_item_description);
            tvDate = (TextView) itemView.findViewById(R.id.tv_item_date);
            cvNote = (CardView) itemView.findViewById(R.id.cv_item_note);
        }
    }
}