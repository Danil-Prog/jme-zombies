package org.jme.zombies.game.factory.entityfactory.impl;

import com.jme3.asset.AssetManager;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.simsilica.es.EntityData;
import com.simsilica.es.EntityId;
import org.jme.zombies.game.component.DetachComponent;
import org.jme.zombies.game.component.ItemComponent;
import org.jme.zombies.game.component.NodeComponent;
import org.jme.zombies.game.controls.RotationControl;
import org.jme.zombies.game.entity.EntityType;
import org.jme.zombies.game.factory.EntityFactory.FactoryData;
import org.jme.zombies.game.factory.entityfactory.EntityCreator;

public class ItemCreator extends EntityCreator<EntityType> {

    private final EntityData entityData;
    private final AssetManager assetManager;

    public ItemCreator(FactoryData data) {
        super(data.world(), data.bulletAppState());
        this.entityData = data.entityData();
        this.assetManager = data.assetManager();
    }

    @Override
    public EntityType getEntityType() {
        return EntityType.ITEM;
    }

    @Override
    public Node createEntity(Object... params) {
        var position = (Vector3f) params[0];

        position.y += 1f;

        var entityId = entityData.createEntity();

        Spatial model = assetManager.loadModel("Items/Gold Bag.j3o");
        model.scale(1f);

        DetachComponent detachComponent = new DetachComponent();
        detachComponent.expireIn = System.currentTimeMillis() + 15000; // 15sec

        NodeComponent nodeComponent = new NodeComponent();
        nodeComponent.entity = new Node();

        var item = nodeComponent.entity;

        item.setName("GoldBag");
        item.setLocalTranslation(position);
        item.attachChild(model);
        item.addControl(new RotationControl());

        System.out.println("Gold bag generate in position: " + position);

        this.render(item);

        entityData.setComponents(
                entityId,
                nodeComponent,
                new ItemComponent(),
                detachComponent
        );

        return item;
    }
}
