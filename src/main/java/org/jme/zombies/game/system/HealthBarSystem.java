package org.jme.zombies.game.system;

import com.jme3.app.Application;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.renderer.queue.RenderQueue.ShadowMode;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial.BatchHint;
import com.jme3.scene.control.BillboardControl;
import com.jme3.scene.control.BillboardControl.Alignment;
import com.jme3.scene.shape.Quad;
import com.simsilica.es.EntitySet;
import org.jme.zombies.game.component.AIComponent;
import org.jme.zombies.game.component.DamageComponent;
import org.jme.zombies.game.component.DetachComponent;
import org.jme.zombies.game.component.HealthComponent;
import org.jme.zombies.game.component.NodeComponent;
import org.jme.zombies.game.controls.AgentAnimationControl;
import org.jme.zombies.game.states.EntityState;

public class HealthBarSystem extends AbstractAppState {

    private EntitySet entities;
    private AssetManager assetManager;
    private EntityState entityState;

    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        super.initialize(stateManager, app);

        this.entityState = stateManager.getState(EntityState.class);

        this.entities = entityState.getEntities(
                NodeComponent.class,
                HealthComponent.class,
                DamageComponent.class
        );

        this.assetManager = app.getAssetManager();
    }

    @Override
    public void update(float tpf) {
        entities.applyChanges();

        entities.forEach(entity -> {
            HealthComponent healthComponent = entity.get(HealthComponent.class);
            NodeComponent nodeComponent = entity.get(NodeComponent.class);
            DamageComponent damageComponent = entity.get(DamageComponent.class);

            var enemy = nodeComponent.entity;

            Node billboard = (Node) enemy.getChild("Billboard");

            if (billboard == null) {
                billboard = healthBillboardInit();
                enemy.attachChild(billboard);
            }

            if (healthComponent.health < 0 || !damageComponent.isHit) {
                return;
            }

            Geometry geometry = (Geometry) billboard.getChild("HP");
            healthComponent.health -= damageComponent.damage;

            System.out.println(healthComponent.health);

            float width = healthComponent.health * 0.01f;

            Quad quad = (Quad) geometry.getMesh();
            quad.updateGeometry(width, 0.15f);

            damageComponent.isHit = false;

            // Animate enemy's death and remove it from the terrain
            if (healthComponent.health < 0) {
                enemy.getControl(AgentAnimationControl.class).markDead();
                entityState.removeComponentByEntityId(entity.getId(), AIComponent.class);
                entity.set(new DetachComponent(System.currentTimeMillis() + 2500));
            }
        });
    }

    private Node healthBillboardInit() {
        Node billboard = new Node("Billboard");

        BillboardControl control = new BillboardControl();
        control.setAlignment(Alignment.Camera);

        billboard.setShadowMode(ShadowMode.Off);
        billboard.setBatchHint(BatchHint.Inherit);
        billboard.setLocalTranslation(0f, 4.5f, 0f);

        float width = 1f;
        float height = 0.15f;

        Material material = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        material.setColor("Color", ColorRGBA.Red);

        Quad quad = new Quad(width, height);

        Geometry geometry = new Geometry("HP", quad);
        geometry.setMaterial(material);
        geometry.setLocalTranslation(-width / 2f, 0f, 0f);

        billboard.attachChild(geometry);

        return billboard;
    }

}
