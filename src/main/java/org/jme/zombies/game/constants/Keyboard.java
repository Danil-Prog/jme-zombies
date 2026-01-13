package org.jme.zombies.game.constants;

import java.util.Arrays;

public enum Keyboard {

    LEFT("Left"),
    RIGHT("Right"),
    UP("Up"),
    DOWN("Down"),
    SPACE("Space");

    private final String name;

    Keyboard(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public static Keyboard fromName(String name) {
        return Arrays.stream(Keyboard.values())
                .filter(keyboard -> keyboard.getName().equals(name))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Failed to get keyboard by name: " + name));
    }
}
