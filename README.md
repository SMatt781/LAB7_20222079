Pasos para ejecutar

## 🚀 Pasos para Ejecutar

### Paso 1: Compilar el proyecto

```bash
cd Lab7_20222079
mvnw.cmd package -DskipTests
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

**Qué sucede:**

1. **Controlador inicia** → Se convierte en Seed Node en `pekko://SDNSystem@controller:2551`
2. **Switch-1 inicia (5 segundos después)** → Se une al cluster
3. **Switch-2 inicia (6 segundos después)** → Se une al cluster
4. **Switches comienzan a enviar eventos** cada 3 segundos al controlador
5. **Controlador imprime logs** detallados con el actor path de cada switch

---

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

---

## 🔑 Conceptos Clave

### 1. **Modelo de Actores (Apache Pekko)**
- Cada componente (Controller, Switch-1, Switch-2) es un **Actor**
- Los actores se comunican mediante **mensajes inmutables** (NetworkEvent)
- **Sin estado compartido:** cada actor tiene su propio estado (map de contadores)

### 2. **Pekko Cluster**
- Múltiples ActorSystems en diferentes máquinas/contenedores
- **Seed Node:** el controlador actúa como nodo de referencia
- Los switches se unen automáticamente via `cluster.seed-nodes`

### 3. **actorSelection vs actorRef**
- **actorSelection:** búsqueda "fuzzy" (intent-based, reconecta si falla)
- **actorRef:** referencia directa (más eficiente, pero falla si el actor muere)
- Los switches usan `actorSelection` porque el controlador puede estar arriba/abajo

### 4. **Serialización**
- `NetworkEvent` implementa `java.io.Serializable`
- Pekko usa Jackson para convertir a JSON y viajar por la red
- Configurado en `application.conf`: `"java.io.Serializable" = jackson-json`

### 5. **Docker Compose Networking**
- Red interna `sdn-network` permite comunicación por hostname
- `pekko.remote.artery.canonical.hostname = ${?HOSTNAME}` lee el nombre del contenedor
- Cada contenedor se identifica como: `pekko://SDNSystem@{hostname}:2551`

---

## 🛠️ Comandos Útiles

```bash
# Ver contenedores en ejecución
docker-compose ps

# Ver logs del controlador en tiempo real
docker-compose logs -f controller

# Ver logs de un switch específico
docker-compose logs -f switch-1

# Ver logs combinados
docker-compose logs -f

# Parar los contenedores
docker-compose down

# Limpiar imágenes
docker image rm sdn-pekko-controller sdn-pekko-switch-1 sdn-pekko-switch-2

# Ejecutar comando dentro de un contenedor (ej: verificar conectividad)
docker-compose exec controller sh -c "ping -c 1 switch-1"
```

---

## 🧪 Validación de Funcionamiento

✅ **El lab funciona correctamente si:**

1. El controlador se levanta sin errores
2. Los switches se unen al cluster (ves "cluster member joined" en logs)
3. Los switches envían eventos cada 3 segundos
4. El controlador recibe eventos y imprime logs detallados
5. Los logs muestran el ActorPath dinámico: `pekko://SDNSystem@switch-1:2551/user/switch`
6. El contador de mensajes incrementa correctamente

---

## 📝 Entregables Requeridos

- ✅ **Código Fuente:** GitHub (GTICS_LAB7_[TU_CÓDIGO])
- ✅ **Dockerfile y docker-compose.yml:** en la raíz del proyecto
- ✅ **Configuración:** `src/main/resources/application.conf`
- ⬜ **Captura de pantalla:** logs del controlador mostrando flujo de mensajes
- ⬜ **Informe:** máximo 1 página describiendo tu experiencia

---

## 🔧 Troubleshooting

| Error | Causa | Solución |
|-------|-------|----------|
| `java.lang.ClassNotFoundException: com.example.sdn.ControllerMain` | Dockerfile mal configurado | Verificar `ADD target/app.jar` y `ENTRYPOINT` |
| `Cannot connect to seed node controller:2551` | Red Docker no configurada | Verificar `networks` en docker-compose.yml |
| `java.io.NotSerializableException` | NetworkEvent no serializable | Agregar `implements java.io.Serializable` |
| `reference.conf not found` | maven-shade-plugin no fusionó configs | Verificar plugin en pom.xml |

---

## 📚 Referencias

- [Apache Pekko Documentation](https://pekko.apache.org/)
- [Pekko Cluster Guide](https://pekko.apache.org/docs/pekko/current/cluster-usage.html)
- [Docker Compose Networking](https://docs.docker.com/compose/networking/)
