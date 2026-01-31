package org.jme.zombies.game.system;

import com.jme3.app.Application;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.simsilica.es.EntitySet;
import org.jme.zombies.game.component.DetachComponent;
import org.jme.zombies.game.component.NodeComponent;
import org.jme.zombies.game.states.EntityState;

public class DetachingSystem extends AbstractAppState {

    private EntitySet detachEntities;
    private EntityState entityState;

    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        super.initialize(stateManager, app);

        this.entityState = stateManager.getState(EntityState.class);
        this.detachEntities = entityState.getEntities(NodeComponent.class, DetachComponent.class);
    }

    @Override
    public void update(float tpf) {
        detachEntities.applyChanges();

        detachEntities.forEach(entity -> {
            DetachComponent detachComponent = entity.get(DetachComponent.class);

            if (detachComponent.expireIn < System.currentTimeMillis()) {
                NodeComponent nodeComponent = entity.get(NodeComponent.class);

                entityState.removeEntityByEntityId(entity.getId());
                nodeComponent.entity.removeFromParent();

//                entityState.createEntityByType(EntityType.ENEMY, 0f, 0f);
            }
        });
    }
}
