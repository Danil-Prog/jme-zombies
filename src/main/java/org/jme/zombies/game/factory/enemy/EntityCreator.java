package org.jme.zombies.game.factory.enemy;

import com.simsilica.es.EntityId;
import org.jme.zombies.game.entity.EntityType;

public interface EntityCreator<T extends EntityType>{

    EntityType getEntityType();

    EntityId createEntity();
}
