package hei.school.app.endpoint.event;

import hei.school.app.endpoint.event.model.PojaEvent;
import java.util.function.Consumer;

public interface EventConsumer<T extends PojaEvent> extends Consumer<T> {
}