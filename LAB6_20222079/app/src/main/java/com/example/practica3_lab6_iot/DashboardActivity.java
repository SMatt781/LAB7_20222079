package com.example.practica3_lab6_iot;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.practica3_lab6_iot.databinding.ActivityDashboardBinding;
import com.firebase.ui.auth.AuthUI;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class DashboardActivity extends AppCompatActivity implements SesionAdapter.OnSesionActionListener {

    private ActivityDashboardBinding binding;
    private SesionRepository repository;
    private SesionAdapter adapter;
    private ListenerRegistration listenerRegistration;
    private String uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDashboardBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            volverALogin();
            return;
        }
        uid = user.getUid();
        repository = new SesionRepository();

        adapter = new SesionAdapter(this);
        binding.rvSesiones.setLayoutManager(new LinearLayoutManager(this));
        binding.rvSesiones.setAdapter(adapter);

        binding.btnCerrarSesion.setOnClickListener(v -> cerrarSesion());
        binding.btnVerGraficos.setOnClickListener(v ->
                startActivity(new Intent(this, GraficosActivity.class)));

        binding.fabAgregar.setOnClickListener(v ->
                startActivity(new Intent(this, FormularioActivity.class)));
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (uid == null) return;
        listenerRegistration = repository.escucharSesiones(uid, (snapshot, error) -> {
            if (error != null) {
                Toast.makeText(this, getString(R.string.error_cargar_sesiones), Toast.LENGTH_SHORT).show();
                return;
            }
            if (snapshot == null) return;

            List<SesionEstudio> lista = new ArrayList<>();
            for (QueryDocumentSnapshot doc : snapshot) {
                SesionEstudio s = doc.toObject(SesionEstudio.class);
                s.setId(doc.getId());
                lista.add(s);
            }

            adapter.actualizarLista(lista);
            int total = lista.size();
            String textoContador = total == 1
                    ? getString(R.string.una_sesion_registrada)
                    : getString(R.string.n_sesiones_registradas, total);
            binding.tvContadorSesiones.setText(textoContador);

            binding.tvEstadoVacio.setVisibility(lista.isEmpty() ? View.VISIBLE : View.GONE);
            binding.rvSesiones.setVisibility(lista.isEmpty() ? View.GONE : View.VISIBLE);
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (listenerRegistration != null) {
            listenerRegistration.remove();
            listenerRegistration = null;
        }
    }

    @Override
    public void onEditar(SesionEstudio sesion) {
        Intent intent = new Intent(this, FormularioActivity.class);
        intent.putExtra(FormularioActivity.EXTRA_SESION_ID, sesion.getId());
        intent.putExtra(FormularioActivity.EXTRA_CURSO, sesion.getCurso());
        intent.putExtra(FormularioActivity.EXTRA_FECHA, sesion.getFecha());
        intent.putExtra(FormularioActivity.EXTRA_HORAS, sesion.getHoras());
        intent.putExtra(FormularioActivity.EXTRA_TIPO, sesion.getTipoActividad());
        intent.putExtra(FormularioActivity.EXTRA_TIMESTAMP, sesion.getTimestamp());
        startActivity(intent);
    }

    @Override
    public void onEliminar(SesionEstudio sesion) {
        new MaterialAlertDialogBuilder(this)
                .setTitle(R.string.confirmar_eliminar_titulo)
                .setMessage(getString(R.string.confirmar_eliminar_mensaje, sesion.getCurso()))
                .setNegativeButton(R.string.cancelar, null)
                .setPositiveButton(R.string.eliminar, (dialog, which) ->
                        repository.eliminarSesion(uid, sesion.getId(),
                                unused -> Toast.makeText(this, R.string.sesion_eliminada, Toast.LENGTH_SHORT).show(),
                                e -> Toast.makeText(this, R.string.error_eliminar_sesion, Toast.LENGTH_SHORT).show()))
                .show();
    }

    private void cerrarSesion() {
        AuthUI.getInstance()
                .signOut(this)
                .addOnCompleteListener(task -> volverALogin());
    }

    private void volverALogin() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
