package com.example.futbol_connection_notes;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ImageButton;
import android.widget.PopupMenu;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.Query;

public class MainActivity extends AppCompatActivity {

    FloatingActionButton addNoteBtn;
    RecyclerView recyclerView;
    NoteAdapter noteAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        addNoteBtn = findViewById(R.id.add_note_btn);
        recyclerView = findViewById(R.id.recyler_view);

        addNoteBtn.setOnClickListener((v) -> startActivity(new Intent(MainActivity.this, NoteDetailsActivity.class)));

        setupRecyclerView();

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigation);
        bottomNavigationView.setSelectedItemId(R.id.nav_main);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.nav_main) {
                return true;
            } else if (itemId == R.id.nav_chat) {
                startActivity(new Intent(getApplicationContext(), ForosActivity.class));
                overridePendingTransition(0, 0);
                return true;
            } else if (itemId == R.id.nav_profile) {
                startActivity(new Intent(getApplicationContext(), MyProfileActivity.class));
                overridePendingTransition(0, 0);
                return true;
            }

            return false;
        });
    }

    void setupRecyclerView() {
        Query query = Utility.getCollectionReferenceForNotes().orderBy("timestamp", Query.Direction.DESCENDING);
        FirestoreRecyclerOptions<Note> options = new FirestoreRecyclerOptions.Builder<Note>()
                .setQuery(query, Note.class).build();
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        noteAdapter = new NoteAdapter(options, this);
        recyclerView.setAdapter(noteAdapter);
    }

    @Override
    protected void onStart() {
        super.onStart();
        noteAdapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        noteAdapter.stopListening();
    }

    @Override
    protected void onResume() {
        super.onResume();
        noteAdapter.notifyDataSetChanged();
    }
}
