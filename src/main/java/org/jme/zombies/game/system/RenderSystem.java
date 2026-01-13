package org.jme.zombies.game.system;

import com.jme3.app.Application;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.simsilica.es.EntitySet;
import org.jme.zombies.game.component.ModelComponent;
import org.jme.zombies.game.component.PositionComponent;
import org.jme.zombies.game.component.VelocityComponent;
import org.jme.zombies.game.entity.EntityFactory;

public class RenderSystem extends AbstractAppState {

    private EntitySet entities;
    private AppStateManager stateManager;

    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        this.stateManager = stateManager;
        this.entities = EntityFactory.entityData.getEntities(
                ModelComponent.class,
                PositionComponent.class,
                VelocityComponent.class
        );
    }

    @Override
    public void update(float tpf) {
    }
}
