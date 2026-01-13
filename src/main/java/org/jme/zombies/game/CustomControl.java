package org.jme.zombies.game;

import com.jme3.bullet.collision.PhysicsCollisionEvent;
import com.jme3.bullet.collision.PhysicsCollisionListener;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.scene.Node;

public class CustomControl extends RigidBodyControl implements PhysicsCollisionListener {

    private Node rootNode;

    public CustomControl(Node rootNode) {
        this.rootNode = rootNode;
    }

    @Override
    public void collision(PhysicsCollisionEvent event) {
//        if (event.getAppliedImpulse() < 1.0f) return;
//
//        if (event.getNodeA().getName().equals("Floor")) {
//            if (event.getNodeB().getName().equals("cannon ball")) {
//                System.out.println("Шар коснулся пола с силой: " + event.getAppliedImpulse());
//            }
//        }
    }

}
