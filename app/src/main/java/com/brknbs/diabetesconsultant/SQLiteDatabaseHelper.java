package com.brknbs.diabetesconsultant;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class SQLiteDatabaseHelper extends SQLiteOpenHelper {
    static final String DATABASE_NAME = "patientDatabase";    // Database Name
    static final String TABLE_NAME = "patientInformations";   // Table Name
    static final int DATABASE_Version = 1;    // Database Version
    static final String ID = "ID";
    static final String USERID = "USERID";     // Column I (Primary Key)
    static final String CURRENTDATE = "CURRENTDATE";    //Column II
    static final String TIMESTAMP= "TIMESTAMP";    // Column III
    static final String BLOODSUGAR= "BLOODSUGAR";    // Column IV
    static final String EXERCISE= "EXERCISE";    // Column V
    static final String NUTRITION= "NUTRITION";    // Column VI
    static final String MEDICINE= "MEDICINE";    // Column VII
    /*static final String CREATE_TABLE = "CREATE TABLE "+TABLE_NAME+
            " ("+ NUMBER + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            USERID +" VARCHAR(255) , " +
            CURRENTDATE +" VARCHAR(255) ,"+
            TIMESTAMP +" LONG ," +
            BLOODSUGAR + " VARCHAR(255)," +
            EXERCISE + " VARCHAR(255)," +
            NUTRITION + " VARCHAR(255)," +
            MEDICINE + " VARCHAR(255)" + ");" ;*/
    private static final String DROP_TABLE ="DROP TABLE IF EXISTS "+TABLE_NAME;
    private Context context;

    public SQLiteDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_Version);
        this.context=context;
    }

    public void onCreate(SQLiteDatabase db) {

        String createTable = "CREATE TABLE " + TABLE_NAME + " (ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "USERID TEXT, CURRENTDATE TEXT, TIMESTAMP LONG, BLOODSUGAR TEXT, EXERCISE TEXT, NUTRITION TEXT, MEDICINE TEXT)";

        try {
            db.execSQL(createTable);
        } catch (Exception e) {
            Message.message(context,""+e);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        try {
            Message.message(context,"OnUpgrade");
            db.execSQL(DROP_TABLE);
            onCreate(db);
        }catch (Exception e) {
            Message.message(context,""+e);
        }
    }

}
