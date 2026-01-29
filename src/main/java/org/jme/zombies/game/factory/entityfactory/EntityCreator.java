package org.jme.zombies.game.factory.entityfactory;

import com.jme3.bullet.BulletAppState;
import com.jme3.scene.Node;
import com.simsilica.es.EntityId;
import org.jme.zombies.game.entity.EntityType;

public abstract class EntityCreator<T extends EntityType> {

    private final Node world;
    private final BulletAppState bulletAppState;

    public EntityCreator(Node world, BulletAppState bulletAppState) {
        this.world = world;
        this.bulletAppState = bulletAppState;
    }

    public abstract EntityType getEntityType();

    public abstract EntityId createEntity(Object... params);

    protected void render(Node entity) {
        bulletAppState.getPhysicsSpace().add(entity);
        world.attachChild(entity);
    }
}
