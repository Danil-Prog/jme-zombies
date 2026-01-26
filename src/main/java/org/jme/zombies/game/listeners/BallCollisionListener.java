package org.jme.zombies.game.listeners;

import com.jme3.asset.AssetManager;
import com.jme3.bullet.collision.PhysicsCollisionEvent;
import com.jme3.bullet.collision.PhysicsCollisionListener;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import org.jme.zombies.Jmezombies;

public class BallCollisionListener implements PhysicsCollisionListener {

    private final AssetManager assetManager;

    public BallCollisionListener(Jmezombies app) {
        this.assetManager = app.getAssetManager();
    }

    @Override
    public void collision(PhysicsCollisionEvent event) {
        var nameA = event.getNodeA().getName();
        var nameB = event.getNodeB().getName();

        if ((nameA.equals("ball") || nameB.equals("ball")) && (nameA.contains("Enemy_") || nameB.equals("Enemy_"))) {
            System.out.println("Попал по врагу: " + nameA);
            Node enemy = (Node) event.getNodeA();

            activateRedFlash(enemy);
        }
    }

    public void activateRedFlash(Node node) {
        Material material = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md");
        material.setColor("Diffuse", ColorRGBA.Red);
        material.setColor("Ambient", ColorRGBA.Red);
        material.setBoolean("UseMaterialColors", true);

        for (Spatial child : node.getChildren()) {
            if (child instanceof Geometry) {
                child.setMaterial(material);
            }
        }
    }
}
