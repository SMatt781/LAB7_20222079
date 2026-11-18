package com.example.practica3_lab6_iot;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.appcompat.app.AppCompatActivity;

import com.example.practica3_lab6_iot.databinding.ActivityMainBinding;
import com.firebase.ui.auth.AuthMethodPickerLayout;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract;
import com.firebase.ui.auth.IdpResponse;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private boolean firebaseDisponible = false;

    private final ActivityResultLauncher<Intent> signInLauncher = registerForActivityResult(
            new FirebaseAuthUIActivityResultContract(),
            result -> {
                IdpResponse response = result.getIdpResponse();
                if (result.getResultCode() == RESULT_OK) {
                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                    if (user != null) {
                        irADashboard();
                    }
                } else {
                    if (response == null) {
                        binding.tvEstadoAuth.setText(R.string.auth_cancelado);
                    } else {
                        String errorMsg = response.getError() != null
                                ? response.getError().getLocalizedMessage()
                                : getString(R.string.auth_error);
                        binding.tvEstadoAuth.setText(errorMsg);
                        Toast.makeText(this, errorMsg, Toast.LENGTH_LONG).show();
                    }
                    binding.tvEstadoAuth.setVisibility(View.VISIBLE);
                    binding.btnReintentar.setVisibility(View.VISIBLE);
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        firebaseDisponible = !FirebaseApp.getApps(this).isEmpty();
        if (!firebaseDisponible) {
            binding.tvEstadoAuth.setText(R.string.falta_google_services);
            binding.tvEstadoAuth.setVisibility(View.VISIBLE);
            binding.btnReintentar.setVisibility(View.GONE);
            return;
        }

        FirebaseAuth auth = FirebaseAuth.getInstance();
        auth.setLanguageCode("es-419");

        if (auth.getCurrentUser() != null) {
            irADashboard();
            return;
        }

        binding.btnReintentar.setOnClickListener(v -> lanzarFirebaseUI());
        lanzarFirebaseUI();
    }

    private void lanzarFirebaseUI() {
        binding.tvEstadoAuth.setVisibility(View.GONE);
        binding.btnReintentar.setVisibility(View.GONE);

        AuthMethodPickerLayout customLayout = new AuthMethodPickerLayout.Builder(R.layout.custom_login)
                .setEmailButtonId(R.id.btnEmail)
                .setGoogleButtonId(R.id.btnGoogle)
                .build();

        List<AuthUI.IdpConfig> providers = Arrays.asList(
                new AuthUI.IdpConfig.EmailBuilder().build(),
                new AuthUI.IdpConfig.GoogleBuilder().build()
        );

        Intent signInIntent = AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setAuthMethodPickerLayout(customLayout)
                .setLogo(R.drawable.ic_logo_estudio)
                .setTheme(R.style.Theme_Lab_Auth)
                .setAvailableProviders(providers)
                .build();

        signInLauncher.launch(signInIntent);
    }

    private void irADashboard() {
        Intent intent = new Intent(this, DashboardActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }
}
