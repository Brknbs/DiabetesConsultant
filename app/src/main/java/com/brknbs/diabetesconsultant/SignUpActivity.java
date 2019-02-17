package com.brknbs.diabetesconsultant;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class SignUpActivity extends AppCompatActivity implements View.OnClickListener{

    EditText emailSignUp,passwordSignUp,nameSignUp;
    Button buttonSignUp;
    RadioButton patientButton,doctorButton;

    private FirebaseAuth mAuth;
    private FirebaseDatabase mFirebaseDatabase;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private DatabaseReference myRef;

    String name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        emailSignUp = findViewById(R.id.emailSignUp);
        passwordSignUp = findViewById(R.id.passwordSignUp);
        buttonSignUp = findViewById(R.id.buttonSignUp);
        nameSignUp = findViewById(R.id.nameSignUp);

        buttonSignUp.setOnClickListener(this);

        patientButton = findViewById(R.id.patientButton);
        doctorButton = findViewById(R.id.doctorButton);

        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        myRef = mFirebaseDatabase.getReference("Users");
    }

    private void registerUser(){
        String email = emailSignUp.getText().toString().trim();
        String password = passwordSignUp.getText().toString().trim();
        name = nameSignUp.getText().toString().trim();

        if(email.isEmpty()){
            emailSignUp.setError("Email is required");
            emailSignUp.requestFocus();
            return;
        }

        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            emailSignUp.setError("Please enter a valid email");
            emailSignUp.requestFocus();
            return;
        }

        if(password.isEmpty()){
            passwordSignUp.setError("Password is required");
            passwordSignUp.requestFocus();
            return;
        }

        if(password.length() < 6){
            passwordSignUp.setError("Minimum lenght of password should be 6");
            passwordSignUp.requestFocus();
            return;
        }

        if(name == ""){
            nameSignUp.setError("Please enter your name");
            nameSignUp.requestFocus();
            return;
        }

        mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    FirebaseUser user = mAuth.getCurrentUser();
                    String userID = user.getUid();

                    if(patientButton.isChecked()){
                        myRef.child(userID).child("User Type").setValue("Patient");
                        myRef.child(userID).child("Name").setValue(name);
                        myRef.child(userID).child("Doctor Name").setValue("");
                    } else if (doctorButton.isChecked()){
                        myRef.child(userID).child("User Type").setValue("Doctor");
                        myRef.child(userID).child("Name").setValue(name);
                        myRef.child(userID).child("Doctor Name").setValue("");
                    }


                    Toast.makeText(getApplicationContext(),"User registered successfully",Toast.LENGTH_SHORT).show();
                } else {
                    if(task.getException() instanceof FirebaseAuthUserCollisionException){
                        Toast.makeText(getApplicationContext(), "You are already registered", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getApplicationContext(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }

            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.buttonSignUp :
                registerUser();
                break;
            case R.id.toSignUp :
                finish();
                startActivity(new Intent(this,MainActivity.class));
                break;
        }
    }
}
