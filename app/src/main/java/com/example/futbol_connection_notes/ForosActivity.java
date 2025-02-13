package com.example.futbol_connection_notes;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;

public class ForosActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private ForosAdapter foroAdapter;
    private List<Foro> listaForos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_foros);
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigation);

        // Resaltar el ítem correspondiente
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






        recyclerView = findViewById(R.id.recyclerForos);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        listaForos = new ArrayList<>();

        // Añadir foros predefinidos
        listaForos.add(new Foro("Alavés", R.drawable.ala_logo));
        listaForos.add(new Foro("Athletic Club", R.drawable.ath_logo));
        listaForos.add(new Foro("Atlético de Madrid", R.drawable.atm_logo));
        listaForos.add(new Foro("FC Barcelona", R.drawable.fcb_logo));
        listaForos.add(new Foro("Real Betis", R.drawable.bet_logo));
        listaForos.add(new Foro("CD Leganés", R.drawable.leg_logo_logo));
        listaForos.add(new Foro("Celta de Vigo", R.drawable.cel_logo));
        listaForos.add(new Foro("Las Palmas", R.drawable.lpa_logo));
        listaForos.add(new Foro("RCD Espanyol", R.drawable.esp_logo));
        listaForos.add(new Foro("Getafe CF", R.drawable.geta_logo));
        listaForos.add(new Foro("Girona FC", R.drawable.gir_logo));
        listaForos.add(new Foro("Osasuna", R.drawable.osa_logo));
        listaForos.add(new Foro("Real Madrid", R.drawable.rma_logo));
        listaForos.add(new Foro("Real Sociedad", R.drawable.rso_logo));
        listaForos.add(new Foro("Rayo Vallecano", R.drawable.ray_logo));
        listaForos.add(new Foro("Sevilla FC", R.drawable.sev_logo));
        listaForos.add(new Foro("Valencia CF", R.drawable.vale_logo));
        listaForos.add(new Foro("Real Valladolid", R.drawable.val_logo));
        listaForos.add(new Foro("Villarreal CF", R.drawable.vill_logo));
        listaForos.add(new Foro("RCD Mallorca", R.drawable.mll_logo));

        // Añadir más foros según lo necesites

        foroAdapter = new ForosAdapter(listaForos);
        recyclerView.setAdapter(foroAdapter);
    }
}
