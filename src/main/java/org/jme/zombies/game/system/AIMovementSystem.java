package org.jme.zombies.game.system;

import com.jme3.app.Application;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.asset.AssetManager;
import com.jme3.bullet.control.CharacterControl;
import com.jme3.math.Vector3f;
import com.jme3.recast4j.ai.NavMeshAgent;
import com.jme3.recast4j.ai.NavMeshAgentDebug;
import com.jme3.recast4j.ai.NavMeshPath;
import com.jme3.recast4j.ai.NavMeshPathStatus;
import com.jme3.recast4j.ai.NavMeshQueryFilter;
import com.simsilica.es.Entity;
import com.simsilica.es.EntityId;
import com.simsilica.es.EntitySet;
import java.util.Arrays;
import java.util.concurrent.Executors;
import javax.annotation.Nonnull;
import org.jme.zombies.game.component.AIComponent;
import org.jme.zombies.game.component.NodeComponent;
import org.jme.zombies.game.component.PositionComponent;
import org.jme.zombies.game.entity.EntityFactory;
import org.recast4j.detour.DefaultQueryFilter;
import org.recast4j.detour.FindRandomPointResult;
import org.recast4j.detour.NavMesh;
import org.recast4j.detour.NavMeshQuery;
import org.recast4j.detour.Result;
import static com.jme3.recast4j.recast.JmeAreaMods.POLYFLAGS_ALL;
import static com.jme3.recast4j.recast.JmeAreaMods.POLYFLAGS_DISABLED;
import static org.recast4j.detour.NavMeshQuery.FRand;

public class AIMovementSystem extends AbstractAppState {

    // AI Entities.
    private EntitySet entities;
    private Entity player;
    private AssetManager assetManager;

    private final NavMeshQuery navMeshQuery;

    public AIMovementSystem(NavMesh navMeshTerrain) {
        this.navMeshQuery = new NavMeshQuery(navMeshTerrain);
    }

    @Override
    public void initialize(
            AppStateManager stateManager,
            Application application
    ) {
        super.initialize(stateManager, application);
        EntityId playerId = EntityFactory.getPlayerEntityId();

        this.entities = EntityFactory.entityData.getEntities(
                AIComponent.class,
                NodeComponent.class
        );

        this.player = EntityFactory.entityData.getEntity(
                playerId,
                PositionComponent.class,
                NodeComponent.class
        );

        this.assetManager = application.getAssetManager();

        var executorService = Executors.newSingleThreadScheduledExecutor();
//        executorService.scheduleAtFixedRate(this::generateRandomEnemy, 2000, 5000, TimeUnit.MILLISECONDS);
    }

    @Override
    public void update(float tpf) {
        entities.applyChanges();

        entities.forEach(entity -> {
            NodeComponent enemyNodeComponent = entity.get(NodeComponent.class);
            NodeComponent playerNodeComponent = player.get(NodeComponent.class);
            AIComponent aiComponent = entity.get(AIComponent.class);

            var playerControl = playerNodeComponent.entity.getControl(CharacterControl.class);

            var agent = getAgent(aiComponent, enemyNodeComponent);

            NavMeshPath path = new NavMeshPath();

            Vector3f targetPosition = playerControl.getPhysicsLocation();
            agent.calculatePath(targetPosition, path);

            if (path.getStatus() == NavMeshPathStatus.PathComplete) {
                agent.setPath(path);
            } else {
                System.err.println("Couldn't find the path");
            }
        });
    }

    private NavMeshAgent getAgent(AIComponent aiComponent, NodeComponent nodeComponent) {
        var enemy = nodeComponent.entity;

        if (aiComponent.agent == null) {
            NavMeshAgentDebug debug = new NavMeshAgentDebug(assetManager);

            aiComponent.agent = enemy.getControl(NavMeshAgent.class);
            aiComponent.agent.setSpeed(4);
            aiComponent.agent.setStoppingDistance(3f);

            enemy.addControl(aiComponent.agent);
            enemy.addControl(debug);

            NavMeshQueryFilter filter = getNavMeshQueryFilter();

            aiComponent.agent.setQueryFilter(filter);
        }

        return enemy.getControl(NavMeshAgent.class);
    }

    @Nonnull
    private static NavMeshQueryFilter getNavMeshQueryFilter() {
        return new NavMeshQueryFilter(POLYFLAGS_ALL, POLYFLAGS_DISABLED);
    }

    private void generateRandomEnemy() {
        var filter = new DefaultQueryFilter();
        var rand = new FRand();

        Result<FindRandomPointResult> result = navMeshQuery.findRandomPoint(filter, rand);

        if (result.succeeded()) {
            FindRandomPointResult temp = result.result;
            float[] position = temp.getRandomPt();

            System.out.println("Generated new enemy position: " + Arrays.toString(position));

            EntityFactory.createEnemy(position[0], position[2]);
        }
    }
}