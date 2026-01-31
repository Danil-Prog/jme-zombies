package org.jme.zombies.game.system;

import com.jme3.app.Application;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.bullet.BulletAppState;
import com.simsilica.es.EntitySet;
import org.jme.zombies.game.component.DetachComponent;
import org.jme.zombies.game.component.NodeComponent;
import org.jme.zombies.game.entity.EntityType;
import org.jme.zombies.game.states.EntityState;

public class DetachingSystem extends AbstractAppState {

    private EntitySet detachEntities;
    private EntityState entityState;
    private BulletAppState bulletAppState;

    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        super.initialize(stateManager, app);

        this.entityState = stateManager.getState(EntityState.class);
        this.detachEntities = entityState.getEntities(NodeComponent.class, DetachComponent.class);
        this.bulletAppState = stateManager.getState(BulletAppState.class);
    }

    @Override
    public void update(float tpf) {
        detachEntities.applyChanges();

        detachEntities.forEach(entity -> {
            DetachComponent detachComponent = entity.get(DetachComponent.class);

            if (detachComponent.expireIn < System.currentTimeMillis()) {
                NodeComponent nodeComponent = entity.get(NodeComponent.class);

                var entityName = nodeComponent.entity.getName();

                if (entityName.contains("Enemy")) {
                    entityState.createEntityByEntiyType(EntityType.ENEMY, 0f, 0f);
                    entityState.createEntityByEntiyType(EntityType.ENEMY, 0f, 0f);
                }

                entityState.removeEntityByEntityId(entity.getId());
                nodeComponent.entity.removeFromParent();

                bulletAppState.getPhysicsSpace().removeAll(nodeComponent.entity);
            }
        });
    }
}
