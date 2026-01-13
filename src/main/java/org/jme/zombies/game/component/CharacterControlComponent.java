package org.jme.zombies.game.component;

import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.bullet.control.CharacterControl;
import com.simsilica.es.EntityComponent;

public class CharacterControlComponent implements EntityComponent {

    public CollisionShape shape;
    public CharacterControl control;
}
