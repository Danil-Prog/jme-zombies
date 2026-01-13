package org.jme.zombies.game.system;

import com.jme3.app.Application;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.asset.AssetManager;
import com.jme3.math.Vector3f;
import com.jme3.recast4j.ai.*;
import com.simsilica.es.Entity;
import com.simsilica.es.EntityId;
import com.simsilica.es.EntitySet;
import org.jme.zombies.game.component.AIComponent;
import org.jme.zombies.game.component.ModelComponent;
import org.jme.zombies.game.component.NameComponent;
import org.jme.zombies.game.component.PositionComponent;
import org.jme.zombies.game.entity.EntityFactory;
import org.recast4j.detour.*;

import javax.annotation.Nonnull;
import java.util.concurrent.Executors;

import static com.jme3.recast4j.recast.JmeAreaMods.*;
import static org.recast4j.detour.NavMeshQuery.FRand;

public class AIMovementSystem extends AbstractAppState {

    /**
     * AI Entities.
     */
    private EntitySet entities;
    private NavMesh navMeshTerrain;
    private Entity player;
    private AssetManager assetManager;

    private NavMeshQuery navMeshQuery;

    public AIMovementSystem(NavMesh navMeshTerrain) {
        this.navMeshTerrain = navMeshTerrain;
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
                NameComponent.class,
                ModelComponent.class
        );

        this.player = EntityFactory.entityData.getEntity(
                playerId,
                PositionComponent.class
        );

        this.assetManager = application.getAssetManager();

        var executorService = Executors.newSingleThreadScheduledExecutor();
//        executorService.scheduleAtFixedRate(this::generateRandomEnemy, 500, 5000, TimeUnit.MILLISECONDS);
    }

    @Override
    public void update(float tpf) {
        entities.forEach(entity -> {
            ModelComponent modelComponent = entity.get(ModelComponent.class);
            AIComponent aiComponent = entity.get(AIComponent.class);

            PositionComponent playerPosition = player.get(PositionComponent.class);

//            System.out.println("Player position: " + playerPosition.position);

            if (aiComponent.agent == null) {
                aiComponent.agent = new NavMeshAgent(navMeshTerrain);

                modelComponent.geometry.addControl(aiComponent.agent);
                modelComponent.geometry.addControl(new NavMeshAgentDebug(assetManager));

                aiComponent.agent.setSpatial(modelComponent.geometry);

                NavMeshQueryFilter filter = getNavMeshQueryFilter();


                aiComponent.agent.setQueryFilter(filter);
            }

            NavMeshPath path = new NavMeshPath();

            Vector3f targetPosition = playerPosition.position;
            aiComponent.agent.calculatePath(targetPosition, path);
//
//            System.out.println(path.getStatus());
//
//            System.out.println("Path from " + modelComponent.geometry.getWorldTranslation() + " to " + playerPosition.position);

            if (path.getStatus() == NavMeshPathStatus.PathComplete) {
                aiComponent.agent.setPath(path);

                System.out.println(path.getCorners());

//                System.out.println("Нашел путь: " + aiComponent.agent.);
            }

        });
    }

    @Nonnull
    private static NavMeshQueryFilter getNavMeshQueryFilter() {
        int includeFlags = POLYFLAGS_WALK | POLYFLAGS_DOOR | POLYFLAGS_SWIM | POLYFLAGS_JUMP;

        float[] polyExtents = new float[]{2, 2, 2};

        NavMeshQueryFilter filter = new NavMeshQueryFilter(includeFlags, POLYFLAGS_DISABLED);
//      filter.setPolyExtents(polyExtents);

        return filter;
    }

    private void generateRandomEnemy() {
        var filter = new DefaultQueryFilter();
        var rand = new FRand();

        Result<FindRandomPointResult> result = navMeshQuery.findRandomPoint(filter, rand);

        if (result.succeeded()) {
            FindRandomPointResult temp = result.result;
            float[] position = temp.getRandomPt();

            EntityFactory.createEnemy(position[0], position[1]);
        }
    }
}