package com.example.futbol_connection_notes;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class CreateAccountActivity extends AppCompatActivity {

    EditText emailEditText,passwordEditText,confirmPasswordEditText;
    Button createAccountBtn;

    TextView loginBtnTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);

        emailEditText = findViewById(R.id.email_edit_text);
        passwordEditText = findViewById(R.id.password_edit_text);
        confirmPasswordEditText = findViewById(R.id.confirm_password_edit_text);
        createAccountBtn = findViewById(R.id.create_account_btn);
        loginBtnTextView = findViewById(R.id.login_text_view_btn);

        createAccountBtn.setOnClickListener(v-> createAccount());
        loginBtnTextView.setOnClickListener(v-> finish());


    }

    void createAccount(){
        //Obtener los datos del usuario
        String email  = emailEditText.getText().toString();
        String password  = passwordEditText.getText().toString();
        String confirmPassword  = confirmPasswordEditText.getText().toString();
        //Verificar la validez de los datos
        boolean isValidated = validateData(email,password,confirmPassword);
        if(!isValidated){
            return;
        }
        //Crear la cuenta en firebase si está es correcto
        createAccountInFirebase(email,password);


    }

    void createAccountInFirebase(String email,String password){


        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(CreateAccountActivity.this,
                new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if(task.isSuccessful()){
                            // Si la cuenta se crea correctamente, se envía un correo de verificación
                            Utility.showToast(CreateAccountActivity.this,"Cuenta creada correctamente. Revisa tu correo para verificar la cuenta");
                            firebaseAuth.getCurrentUser().sendEmailVerification();
                            firebaseAuth.signOut();
                            finish();
                        }else{
                            // Mensaje de error si falla.
                            Utility.showToast(CreateAccountActivity.this,"No se ha podido crear la cuenta");
                        }
                    }
                }
        );



    }

    boolean validateData(String email,String password,String confirmPassword){
    //Para verificar los datos introducidos para crear la cuenta.
        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            emailEditText.setError("Correo electrónico no válido");
            return false;
        }
        if(password.length()<6){
            passwordEditText.setError("La contraseña tiene que tener mínimo 6 carácteres");
            return false;
        }
        if(!password.equals(confirmPassword)){
            confirmPasswordEditText.setError("No coinciden las contraseñas");
            return false;
        }
        return true;
    }
    }