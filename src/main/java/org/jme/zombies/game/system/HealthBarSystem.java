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
import org.jme.zombies.game.component.HealthComponent;
import org.jme.zombies.game.component.NodeComponent;
import org.jme.zombies.game.states.EntityState;

public class HealthBarSystem extends AbstractAppState {

    private EntitySet entities;
    private AssetManager assetManager;

    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        super.initialize(stateManager, app);

        var entityState = stateManager.getState(EntityState.class);

        this.entities = entityState.getEntities(
                NodeComponent.class,
                HealthComponent.class
        );

        this.assetManager = app.getAssetManager();
    }

    @Override
    public void update(float tpf) {
        entities.applyChanges();

//        BillboardControl
        entities.forEach(entity -> {
            HealthComponent healthComponent = entity.get(HealthComponent.class);
            NodeComponent nodeComponent = entity.get(NodeComponent.class);

            var enemy = nodeComponent.entity;

            Node node = (Node) enemy.getChild("Billboard");

            if (node == null) {
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
//                billboard.addControl(control);

                enemy.attachChild(billboard);
            }
        });
    }

}
