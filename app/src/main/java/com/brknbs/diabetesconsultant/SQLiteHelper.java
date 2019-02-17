package com.brknbs.diabetesconsultant;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class SQLiteHelper extends SQLiteOpenHelper {
    static final String DATABASE_NAME = "patients.db";    // Database Name
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

    public SQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
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

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        try {
            Message.message(context,"OnUpgrade");
            db.execSQL(DROP_TABLE);
            onCreate(db);
        }catch (Exception e) {
            Message.message(context,""+e);
        }
    }

    public boolean addData(String userID, String currentDate, long timeStamp, String bloodSugar,
                           String exercise, String nutrition, String medicine){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(USERID,userID);
        contentValues.put(CURRENTDATE,currentDate);
        contentValues.put(TIMESTAMP, timeStamp);
        contentValues.put(BLOODSUGAR, bloodSugar);
        contentValues.put(EXERCISE, exercise);
        contentValues.put(NUTRITION, nutrition);
        contentValues.put(MEDICINE, medicine);


        long result  = db.insert(TABLE_NAME, null, contentValues);

        if(result == -1){
            return false;
        }else{
            return true;
        }
    }

    public Cursor showData(String userID){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor data = db.rawQuery("SELECT * FROM " + TABLE_NAME , null);
        return data;
    }

    public Cursor showBloodSugarData(String userID){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor data = db.rawQuery("SELECT BLOODSUGAR FROM " + TABLE_NAME + " WHERE USERID = " + userID, null);
        return data;
    }

    /*public String getData()
    {
        SQLiteDatabase db = this.getWritableDatabase();
        String[] columns = {SQLiteDatabaseHelper.USERID,SQLiteDatabaseHelper.CURRENTDATE,SQLiteDatabaseHelper.TIMESTAMP,
                SQLiteDatabaseHelper.BLOODSUGAR,SQLiteDatabaseHelper.EXERCISE,SQLiteDatabaseHelper.NUTRITION,
                SQLiteDatabaseHelper.MEDICINE};
        Cursor cursor =db.query(SQLiteDatabaseHelper.TABLE_NAME,columns,null,null,null,null,null);
        StringBuffer buffer= new StringBuffer();
        while (cursor.moveToNext())
        {
            String userID =cursor.getString(cursor.getColumnIndex(SQLiteDatabaseHelper.USERID));
            String currentDate =cursor.getString(cursor.getColumnIndex(SQLiteDatabaseHelper.CURRENTDATE));
            long timeStamp =cursor.getLong(cursor.getColumnIndex(SQLiteDatabaseHelper.TIMESTAMP));
            String bloodSugar =cursor.getString(cursor.getColumnIndex(SQLiteDatabaseHelper.BLOODSUGAR));
            String exercise =cursor.getString(cursor.getColumnIndex(SQLiteDatabaseHelper.EXERCISE));
            String nutrition =cursor.getString(cursor.getColumnIndex(SQLiteDatabaseHelper.NUTRITION));
            String medicine =cursor.getString(cursor.getColumnIndex(SQLiteDatabaseHelper.MEDICINE));
            buffer.append(userID+ "   " + currentDate + "   " + timeStamp + "   " + bloodSugar + "   " +
                    exercise + "   " + nutrition + "   " + medicine + " \n");
        }
        return buffer.toString();
    }*/

    /*public String getBloodSugar(String id){
        SQLiteDatabase db = myhelper.getWritableDatabase();
        String get_Status = "0";
        String selectQuery = "SELECT " + SQLiteDatabaseHelper.BLOODSUGAR + "FROM" + SQLiteDatabaseHelper.TABLE_NAME +
                "WHERE" +  SQLiteDatabaseHelper.UID + " = " + id;
        Cursor myCursor = db.rawQuery(selectQuery, null);
        if (myCursor != null && myCursor.moveToFirst()) {

            get_Status = myCursor.getString(0); // Return 1 selected result
        }
        db.close();
        return get_Status;
    }*/
}
