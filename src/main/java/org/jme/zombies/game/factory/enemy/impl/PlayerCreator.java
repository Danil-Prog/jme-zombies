package org.jme.zombies.game.factory.enemy.impl;

import com.simsilica.es.EntityData;
import com.simsilica.es.EntityId;
import org.jme.zombies.game.entity.EntityType;
import org.jme.zombies.game.factory.enemy.EntityCreator;

public class PlayerCreator implements EntityCreator<EntityType> {

    private final EntityData entityData;

    public PlayerCreator(EntityData entityData) {
        this.entityData = entityData;
    }


    @Override
    public EntityType getEntityType() {
        return EntityType.PLAYER;
    }

    @Override
    public EntityId createEntity() {
        return null;
    }
}
