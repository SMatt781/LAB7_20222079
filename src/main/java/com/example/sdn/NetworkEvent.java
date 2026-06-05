package com.example.sdn;

/**
 * Mensaje de telemetría que viaja entre Actores
 */
public record NetworkEvent(
    String switchId,
    String eventType,
    long timestamp
) implements java.io.Serializable {}
