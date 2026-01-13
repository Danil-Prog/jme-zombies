package org.jme.zombies.game.system;

import com.jme3.app.Application;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.simsilica.es.Entity;
import com.simsilica.es.EntityId;
import org.jme.zombies.game.component.CharacterControlComponent;
import org.jme.zombies.game.component.MoveComponent;
import org.jme.zombies.game.component.PositionComponent;
import org.jme.zombies.game.component.VelocityComponent;
import org.jme.zombies.game.entity.EntityFactory;

public class PlayerMovementSystem extends AbstractAppState {

    private Entity player;
    private Camera camera;

    private final Vector3f walkDirection = new Vector3f();

    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        EntityId playerId = EntityFactory.getPlayerEntityId();

        this.player = EntityFactory.entityData.getEntity(
                playerId,
                MoveComponent.class,
                VelocityComponent.class,
                CharacterControlComponent.class,
                PositionComponent.class
        );

        this.camera = app.getCamera();

        camera.setLocation(new Vector3f(-100, 5, 10));
        camera.lookAt(Vector3f.ZERO, Vector3f.UNIT_Y);
        camera.setFov(75f);
    }

    @Override
    public void update(float tpf) {
        MoveComponent moveComponent = player.get(MoveComponent.class);
        CharacterControlComponent controlComponent = player.get(CharacterControlComponent.class);
        VelocityComponent velocityComponent = player.get(VelocityComponent.class);
        PositionComponent positionComponent = player.get(PositionComponent.class);

        Vector3f camDir = camera.getDirection().clone().multLocal(0.3f);
        Vector3f camLeft = camera.getLeft().clone().multLocal(0.3f);

        walkDirection.set(0, 0, 0);

        if (moveComponent.left) {
            walkDirection.addLocal(camLeft);
        }

        if (moveComponent.right) {
            walkDirection.addLocal(camLeft.negate());
        }

        if (moveComponent.up) {
            walkDirection.addLocal(camDir);
        }

        if (moveComponent.down) {
            walkDirection.addLocal(camDir.negate());
        }

        walkDirection.multLocal(velocityComponent.velocity);

        controlComponent.control.setWalkDirection(walkDirection);
        camera.setLocation(controlComponent.control.getPhysicsLocation());

        positionComponent.position = controlComponent.control.getPhysicsLocation();
    }
}
