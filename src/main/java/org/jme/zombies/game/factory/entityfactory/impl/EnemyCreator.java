package org.jme.zombies.game.factory.entityfactory.impl;

import com.jme3.anim.util.AnimMigrationUtils;
import com.jme3.asset.AssetManager;
import com.jme3.bullet.control.BetterCharacterControl;
import com.jme3.math.Vector3f;
import com.jme3.recast4j.ai.NavMeshAgent;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.simsilica.es.EntityData;
import com.simsilica.es.EntityId;
import org.jme.zombies.game.component.AIComponent;
import org.jme.zombies.game.component.CrowdComponent;
import org.jme.zombies.game.component.DamageComponent;
import org.jme.zombies.game.component.HealthComponent;
import org.jme.zombies.game.component.NodeComponent;
import org.jme.zombies.game.controls.AgentAnimationControl;
import org.jme.zombies.game.controls.AnimatorControl;
import org.jme.zombies.game.entity.EntityType;
import org.jme.zombies.game.factory.EntityFactory.FactoryData;
import org.jme.zombies.game.factory.entityfactory.EntityCreator;
import org.recast4j.detour.NavMesh;

public class EnemyCreator extends EntityCreator<EntityType> {

    private final EntityData entityData;
    private final AssetManager assetManager;
    private final NavMesh navMesh;

    private int index = 0;

    public EnemyCreator(FactoryData data) {
        super(data.world(), data.bulletAppState());
        this.entityData = data.entityData();
        this.assetManager = data.assetManager();
        this.navMesh = data.navMesh();
    }

    @Override
    public EntityType getEntityType() {
        return EntityType.ENEMY;
    }

    @Override
    public Node createEntity(Object... params) {
        float x = (float) params[0];
        float z = (float) params[1];

        EntityId entityId = entityData.createEntity();
        Spatial model = assetManager.loadModel("Models/Zombie.j3o");

        NodeComponent nodeComponent = new NodeComponent();
        nodeComponent.entity = (Node) AnimMigrationUtils.migrate(model);

        DamageComponent damageComponent = new DamageComponent();

        var npc = nodeComponent.entity;
        npc.scale(0.8f);
        npc.setName("Enemy_" + index++);
        npc.setLocalTranslation(new Vector3f(x, 3f, z));

        npc.addControl(new BetterCharacterControl(0.5f, 3f, 10f));
        npc.addControl(new NavMeshAgent(navMesh));
        npc.addControl(new AnimatorControl());
        npc.addControl(new AgentAnimationControl());
        npc.setUserData("entityId", entityId.getId());

        npc.setShadowMode(RenderQueue.ShadowMode.CastAndReceive);

        this.render(npc);

        AIComponent aiComponent = new AIComponent();

        entityData.setComponents(
                entityId,
                aiComponent,
                nodeComponent,
                damageComponent,
                new HealthComponent(),
                new CrowdComponent()
        );

        return npc;
    }
}
