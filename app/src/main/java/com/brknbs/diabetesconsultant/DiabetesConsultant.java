package com.brknbs.diabetesconsultant;

import android.app.Application;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.FirebaseDatabase;

public class DiabetesConsultant extends Application
{
    @Override
    public void onCreate() {
        super.onCreate();

        //Firebase.getDefaultConfig().setPersistenceEnabled(true);

        if (!FirebaseApp.getApps(this).isEmpty())
            FirebaseDatabase.getInstance().setPersistenceEnabled(true);
    }
}
