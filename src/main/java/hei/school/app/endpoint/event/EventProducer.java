package hei.school.app.endpoint.event;

import hei.school.app.endpoint.event.model.PojaEvent;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EventProducer<T extends PojaEvent> {

    private final List<EventConsumer<T>> consumers;

    public void accept(List<T> events) {
        events.forEach(event -> {
            consumers.forEach(consumer -> consumer.accept(event));
        });
    }
}