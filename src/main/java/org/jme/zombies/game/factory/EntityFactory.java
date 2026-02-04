package org.jme.zombies.game.factory;

import com.jme3.app.Application;
import com.jme3.app.state.AppStateManager;
import com.jme3.asset.AssetManager;
import com.jme3.bullet.BulletAppState;
import com.jme3.renderer.Camera;
import com.jme3.scene.Node;
import com.simsilica.es.Entity;
import com.simsilica.es.EntityData;
import com.simsilica.es.EntityId;
import com.simsilica.es.base.DefaultEntityData;
import org.jme.zombies.GameApplication;
import org.jme.zombies.game.entity.EntityType;
import org.jme.zombies.game.factory.entityfactory.EntityCreator;
import org.jme.zombies.game.factory.entityfactory.impl.BulletCreator;
import org.jme.zombies.game.factory.entityfactory.impl.EnemyCreator;
import org.jme.zombies.game.factory.entityfactory.impl.ItemCreator;
import org.jme.zombies.game.factory.entityfactory.impl.PlayerCreator;
import org.jme.zombies.game.states.NavigationMeshAppState;
import org.jme.zombies.game.states.WorldAppState;
import org.recast4j.detour.NavMesh;

import java.util.HashMap;

public class EntityFactory {

    private final EntityData entityData = new DefaultEntityData();

    private final HashMap<EntityType, EntityCreator<EntityType>> entityCreators = new HashMap<>();

    public EntityFactory(AppStateManager stateManager, Application app) {
        var application = ((GameApplication) app);

        var worldAppState = stateManager.getState(WorldAppState.class);
        var navigationMeshAppState = stateManager.getState(NavigationMeshAppState.class);

        Node worldNode = worldAppState.getWorldNode();
        AssetManager assetManager = application.getAssetManager();

        BulletAppState bulletAppState = stateManager.getState(BulletAppState.class);
        NavMesh navMesh = navigationMeshAppState.getNavigationMesh();

        var camera = app.getCamera();

        FactoryData factoryData = new FactoryData(
                worldNode,
                entityData,
                bulletAppState,
                assetManager,
                navMesh,
                camera
        );

        entityCreators.put(EntityType.PLAYER, new PlayerCreator(factoryData));
        entityCreators.put(EntityType.ENEMY, new EnemyCreator(factoryData));
        entityCreators.put(EntityType.BULLET, new BulletCreator(factoryData));
        entityCreators.put(EntityType.ITEM, new ItemCreator(factoryData));
    }

    public Node createEntity(EntityType type, Object... params) {
        return entityCreators.get(type).createEntity(params);
    }

    public Entity getEntity(long entityId, Class... types) {
        return entityData.getEntity(new EntityId(entityId), types);
    }

    public void removeEntity(EntityId entityId) {
        entityData.removeEntity(entityId);
    }

    public EntityData getEntityData() {
        return entityData;
    }

    public boolean removeComponent(EntityId entityId, Class type) {
        return entityData.removeComponent(entityId, type);
    }

    public record FactoryData(
            Node world,
            EntityData entityData,
            BulletAppState bulletAppState,
            AssetManager assetManager,
            NavMesh navMesh,
            Camera camera
    ) {
    }
}
