package com.example.futbol_connection_notes;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    EditText emailEditText,passwordEditText;
    Button loginBtn;

    TextView createAccountBtnTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        emailEditText = findViewById(R.id.email_edit_text);
        passwordEditText = findViewById(R.id.password_edit_text);
        loginBtn = findViewById(R.id.login_btn);

        createAccountBtnTextView = findViewById(R.id.create_account_text_view_btn);

        loginBtn.setOnClickListener((v)-> loginUser() );
        createAccountBtnTextView.setOnClickListener((v)->startActivity(new Intent(LoginActivity.this,CreateAccountActivity.class)) );

    }

    void loginUser(){
        // Obtener datos ingresados por el usuario y validarlos
        String email  = emailEditText.getText().toString();
        String password  = passwordEditText.getText().toString();


        boolean isValidated = validateData(email,password);
        if(!isValidated){
            return;
        }
        // Si los datos son correctos, se inicia sesion con la autenticación en Firebase
        loginAccountInFirebase(email,password);

    }

    void loginAccountInFirebase(String email,String password){
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

        firebaseAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if(task.isSuccessful()){
                    // Si el inicio de sesión es exitoso, verificar si el correo está validado, si no, mensaje de error.
                    if(firebaseAuth.getCurrentUser().isEmailVerified()){

                        startActivity(new Intent(LoginActivity.this,MainActivity.class));
                        finish();
                    }else{
                        Utility.showToast(LoginActivity.this,"Cuenta no verificada. Revisa tu correo.");
                    }

                }else{

                    Utility.showToast(LoginActivity.this,"No se ha podido iniciar sesión.");
                }
            }
        });
    }


    boolean validateData(String email,String password){
    //Verificar los datos introducidos por el usuario

        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            emailEditText.setError("Correo electrónico no válido");
            return false;
        }
        if(password.length()<6){
            passwordEditText.setError("La contraseña tiene que tener mínimo 6 carácteres");
            return false;
        }
        return true;
    }
}