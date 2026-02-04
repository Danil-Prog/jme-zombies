package org.jme.zombies.game.server;

import com.simsilica.es.Entity;
import com.simsilica.es.EntityData;
import com.simsilica.es.EntityId;
import com.simsilica.es.EntitySet;
import org.jme.zombies.game.component.NodeComponent;
import org.jme.zombies.game.component.PositionComponent;

public class GameLogic {

    private final EntityData ed;
    private final EntitySet invaders;

    public GameLogic(EntityData ed) {
        this.ed = ed;
        for (int x = -20; x < 20; x += 4) {
            for (int y = 0; y < 20; y += 4) {
                EntityId invader = ed.createEntity();
                this.ed.setComponents(invader,
                        new PositionComponent(),
                        new NodeComponent()
                );
            }
        }
        invaders = ed.getEntities(PositionComponent.class, NodeComponent.class);
    }

    public void update() {
        invaders.applyChanges();
        for (Entity e : invaders) {
        }
    }
}
