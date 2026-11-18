package com.example.practica3_lab6_iot;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.practica3_lab6_iot.databinding.ActivityFormularioBinding;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class FormularioActivity extends AppCompatActivity {

    public static final String EXTRA_SESION_ID = "extra_sesion_id";
    public static final String EXTRA_CURSO = "extra_curso";
    public static final String EXTRA_FECHA = "extra_fecha";
    public static final String EXTRA_HORAS = "extra_horas";
    public static final String EXTRA_TIPO = "extra_tipo";
    public static final String EXTRA_TIMESTAMP = "extra_timestamp";

    private ActivityFormularioBinding binding;
    private SesionRepository repository;
    private String uid;
    private String sesionId;
    private long fechaTimestamp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityFormularioBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            finish();
            return;
        }
        uid = user.getUid();
        repository = new SesionRepository();

        binding.toolbar.setNavigationOnClickListener(v -> finish());

        String[] tipos = getResources().getStringArray(R.array.tipo_actividad_options);
        ArrayAdapter<String> tipoAdapter = new ArrayAdapter<>(
                this, android.R.layout.simple_dropdown_item_1line, tipos);
        binding.actTipoActividad.setAdapter(tipoAdapter);

        if (getIntent().hasExtra(EXTRA_SESION_ID)) {
            sesionId = getIntent().getStringExtra(EXTRA_SESION_ID);
            binding.toolbar.setTitle(R.string.editar_sesion_titulo);
            binding.btnGuardar.setText(R.string.actualizar_sesion);

            binding.etCurso.setText(getIntent().getStringExtra(EXTRA_CURSO));
            binding.etFecha.setText(getIntent().getStringExtra(EXTRA_FECHA));
            double horas = getIntent().getDoubleExtra(EXTRA_HORAS, 1.0);
            if (horas == Math.floor(horas)) {
                binding.etHoras.setText(String.format(Locale.US, "%.0f", horas));
            } else {
                binding.etHoras.setText(String.format(Locale.US, "%.1f", horas));
            }
            String tipo = getIntent().getStringExtra(EXTRA_TIPO);
            if (tipo != null) {
                binding.actTipoActividad.setText(tipo, false);
            }
            fechaTimestamp = getIntent().getLongExtra(EXTRA_TIMESTAMP, System.currentTimeMillis());
        } else {
            binding.toolbar.setTitle(R.string.nueva_sesion_titulo);
            binding.btnGuardar.setText(R.string.guardar_sesion);
            fechaTimestamp = System.currentTimeMillis();
            binding.etFecha.setText(formatearFecha(fechaTimestamp));
            binding.actTipoActividad.setText(tipos[0], false);
        }

        binding.etFecha.setOnClickListener(v -> mostrarDatePicker());
        binding.tilFecha.setEndIconOnClickListener(v -> mostrarDatePicker());
        binding.btnGuardar.setOnClickListener(v -> guardar());
    }

    private void mostrarDatePicker() {
        MaterialDatePicker<Long> picker = MaterialDatePicker.Builder.datePicker()
                .setTitleText(R.string.seleccionar_fecha)
                .setSelection(fechaTimestamp)
                .build();
        picker.addOnPositiveButtonClickListener(selection -> {
            fechaTimestamp = selection;
            binding.etFecha.setText(formatearFechaUtc(selection));
        });
        picker.show(getSupportFragmentManager(), "DATE_PICKER");
    }

    private String formatearFecha(long millis) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        return sdf.format(new Date(millis));
    }

    private String formatearFechaUtc(long millis) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        return sdf.format(new Date(millis));
    }

    private void guardar() {
        String curso = binding.etCurso.getText() != null ? binding.etCurso.getText().toString().trim() : "";
        String fecha = binding.etFecha.getText() != null ? binding.etFecha.getText().toString().trim() : "";
        String horasStr = binding.etHoras.getText() != null ? binding.etHoras.getText().toString().trim() : "";
        String tipo = binding.actTipoActividad.getText() != null ? binding.actTipoActividad.getText().toString().trim() : "";

        binding.tilCurso.setError(null);
        binding.tilFecha.setError(null);
        binding.tilHoras.setError(null);
        binding.tilTipoActividad.setError(null);

        if (TextUtils.isEmpty(curso)) {
            binding.tilCurso.setError(getString(R.string.error_curso_requerido));
            return;
        }
        if (TextUtils.isEmpty(fecha)) {
            binding.tilFecha.setError(getString(R.string.error_fecha_requerida));
            return;
        }
        double horas;
        try {
            horas = Double.parseDouble(horasStr);
            if (horas <= 0 || horas > 24) {
                binding.tilHoras.setError(getString(R.string.error_horas_rango));
                return;
            }
        } catch (NumberFormatException e) {
            binding.tilHoras.setError(getString(R.string.error_horas_invalidas));
            return;
        }
        if (TextUtils.isEmpty(tipo)) {
            binding.tilTipoActividad.setError(getString(R.string.error_tipo_requerido));
            return;
        }

        binding.btnGuardar.setEnabled(false);

        SesionEstudio sesion = new SesionEstudio(curso, fecha, horas, tipo, fechaTimestamp);
        if (sesionId != null) {
            sesion.setId(sesionId);
        }

        repository.guardarSesion(uid, sesion,
                unused -> {
                    Toast.makeText(this,
                            sesionId == null ? R.string.sesion_guardada : R.string.sesion_actualizada,
                            Toast.LENGTH_SHORT).show();
                    finish();
                },
                e -> {
                    binding.btnGuardar.setEnabled(true);
                    Toast.makeText(this, getString(R.string.error_guardar_sesion), Toast.LENGTH_LONG).show();
                });
    }
}
