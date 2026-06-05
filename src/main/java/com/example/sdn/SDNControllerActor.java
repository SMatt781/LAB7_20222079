package com.example.sdn;

import org.apache.pekko.actor.AbstractActor;
import org.apache.pekko.actor.ActorRef;
import org.apache.pekko.actor.Props;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class SDNControllerActor extends AbstractActor {
    private static final Logger log = LoggerFactory.getLogger(SDNControllerActor.class);

    private final Map<String, Integer> messageCountBySwitch = new HashMap<>();

    public static Props props() {
        return Props.create(SDNControllerActor.class);
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
            .match(NetworkEvent.class, this::handleNetworkEvent)
            .matchAny(msg -> log.warn("Mensaje desconocido: {}", msg))
            .build();
    }

    private void handleNetworkEvent(NetworkEvent event) {
        // Incrementar contador para este switch
        String switchId = event.switchId();
        messageCountBySwitch.put(switchId,
            messageCountBySwitch.getOrDefault(switchId, 0) + 1);

        // ActorPath del switch que envió el mensaje
        String senderPath = getSender().path().toString();

        // Log detallado
        log.info("═════════════════════════════════════════════════════");
        log.info("📡 EVENTO RECIBIDO DEL CONTROLADOR SDN");
        log.info("─────────────────────────────────────────────────────");
        log.info("   Switch ID       : {}", switchId);
        log.info("   Tipo Evento     : {}", event.eventType());
        log.info("   Timestamp       : {}", event.timestamp());
        log.info("   Remitente Path  : {}", senderPath);
        log.info("   Contador Global : {} mensajes de {}", messageCountBySwitch.get(switchId), switchId);
        log.info("─────────────────────────────────────────────────────");
        log.info("   Total por switch:");
        messageCountBySwitch.forEach((id, count) ->
            log.info("     • {} → {} mensajes", id, count)
        );
        log.info("═════════════════════════════════════════════════════");
    }
}
