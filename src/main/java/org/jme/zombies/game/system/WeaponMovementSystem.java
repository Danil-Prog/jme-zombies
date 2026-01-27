package org.jme.zombies.game.system;

import com.jme3.app.Application;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.renderer.Camera;
import com.simsilica.es.Entity;
import com.simsilica.es.EntityId;
import org.jme.zombies.game.component.MoveComponent;
import org.jme.zombies.game.component.NodeComponent;
import org.jme.zombies.game.component.PlayerComponent;
import org.jme.zombies.game.component.PositionComponent;
import org.jme.zombies.game.component.VelocityComponent;
import org.jme.zombies.game.factory.EntityFactory;
import org.jme.zombies.game.states.EntityState;

public class WeaponMovementSystem extends AbstractAppState {

    private Entity player;
    private Camera camera;

    @Override
    public void initialize(AppStateManager stateManager, Application app) {

        var entityState = stateManager.getState(EntityState.class);

        this.player = entityState.getEntityOrThrow(
                PlayerComponent.class,
                PositionComponent.class,
                NodeComponent.class);

        this.camera = app.getCamera();
    }

    @Override
    public void update(float tpf) {
        NodeComponent nodeComponent = player.get(NodeComponent.class);

    }
}
