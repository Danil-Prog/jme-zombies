package org.jme.zombies.game.states;

import com.jme3.app.Application;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.math.Vector3f;
import com.simsilica.es.Entity;
import com.simsilica.es.EntityId;
import com.simsilica.es.EntitySet;
import org.jme.zombies.game.factory.EntityFactory;

import java.util.HashSet;
import java.util.Set;

public class EntityState extends AbstractAppState {

    private EntityFactory entityFactory;
    private EntityId playerId;
    private Set<EntityId> enemyIds;

    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        super.initialize(stateManager, app);

        this.entityFactory = new EntityFactory(stateManager, app);
        this.enemyIds = new HashSet<>();

        createDefaultEntity();
    }

    public EntityId getPlayerId() {
        return playerId;
    }

    public Set<EntityId> getEnemyIds() {
        return enemyIds;
    }

    public void createBall(Vector3f location, Vector3f direction) {
        entityFactory.createBall(location, direction);
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
                .orElseThrow(() -> new RuntimeException("Failed to get entity by types: " + types));
    }

    private void createDefaultEntity() {
        playerId = entityFactory.createPlayer();
        enemyIds.add(entityFactory.createEnemy(0f, 0f));
    }

    public void createEnemy() {
        entityFactory.createEnemy(0f, 0f);
    }
}
