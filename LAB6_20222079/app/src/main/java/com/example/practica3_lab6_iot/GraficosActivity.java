package com.example.practica3_lab6_iot;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.practica3_lab6_iot.databinding.ActivityGraficosBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

public class GraficosActivity extends AppCompatActivity {

    private ActivityGraficosBinding binding;
    private SesionRepository repository;
    private ListenerRegistration listenerRegistration;
    private String uid;

    private final List<SesionEstudio> todasLasSesiones = new ArrayList<>();
    private final List<String> opcionesCursos = new ArrayList<>();
    private ArrayAdapter<String> spinnerAdapter;
    private String cursoSeleccionado = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityGraficosBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            finish();
            return;
        }
        uid = user.getUid();
        repository = new SesionRepository();

        binding.toolbar.setNavigationOnClickListener(v -> finish());

        cursoSeleccionado = getString(R.string.todos_los_cursos);
        opcionesCursos.add(cursoSeleccionado);
        spinnerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, opcionesCursos);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spinnerCurso.setAdapter(spinnerAdapter);

        binding.spinnerCurso.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                cursoSeleccionado = opcionesCursos.get(position);
                actualizarGraficos();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        listenerRegistration = repository.escucharSesiones(uid, (snapshot, error) -> {
            if (error != null) {
                Toast.makeText(this, R.string.error_cargar_sesiones, Toast.LENGTH_SHORT).show();
                return;
            }
            if (snapshot == null) return;

            todasLasSesiones.clear();
            Set<String> cursosUnicos = new LinkedHashSet<>();
            for (QueryDocumentSnapshot doc : snapshot) {
                SesionEstudio s = doc.toObject(SesionEstudio.class);
                s.setId(doc.getId());
                todasLasSesiones.add(s);
                if (s.getCurso() != null && !s.getCurso().trim().isEmpty()) {
                    cursosUnicos.add(s.getCurso().trim());
                }
            }

            List<String> cursosOrdenados = new ArrayList<>(cursosUnicos);
            Collections.sort(cursosOrdenados, String.CASE_INSENSITIVE_ORDER);

            String todos = getString(R.string.todos_los_cursos);
            opcionesCursos.clear();
            opcionesCursos.add(todos);
            opcionesCursos.addAll(cursosOrdenados);
            spinnerAdapter.notifyDataSetChanged();

            int idx = opcionesCursos.indexOf(cursoSeleccionado);
            if (idx >= 0) {
                binding.spinnerCurso.setSelection(idx);
            } else {
                cursoSeleccionado = todos;
                binding.spinnerCurso.setSelection(0);
            }

            actualizarGraficos();
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

    private void actualizarGraficos() {
        String todos = getString(R.string.todos_los_cursos);
        List<SesionEstudio> filtradas = new ArrayList<>();
        for (SesionEstudio s : todasLasSesiones) {
            if (todos.equals(cursoSeleccionado) ||
                    (s.getCurso() != null && s.getCurso().trim().equalsIgnoreCase(cursoSeleccionado))) {
                filtradas.add(s);
            }
        }

        // 1. Horas por mes (clave ordenable yyyy-MM -> etiqueta MM/yyyy)
        TreeMap<String, Double> horasPorMesOrdenado = new TreeMap<>();
        SimpleDateFormat sdfEntrada = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        SimpleDateFormat sdfClave = new SimpleDateFormat("yyyy-MM", Locale.getDefault());
        SimpleDateFormat sdfEtiqueta = new SimpleDateFormat("MMM yyyy", new Locale("es", "PE"));

        Map<String, String> etiquetasMes = new LinkedHashMap<>();

        for (SesionEstudio s : filtradas) {
            String clave = "0000-00";
            String etiqueta = "Sin fecha";
            try {
                Date d = sdfEntrada.parse(s.getFecha());
                if (d != null) {
                    clave = sdfClave.format(d);
                    etiqueta = sdfEtiqueta.format(d);
                }
            } catch (ParseException ignored) {
            }
            etiquetasMes.put(clave, etiqueta);
            Double actual = horasPorMesOrdenado.get(clave);
            horasPorMesOrdenado.put(clave, (actual != null ? actual : 0.0) + s.getHoras());
        }

        List<String> mesLabels = new ArrayList<>();
        List<Float> mesHoras = new ArrayList<>();
        for (Map.Entry<String, Double> entry : horasPorMesOrdenado.entrySet()) {
            mesLabels.add(etiquetasMes.get(entry.getKey()));
            mesHoras.add(entry.getValue().floatValue());
        }
        binding.barChartView.setData(mesLabels, mesHoras);

        // 2. Horas por tipo de actividad
        float teoria = 0f;
        float practica = 0f;
        float laboratorio = 0f;
        for (SesionEstudio s : filtradas) {
            String tipo = s.getTipoActividad() != null ? s.getTipoActividad() : "";
            if ("Laboratorio".equalsIgnoreCase(tipo)) {
                laboratorio += (float) s.getHoras();
            } else if ("Práctica".equalsIgnoreCase(tipo) || "Practica".equalsIgnoreCase(tipo)) {
                practica += (float) s.getHoras();
            } else {
                teoria += (float) s.getHoras();
            }
        }
        binding.pieChartView.setData(teoria, practica, laboratorio);

        double totalHoras = teoria + practica + laboratorio;
        binding.tvResumenHoras.setText(getString(R.string.resumen_total_horas, totalHoras, filtradas.size()));
    }
}
