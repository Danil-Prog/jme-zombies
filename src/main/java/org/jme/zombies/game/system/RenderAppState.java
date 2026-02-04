package org.jme.zombies.game.system;

import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.scene.Spatial;
import com.simsilica.es.Entity;
import com.simsilica.es.EntityData;
import com.simsilica.es.EntityId;
import com.simsilica.es.EntitySet;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import org.jme.zombies.game.component.NodeComponent;
import org.jme.zombies.game.component.PositionComponent;
import org.jme.zombies.game.entity.EntityType;
import org.jme.zombies.game.states.EntityState;

public class RenderAppState extends AbstractAppState {

    private SimpleApplication app;
    private EntityData ed;
    private EntitySet entities;
    private EntityState entityState;

    private final Map<EntityId, Spatial> models;

    public RenderAppState() {
        this.models = new HashMap<>();
    }

    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        super.initialize(stateManager, app);

        this.app = (SimpleApplication) app;

        this.entityState = stateManager.getState(EntityState.class);

        ed = entityState.getEntityData();
        entities = ed.getEntities(PositionComponent.class, NodeComponent.class);
    }

    @Override
    public void update(float tpf) {
        if (entities.applyChanges()) {
            removeModels(entities.getRemovedEntities());
            addModels(entities.getAddedEntities());
            updateModels(entities.getChangedEntities());
        }
    }

    @Override
    public void cleanup() {
        entities.release();
        entities = null;
    }

    private void removeModels(Set<Entity> entities) {
        for (Entity e : entities) {
            Spatial s = models.remove(e.getId());
            s.removeFromParent();
        }
    }

    private void addModels(Set<Entity> entities) {
        for (Entity e : entities) {
            Spatial s = createVisual(e);
            models.put(e.getId(), s);
            updateModelSpatial(e, s);
            this.app.getRootNode().attachChild(s);
        }
    }

    private void updateModels(Set<Entity> entities) {
        for (Entity e : entities) {
            Spatial s = models.get(e.getId());
            updateModelSpatial(e, s);
        }
    }

    private void updateModelSpatial(Entity e, Spatial s) {
        PositionComponent positionComponent = e.get(PositionComponent.class);
        s.setLocalTranslation(positionComponent.position);
        s.setLocalRotation(positionComponent.rotate);
    }

    private Spatial createVisual(Entity e) {
        NodeComponent model = e.get(NodeComponent.class);
        System.out.println("Create new node by name:" + model.entity.getName());
        return entityState.createEntityByEntityType(EntityType.ENEMY, 0f, 0f);
    }
}
