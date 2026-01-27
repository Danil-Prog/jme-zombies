package org.jme.zombies.game.factory;

import com.jme3.anim.util.AnimMigrationUtils;
import com.jme3.app.Application;
import com.jme3.app.state.AppStateManager;
import com.jme3.asset.AssetManager;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.collision.shapes.CapsuleCollisionShape;
import com.jme3.bullet.control.BetterCharacterControl;
import com.jme3.bullet.control.CharacterControl;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.math.Vector3f;
import com.jme3.recast4j.ai.NavMeshAgent;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.renderer.queue.RenderQueue.ShadowMode;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.simsilica.es.EntityData;
import com.simsilica.es.EntityId;
import com.simsilica.es.base.DefaultEntityData;
import org.jme.zombies.GameApplication;
import org.jme.zombies.game.component.AIComponent;
import org.jme.zombies.game.component.DetachComponent;
import org.jme.zombies.game.component.MoveComponent;
import org.jme.zombies.game.component.NodeComponent;
import org.jme.zombies.game.component.PlayerComponent;
import org.jme.zombies.game.component.PositionComponent;
import org.jme.zombies.game.component.ShootComponent;
import org.jme.zombies.game.component.VelocityComponent;
import org.jme.zombies.game.controls.AgentAnimationControl;
import org.jme.zombies.game.controls.AnimatorControl;
import org.jme.zombies.game.states.NavigationMeshAppState;
import org.jme.zombies.game.states.WorldAppState;
import org.jme.zombies.game.utils.ShapeUtils;
import org.recast4j.detour.NavMesh;

public class EntityFactory {

    private final EntityData entityData = new DefaultEntityData();

    private final Node worldNode;
    private final AssetManager assetManager;
    private final BulletAppState bulletAppState;
    private final NavMesh navMesh;

    private static int index = 0;

    public EntityFactory(AppStateManager stateManager, Application app) {
        var application = ((GameApplication) app);

        var worldAppState = stateManager.getState(WorldAppState.class);
        var navigationMeshAppState = stateManager.getState(NavigationMeshAppState.class);

        this.worldNode = worldAppState.getWorldNode();
        this.assetManager = application.getAssetManager();

        this.bulletAppState = stateManager.getState(BulletAppState.class);
        this.navMesh = navigationMeshAppState.getNavigationMesh();
    }

    public EntityId createPlayer() {
        EntityId entityId = entityData.createEntity();

        NodeComponent nodeComponent = new NodeComponent();
        nodeComponent.entity = new Node("Player");

        var player = nodeComponent.entity;

        var shape = new CapsuleCollisionShape(1.5f, 2f, 1);
        var control = new CharacterControl(shape, 0.01f);

        control.setJumpSpeed(20);
        control.setFallSpeed(30);
        control.setGravity(50);

        player.setLocalTranslation(new Vector3f(0f, 10, 0f));

        player.addControl(control);
        player.setShadowMode(ShadowMode.CastAndReceive);

        worldNode.attachChild(player);

        bulletAppState.getPhysicsSpace().add(player);

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

    public EntityId createEnemy(float x, float z) {
        EntityId entityId = entityData.createEntity();
        Spatial model = assetManager.loadModel("Models/Zombie.j3o");

        NodeComponent nodeComponent = new NodeComponent();
        nodeComponent.entity = (Node) AnimMigrationUtils.migrate(model);

        var npc = nodeComponent.entity;
        npc.scale(0.8f);
        npc.setName("Enemy_" + index++);
        npc.setLocalTranslation(new Vector3f(x, 3f, z));

        npc.addControl(new BetterCharacterControl(0.5f, 3f, 10f));
        npc.addControl(new NavMeshAgent(navMesh));
        npc.addControl(new AnimatorControl());
        npc.addControl(new AgentAnimationControl());

        npc.setShadowMode(RenderQueue.ShadowMode.CastAndReceive);

        bulletAppState.getPhysicsSpace().add(npc);
        worldNode.attachChild(npc);

        AIComponent aiComponent = new AIComponent();

        entityData.setComponents(
                entityId,
                aiComponent,
                nodeComponent
        );

        return entityId;
    }

    public void createBall(Vector3f location, Vector3f direction) {
        EntityId id = entityData.createEntity();

        ShapeUtils shapeUtils = new ShapeUtils(assetManager);

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

        bulletAppState.getPhysicsSpace().add(ball);
        worldNode.attachChild(ball);

        entityData.setComponents(
                id,
                nodeComponent,
                shootComponent,
                detachComponent
        );
    }

    public void removeEntity(EntityId entityId) {
        entityData.removeEntity(entityId);
    }

    public EntityData getEntityData() {
        return entityData;
    }
}
