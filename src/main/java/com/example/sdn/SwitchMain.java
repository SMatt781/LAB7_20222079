package com.example.sdn;

import org.apache.pekko.actor.ActorSystem;
import org.apache.pekko.actor.ActorRef;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Punto de entrada de un Switch
 * Crea su propio ActorSystem con nombre "SDNSystem"
 * El hostname viene de variable de entorno HOSTNAME (Docker lo asigna)
 *  Se une al cluster usando el seed-node del controlador
 * Instancia un SwitchActor que comienza a enviar eventos
 */
public class SwitchMain {
    private static final Logger log = LoggerFactory.getLogger(SwitchMain.class);

    public static void main(String[] args) {
        String switchId = System.getenv("SWITCH_ID");
        if (switchId == null) {
            switchId = "unknown-switch";
        }

        log.info("═══════════════════════════════════════════════════");
        log.info("Iniciando SWITCH: {}", switchId);
        log.info("═══════════════════════════════════════════════════");

        // ActorSystem carga automáticamente application.conf
        // El hostname viene de la variable de entorno HOSTNAME en Docker
        ActorSystem system = ActorSystem.create("SDNSystem");

        // Crear el actor del switch con su ID
        ActorRef switchActor = system.actorOf(SwitchActor.props(switchId), "switch");

        log.info("✅ Switch {} iniciado", switchId);
        log.info("Se conectará automáticamente al controlador...");
    }
}
