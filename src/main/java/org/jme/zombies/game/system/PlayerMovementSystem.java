package org.jme.zombies.game.system;

import com.jme3.app.Application;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.bullet.control.CharacterControl;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.simsilica.es.Entity;
import org.jme.zombies.game.component.MoveComponent;
import org.jme.zombies.game.component.NodeComponent;
import org.jme.zombies.game.component.PlayerComponent;
import org.jme.zombies.game.component.PositionComponent;
import org.jme.zombies.game.component.VelocityComponent;
import org.jme.zombies.game.states.EntityState;

public class PlayerMovementSystem extends AbstractAppState {

    private Entity player;
    private Camera camera;

    private final Vector3f walkDirection = new Vector3f();

    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        var entityState = stateManager.getState(EntityState.class);

        this.player = entityState.getEntityOrThrow(
                PlayerComponent.class,
                NodeComponent.class,
                MoveComponent.class,
                VelocityComponent.class,
                PositionComponent.class);

        this.camera = app.getCamera();

        camera.setLocation(new Vector3f(-100, 5, 10));
        camera.lookAt(Vector3f.ZERO, Vector3f.UNIT_Y);
        camera.setFov(75f);
    }

    @Override
    public void update(float tpf) {
        MoveComponent moveComponent = player.get(MoveComponent.class);
        NodeComponent nodeComponent = player.get(NodeComponent.class);
        VelocityComponent velocityComponent = player.get(VelocityComponent.class);
        PositionComponent positionComponent = player.get(PositionComponent.class);

        Vector3f camDir = camera.getDirection().clone().multLocal(0.3f);
        Vector3f camLeft = camera.getLeft().clone().multLocal(0.3f);

        CharacterControl control = nodeComponent.entity.getControl(CharacterControl.class);

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

        control.setWalkDirection(walkDirection);
        camera.setLocation(control.getPhysicsLocation());

        positionComponent.position = control.getPhysicsLocation();
    }
}
