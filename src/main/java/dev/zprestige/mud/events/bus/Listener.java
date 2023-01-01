package dev.zprestige.mud.events.bus;

import java.lang.reflect.Method;


public final class Listener {
    private final Method method;
    private final Object object;
    private final Class<?> event;

    public Listener(final Method method, final Object object, final Class<?> event) {
        this.method = method;
        this.object = object;
        this.event = event;
    }

    public Method getMethod() {
        return method;
    }

    public Object getObject() {
        return object;
    }

    public Class<?> getEvent() {
        return event;
    }

}