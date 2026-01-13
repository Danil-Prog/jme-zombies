package org.jme.zombies.game.component;

import com.jme3.bullet.collision.shapes.BoxCollisionShape;
import com.jme3.bullet.control.BetterCharacterControl;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Box;
import com.simsilica.es.EntityComponent;

public class ModelComponent implements EntityComponent {
    public Mesh box;
    public Spatial geometry;
    public BetterCharacterControl betterCharacterControl;
}
