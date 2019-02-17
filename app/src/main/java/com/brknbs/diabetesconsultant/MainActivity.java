package com.brknbs.diabetesconsultant;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {


    EditText emailLogin,passwordLogin;
    Button buttonLogin;
    TextView toSignUp;
    private FirebaseDatabase mFirebaseDatabase;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private DatabaseReference myRef;
    String userType;
    String patient = "Patient";
    String doctor = "Doctor";
    String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        myRef = mFirebaseDatabase.getReference("Users");

        emailLogin = findViewById(R.id.emailLogin);
        passwordLogin = findViewById(R.id.passwordLogin);
        buttonLogin = findViewById(R.id.buttonLogin);

        toSignUp = findViewById(R.id.toSignUp);

        toSignUp.setOnClickListener(this);
        buttonLogin.setOnClickListener(this);

        userType = "";


    }

    String email;
    String password;

    private void userLogin(){
        email = emailLogin.getText().toString().trim();
        password = passwordLogin.getText().toString().trim();

        if(email.isEmpty()){
            emailLogin.setError("Email is required");
            emailLogin.requestFocus();
            return;
        }

        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            emailLogin.setError("Please enter a valid email");
            emailLogin.requestFocus();
            return;
        }

        if(password.isEmpty()){
            passwordLogin.setError("Password is required");
            passwordLogin.requestFocus();
            return;
        }

        if(password.length() < 6){
            passwordLogin.setError("Minimum lenght of password should be 6");
            passwordLogin.requestFocus();
            return;
        }

        mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){

                    final FirebaseUser user = mAuth.getCurrentUser();
                    userID = user.getUid();

                    myRef.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            userType = dataSnapshot.child(userID).child("User Type").getValue(String.class);
                            //userType = dataSnapshot.child().getKey();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });


                    if(userType.equals(patient)){
                        Intent intent = new Intent(MainActivity.this,Side_Menu_Activity.class);
                        //intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        //intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                    }

                    if (userType.equals(doctor)){
                        Intent intent = new Intent(MainActivity.this,DoctorPanelActivity.class);
                        startActivity(intent);
                    }

                } else {
                    Toast.makeText(getApplicationContext(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void onClick(View view){
        switch (view.getId()){

            case R.id.toSignUp :
                startActivity(new Intent(this,SignUpActivity.class));
                break;

            case R.id.buttonLogin :
                //finish();
                userLogin();
                break;
        }
    }
}
