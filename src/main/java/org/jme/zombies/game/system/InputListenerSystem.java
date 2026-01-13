package org.jme.zombies.game.system;

import com.jme3.app.Application;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.input.InputManager;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.simsilica.es.Entity;
import com.simsilica.es.EntityId;
import org.jme.zombies.game.component.CharacterControlComponent;
import org.jme.zombies.game.component.MoveComponent;
import org.jme.zombies.game.constants.Keyboard;
import org.jme.zombies.game.entity.EntityFactory;

public class InputListenerSystem extends AbstractAppState implements ActionListener {

    private InputManager inputManager;
    private MoveComponent moveComponent;
    private CharacterControlComponent characterComponent;

    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        EntityId playerId = EntityFactory.getPlayerEntityId();

        inputManager = app.getInputManager();
        Entity entity = EntityFactory.entityData.getEntity(
                playerId,
                MoveComponent.class,
                CharacterControlComponent.class
        );

        moveComponent = entity.get(MoveComponent.class);
        characterComponent = entity.get(CharacterControlComponent.class);

        mappingKeyboard();
    }

    public void mappingKeyboard() {
        String left = Keyboard.LEFT.getName();
        String right = Keyboard.RIGHT.getName();
        String up = Keyboard.UP.getName();
        String down = Keyboard.DOWN.getName();
        String jump = Keyboard.SPACE.getName();

        inputManager.addMapping(left, new KeyTrigger(KeyInput.KEY_A));
        inputManager.addMapping(right, new KeyTrigger(KeyInput.KEY_D));
        inputManager.addMapping(up, new KeyTrigger(KeyInput.KEY_W));
        inputManager.addMapping(down, new KeyTrigger(KeyInput.KEY_S));
        inputManager.addMapping(jump, new KeyTrigger(KeyInput.KEY_SPACE));

        inputManager.addListener(this, left);
        inputManager.addListener(this, right);
        inputManager.addListener(this, up);
        inputManager.addListener(this, down);
        inputManager.addListener(this, jump);
    }

    @Override
    public void onAction(String binding, boolean value, float tpf) {
        Keyboard keyboard = Keyboard.fromName(binding);
        switch (keyboard) {
            case LEFT -> moveComponent.left = value;
            case RIGHT -> moveComponent.right = value;
            case UP -> moveComponent.up = value;
            case DOWN -> moveComponent.down = value;
            case SPACE -> characterComponent.control.jump();
        }
    }


}
