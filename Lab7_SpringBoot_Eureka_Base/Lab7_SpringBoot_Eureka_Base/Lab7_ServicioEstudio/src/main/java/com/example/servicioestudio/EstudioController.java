package com.example.servicioestudio;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/estudio")
public class EstudioController {

    @GetMapping("/estado")
    public Map<String, String> estado() {
        return Map.of("servicio", "Lab7-Servicio-Estudio", "estado", "activo");
    }

    @GetMapping("/resumen/{curso}/{horas}")
    public Map<String, Object> resumen(
            @PathVariable String curso,
            @PathVariable double horas) {
        String recomendacion = horas >= 10
                ? "Buen ritmo acumulado"
                : "Se recomienda reforzar horas de estudio";
        return Map.of(
                "curso", curso,
                "horas", horas,
                "recomendacion", recomendacion
        );
    }
}
