package org.jme.zombies.game.listeners;

import com.jme3.asset.AssetManager;
import com.jme3.bullet.collision.PhysicsCollisionEvent;
import com.jme3.bullet.collision.PhysicsCollisionListener;
import com.jme3.scene.Node;
import org.jme.zombies.GameApplication;
import org.jme.zombies.game.component.DamageComponent;
import org.jme.zombies.game.controls.BulletControl;
import org.jme.zombies.game.states.EntityState;

import java.util.Random;

public class BulletCollisionListener implements PhysicsCollisionListener {

    private final AssetManager assetManager;
    private final Random random;
    private final EntityState entityState;

    public BulletCollisionListener(GameApplication app) {
        this.assetManager = app.getAssetManager();
        this.random = new Random();
        this.entityState = app.getStateManager().getState(EntityState.class);
    }

    @Override
    public void collision(PhysicsCollisionEvent event) {
        var nameA = event.getNodeA().getName();
        var nameB = event.getNodeB().getName();

        Node bullet = null;
        Node target = null;

        if (nameA.contains("Bullet") && nameB.contains("Enemy")) {
            bullet = (Node) event.getNodeA();
            target = (Node) event.getNodeB();
        } else if (nameB.contains("Bullet") && nameA.contains("Enemy")) {
            bullet = (Node) event.getNodeB();
            target = (Node) event.getNodeA();
        }

        if (bullet == null || target == null) {
            return;
        }

        BulletControl bulletControl = bullet.getControl(BulletControl.class);
        if (bulletControl == null || bulletControl.isHasHit()) return;

        bulletControl.markHit();

        var entityId = (long) target.getUserData("entityId");

        var enemy = entityState.getEntityById(entityId, DamageComponent.class);

        var damageComponent = enemy.get(DamageComponent.class);

        if (damageComponent == null) {
            return;
        }

        damageComponent.isHit = true;
        damageComponent.damage = random.nextFloat(5, 20);

        System.out.println("Попал по врагу: " + target.getName() + ", нанес урону: " + damageComponent.damage);
    }
}
