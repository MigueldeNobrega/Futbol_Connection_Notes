package com.example.futbol_connection_notes;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChatActivity extends AppCompatActivity {

    private EditText editMensaje;
    private Button btnEnviar;
    private RecyclerView recyclerView;
    private ChatAdapter chatAdapter;
    private List<Message> listaMensajes;
    private FirebaseAuth auth;
    private FirebaseFirestore db;
    private ImageView teamLogo;
    private TextView teamName;
    private String foroNombre;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);


        teamLogo = findViewById(R.id.team_logo);
        teamName = findViewById(R.id.team_name);
        editMensaje = findViewById(R.id.editMensaje);
        btnEnviar = findViewById(R.id.btnEnviar);
        recyclerView = findViewById(R.id.recyclerChat);
        Button btnVolverAlMenu = findViewById(R.id.btn_GoToForos);

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Recibir los datos del foro desde el Intent
        Intent intent = getIntent();
        foroNombre = intent.getStringExtra("foroNombre");
        String equipoNombre = intent.getStringExtra("team_name");
        int equipoLogo = intent.getIntExtra("team_logo", R.drawable.placeholder);

        if (foroNombre != null) {
            teamName.setText(equipoNombre);
        } else {
            Toast.makeText(this, "Error: No se pudo obtener el nombre del foro.", Toast.LENGTH_SHORT).show();

        }
        teamLogo.setImageResource(equipoLogo);
        // Cargar la imagen del equipo
        int imageResourceId = intent.getIntExtra("team_logo", 0);
        if (imageResourceId != 0) {
            teamLogo.setImageResource(imageResourceId);
        } else {
            teamLogo.setImageResource(R.drawable.placeholder);
        }


        listaMensajes = new ArrayList<>();
        chatAdapter = new ChatAdapter(listaMensajes);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(chatAdapter);

        // Cargar los mensajes del foro seleccionado
        cargarMensajes();

        // Botón para enviar mensaje
        btnEnviar.setOnClickListener(v -> enviarMensaje());

        // Botón para volver al menú de foros
        btnVolverAlMenu.setOnClickListener(v -> {
            Intent backIntent = new Intent(ChatActivity.this, ForosActivity.class);
            startActivity(backIntent);
            finish();
        });
    }

    private void enviarMensaje() {
        String textoMensaje = editMensaje.getText().toString().trim();
        if (!textoMensaje.isEmpty()) {
            FirebaseUser usuarioActual = auth.getCurrentUser();
            if (usuarioActual != null) {
                String nombreUsuario = usuarioActual.getEmail();
                if (nombreUsuario == null || nombreUsuario.isEmpty()) {
                    nombreUsuario = "Anónimo";
                }

                Map<String, Object> mensaje = new HashMap<>();
                mensaje.put("usuario", nombreUsuario);
                mensaje.put("mensaje", textoMensaje);
                mensaje.put("timestamp", System.currentTimeMillis());

                db.collection("foros")
                        .document(foroNombre)
                        .collection("mensajes")
                        .add(mensaje)
                        .addOnSuccessListener(documentReference -> {
                            Log.d("Firestore", "Mensaje enviado con ID: " + documentReference.getId());
                            editMensaje.setText("");
                        })
                        .addOnFailureListener(e -> {
                            Log.e("Firestore", "Error al enviar mensaje", e);
                            Toast.makeText(ChatActivity.this, "Error al enviar", Toast.LENGTH_SHORT).show();
                        });
            } else {
                Toast.makeText(this, "Debes iniciar sesión", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void cargarMensajes() {
        db.collection("foros")
                .document(foroNombre)
                .collection("mensajes")
                .orderBy("timestamp", Query.Direction.ASCENDING)
                .addSnapshotListener((snapshots, e) -> {
                    if (e != null) {
                        Log.e("Firestore", "Error al recibir mensajes", e);
                        return;
                    }

                    for (DocumentChange dc : snapshots.getDocumentChanges()) {
                        if (dc.getType() == DocumentChange.Type.ADDED) {
                            Message mensaje = dc.getDocument().toObject(Message.class);
                            listaMensajes.add(mensaje);
                        }
                    }

                    chatAdapter.notifyDataSetChanged();
                    recyclerView.scrollToPosition(listaMensajes.size() - 1);
                });
    }
}
