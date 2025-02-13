package com.example.futbol_connection_notes;

import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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

    private String foroNombre;  // Aquí almacenaremos el nombre del foro seleccionado

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        // Recibir el nombre del foro que fue seleccionado en la ForosActivity
        foroNombre = getIntent().getStringExtra("foroNombre");

        editMensaje = findViewById(R.id.editMensaje);
        btnEnviar = findViewById(R.id.btnEnviar);
        recyclerView = findViewById(R.id.recyclerChat);

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Configurar RecyclerView
        listaMensajes = new ArrayList<>();
        chatAdapter = new ChatAdapter(listaMensajes);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(chatAdapter);

        // Cargar los mensajes del foro seleccionado
        cargarMensajes();

        // Botón para enviar mensaje
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

                // Guardar en Firestore en la subcolección del foro seleccionado
                db.collection("foros")
                        .document(foroNombre)  // Referencia al foro específico
                        .collection("mensajes")
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
        // Cargar los mensajes del foro seleccionado desde Firestore
        db.collection("foros")
                .document(foroNombre)  // Seleccionamos el foro por nombre
                .collection("mensajes")
                .orderBy("timestamp", Query.Direction.ASCENDING)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(QuerySnapshot snapshots, FirebaseFirestoreException e) {
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
                        recyclerView.scrollToPosition(listaMensajes.size() - 1); // Auto-scroll al último mensaje
                    }
                });
    }
}
