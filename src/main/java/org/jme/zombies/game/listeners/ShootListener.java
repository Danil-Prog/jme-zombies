package org.jme.zombies.game.listeners;

import com.jme3.app.Application;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.input.InputManager;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.renderer.Camera;
import com.simsilica.es.Entity;
import org.jme.zombies.game.component.NodeComponent;
import org.jme.zombies.game.component.PlayerComponent;
import org.jme.zombies.game.constants.InputTriggers;
import org.jme.zombies.game.entity.EntityType;
import org.jme.zombies.game.states.EntityState;

/**
 * Tracks the left mouse button click and creates entity shot.
 */
public class ShootListener extends AbstractAppState implements ActionListener {

    private Camera camera;
    private EntityState entityState;
    private InputManager inputManager;

    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        super.initialize(stateManager, app);

        this.entityState = stateManager.getState(EntityState.class);
        this.camera = app.getCamera();
        this.inputManager = app.getInputManager();

        addMappingAndListener();
    }

    @Override
    public void onAction(String name, boolean isPressed, float tpf) {
        if (!isPressed) {
            entityState.createEntityByType(EntityType.BULLET, camera.getLocation(), camera.getDirection());
        }
    }

    private void addMappingAndListener() {
        String mouseLeft = InputTriggers.MOUSE_LEFT.getName();

        inputManager.addMapping(mouseLeft, new MouseButtonTrigger(MouseInput.BUTTON_LEFT));
        inputManager.addListener(this, mouseLeft);
    }
}
