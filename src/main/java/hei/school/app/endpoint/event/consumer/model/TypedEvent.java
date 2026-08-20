package hei.school.app.endpoint.event.consumer.model;

import hei.school.app.PojaGenerated;
import hei.school.app.endpoint.event.model.PojaEvent;

@PojaGenerated
public record TypedEvent(String typeName, PojaEvent payload) {}
