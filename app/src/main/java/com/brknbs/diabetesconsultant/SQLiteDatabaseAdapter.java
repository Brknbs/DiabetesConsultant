package com.brknbs.diabetesconsultant;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class SQLiteDatabaseAdapter {

    SQLiteDatabaseHelper myhelper;
    public SQLiteDatabaseAdapter(Context context)
    {
        myhelper = new SQLiteDatabaseHelper(context);
    }

    /*public void insertData(String userID, String currentDate, long timeStamp, String bloodSugar,
                           String exercise, String nutrition, String medicine)
    {
        SQLiteDatabase dbb = myhelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(SQLiteDatabaseHelper.USERID, userID);
        contentValues.put(SQLiteDatabaseHelper.CURRENTDATE, currentDate);
        contentValues.put(SQLiteDatabaseHelper.TIMESTAMP, timeStamp);
        contentValues.put(SQLiteDatabaseHelper.BLOODSUGAR, bloodSugar);
        contentValues.put(SQLiteDatabaseHelper.EXERCISE, exercise);
        contentValues.put(SQLiteDatabaseHelper.NUTRITION, nutrition);
        contentValues.put(SQLiteDatabaseHelper.MEDICINE, medicine);
        dbb.insert(SQLiteDatabaseHelper.TABLE_NAME, null , contentValues);
        //return id;
    }*/

    public boolean addData(String userID, String currentDate, long timeStamp, String bloodSugar,
                           String exercise, String nutrition, String medicine){
        SQLiteDatabase db = myhelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(myhelper.USERID,userID);
        contentValues.put(myhelper.CURRENTDATE,currentDate);
        contentValues.put(myhelper.TIMESTAMP, timeStamp);
        contentValues.put(myhelper.BLOODSUGAR, bloodSugar);
        contentValues.put(myhelper.EXERCISE, exercise);
        contentValues.put(myhelper.NUTRITION, nutrition);
        contentValues.put(myhelper.MEDICINE, medicine);


        long result  = db.insert(myhelper.TABLE_NAME, null, contentValues);

        if(result == -1){
            return false;
        }else{
            return true;
        }
    }

    public String getData()
    {
        SQLiteDatabase db = myhelper.getWritableDatabase();
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
    }

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
