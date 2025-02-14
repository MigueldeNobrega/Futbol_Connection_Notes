package com.example.futbol_connection_notes;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MyProfileActivity extends AppCompatActivity {

    private TextView textEmail;
    private EditText editNewPassword;
    private Button btnChangePassword, btnLogout;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_my_profile);
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigation);

        bottomNavigationView.setSelectedItemId(R.id.nav_profile);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId(); // Obtener el ID seleccionado

            if (itemId == R.id.nav_profile) {
                return true; // Ya estamos aquí
            } else if (itemId == R.id.nav_main) {
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                overridePendingTransition(0, 0);
                return true;
            } else if (itemId == R.id.nav_chat) {
                startActivity(new Intent(getApplicationContext(), ForosActivity.class));
                overridePendingTransition(0, 0);
                return true;
            }

            return false;
        });

        // Inicializar Firebase Auth
        auth = FirebaseAuth.getInstance();
        FirebaseUser usuarioActual = auth.getCurrentUser();

        // Vincular vistas
        textEmail = findViewById(R.id.textEmail);
        editNewPassword = findViewById(R.id.editNewPassword);
        btnChangePassword = findViewById(R.id.btnChangePassword);
        btnLogout = findViewById(R.id.btnLogout);

        // Mostrar el correo del usuario autenticado
        if (usuarioActual != null) {
            textEmail.setText(usuarioActual.getEmail());
        } else {
            textEmail.setText("No hay usuario autenticado");
        }

        // Botón para cambiar la contraseña
        btnChangePassword.setOnClickListener(v -> cambiarContrasena());

        // Botón para cerrar sesión
        btnLogout.setOnClickListener(v -> cerrarSesion());
    }

    private void cambiarContrasena() {
        String nuevaContrasena = editNewPassword.getText().toString().trim();

        if (TextUtils.isEmpty(nuevaContrasena) || nuevaContrasena.length() < 6) {
            Toast.makeText(this, "La contraseña debe tener al menos 6 caracteres", Toast.LENGTH_SHORT).show();
            return;
        }

        FirebaseUser usuario = auth.getCurrentUser();
        if (usuario != null) {
            usuario.updatePassword(nuevaContrasena)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(MyProfileActivity.this, "Contraseña actualizada", Toast.LENGTH_SHORT).show();
                        editNewPassword.setText("");
                    })
                    .addOnFailureListener(e -> Toast.makeText(MyProfileActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show());
        }
    }

    private void cerrarSesion() {
        auth.signOut();
        Intent intent = new Intent(MyProfileActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish(); // Cierra esta actividad para que no pueda volver atrás
    }
    }
