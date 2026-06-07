Pasos para ejecutar

## 🚀 Pasos para Ejecutar

### Paso 1: Compilar el proyecto

```bash
cd Lab7_20222079
En maven package
```

**Resultado esperado:** Se genera `target/app.jar` (fat JAR con todas las dependencias)

### Paso 2: Construir imágenes Docker

```bash
docker-compose build
```

**Nota:** Docker crea 3 imágenes de la misma Dockerfile:
- `sdn-controller` con ENTRYPOINT hacia `ControllerMain`
- `sdn-switch-1` con ENTRYPOINT hacia `SwitchMain` + `SWITCH_ID=switch-1`
- `sdn-switch-2` con ENTRYPOINT hacia `SwitchMain` + `SWITCH_ID=switch-2`

### Paso 3: Levantar los contenedores

```bash
docker-compose up
```


## 📊 Logs Esperados

### Terminal del Controlador

```
INFO SDNControllerActor - ═════════════════════════════════════════════════════
INFO SDNControllerActor - 📡 EVENTO RECIBIDO DEL CONTROLADOR SDN
INFO SDNControllerActor - ─────────────────────────────────────────────────────
INFO SDNControllerActor -    Switch ID       : switch-1
INFO SDNControllerActor -    Tipo Evento     : TELEMETRY_REPORT
INFO SDNControllerActor -    Timestamp       : 1717530234567
INFO SDNControllerActor -    Remitente Path  : pekko://SDNSystem@switch-1:2551/user/switch
INFO SDNControllerActor -    Contador Global : 1 mensajes de switch-1
INFO SDNControllerActor - ─────────────────────────────────────────────────────
INFO SDNControllerActor -    Total por switch:
INFO SDNControllerActor -      • switch-1 → 1 mensajes
INFO SDNControllerActor - ═════════════════════════════════════════════════════
```
