package com.brknbs.diabetesconsultant;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.firebase.client.Firebase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class MyDoctorActivity extends AppCompatActivity {

    TextView doctorInfo;
    ListView userList;

    ArrayList<String> myArrayList;
    ArrayList<String> myArrayListID;

    ArrayAdapter<String> myArrayAdapter;
    ArrayAdapter<String> myArrayAdapterID;

    String doctor = "Doctor";
    String userType;
    String userName;


    Firebase myFirebase;
    private FirebaseDatabase mFirebaseDatabase;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private DatabaseReference myRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_doctor);

        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        myRef = mFirebaseDatabase.getReference("Users");

        final FirebaseUser user = mAuth.getCurrentUser();
        final String userID = user.getUid();


        doctorInfo = findViewById(R.id.doctorInfo);
        //String warning = "You haven't choose a doctor yet.";

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String myDoctorID = dataSnapshot.child(userID).child("Doctor ID").getValue(String.class);
                String myDoctorName = dataSnapshot.child(userID).child("Doctor Name").getValue(String.class);
                if(!myDoctorName.isEmpty()){
                    doctorInfo.setText("Your doctor is : " + myDoctorName);
                } else {
                    doctorInfo.setText("You haven't choose a doctor yet.");
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        if(myRef.child(userID).child("Doctor ID").toString().isEmpty()){

        } else {

        }


        userList = findViewById(R.id.userList);

        myArrayList = new ArrayList<String>();
        myArrayListID = new ArrayList<String>();

        myArrayAdapter = new ArrayAdapter<String>(MyDoctorActivity.this,android.R.layout.simple_list_item_1,myArrayList);
        myArrayAdapterID = new ArrayAdapter<String>(MyDoctorActivity.this,android.R.layout.simple_list_item_1,myArrayListID);

        userList.setAdapter(myArrayAdapter);


        myRef.orderByChild("User Type").equalTo("Doctor").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                //Set<String> set = new HashSet<>();
                //Set<String> setID = new HashSet<>();
                HashMap<String,String> hashMap = new HashMap<>();
                Iterator iterator = dataSnapshot.getChildren().iterator();

                while (iterator.hasNext()){
                    String key = ((DataSnapshot)iterator.next()).getKey();
                    String value = dataSnapshot.child(key).child("Name").getValue(String.class);
                    //set.add(value);
                    //setID.add(key);
                    hashMap.put(key,value);
                }

                myArrayList.clear();
                myArrayListID.clear();
                myArrayList.addAll(hashMap.values());
                myArrayListID.addAll(hashMap.keySet());
                myArrayAdapter.notifyDataSetChanged();
                myArrayAdapterID.notifyDataSetChanged();
                /*for (DataSnapshot i : dataSnapshot.getChildren()) {
                    //System.out.println(child.getKey());
                    //userKey = i.getKey();
                    userType = i.child("User Type").getValue(String.class);
                    userName = i.child("Name").getValue(String.class);
                    if(userType == "Doctor"){
                        myArrayList.add(userName);
                    }
                }*/
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        userList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String doctorID = myArrayListID.get(i);
                //String doctorName = userList.getItemAtPosition(i).toString();
                String doctorName = myArrayList.get(i);
                //Toast.makeText(MyDoctorActivity.this,doctorID,Toast.LENGTH_SHORT).show();

                myRef.child(userID).child("Doctor ID").setValue(doctorID);
                myRef.child(userID).child("Doctor Name").setValue(doctorName);

                doctorInfo.setText("Your doctor is " + doctorName);

            }
        });
    }
}
