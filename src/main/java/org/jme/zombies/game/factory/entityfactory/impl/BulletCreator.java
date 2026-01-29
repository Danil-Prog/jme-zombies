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
        RigidBodyControl ballControl = new RigidBodyControl(0.1f);

        NodeComponent nodeComponent = new NodeComponent();
        nodeComponent.entity = new Node();

        DetachComponent detachComponent = new DetachComponent();
        detachComponent.expireIn = System.currentTimeMillis() + 5000;

        ShootComponent shootComponent = new ShootComponent();

        var ball = nodeComponent.entity;

        ball.attachChild(sphere);
        ball.setName("Ball_" + index++);
        ball.addControl(ballControl);
        ball.setShadowMode(ShadowMode.CastAndReceive);

        ballControl.setPhysicsLocation(location.add(direction.mult(3f)));
        ballControl.setLinearVelocity(direction.mult(20));

        super.render(ball);

        entityData.setComponents(
                id,
                nodeComponent,
                shootComponent,
                detachComponent
        );

        return id;
    }
}
