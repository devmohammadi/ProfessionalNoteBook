package com.mohammadi.dashti.professionalnotebook.adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.mohammadi.dashti.professionalnotebook.R;
import com.mohammadi.dashti.professionalnotebook.activity.CreateNoteActivity;
import com.mohammadi.dashti.professionalnotebook.activity.MainActivity;
import com.mohammadi.dashti.professionalnotebook.model.Note;

import java.util.List;

import saman.zamani.persiandate.PersianDate;
import saman.zamani.persiandate.PersianDateFormat;

public class NoteAdapter extends RecyclerView.Adapter<NoteAdapter.ViewHolder> {

    private Context mContext;
    private List<Note> mNote;


    private OnRecyclerItemClick onRecyclerItemClick;
    private OnRecyclerItemClickDelete OnRecyclerItemClickDelete;

    public NoteAdapter(Context mContext, List<Note> mNote) {
        this.mContext = mContext;
        this.mNote = mNote;
    }

    public interface OnRecyclerItemClick {
        void onClick(Note note);
    }

    public interface OnRecyclerItemClickDelete {
        void onClickDelete(Note note, int pos);
    }

    public void setOnRecyclerItemClick(OnRecyclerItemClick onRecyclerItemClick) {
        this.onRecyclerItemClick = onRecyclerItemClick;
    }

    public void setOnRecyclerItemClickDelete(OnRecyclerItemClickDelete OnRecyclerItemClickDelete) {
        this.OnRecyclerItemClickDelete = OnRecyclerItemClickDelete;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(mContext).inflate(R.layout.note_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.title.setText(mNote.get(position).getTitle());
        holder.note.setText(mNote.get(position).getNote());
        PersianDate date = new PersianDate(mNote.get(position).getTime());
        String time = date.getHour() + ":" + date.getMinute() + "  " + date.getShYear() + "/" + date.getShMonth() + "/" + date.getShDay();
        holder.time.setText(time);

        switch (mNote.get(position).getCategory()) {
            case "Programming":
                holder.view.setBackgroundColor(mContext.getResources().getColor(R.color.color1));
                break;
            case "Cleaning":
                holder.view.setBackgroundColor(mContext.getResources().getColor(R.color.color2));
                break;
            case "Lesson":
                holder.view.setBackgroundColor(mContext.getResources().getColor(R.color.color3));
                break;
            case "Movie":
                holder.view.setBackgroundColor(mContext.getResources().getColor(R.color.color4));
                break;
            case "Music":
                holder.view.setBackgroundColor(mContext.getResources().getColor(R.color.color5));
                break;
            case "Buy":
                holder.view.setBackgroundColor(mContext.getResources().getColor(R.color.color6));
                break;
            case "Other":
                holder.view.setBackgroundColor(mContext.getResources().getColor(R.color.color7));
                break;
            default:
                holder.view.setBackgroundColor(mContext.getResources().getColor(R.color.color8));
                break;
        }
    }

    @Override
    public int getItemCount() {
        return mNote.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView delete;
        private TextView edit;

        private TextView title;
        private TextView note;
        private TextView time;

        private View view;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            delete = itemView.findViewById(R.id.tvDelete);
            edit = itemView.findViewById(R.id.tvEdit);

            title = itemView.findViewById(R.id.tvTitle);
            note = itemView.findViewById(R.id.tvNote);
            time = itemView.findViewById(R.id.tvTime);

            view = itemView.findViewById(R.id.view);

            itemView.setOnClickListener(itemViewClick -> getNotePosition());

            delete.setOnClickListener(itemDelete -> getNotePositionDelete());

            edit.setOnClickListener(itemEdit -> getNotePosition());

        }

        private void getNotePosition() {
            if (onRecyclerItemClick != null) {
                int pos = getAdapterPosition();
                if (pos != RecyclerView.NO_POSITION) {
                    onRecyclerItemClick.onClick(mNote.get(pos));
                }
            }
        }

        private void getNotePositionDelete() {
            if (OnRecyclerItemClickDelete != null) {
                int pos = getAdapterPosition();
                if (pos != RecyclerView.NO_POSITION) {
                    OnRecyclerItemClickDelete.onClickDelete(mNote.get(getAdapterPosition()), pos);
                }
            }
        }

    }

}
