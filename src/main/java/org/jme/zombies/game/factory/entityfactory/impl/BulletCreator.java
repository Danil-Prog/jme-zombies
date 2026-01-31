package org.jme.zombies.game.factory.entityfactory.impl;

import com.jme3.asset.AssetManager;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.math.Vector3f;
import com.jme3.renderer.queue.RenderQueue.ShadowMode;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.simsilica.es.EntityData;
import com.simsilica.es.EntityId;
import org.jme.zombies.game.component.DetachComponent;
import org.jme.zombies.game.component.NodeComponent;
import org.jme.zombies.game.component.ShootComponent;
import org.jme.zombies.game.controls.BulletControl;
import org.jme.zombies.game.entity.EntityType;
import org.jme.zombies.game.factory.EntityFactory.FactoryData;
import org.jme.zombies.game.factory.entityfactory.EntityCreator;
import org.jme.zombies.game.utils.ShapeUtils;

public class BulletCreator extends EntityCreator<EntityType> {

    private final EntityData entityData;
    private final AssetManager assetManager;
    private final ShapeUtils shapeUtils;

    private int index = 0;

    public BulletCreator(FactoryData data) {
        super(data.world(), data.bulletAppState());
        this.entityData = data.entityData();
        this.assetManager = data.assetManager();
        this.shapeUtils = new ShapeUtils(assetManager);
    }

    @Override
    public EntityType getEntityType() {
        return EntityType.BULLET;
    }

    @Override
    public EntityId createEntity(Object... params) {
        var location = (Vector3f) params[0];
        var direction = (Vector3f) params[1];

        EntityId id = entityData.createEntity();

        Geometry sphere = shapeUtils.createBall();
        RigidBodyControl rigidBodyControl = new RigidBodyControl(0.1f);
        BulletControl bulletControl = new BulletControl();

        NodeComponent nodeComponent = new NodeComponent();
        nodeComponent.entity = new Node();

        DetachComponent detachComponent = new DetachComponent();
        detachComponent.expireIn = System.currentTimeMillis() + 5000;

        ShootComponent shootComponent = new ShootComponent();

        var bullet = nodeComponent.entity;

        bullet.attachChild(sphere);
        bullet.setName("Bullet_" + index++);
        bullet.addControl(rigidBodyControl);
        bullet.setShadowMode(ShadowMode.CastAndReceive);
        bullet.addControl(bulletControl);

        rigidBodyControl.setPhysicsLocation(location.add(direction.mult(3f)));
        rigidBodyControl.setLinearVelocity(direction.mult(20));


        super.render(bullet);

        entityData.setComponents(
                id,
                nodeComponent,
                shootComponent,
                detachComponent
        );

        return id;
    }
}
