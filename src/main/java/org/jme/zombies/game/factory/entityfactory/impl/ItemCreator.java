package org.jme.zombies.game.factory.entityfactory.impl;

import com.jme3.bullet.BulletAppState;
import com.jme3.scene.Node;
import com.simsilica.es.EntityData;
import com.simsilica.es.EntityId;
import org.jme.zombies.game.entity.EntityType;
import org.jme.zombies.game.factory.EntityFactory.FactoryData;
import org.jme.zombies.game.factory.entityfactory.EntityCreator;

public class ItemCreator extends EntityCreator<EntityType> {

    private final EntityData entityData;

    public ItemCreator(FactoryData data) {
        super(data.world(), data.bulletAppState());
        this.entityData = data.entityData();
    }

    @Override
    public EntityType getEntityType() {
        return EntityType.ITEM;
    }

    @Override
    public EntityId createEntity(Object... params) {
        return null;
    }
}
