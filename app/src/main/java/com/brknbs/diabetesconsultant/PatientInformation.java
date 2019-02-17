package com.brknbs.diabetesconsultant;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.format.DateFormat;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.Locale;

public class PatientInformation extends AppCompatActivity {

    TextView showPatientName;

    ListView patientView;

    private FirebaseDatabase mFirebaseDatabase;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private DatabaseReference myRef;

    ArrayList<String> myArrayListKeys;
    ArrayList<String> myArrayListValues;

    ArrayAdapter<String> myArrayAdapterKeys;
    ArrayAdapter<String> myArrayAdapterValues;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_information);

        patientView = findViewById(R.id.patientView);

        myArrayListKeys = new ArrayList<>();
        myArrayListValues = new ArrayList<>();

        //myArrayAdapterKeys = new ArrayAdapter<>(PatientInformation.this,android.R.layout.simple_list_item_1,myArrayListKeys);
        myArrayAdapterValues = new ArrayAdapter<>(PatientInformation.this,android.R.layout.simple_list_item_1,myArrayListValues);

        patientView.setAdapter(myArrayAdapterValues);
        //patientView.setAdapter(myArrayAdapterKeys);

        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        myRef = mFirebaseDatabase.getReference("Values");

        showPatientName = findViewById(R.id.showPatientName);

        String userID = getIntent().getStringExtra("patientID");
        String userName = getIntent().getStringExtra("patientName");

        //info.setText(userName + "-" + userID);

        showPatientName.setText(userName);

        myRef.child(userID).orderByChild("timeStamp").startAt(1500000000).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                //HashMap<String,List<String>> map = new HashMap<>();
                //List<String> values = new ArrayList<>();
                //HashMap<String,String> map = new HashMap<>();
                String values;

                myArrayListValues.clear();

                Iterator iterator = dataSnapshot.getChildren().iterator();

                while (iterator.hasNext()){
                    String key = ((DataSnapshot)iterator.next()).getKey();
                    String bloodSugar,exercise,nutrition,medicine;
                    if(dataSnapshot.child(key).child("Blood Sugar").exists()){
                        bloodSugar = dataSnapshot.child(key).child("Blood Sugar").getValue(String.class);
                    } else {
                        bloodSugar = " - ";
                    }
                    if(dataSnapshot.child(key).child("Exercise").exists()){
                        exercise = dataSnapshot.child(key).child("Exercise").getValue(String.class);
                    } else {
                        exercise = " - ";
                    }
                    if(dataSnapshot.child(key).child("Nutrition").exists()){
                        nutrition = dataSnapshot.child(key).child("Nutrition").getValue(String.class);
                    } else {
                        nutrition = " - ";
                    }
                    if(dataSnapshot.child(key).child("Medicine").exists()){
                        medicine = dataSnapshot.child(key).child("Medicine").getValue(String.class);
                    } else {
                        medicine = " - ";
                    }

                    long timeStamp = dataSnapshot.child(key).child("timeStamp").getValue(long.class);
                    String postDate = getDate(timeStamp);
                    /*values.add("Blood Sugar : " + bloodSugar);
                    values.add("Exercise : " + exercise);
                    values.add("Nutrition : " + nutrition);
                    values.add("Medicine : " + medicine);
                    map.put(postDate,values);*/
                    values = '\n' + postDate + '\n' + "Blood Sugar : " + bloodSugar + '\n' + "Exercise : " + exercise + '\n' +
                            "Nutrition : " + nutrition + '\n' + "Medicine : " + medicine + '\n';
                    myArrayListValues.add(values);
                    myArrayAdapterValues.notifyDataSetChanged();
                    //Toast.makeText(PatientInformation.this,bloodSugar,Toast.LENGTH_SHORT).show();
                }


                //myArrayListKeys.clear();
                //myArrayListValues.addAll(map.values());
                //myArrayListKeys.addAll(map.keySet());
                //myArrayAdapterKeys.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private String getDate(long time) {
        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
        cal.setTimeInMillis(time * 1000L);
        String date = DateFormat.format("dd-MM-yyyy", cal).toString();
        return date;
    }

}
