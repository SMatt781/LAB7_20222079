package com.example.sdn;

import org.apache.pekko.actor.ActorSystem;
import org.apache.pekko.actor.ActorRef;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ControllerMain {
    private static final Logger log = LoggerFactory.getLogger(ControllerMain.class);

    public static void main(String[] args) {
        log.info("═══════════════════════════════════════════════════");
        log.info("Iniciando CONTROLADOR SDN");
        log.info("═══════════════════════════════════════════════════");

        // ActorSystem carga automaticamente application.conf
        ActorSystem system = ActorSystem.create("SDNSystem");

        // Crea el actor del controlador
        ActorRef controller = system.actorOf(SDNControllerActor.props(), "controller");

        log.info("✅ Controlador iniciado en pekko://SDNSystem@controller:2551/user/controller");
        log.info("Esperando conexiones de switches...");
    }
}
