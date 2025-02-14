package com.example.futbol_connection_notes;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

public class NoteAdapter extends FirestoreRecyclerAdapter<Note, NoteAdapter.NoteViewHolder> {
    Context context;

    public NoteAdapter(@NonNull FirestoreRecyclerOptions<Note> options, Context context) {
        super(options);
        this.context = context;
    }

    @Override
    protected void onBindViewHolder(@NonNull NoteViewHolder holder, int position, @NonNull Note note) {
        holder.localTeamTextView.setText(note.localTeam);
        holder.visitorTeamTextView.setText(note.visitorTeam);
        holder.scoreTextView.setText(note.localGoals + " - " + note.visitorGoals);
        holder.timestampTextView.setText(Utility.timestampToString(note.timestamp));

        holder.itemView.setOnClickListener((v) -> {
            Intent intent = new Intent(context, NoteDetailsActivity.class);
            intent.putExtra("localTeam", note.localTeam);
            intent.putExtra("visitorTeam", note.visitorTeam);
            intent.putExtra("localGoals", note.localGoals);
            intent.putExtra("visitorGoals", note.visitorGoals);
            intent.putExtra("description", note.description);
            String docId = this.getSnapshots().getSnapshot(position).getId();
            intent.putExtra("docId", docId);
            context.startActivity(intent);
        });
    }

    @NonNull
    @Override
    public NoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_note_item, parent, false);
        return new NoteViewHolder(view);
    }

    class NoteViewHolder extends RecyclerView.ViewHolder {
        TextView localTeamTextView, visitorTeamTextView, scoreTextView, timestampTextView;

        public NoteViewHolder(@NonNull View itemView) {
            super(itemView);
            localTeamTextView = itemView.findViewById(R.id.note_local_team);
            visitorTeamTextView = itemView.findViewById(R.id.note_visitor_team);
            scoreTextView = itemView.findViewById(R.id.note_score);
            timestampTextView = itemView.findViewById(R.id.note_timestamp_text_view);
        }
    }
}
