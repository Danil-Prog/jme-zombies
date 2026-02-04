package org.jme.zombies.game.component;

import com.jme3.scene.Node;
import com.simsilica.es.EntityComponent;

public class NodeComponent implements EntityComponent {
    public Node entity;

    public NodeComponent() {
        // for serialization
    }
}
