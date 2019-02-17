package com.brknbs.diabetesconsultant;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

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

public class DoctorPanelActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private FirebaseDatabase mFirebaseDatabase;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private DatabaseReference myRef;

    ListView userList;
    TextView showDoctorEmail;
    TextView showDoctorName;

    ArrayList<String> myArrayList;
    ArrayList<String> myArrayListID;

    ArrayAdapter<String> myArrayAdapter;
    ArrayAdapter<String> myArrayAdapterID;

    FirebaseUser user;
    String userID;

    String userName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_panel);
        //setContentView(R.layout.nav_header_doctor_panel);
        //Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);




        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        myRef = mFirebaseDatabase.getReference("Users");

        userList = findViewById(R.id.userList);

        user = mAuth.getCurrentUser();
        userID = user.getUid();

        myArrayList = new ArrayList<String>();
        myArrayListID = new ArrayList<String>();

        myArrayAdapter = new ArrayAdapter<String>(DoctorPanelActivity.this,android.R.layout.simple_list_item_1,myArrayList);
        myArrayAdapterID = new ArrayAdapter<String>(DoctorPanelActivity.this,android.R.layout.simple_list_item_1,myArrayListID);

        userList.setAdapter(myArrayAdapter);

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    //Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                    //toastMessage("Successfully signed in with: " + user.getEmail());
                    Toast.makeText(DoctorPanelActivity.this,"Successfully signed in with: " + user.getEmail(),Toast.LENGTH_SHORT).show();
                } else {
                    // User is signed out
                    //Log.d(TAG, "onAuthStateChanged:signed_out");
                    //toastMessage("Successfully signed out.");
                    Toast.makeText(DoctorPanelActivity.this,"Successfully signed out.",Toast.LENGTH_SHORT).show();

                }
                // ...
            }
        };

        /*FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });*/

        /*DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();*/

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        myRef.orderByChild("Doctor ID").equalTo(userID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                HashMap<String,String> hashMap = new HashMap<>();
                Iterator iterator = dataSnapshot.getChildren().iterator();

                while (iterator.hasNext()){
                    String key = ((DataSnapshot)iterator.next()).getKey();
                    String value = dataSnapshot.child(key).child("Name").getValue(String.class);
                    hashMap.put(key,value);
                }

                myArrayList.clear();
                myArrayListID.clear();
                myArrayList.addAll(hashMap.values());
                myArrayListID.addAll(hashMap.keySet());
                myArrayAdapter.notifyDataSetChanged();
                myArrayAdapterID.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        userList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String patientID = myArrayListID.get(i);
                String patientName = myArrayList.get(i);

                Intent intent = new Intent(DoctorPanelActivity.this,PatientInformation.class);
                intent.putExtra("patientID",patientID);
                intent.putExtra("patientName",patientName);
                startActivity(intent);

                Toast.makeText(DoctorPanelActivity.this,myArrayListID.get(i),Toast.LENGTH_SHORT).show();
            }
        });

        View headerView = navigationView.getHeaderView(0);
        showDoctorEmail = (TextView) headerView.findViewById(R.id.showDoctorEmail);
        showDoctorName = headerView.findViewById(R.id.showDoctorName);
        showDoctorEmail.setText(user.getEmail());

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                userName = dataSnapshot.child(userID).child("Name").getValue(String.class);
                showDoctorName.setText(userName);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });





    }

    /*public void showMyEmail(){
        FirebaseUser user = mAuth.getCurrentUser();
    }*/

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    /*@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.doctor_panel, menu);
        return true;
    }*/

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_my_doctor) {
            // Handle the camera action
        }  else if (id == R.id.nav_sign_out) {
            mAuth.signOut();
            finish();
            Intent intent = new Intent(DoctorPanelActivity.this,MainActivity.class);
            startActivity(intent);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
