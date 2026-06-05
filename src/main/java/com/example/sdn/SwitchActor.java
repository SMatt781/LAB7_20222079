package com.example.sdn;

import org.apache.pekko.actor.AbstractActor;
import org.apache.pekko.actor.ActorRef;
import org.apache.pekko.actor.ActorSelection;
import org.apache.pekko.actor.Props;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;

/**
 * Actor del Switch (Data Plane)
 * Envía un NetworkEvent cada 3 segundos al Controlador
 * Usa actorSelection para localizar el controlador en el cluster
 * El controlador está en pekko://SDNSystem@controller:2551/user/controller
 */
public class SwitchActor extends AbstractActor {
    private static final Logger log = LoggerFactory.getLogger(SwitchActor.class);

    private final String switchId;
    private int eventCounter = 0;

    public SwitchActor(String switchId) {
        this.switchId = switchId;
    }

    public static Props props(String switchId) {
        return Props.create(SwitchActor.class, switchId);
    }

    @Override
    public void preStart() throws Exception {
        super.preStart();
        log.info("🔌 Switch {} iniciado. Enviando eventos cada 3 segundos...", switchId);

        // Programar el envío inicial
        getContext().getSelf().tell(SendEvent.INSTANCE, ActorRef.noSender());
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
            .match(SendEvent.class, msg -> sendEventToController())
            .build();
    }

    private void sendEventToController() {
        eventCounter++;

        // Usar actorSelection para localizar el controlador
        ActorSelection controllerSelection = getContext().actorSelection(
            "pekko://SDNSystem@controller:2551/user/controller"
        );

        NetworkEvent event = new NetworkEvent(
            switchId,
            "TELEMETRY_REPORT",
            System.currentTimeMillis()
        );

        controllerSelection.tell(event, getSelf());
        log.debug("📤 {} envió evento #{} al controller", switchId, eventCounter);

        // Programar el próximo envío en 3 segundos
        // scheduleOnce está en system().scheduler(), NO en getContext()
        getContext().system().scheduler().scheduleOnce(
            Duration.ofSeconds(3),
            getSelf(),
            SendEvent.INSTANCE,
            getContext().dispatcher(),
            ActorRef.noSender()
        );
    }


    // Mensaje interno para disparar el envío de eventos

    enum SendEvent {
        INSTANCE
    }
}
