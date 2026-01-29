package org.jme.zombies.game.states;

import com.jme3.app.Application;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.simsilica.es.Entity;
import com.simsilica.es.EntityId;
import com.simsilica.es.EntitySet;
import org.jme.zombies.game.entity.EntityType;
import org.jme.zombies.game.factory.EntityFactory;

import java.util.Arrays;

public class EntityState extends AbstractAppState {

    private EntityFactory entityFactory;

    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        super.initialize(stateManager, app);

        this.entityFactory = new EntityFactory(stateManager, app);

        this.createEntityByType(EntityType.PLAYER);
        this.createEntityByType(EntityType.ENEMY, 0f, 0f);
    }

    public void createEntityByType(EntityType type, Object... params) {
        entityFactory.createEntity(type, params);
    }

    public void removeEntityById(EntityId entityId) {
        entityFactory.removeEntity(entityId);
    }

    public EntitySet getEntities(Class... types) {
        return entityFactory.getEntityData().getEntities(types);
    }

    public Entity getEntityOrThrow(Class... types) {
        return entityFactory.getEntityData()
                .getEntities(types)
                .stream()
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Failed to get entity by types: " + Arrays.toString(types)));
    }
}
