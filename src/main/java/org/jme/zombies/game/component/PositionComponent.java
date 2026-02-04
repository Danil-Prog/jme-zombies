package org.jme.zombies.game.component;

import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.simsilica.es.EntityComponent;

public class PositionComponent implements EntityComponent {
    public Vector3f position = new Vector3f();
    public Quaternion rotate = new Quaternion();

    public PositionComponent() {
        // for serialization
    }
}
