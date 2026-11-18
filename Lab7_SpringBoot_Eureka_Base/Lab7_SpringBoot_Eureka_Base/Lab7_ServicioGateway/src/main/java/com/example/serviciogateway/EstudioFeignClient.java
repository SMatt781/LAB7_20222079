package com.example.serviciogateway;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Map;

@FeignClient(name = "Lab7-Servicio-Estudio")
public interface EstudioFeignClient {

    @GetMapping("/estudio/estado")
    Map<String, String> obtenerEstado();

    @GetMapping("/estudio/resumen/{curso}/{horas}")
    Map<String, Object> obtenerResumen(@PathVariable("curso") String curso,
                                       @PathVariable("horas") double horas);
}
