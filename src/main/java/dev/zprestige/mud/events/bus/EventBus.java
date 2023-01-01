package dev.zprestige.mud.events.bus;

import java.util.ArrayList;
import java.util.Arrays;

public final class EventBus {
    private final ArrayList<Listener> listeners;

    public EventBus() {
        listeners = new ArrayList<>();
    }

    public void registerListener(final Object object) {
        listeners(object);
    }

    public void unregisterListener(final Object object) {
        listeners.removeIf(listener -> listener.getObject().equals(object));
    }

    private void listeners(final Object object) {
        final Class<?> c = object.getClass();
        Arrays.stream(c.getDeclaredMethods()).forEach(method -> {
            if (method.isAnnotationPresent(EventListener.class)) {
                final Class<?>[] parameterTypes = method.getParameterTypes();
                listeners.add(new Listener(method, object, parameterTypes[0]));
            }
        });
    }

    public void invoke(final Event event) {
        new ArrayList<>(listeners).stream().filter(listener -> listener.getEvent().equals(event.getClass())).forEach(listener -> {
            try {
                listener.getMethod().invoke(listener.getObject(), event);
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        });
    }
}