package com.example.futbol_connection_notes;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.annotations.Nullable;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
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
    private FirebaseFirestore db;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigation);
        bottomNavigationView.setSelectedItemId(R.id.nav_chat);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId(); // Obtener el ID seleccionado

            if (itemId == R.id.nav_chat) {
                return true; // Ya estamos aquí
            } else if (itemId == R.id.nav_main) {
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                overridePendingTransition(0, 0);
                return true;
            } else if (itemId == R.id.nav_profile) {
                startActivity(new Intent(getApplicationContext(), MyProfileActivity.class));
                overridePendingTransition(0, 0);
                return true;
            }

            return false;
        });

        // Inicializar Firebase
        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Vincular elementos del layout
        editMensaje = findViewById(R.id.editMensaje);
        btnEnviar = findViewById(R.id.btnEnviar);
        recyclerView = findViewById(R.id.recyclerChat);

        // Configurar RecyclerView
        listaMensajes = new ArrayList<>();
        chatAdapter = new ChatAdapter(listaMensajes);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(chatAdapter);

        // Cargar mensajes en tiempo real
        cargarMensajes();

        // Botón enviar
        btnEnviar.setOnClickListener(v -> enviarMensaje());
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

                // Crear objeto mensaje
                Map<String, Object> mensaje = new HashMap<>();
                mensaje.put("usuario", nombreUsuario);
                mensaje.put("mensaje", textoMensaje);
                mensaje.put("timestamp", System.currentTimeMillis());

                // Guardar en Firestore
                db.collection("chat_global")
                        .add(mensaje)
                        .addOnSuccessListener(documentReference -> {
                            Log.d("Firestore", "Mensaje enviado con ID: " + documentReference.getId());
                            editMensaje.setText(""); // Limpiar input después de enviar
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
        db.collection("chat_global")
                .orderBy("timestamp", Query.Direction.ASCENDING)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot snapshots, @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            Log.e("Firestore", "Error al recibir mensajes", e);
                            return;
                        }

                        for (DocumentChange dc : snapshots.getDocumentChanges()) {
                            if (dc.getType() == DocumentChange.Type.ADDED) {
                                QueryDocumentSnapshot doc = dc.getDocument();
                                Message mensaje = new Message(
                                        doc.getString("usuario"),
                                        doc.getString("mensaje")
                                );
                                listaMensajes.add(mensaje);
                            }
                        }
                        chatAdapter.notifyDataSetChanged();
                        recyclerView.scrollToPosition(listaMensajes.size() - 1); // Auto-scroll al último mensaje
                    }
                });
    }
}
