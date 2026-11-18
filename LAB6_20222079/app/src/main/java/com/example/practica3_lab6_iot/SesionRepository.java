package com.example.practica3_lab6_iot;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

public class SesionRepository {

    private final FirebaseFirestore db;

    public SesionRepository() {
        this.db = FirebaseFirestore.getInstance();
    }

    private CollectionReference getSesionesRef(String uid) {
        return db.collection("usuarios").document(uid).collection("sesiones");
    }

    public ListenerRegistration escucharSesiones(String uid, EventListener<QuerySnapshot> listener) {
        return getSesionesRef(uid)
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .addSnapshotListener(listener);
    }

    public void guardarSesion(String uid, SesionEstudio sesion,
                              OnSuccessListener<Void> onSuccess,
                              OnFailureListener onFailure) {
        CollectionReference ref = getSesionesRef(uid);
        if (sesion.getId() == null || sesion.getId().trim().isEmpty()) {
            String nuevoId = ref.document().getId();
            sesion.setId(nuevoId);
        }
        ref.document(sesion.getId())
                .set(sesion)
                .addOnSuccessListener(onSuccess)
                .addOnFailureListener(onFailure);
    }

    public void eliminarSesion(String uid, String sesionId,
                               OnSuccessListener<Void> onSuccess,
                               OnFailureListener onFailure) {
        getSesionesRef(uid)
                .document(sesionId)
                .delete()
                .addOnSuccessListener(onSuccess)
                .addOnFailureListener(onFailure);
    }
}
