package com.example.futbol_connection_notes;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;

public class NoteDetailsActivity extends AppCompatActivity {
    EditText localTeamEditText, visitorTeamEditText, localGoalsEditText, visitorGoalsEditText, descriptionEditText;
    ImageButton saveNoteBtn;
    TextView pageTitleTextView, deleteNoteTextViewBtn;
    String docId;
    boolean isEditMode = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_details);

        localTeamEditText = findViewById(R.id.notes_local_team);
        visitorTeamEditText = findViewById(R.id.notes_visitor_team);
        localGoalsEditText = findViewById(R.id.notes_local_goals);
        visitorGoalsEditText = findViewById(R.id.notes_visitor_goals);
        descriptionEditText = findViewById(R.id.notes_description);
        saveNoteBtn = findViewById(R.id.save_note_btn);
        pageTitleTextView = findViewById(R.id.page_title);
        deleteNoteTextViewBtn = findViewById(R.id.delete_note_text_view_btn);

        // Recibir datos
        docId = getIntent().getStringExtra("docId");
        if (docId != null && !docId.isEmpty()) {
            isEditMode = true;
            localTeamEditText.setText(getIntent().getStringExtra("localTeam"));
            visitorTeamEditText.setText(getIntent().getStringExtra("visitorTeam"));
            localGoalsEditText.setText(String.valueOf(getIntent().getIntExtra("localGoals", 0)));
            visitorGoalsEditText.setText(String.valueOf(getIntent().getIntExtra("visitorGoals", 0)));
            descriptionEditText.setText(getIntent().getStringExtra("description"));
            pageTitleTextView.setText("Editar Partido");
            deleteNoteTextViewBtn.setVisibility(View.VISIBLE);
        }

        saveNoteBtn.setOnClickListener((v) -> saveNote());
        deleteNoteTextViewBtn.setOnClickListener((v) -> deleteNoteFromFirebase());
    }

    void saveNote() {
        String localTeam = localTeamEditText.getText().toString();
        String visitorTeam = visitorTeamEditText.getText().toString();
        int localGoals = Integer.parseInt(localGoalsEditText.getText().toString());
        int visitorGoals = Integer.parseInt(visitorGoalsEditText.getText().toString());
        String description = descriptionEditText.getText().toString();

        if (localTeam.isEmpty()) {
            localTeamEditText.setError("El equipo local es obligatorio");
            return;
        }
        if (visitorTeam.isEmpty()) {
            visitorTeamEditText.setError("El equipo visitante es obligatorio");
            return;
        }

        Note note = new Note();
        note.setLocalTeam(localTeam);
        note.setVisitorTeam(visitorTeam);
        note.setLocalGoals(localGoals);
        note.setVisitorGoals(visitorGoals);
        note.setDescription(description);
        note.setTimestamp(Timestamp.now());

        saveNoteToFirebase(note);
    }

    void saveNoteToFirebase(Note note) {
        DocumentReference documentReference;
        if (isEditMode) {
            documentReference = Utility.getCollectionReferenceForNotes().document(docId);
        } else {
            documentReference = Utility.getCollectionReferenceForNotes().document();
        }
        documentReference.set(note).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Utility.showToast(NoteDetailsActivity.this, "Partido guardado exitosamente");
                    finish();
                } else {
                    Utility.showToast(NoteDetailsActivity.this, "Error al guardar partido: " + task.getException().getMessage());
                }
            }
        });
    }

    void deleteNoteFromFirebase() {
        DocumentReference documentReference = Utility.getCollectionReferenceForNotes().document(docId);
        documentReference.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Utility.showToast(NoteDetailsActivity.this, "Partido eliminado");
                    finish();
                } else {
                    Utility.showToast(NoteDetailsActivity.this, "Error al eliminar partido");
                }
            }
        });
    }
}
