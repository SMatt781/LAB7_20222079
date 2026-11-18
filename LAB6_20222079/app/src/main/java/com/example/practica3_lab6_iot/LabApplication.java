package com.example.practica3_lab6_iot;

import android.app.Application;
import com.google.firebase.FirebaseApp;

public class LabApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        try {
            if (FirebaseApp.getApps(this).isEmpty()) {
                FirebaseApp.initializeApp(this);
            }
        } catch (Exception ignored) {
            // Si falta google-services.json, MainActivity mostrará el aviso correspondiente
        }
    }
}
