package com.example.serviciogateway;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api")
public class GatewayController {

    private final EstudioFeignClient estudioClient;

    public GatewayController(EstudioFeignClient estudioClient) {
        this.estudioClient = estudioClient;
    }

    @GetMapping("/estado")
    public Map<String, String> estado() {
        return estudioClient.obtenerEstado();
    }

    @GetMapping("/resumen/{curso}/{horas}")
    public Map<String, Object> resumen(@PathVariable String curso,
                                       @PathVariable double horas) {
        return estudioClient.obtenerResumen(curso, horas);
    }
}
