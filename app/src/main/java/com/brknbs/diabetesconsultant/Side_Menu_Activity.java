package com.brknbs.diabetesconsultant;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.Firebase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import static android.support.design.widget.NavigationView.OnNavigationItemSelectedListener;

public class Side_Menu_Activity extends AppCompatActivity
        implements OnNavigationItemSelectedListener{

    private static final String TAG = "AddToDatabase";



    View mHeaderView;
    Button buttonBloodSugar,buttonExercise,buttonNutrition,buttonMedicine;
    Firebase myFirebase;
    String bloodSugar = " ";
    String exercise = " ";
    String medicine = " ";
    String nutrition = " ";
    EditText et;
    private FirebaseDatabase mFirebaseDatabase;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private DatabaseReference myRef;
    private DatabaseReference myRef2;

    TextView showPatientEmail;
    TextView showPatientName;

    Date time;
    DateFormat df;

    FirebaseUser user;
    String userID;

    String userName;

    //SQLiteDatabaseAdapter sqLiteDatabaseAdapter;
    SQLiteHelper sqLiteHelper;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_side__menu_);

        final FileHelper fileHelper = new FileHelper();

        //sqLiteDatabaseAdapter = new SQLiteDatabaseAdapter(Side_Menu_Activity.this);
        sqLiteHelper = new SQLiteHelper(this);

        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        myRef = mFirebaseDatabase.getReference("Values");
        myRef2 = mFirebaseDatabase.getReference("Users");

        myRef.keepSynced(true);

        user = mAuth.getCurrentUser();
        userID = user.getUid();

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    //Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                    //toastMessage("Successfully signed in with: " + user.getEmail());
                    Toast.makeText(Side_Menu_Activity.this,"Successfully signed in with: " + user.getEmail(),Toast.LENGTH_SHORT).show();
                } else {
                    // User is signed out
                    //Log.d(TAG, "onAuthStateChanged:signed_out");
                    //toastMessage("Successfully signed out.");
                    Toast.makeText(Side_Menu_Activity.this,"Successfully signed out.",Toast.LENGTH_SHORT).show();

                }
                // ...
            }
        };
        //setContentView(R.layout.nav_header_side__menu_);
        /*Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);*/

        /*FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });*/

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, null, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        buttonBloodSugar = findViewById(R.id.buttonBloodSugar);
        buttonExercise = findViewById(R.id.buttonExercise);
        buttonMedicine = findViewById(R.id.buttonMedicine);
        buttonNutrition = findViewById(R.id.buttonNutrition);

        /*buttonBloodSugar.setOnClickListener(this);
        buttonExercise.setOnClickListener(this);
        buttonNutrition.setOnClickListener(this);
        buttonMedicine.setOnClickListener(this);*/

        buttonBloodSugar.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){

                AlertDialog.Builder ab = new AlertDialog.Builder(Side_Menu_Activity.this);
                ab.setTitle("Enter your blood sugar value");
                et = new EditText(Side_Menu_Activity.this);
                ab.setView(et);
                ab.setPositiveButton("SAVE", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        /*bloodSugar = et.getText().toString();
                        Firebase myNewChild = myFirebase.child("Blood Sugar");
                        myNewChild.setValue(bloodSugar);
                        Toast.makeText(Side_Menu_Activity.this,"Info saved successfully",Toast.LENGTH_SHORT).show();*/
                        // Çift  referans yazmamızın sebebi, daha sonra eklenen bilgilerin önceki bilgilerin üzerine yazılmaması içindir

                        if(haveNetwork()){
                            //DatabaseReference myNewRef = myRef.push();
                            Log.d(TAG, "onClick: Attempting to add object to database.");
                            bloodSugar = et.getText().toString();
                            if(!bloodSugar.equals("")){
                                //time = new Date();
                                df = new SimpleDateFormat("yyyy/MM/dd");
                                long timeStamp = System.currentTimeMillis()/1000;
                                // ikinci seçenek olarak df yi iptal et time.toString() yaz.

                                Calendar calendar = Calendar.getInstance();
                                String year = Integer.toString(calendar.get(Calendar.YEAR));
                                String month = Integer.toString(calendar.get(Calendar.MONTH)) + 1;
                                String day = Integer.toString(calendar.get(Calendar.DATE));
                                if(day.length() == 1){
                                    day =  "0" + day;
                                }

                                String currentDate = year + month + day;

                                FirebaseUser user = mAuth.getCurrentUser();
                                String userID = user.getUid();
                                //Firebase bloodSugarChild = myFirebase.child(userID).child("Blood Sugar");
                                //myRef.child(userID).child(df.format(time)).child("Blood Sugar").setValue(bloodSugar);
                                myRef.child(userID).child(currentDate).child("Blood Sugar").setValue(bloodSugar);
                                myRef.child(userID).child(currentDate).child("timeStamp").setValue(timeStamp);

                                Toast.makeText(Side_Menu_Activity.this,"Adding " + bloodSugar + " to database...",Toast.LENGTH_SHORT).show();
                                //reset the text
                                et.setText("");
                            } else {
                                Toast.makeText(Side_Menu_Activity.this,"Please type something",Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(Side_Menu_Activity.this,"Internet Connection Problem",Toast.LENGTH_SHORT).show();

                            // Text dosyasına kaydetme denemesi

                            /*fileHelper.saveToFile(bloodSugar);
                            String data = fileHelper.ReadFile(Side_Menu_Activity.this);
                            Toast.makeText(Side_Menu_Activity.this,data,Toast.LENGTH_SHORT).show();*/

                            // SQLite'a kaydetme denemeleri

                            bloodSugar = et.getText().toString();
                            if(!bloodSugar.equals("")) {
                                //time = new Date();
                                df = new SimpleDateFormat("yyyy/MM/dd");
                                long timeStamp = System.currentTimeMillis() / 1000;
                                // ikinci seçenek olarak df yi iptal et time.toString() yaz.

                                Calendar calendar = Calendar.getInstance();
                                String year = Integer.toString(calendar.get(Calendar.YEAR));
                                String month = Integer.toString(calendar.get(Calendar.MONTH)) + 1;
                                String day = Integer.toString(calendar.get(Calendar.DATE));
                                if (day.length() == 1) {
                                    day = "0" + day;
                                }

                                String currentDate = year + month + day;

                                FirebaseUser user = mAuth.getCurrentUser();
                                String userID = user.getUid();
                                //Firebase bloodSugarChild = myFirebase.child(userID).child("Blood Sugar");
                                //myRef.child(userID).child(df.format(time)).child("Blood Sugar").setValue(bloodSugar);
                                myRef.child(userID).child(currentDate).child("Blood Sugar").setValue(bloodSugar);
                                myRef.child(userID).child(currentDate).child("timeStamp").setValue(timeStamp);

                                et.setText("");
                            }

                            /*boolean insertData = sqLiteHelper.addData(userID,currentDate,timeStamp,bloodSugar,exercise,nutrition,medicine);

                            if (insertData == true) {
                                Toast.makeText(Side_Menu_Activity.this, "Data Successfully Inserted!", Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(Side_Menu_Activity.this, "Something went wrong :(.", Toast.LENGTH_LONG).show();
                            }

                            ViewData();*/

                            //ViewBloodSugar();*/

                            /*sqLiteDatabaseAdapter.insertData(userID,currentDate,timeStamp,bloodSugar,exercise,nutrition,medicine);
                            String data = sqLiteDatabaseAdapter.getData();
                            Message.message(Side_Menu_Activity.this,data);*/

                            /*String savedBloodSugar = sqLiteDatabaseAdapter.getBloodSugar(userID);
                            Message.message(Side_Menu_Activity.this,savedBloodSugar);*/
                        }

                    }
                });
                ab.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
                AlertDialog a = ab.create();
                a.show();
            }
        });

        buttonExercise.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                AlertDialog.Builder ab = new AlertDialog.Builder(Side_Menu_Activity.this);
                ab.setTitle("Enter your exercise info");
                et = new EditText(Side_Menu_Activity.this);
                ab.setView(et);
                ab.setPositiveButton("SAVE", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        if(haveNetwork()){
                            //DatabaseReference myNewRef = myRef.push();
                            Log.d(TAG, "onClick: Attempting to add object to database.");
                            exercise = et.getText().toString();
                            if(!exercise.equals("")){
                                //time = new Date();
                                df = new SimpleDateFormat("yyyy/MM/dd");
                                long tsLong = System.currentTimeMillis()/1000;
                                // ikinci seçenek olarak df yi iptal et time.toString() yaz.

                                Calendar calendar = Calendar.getInstance();
                                String year = Integer.toString(calendar.get(Calendar.YEAR));
                                String month = Integer.toString(calendar.get(Calendar.MONTH)) + 1;
                                String day = Integer.toString(calendar.get(Calendar.DATE));
                                if(day.length() == 1){
                                    day =  "0" + day;
                                }

                                String currentDate = year + month + day;

                                FirebaseUser user = mAuth.getCurrentUser();
                                String userID = user.getUid();
                                //Firebase bloodSugarChild = myFirebase.child(userID).child("Blood Sugar");
                                myRef.child(userID).child(currentDate).child("Exercise").setValue(exercise);
                                myRef.child(userID).child(currentDate).child("timeStamp").setValue(tsLong);

                                Toast.makeText(Side_Menu_Activity.this,"Adding " + exercise + " to database...",Toast.LENGTH_SHORT).show();
                                //reset the text
                                et.setText("");
                            } else {
                                Toast.makeText(Side_Menu_Activity.this,"Please type something",Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(Side_Menu_Activity.this,"Internet Connection Problem",Toast.LENGTH_SHORT).show();
                            exercise = et.getText().toString();
                            if(!exercise.equals("")){
                                //time = new Date();
                                df = new SimpleDateFormat("yyyy/MM/dd");
                                long tsLong = System.currentTimeMillis()/1000;
                                // ikinci seçenek olarak df yi iptal et time.toString() yaz.

                                Calendar calendar = Calendar.getInstance();
                                String year = Integer.toString(calendar.get(Calendar.YEAR));
                                String month = Integer.toString(calendar.get(Calendar.MONTH)) + 1;
                                String day = Integer.toString(calendar.get(Calendar.DATE));
                                if(day.length() == 1){
                                    day =  "0" + day;
                                }

                                String currentDate = year + month + day;

                                FirebaseUser user = mAuth.getCurrentUser();
                                String userID = user.getUid();
                                //Firebase bloodSugarChild = myFirebase.child(userID).child("Blood Sugar");
                                myRef.child(userID).child(currentDate).child("Exercise").setValue(exercise);
                                myRef.child(userID).child(currentDate).child("timeStamp").setValue(tsLong);

                                //reset the text
                                et.setText("");
                            }
                        }

                    }
                });
                ab.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
                AlertDialog a = ab.create();
                a.show();
            }
        });
        buttonMedicine.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                AlertDialog.Builder ab = new AlertDialog.Builder(Side_Menu_Activity.this);
                ab.setTitle("Enter your medicine info");
                et = new EditText(Side_Menu_Activity.this);
                ab.setView(et);
                ab.setPositiveButton("SAVE", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        if(haveNetwork()){
                            //DatabaseReference myNewRef = myRef.push();
                            Log.d(TAG, "onClick: Attempting to add object to database.");
                            medicine = et.getText().toString();
                            if(!medicine.equals("")){
                                //time = new Date();
                                df = new SimpleDateFormat("yyyy/MM/dd");
                                long tsLong = System.currentTimeMillis()/1000;
                                // ikinci seçenek olarak df yi iptal et time.toString() yaz.

                                Calendar calendar = Calendar.getInstance();
                                String year = Integer.toString(calendar.get(Calendar.YEAR));
                                String month = Integer.toString(calendar.get(Calendar.MONTH)) + 1;
                                String day = Integer.toString(calendar.get(Calendar.DATE));
                                if(day.length() == 1){
                                    day =  "0" + day;
                                }

                                String currentDate = year + month + day;

                                FirebaseUser user = mAuth.getCurrentUser();
                                String userID = user.getUid();
                                //Firebase bloodSugarChild = myFirebase.child(userID).child("Blood Sugar");
                                myRef.child(userID).child(currentDate).child("Medicine").setValue(medicine);
                                myRef.child(userID).child(currentDate).child("timeStamp").setValue(tsLong);

                                Toast.makeText(Side_Menu_Activity.this,"Adding " + medicine + " to database...",Toast.LENGTH_SHORT).show();
                                //reset the text
                                et.setText("");
                            } else {
                                Toast.makeText(Side_Menu_Activity.this,"Please type something",Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(Side_Menu_Activity.this,"Internet Connection Problem",Toast.LENGTH_SHORT).show();
                            medicine = et.getText().toString();
                            if(!medicine.equals("")){
                                //time = new Date();
                                df = new SimpleDateFormat("yyyy/MM/dd");
                                long tsLong = System.currentTimeMillis()/1000;
                                // ikinci seçenek olarak df yi iptal et time.toString() yaz.

                                Calendar calendar = Calendar.getInstance();
                                String year = Integer.toString(calendar.get(Calendar.YEAR));
                                String month = Integer.toString(calendar.get(Calendar.MONTH)) + 1;
                                String day = Integer.toString(calendar.get(Calendar.DATE));
                                if(day.length() == 1){
                                    day =  "0" + day;
                                }

                                String currentDate = year + month + day;

                                FirebaseUser user = mAuth.getCurrentUser();
                                String userID = user.getUid();
                                //Firebase bloodSugarChild = myFirebase.child(userID).child("Blood Sugar");
                                myRef.child(userID).child(currentDate).child("Medicine").setValue(medicine);
                                myRef.child(userID).child(currentDate).child("timeStamp").setValue(tsLong);

                                //reset the text
                                et.setText("");
                            }
                        }


                    }
                });
                ab.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
                AlertDialog a = ab.create();
                a.show();
            }
        });
        buttonNutrition.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                AlertDialog.Builder ab = new AlertDialog.Builder(Side_Menu_Activity.this);
                ab.setTitle("Enter your nutrition info");
                et = new EditText(Side_Menu_Activity.this);
                ab.setView(et);
                ab.setPositiveButton("SAVE", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        if(haveNetwork()){
                            //DatabaseReference myNewRef = myRef.push();
                            Log.d(TAG, "onClick: Attempting to add object to database.");
                            nutrition = et.getText().toString();
                            if(!nutrition.equals("")){
                                //time = new Date();
                                df = new SimpleDateFormat("yyyy/MM/dd");
                                long tsLong = System.currentTimeMillis()/1000;
                                // ikinci seçenek olarak df yi iptal et time.toString() yaz.

                                Calendar calendar = Calendar.getInstance();
                                String year = Integer.toString(calendar.get(Calendar.YEAR));
                                String month = Integer.toString(calendar.get(Calendar.MONTH)) + 1;
                                String day = Integer.toString(calendar.get(Calendar.DATE));
                                if(day.length() == 1){
                                    day =  "0" + day;
                                }

                                String currentDate = year + month + day;

                                FirebaseUser user = mAuth.getCurrentUser();
                                String userID = user.getUid();
                                //Firebase bloodSugarChild = myFirebase.child(userID).child("Blood Sugar");
                                myRef.child(userID).child(currentDate).child("Nutrition").setValue(nutrition);
                                myRef.child(userID).child(currentDate).child("timeStamp").setValue(tsLong);

                                Toast.makeText(Side_Menu_Activity.this,"Adding " + nutrition + " to database...",Toast.LENGTH_SHORT).show();
                                //reset the text
                                et.setText("");
                            } else {
                                Toast.makeText(Side_Menu_Activity.this,"Please type something",Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(Side_Menu_Activity.this,"Internet Connection Problem",Toast.LENGTH_SHORT).show();
                            nutrition = et.getText().toString();
                            if(!nutrition.equals("")){
                                //time = new Date();
                                df = new SimpleDateFormat("yyyy/MM/dd");
                                long tsLong = System.currentTimeMillis()/1000;
                                // ikinci seçenek olarak df yi iptal et time.toString() yaz.

                                Calendar calendar = Calendar.getInstance();
                                String year = Integer.toString(calendar.get(Calendar.YEAR));
                                String month = Integer.toString(calendar.get(Calendar.MONTH)) + 1;
                                String day = Integer.toString(calendar.get(Calendar.DATE));
                                if(day.length() == 1){
                                    day =  "0" + day;
                                }

                                String currentDate = year + month + day;

                                FirebaseUser user = mAuth.getCurrentUser();
                                String userID = user.getUid();
                                //Firebase bloodSugarChild = myFirebase.child(userID).child("Blood Sugar");
                                myRef.child(userID).child(currentDate).child("Nutrition").setValue(nutrition);
                                myRef.child(userID).child(currentDate).child("timeStamp").setValue(tsLong);

                                //reset the text
                                et.setText("");
                            }
                        }

                    }
                });
                ab.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
                AlertDialog a = ab.create();
                a.show();
            }
        });


        Firebase.setAndroidContext(this);
        myFirebase = new Firebase("https://diabates-consultant.firebaseio.com");

        // Read from the database
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                Object value = dataSnapshot.getValue();
                Log.d(TAG, "Value is: " + value);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });

        /*FirebaseUser user = mAuth.getCurrentUser();
        String userEmail = user.getEmail();
        showEmail.setText(userEmail);*/

        View headerView = navigationView.getHeaderView(0);
        showPatientEmail = (TextView) headerView.findViewById(R.id.showPatientEmail);
        showPatientName = (TextView) headerView.findViewById(R.id.showPatientName);
        showPatientEmail.setText(user.getEmail());

        myRef2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                userName = dataSnapshot.child(userID).child("Name").getValue(String.class);
                showPatientName.setText(userName);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                haveNetwork();
            }
        },0,3000);
    }
    public void writeData(String bloodSugar) {

        // add-write text into file

        try {
            File myFile = new File(Environment.getExternalStorageDirectory().getAbsoluteFile()+ File.separator +"bloodSugar.txt");
            if(!myFile.exists()){
                FileOutputStream fileout= new FileOutputStream(myFile);
                OutputStreamWriter outputWriter=new OutputStreamWriter(fileout);
                outputWriter.write(bloodSugar);
                outputWriter.close();

                //display file saved message
                Toast.makeText(getBaseContext(), "File saved successfully!",
                        Toast.LENGTH_SHORT).show();
            }


        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void readData(String bloodSugar) {
        //reading text from file
        try {
            FileInputStream fileIn=openFileInput(Environment.getExternalStorageDirectory().getAbsoluteFile()+ File.separator +"bloodSugar.txt");
            InputStreamReader InputRead= new InputStreamReader(fileIn);

            char[] inputBuffer= new char[256];
            String s="";
            int charRead;

            while ((charRead=InputRead.read(inputBuffer))>0) {
                // char to string conversion
                String readstring=String.copyValueOf(inputBuffer,0,charRead);
                s +=readstring;
            }
            InputRead.close();
            Toast.makeText(getBaseContext(), s,Toast.LENGTH_SHORT).show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.side__menu_, menu);
        return true;
    }

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

        if (id == R.id.nav_add_info) {
            Intent intent = new Intent(this,Side_Menu_Activity.class);
            startActivity(intent);
        } else if (id == R.id.nav_my_doctor) {
            Intent intent = new Intent(this,MyDoctorActivity.class);
            startActivity(intent);

        } else if (id == R.id.nav_sign_out) {
            mAuth.signOut();
            finish();
            Intent intent = new Intent(Side_Menu_Activity.this,MainActivity.class);
            startActivity(intent);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    //internet connection control method
    private boolean haveNetwork(){
        boolean haveWIFI = false;
        boolean haveMobileData = false;

        ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo[] networkInfos = connectivityManager.getAllNetworkInfo();

        for(NetworkInfo info:networkInfos){
            if(info.getTypeName().equalsIgnoreCase("WIFI")){
                if(info.isConnected())
                    haveWIFI = true;
            }
            if(info.getTypeName().equalsIgnoreCase("MOBILE")){
                if(info.isConnected())
                    haveMobileData = true;
            }
        }

        return haveWIFI || haveMobileData;
    }

    public void display(String title, String message){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.show();
    }

    public void ViewData(){

                Cursor data = sqLiteHelper.showData(userID);

                if (data.getCount() == 0) {
                    display("Error", "No Data Found.");
                    return;
                }
                StringBuffer buffer = new StringBuffer();
                while (data.moveToNext()) {
                    buffer.append("ID: " + data.getString(0) + "\n");
                    buffer.append("User ID: " + data.getString(1) + "\n");
                    buffer.append("Current Date: " + data.getString(2) + "\n");
                    buffer.append("timeStamp: " + data.getString(3) + "\n");
                    buffer.append("Blood Sugar: " + data.getString(4) + "\n");
                    buffer.append("Exercise: " + data.getString(5) + "\n");
                    buffer.append("Nutrition: " + data.getString(6) + "\n");
                    buffer.append("Medicine: " + data.getString(7) + "\n");

                    display("These are saved to SQLite because of the internet connection problem :", buffer.toString());
                }

    }

    /*public void ViewBloodSugar(){
        Cursor data = sqLiteHelper.showBloodSugarData(userID);
        if (data.getCount() == 0) {
            display("Error", "No Data Found.");
            return;
        }
        StringBuffer buffer = new StringBuffer();
        String bloodSugar = data.getString(4) + "\n";

        display("These are saved to SQLite because of the internet connection problem :", bloodSugar);

    }*/

    /*public void onClick(View view){
        switch (view.getId()){

            case R.id.buttonBloodSugar :
                AlertDialog.Builder ab = new AlertDialog.Builder(Side_Menu_Activity.this);
                ab.setTitle("Enter your blood sugar value");
                EditText et = new EditText(Side_Menu_Activity.this);
                ab.setView(et);
                ab.setPositiveButton("SAVE", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
                ab.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
                AlertDialog a = ab.create();
                a.show();
                break;

            case R.id.buttonExercise :

                break;

            case R.id.buttonNutrition :

                break;

            case R.id.buttonMedicine :

                break;
        }
    }*/
}
