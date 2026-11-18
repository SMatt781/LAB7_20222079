package com.example.practica3_lab6_iot;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class BarChartView extends View {

    private final List<String> labels = new ArrayList<>();
    private final List<Float> values = new ArrayList<>();

    private final Paint barPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint gridPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint valuePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint emptyPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final RectF barRect = new RectF();

    public BarChartView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        float density = context.getResources().getDisplayMetrics().density;

        barPaint.setColor(Color.parseColor("#1E40AF"));
        barPaint.setStyle(Paint.Style.FILL);

        gridPaint.setColor(Color.parseColor("#E2E8F0"));
        gridPaint.setStrokeWidth(1f * density);

        textPaint.setColor(Color.parseColor("#475569"));
        textPaint.setTextSize(11f * density);
        textPaint.setTextAlign(Paint.Align.CENTER);

        valuePaint.setColor(Color.parseColor("#1E293B"));
        valuePaint.setTextSize(11f * density);
        valuePaint.setFakeBoldText(true);
        valuePaint.setTextAlign(Paint.Align.CENTER);

        emptyPaint.setColor(Color.parseColor("#64748B"));
        emptyPaint.setTextSize(14f * density);
        emptyPaint.setTextAlign(Paint.Align.CENTER);
    }

    public void setData(List<String> newLabels, List<Float> newValues) {
        labels.clear();
        values.clear();
        if (newLabels != null && newValues != null) {
            labels.addAll(newLabels);
            values.addAll(newValues);
        }
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int width = getWidth();
        int height = getHeight();
        if (width <= 0 || height <= 0) return;

        if (values.isEmpty()) {
            canvas.drawText("Sin datos para mostrar", width / 2f, height / 2f, emptyPaint);
            return;
        }

        float density = getResources().getDisplayMetrics().density;
        float leftPad = 38f * density;
        float rightPad = 16f * density;
        float topPad = 24f * density;
        float bottomPad = 36f * density;

        float chartWidth = width - leftPad - rightPad;
        float chartHeight = height - topPad - bottomPad;
        if (chartWidth <= 0 || chartHeight <= 0) return;

        float maxVal = 0f;
        for (Float v : values) {
            if (v > maxVal) maxVal = v;
        }
        if (maxVal <= 0f) maxVal = 1f;

        // Líneas horizontales de guía
        textPaint.setTextAlign(Paint.Align.RIGHT);
        int steps = 4;
        for (int i = 0; i <= steps; i++) {
            float ratio = i / (float) steps;
            float y = topPad + chartHeight * (1f - ratio);
            canvas.drawLine(leftPad, y, width - rightPad, y, gridPaint);
            float tickVal = maxVal * ratio;
            canvas.drawText(String.format(Locale.US, "%.1f", tickVal),
                    leftPad - 6f * density, y + 4f * density, textPaint);
        }

        textPaint.setTextAlign(Paint.Align.CENTER);
        int count = values.size();
        float slotWidth = chartWidth / count;
        float barWidth = Math.min(slotWidth * 0.55f, 44f * density);
        float radius = 6f * density;

        for (int i = 0; i < count; i++) {
            float val = values.get(i);
            float barHeight = (val / maxVal) * chartHeight;
            float centerX = leftPad + slotWidth * i + slotWidth / 2f;
            float left = centerX - barWidth / 2f;
            float right = centerX + barWidth / 2f;
            float bottom = topPad + chartHeight;
            float top = bottom - barHeight;

            barRect.set(left, top, right, bottom);
            canvas.drawRoundRect(barRect, radius, radius, barPaint);

            String valLabel = val == Math.floor(val)
                    ? String.format(Locale.US, "%.0fh", val)
                    : String.format(Locale.US, "%.1fh", val);
            canvas.drawText(valLabel, centerX, top - 6f * density, valuePaint);

            String label = labels.get(i);
            canvas.drawText(label, centerX, bottom + 18f * density, textPaint);
        }
    }
}
