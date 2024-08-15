package com.ngocrong.backend.event;

import com.ngocrong.backend.character.Char;
import com.ngocrong.backend.lib.RandomCollection;

public abstract class Event {
    private static Event event;

    public RandomCollection<Integer> items;

    public static boolean isEvent() {
        return event != null;
    }

    public static void exchange(int type, Char _c) {
        event.action(type, _c);
    }

    public static RandomCollection<Integer> getItems() {
        return event.items;
    }

    public Event() {
        items = new RandomCollection<>();
    }

    public abstract void action(int type, Char _c);
}
