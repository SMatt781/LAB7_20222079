package com.example.practica3_lab6_iot;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

import java.util.Locale;

public class PieChartView extends View {

    private float teoriaHoras = 0f;
    private float practicaHoras = 0f;
    private float laboratorioHoras = 0f;

    private final Paint slicePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint labelPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint legendPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint emptyPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final RectF oval = new RectF();

    private final int colorTeoria = Color.parseColor("#2563EB");
    private final int colorPractica = Color.parseColor("#F59E0B");
    private final int colorLab = Color.parseColor("#10B981");

    public PieChartView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        float density = context.getResources().getDisplayMetrics().density;

        slicePaint.setStyle(Paint.Style.FILL);

        labelPaint.setColor(Color.WHITE);
        labelPaint.setTextSize(11f * density);
        labelPaint.setFakeBoldText(true);
        labelPaint.setTextAlign(Paint.Align.CENTER);

        legendPaint.setColor(Color.parseColor("#1E293B"));
        legendPaint.setTextSize(12f * density);
        legendPaint.setTextAlign(Paint.Align.LEFT);

        emptyPaint.setColor(Color.parseColor("#64748B"));
        emptyPaint.setTextSize(14f * density);
        emptyPaint.setTextAlign(Paint.Align.CENTER);
    }

    public void setData(float teoria, float practica, float laboratorio) {
        this.teoriaHoras = Math.max(0f, teoria);
        this.practicaHoras = Math.max(0f, practica);
        this.laboratorioHoras = Math.max(0f, laboratorio);
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int width = getWidth();
        int height = getHeight();
        if (width <= 0 || height <= 0) return;

        float total = teoriaHoras + practicaHoras + laboratorioHoras;
        if (total <= 0f) {
            canvas.drawText("Sin datos para mostrar", width / 2f, height / 2f, emptyPaint);
            return;
        }

        float density = getResources().getDisplayMetrics().density;
        float legendHeight = 76f * density;
        float availableH = Math.max(100f, height - legendHeight);
        float diameter = Math.min(width * 0.65f, availableH - 16f * density);

        float cx = width / 2f;
        float cy = (availableH) / 2f;
        oval.set(cx - diameter / 2f, cy - diameter / 2f, cx + diameter / 2f, cy + diameter / 2f);

        float[] vals = {teoriaHoras, practicaHoras, laboratorioHoras};
        int[] colors = {colorTeoria, colorPractica, colorLab};
        String[] names = {"Teoría", "Práctica", "Laboratorio"};

        float startAngle = -90f;
        for (int i = 0; i < 3; i++) {
            if (vals[i] <= 0f) continue;
            float sweep = (vals[i] / total) * 360f;
            slicePaint.setColor(colors[i]);
            canvas.drawArc(oval, startAngle, sweep, true, slicePaint);

            float pct = (vals[i] / total) * 100f;
            if (pct >= 6f) {
                double midRad = Math.toRadians(startAngle + sweep / 2f);
                float rLabel = diameter * 0.32f;
                float lx = cx + (float) (rLabel * Math.cos(midRad));
                float ly = cy + (float) (rLabel * Math.sin(midRad)) + 4f * density;
                canvas.drawText(String.format(Locale.US, "%.0f%%", pct), lx, ly, labelPaint);
            }
            startAngle += sweep;
        }

        // Leyenda inferior
        float startY = availableH + 14f * density;
        float leftX = 24f * density;
        float boxSize = 12f * density;
        for (int i = 0; i < 3; i++) {
            float y = startY + i * 20f * density;
            slicePaint.setColor(colors[i]);
            canvas.drawRect(leftX, y - boxSize + 2f * density, leftX + boxSize, y + 2f * density, slicePaint);
            float pct = (vals[i] / total) * 100f;
            String text = String.format(Locale.US, "%s: %.1f h (%.1f%%)", names[i], vals[i], pct);
            canvas.drawText(text, leftX + boxSize + 8f * density, y, legendPaint);
        }
    }
}
