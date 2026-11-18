package com.example.practica3_lab6_iot;

import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.practica3_lab6_iot.databinding.ItemSesionBinding;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class SesionAdapter extends RecyclerView.Adapter<SesionAdapter.SesionViewHolder> {

    public interface OnSesionActionListener {
        void onEditar(SesionEstudio sesion);
        void onEliminar(SesionEstudio sesion);
    }

    private final List<SesionEstudio> sesiones = new ArrayList<>();
    private final OnSesionActionListener listener;

    public SesionAdapter(OnSesionActionListener listener) {
        this.listener = listener;
    }

    public void actualizarLista(List<SesionEstudio> nuevasSesiones) {
        sesiones.clear();
        sesiones.addAll(nuevasSesiones);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public SesionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemSesionBinding binding = ItemSesionBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new SesionViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull SesionViewHolder holder, int position) {
        holder.bind(sesiones.get(position));
    }

    @Override
    public int getItemCount() {
        return sesiones.size();
    }

    class SesionViewHolder extends RecyclerView.ViewHolder {
        private final ItemSesionBinding binding;

        SesionViewHolder(ItemSesionBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(SesionEstudio sesion) {
            binding.tvCurso.setText(sesion.getCurso());

            String horasStr;
            if (sesion.getHoras() == Math.floor(sesion.getHoras())) {
                horasStr = String.format(Locale.getDefault(), "%.0f h", sesion.getHoras());
            } else {
                horasStr = String.format(Locale.US, "%.1f h", sesion.getHoras());
            }
            binding.tvFechaHoras.setText(sesion.getFecha() + " · " + horasStr);

            String tipo = sesion.getTipoActividad() != null ? sesion.getTipoActividad() : "Teoría";
            binding.tvTipoActividad.setText(tipo);

            int bgColorRes;
            int textColorRes;
            if ("Laboratorio".equalsIgnoreCase(tipo)) {
                bgColorRes = R.color.chip_lab_bg;
                textColorRes = R.color.chip_lab_text;
            } else if ("Práctica".equalsIgnoreCase(tipo) || "Practica".equalsIgnoreCase(tipo)) {
                bgColorRes = R.color.chip_practica_bg;
                textColorRes = R.color.chip_practica_text;
            } else {
                bgColorRes = R.color.chip_teoria_bg;
                textColorRes = R.color.chip_teoria_text;
            }

            binding.tvTipoActividad.setBackgroundTintList(
                    ColorStateList.valueOf(ContextCompat.getColor(binding.getRoot().getContext(), bgColorRes)));
            binding.tvTipoActividad.setTextColor(
                    ContextCompat.getColor(binding.getRoot().getContext(), textColorRes));

            binding.btnEditar.setOnClickListener(v -> listener.onEditar(sesion));
            binding.btnEliminar.setOnClickListener(v -> listener.onEliminar(sesion));
        }
    }
}
