# ⚡ Instrucciones Rápidas - Lab7 Pekko SDN

## 🎯 Objetivo del Lab
Crear un **controlador SDN distribuido** donde 2 switches envían eventos cada 3 segundos a un controlador central usando **Apache Pekko Actor Model**.

---

## 📦 Qué ya tenemos en esta carpeta

- ✅ `pom.xml` - Dependencias de Pekko + Maven Shade Plugin
- ✅ `Dockerfile` - Imagen Docker (Java 17)
- ✅ `docker-compose.yml` - Orquestación: 1 controller + 2 switches
- ✅ Código Java completo (NetworkEvent, Actors, Main)
- ✅ `application.conf` - Configuración de Pekko Cluster
- ✅ `README.md` - Documentación detallada

---

## 🚀 Pasos para ejecutar HOY

### 1️⃣ Compilar (en CMD en la carpeta Lab7_20222079)
```bash
mvnw.cmd package -DskipTests
```
**Esto genera:** `target/app.jar` (fat JAR con todas las dependencias)

### 2️⃣ Construir imágenes Docker
```bash
docker-compose build
```
**Nota:** Usa el mismo Dockerfile pero diferentes ENTRYPOINT para controller y switches

### 3️⃣ Levantar los contenedores
```bash
docker-compose up
```

**Espera ver en la consola:**
- ✅ Controller iniciado como Seed Node
- ✅ Switch-1 se une al cluster
- ✅ Switch-2 se une al cluster
- ✅ Logs cada 3 segundos mostrando mensajes recibidos

---

## 📊 Qué verás en los logs

```
INFO SDNControllerActor - ═════════════════════════════════════════════════════
INFO SDNControllerActor - 📡 EVENTO RECIBIDO DEL CONTROLADOR SDN
INFO SDNControllerActor -    Switch ID       : switch-1
INFO SDNControllerActor -    Remitente Path  : pekko://SDNSystem@switch-1:2551/user/switch
INFO SDNControllerActor -    Contador Global : 1 mensajes de switch-1
INFO SDNControllerActor -    Total por switch:
INFO SDNControllerActor -      • switch-1 → 1 mensajes
INFO SDNControllerActor -      • switch-2 → 5 mensajes
INFO SDNControllerActor - ═════════════════════════════════════════════════════
```

Este es el **log de evidencia** que necesitas capturar para el informe.

---

## 📸 Evidencia de Funcionamiento

**Paso 1:** Cuando veas los logs del controller con mensajes de múltiples switches
```bash
# En otra terminal, mientras docker-compose up está corriendo:
docker-compose logs controller > evidencia_logs.txt
```

**Paso 2:** Tomar captura de pantalla de la consola mostrando:
- Líneas de "EVENTO RECIBIDO" de diferentes switches
- El ActorPath dinámico
- Los contadores incrementando

---

## 📝 Informe (máx. 1 página)

Incluye:
1. **Breve descripción:** qué es SDN, qué es Pekko Actor Model
2. **Solución implementada:** controlador + 2 switches, comunicación cada 3 seg
3. **Problemas encontrados y soluciones:**
   - ¿Tardó en conectar?
   - ¿Necesitaste ajustar timeouts?
   - ¿Algo de Pekko que no entendías?
4. **Conclusión:** qué aprendiste sobre actores y sistemas distribuidos

---

## 📤 Entregar en GitHub

Crear repo con nombre: **GTICS_LAB7_[TU_CÓDIGO]**

Subir:
```
GTICS_LAB7_20222079/
├── pom.xml
├── Dockerfile
├── docker-compose.yml
├── README.md
├── src/
├── evidencia_logs.txt          (logs del docker-compose)
├── evidencia_screenshot.png    (captura de pantalla)
└── INFORME.pdf                 (máx 1 página)
```

---

## 🆘 Si algo falla

| Síntoma | Solución |
|---------|----------|
| `ClassNotFoundException` | Verificar que `target/app.jar` existe (Paso 1) |
| `Cannot connect to seed node` | Esperar 10 segundos - red Docker tarda en conectar |
| `Port 2551 already in use` | `docker-compose down` y luego `docker-compose up` |
| `Poco o ningún log` | Revisar `docker-compose logs controller` para ver qué pasó |

---

## ✅ Checklist Final

- [ ] ✅ Compilé con `mvnw.cmd package -DskipTests`
- [ ] ✅ Ejecuté `docker-compose build`
- [ ] ✅ Ejecuté `docker-compose up`
- [ ] ✅ Vi logs del controller con eventos de múltiples switches
- [ ] ✅ Capturé screenshot de los logs
- [ ] ✅ Guardé los logs en `evidencia_logs.txt`
- [ ] ✅ Escribí informe (1 página máx)
- [ ] ✅ Subí todo a GitHub

**¡Listo! Ya está lista la solución del lab.**

