package hei.school.app.endpoint.event.model;

import hei.school.app.PojaGenerated;
import lombok.Getter;

import java.time.Duration;
import java.util.UUID;

@Getter
@PojaGenerated
public abstract class PojaEvent {
    private final UUID id = UUID.randomUUID();
    private final long createdAt = System.currentTimeMillis();

    public abstract Duration maxConsumerDuration();
    public abstract Duration maxConsumerBackoffBetweenRetries();
}