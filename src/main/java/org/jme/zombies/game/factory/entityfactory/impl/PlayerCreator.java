package org.jme.zombies.game.factory.entityfactory.impl;

import com.jme3.asset.AssetManager;
import com.jme3.bullet.collision.shapes.CapsuleCollisionShape;
import com.jme3.bullet.control.CharacterControl;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.renderer.queue.RenderQueue.ShadowMode;
import com.jme3.scene.CameraNode;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.CameraControl.ControlDirection;
import com.simsilica.es.EntityData;
import com.simsilica.es.EntityId;
import org.jme.zombies.game.component.MoveComponent;
import org.jme.zombies.game.component.NodeComponent;
import org.jme.zombies.game.component.PlayerComponent;
import org.jme.zombies.game.component.PositionComponent;
import org.jme.zombies.game.component.VelocityComponent;
import org.jme.zombies.game.entity.EntityType;
import org.jme.zombies.game.factory.EntityFactory.FactoryData;
import org.jme.zombies.game.factory.entityfactory.EntityCreator;

public class PlayerCreator extends EntityCreator<EntityType> {

    private final EntityData entityData;
    private final Camera camera;
    private final AssetManager assetManager;

    public PlayerCreator(FactoryData data) {
        super(data.world(), data.bulletAppState());
        this.entityData = data.entityData();
        this.camera = data.camera();
        this.assetManager = data.assetManager();
    }


    @Override
    public EntityType getEntityType() {
        return EntityType.PLAYER;
    }

    @Override
    public EntityId createEntity(Object... params) {
        EntityId entityId = entityData.createEntity();

        Spatial weapon = assetManager.loadModel("Weapons/Pistol.j3o");
        weapon.scale(0.3f);

        Node weaponNode = new Node("WeaponNode");

        weaponNode.attachChild(weapon);
        weaponNode.setLocalTranslation(-1.4f, -0.4f, 1.8f);
        weaponNode.rotate(0f, 160f, 0f);

        CameraNode cameraNode = new CameraNode("CameraNode", camera);
        cameraNode.setControlDir(ControlDirection.CameraToSpatial);

        cameraNode.attachChild(weaponNode);

        NodeComponent nodeComponent = new NodeComponent();
        nodeComponent.entity = new Node("Player");

        var player = nodeComponent.entity;

        var shape = new CapsuleCollisionShape(1.5f, 2f, 1);
        var control = new CharacterControl(shape, 0.01f);

        control.setJumpSpeed(20);
        control.setFallSpeed(30);
        control.setGravity(50);

        player.setLocalTranslation(new Vector3f(10f, 5, 20f));

        player.attachChild(cameraNode);
        player.addControl(control);
        player.setShadowMode(ShadowMode.CastAndReceive);

        super.render(player);

        entityData.setComponents(
                entityId,
                nodeComponent,
                new PlayerComponent(),
                new MoveComponent(),
                new VelocityComponent(),
                new PositionComponent()
        );

        return entityId;
    }
}
