package com.example.futbol_connection_notes;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoadScreenSplash extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_load_screen_splash);

        new Handler().postDelayed(new Runnable() { //Ejecuta la funcion 1seg después.
            @Override
            public void run() {
                //Verifica si hay una sesion iniciada, si la hay entra en la mainActivity, si no, lleva al login.
                FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                if(currentUser==null){
                    startActivity(new Intent(LoadScreenSplash.this,LoginActivity.class));
                }else{
                    startActivity(new Intent(LoadScreenSplash.this,MainActivity.class));
                }
                finish();//Termina la actividad y no puede volver con botones de retroceso.
            }
        },1000); //1Seg

    }
}
