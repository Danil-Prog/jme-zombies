package org.jme.zombies.game.listeners;

import com.jme3.app.Application;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.bullet.control.CharacterControl;
import com.jme3.input.InputManager;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.simsilica.es.Entity;
import org.jme.zombies.game.component.MoveComponent;
import org.jme.zombies.game.component.NodeComponent;
import org.jme.zombies.game.component.PlayerComponent;
import org.jme.zombies.game.constants.InputTriggers;
import org.jme.zombies.game.states.EntityState;

public class InputListener extends AbstractAppState implements ActionListener {

    private MoveComponent moveComponent;
    private CharacterControl control;
    private InputManager inputManager;

    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        super.initialize(stateManager, app);

        var entityState = stateManager.getState(EntityState.class);

        this.inputManager = app.getInputManager();

        Entity entity = entityState.getEntityOrThrow(
                PlayerComponent.class,
                MoveComponent.class,
                NodeComponent.class
        );

        moveComponent = entity.get(MoveComponent.class);
        NodeComponent nodeComponent = entity.get(NodeComponent.class);

        control = nodeComponent.entity.getControl(CharacterControl.class);

        addMappingAndListener();
    }

    @Override
    public void onAction(String binding, boolean value, float tpf) {
        InputTriggers inputTriggers = InputTriggers.fromName(binding);

        switch (inputTriggers) {
            case LEFT -> moveComponent.left = value;
            case RIGHT -> moveComponent.right = value;
            case UP -> moveComponent.up = value;
            case DOWN -> moveComponent.down = value;
            case SPACE -> control.jump();
        }
    }

    private void addMappingAndListener() {
        String left = InputTriggers.LEFT.getName();
        String right = InputTriggers.RIGHT.getName();
        String up = InputTriggers.UP.getName();
        String down = InputTriggers.DOWN.getName();
        String jump = InputTriggers.SPACE.getName();

        inputManager.addMapping(left, new KeyTrigger(KeyInput.KEY_A));
        inputManager.addMapping(right, new KeyTrigger(KeyInput.KEY_D));
        inputManager.addMapping(up, new KeyTrigger(KeyInput.KEY_W));
        inputManager.addMapping(down, new KeyTrigger(KeyInput.KEY_S));
        inputManager.addMapping(jump, new KeyTrigger(KeyInput.KEY_SPACE));

        inputManager.addListener(this, left, right, up, down, jump);
    }
}
