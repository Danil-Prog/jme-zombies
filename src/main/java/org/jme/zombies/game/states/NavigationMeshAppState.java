package org.jme.zombies.game.states;

import com.jme3.app.Application;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.recast4j.geom.InputGeomProviderBuilder;
import com.jme3.recast4j.geom.JmeInputGeomProvider;
import com.jme3.recast4j.geom.JmeRecastBuilder;
import com.jme3.recast4j.geom.JmeRecastVoxelization;
import com.jme3.recast4j.geom.NavMeshModifier;
import com.jme3.recast4j.geom.OffMeshLink;
import com.jme3.recast4j.geom.Telemetry;
import com.jme3.recast4j.recast.NavMeshDebugRenderer;
import com.jme3.recast4j.recast.RecastConfigBuilder;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.SceneGraphVisitorAdapter;
import org.jme.zombies.GameApplication;
import org.recast4j.detour.MeshData;
import org.recast4j.detour.NavMesh;
import org.recast4j.detour.NavMeshBuilder;
import org.recast4j.detour.NavMeshDataCreateParams;
import org.recast4j.recast.Heightfield;
import org.recast4j.recast.PolyMesh;
import org.recast4j.recast.PolyMeshDetail;
import org.recast4j.recast.RecastBuilder.RecastBuilderResult;
import org.recast4j.recast.RecastBuilderConfig;
import org.recast4j.recast.RecastConfig;
import org.recast4j.recast.RecastConstants;

import static com.jme3.recast4j.recast.JmeAreaMods.AREAMOD_DOOR;
import static com.jme3.recast4j.recast.JmeAreaMods.AREAMOD_GRASS;
import static com.jme3.recast4j.recast.JmeAreaMods.AREAMOD_GROUND;
import static com.jme3.recast4j.recast.JmeAreaMods.AREAMOD_ROAD;
import static com.jme3.recast4j.recast.JmeAreaMods.AREAMOD_WATER;
import static com.jme3.recast4j.recast.JmeAreaMods.POLYAREA_TYPE_DOOR;
import static com.jme3.recast4j.recast.JmeAreaMods.POLYAREA_TYPE_GRASS;
import static com.jme3.recast4j.recast.JmeAreaMods.POLYAREA_TYPE_GROUND;
import static com.jme3.recast4j.recast.JmeAreaMods.POLYAREA_TYPE_ROAD;
import static com.jme3.recast4j.recast.JmeAreaMods.POLYAREA_TYPE_WATER;
import static com.jme3.recast4j.recast.JmeAreaMods.POLYFLAGS_DOOR;
import static com.jme3.recast4j.recast.JmeAreaMods.POLYFLAGS_SWIM;
import static com.jme3.recast4j.recast.JmeAreaMods.POLYFLAGS_WALK;

public class NavigationMeshAppState extends AbstractAppState {

    private Node worldNode;
    private NavMeshDebugRenderer navMeshRenderer;
    private NavMesh navigationMesh;

    private final static float agentRadius = 0.3f;
    private final static float agentHeight = 1.7f;
    private final static float agentMaxClimb = 0.3f; // > 2*ch
    private final static float cellSize = 0.1f;      // cs=r/2
    private final static float cellHeight = 0.5f;    // ch=cs/2 but not < .1f


    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        super.initialize(stateManager, app);

        var application = ((GameApplication) app);

        this.worldNode = stateManager.getState(WorldAppState.class).getWorldNode();
        this.navMeshRenderer = new NavMeshDebugRenderer(application.getAssetManager());

        buildSoloModified();
    }

    public NavMesh getNavigationMesh() {
        return navigationMesh;
    }

    private void buildSoloModified() {
        JmeInputGeomProvider geomProvider = InputGeomProviderBuilder.build(worldNode);
        setNavMeshModifiers(geomProvider, worldNode);

        RecastConfig recastConfig = new RecastConfigBuilder()
                .withPartitionType(RecastConstants.PartitionType.WATERSHED)
                .withWalkableAreaMod(AREAMOD_GROUND)
                .withAgentRadius(agentRadius)
                .withAgentHeight(agentHeight)
                .withCellSize(cellSize)
                .withCellHeight(cellHeight)
                .withAgentMaxClimb(agentMaxClimb)
                .withAgentMaxSlope(90f)
                .withEdgeMaxLen(2.4f) // r*8
                .withEdgeMaxError(1.3f) // 1.1 - 1.5
                .withDetailSampleDistance(16.0f) // increase if exception
                .withDetailSampleMaxError(16.0f) // increase if exception
                .withVertsPerPoly(5)
                .build();

        //Create a RecastBuilderConfig builder with world bounds of our geometry.
        RecastBuilderConfig builderCfg = new RecastBuilderConfig(
                recastConfig,
                geomProvider.getMeshBoundsMin(),
                geomProvider.getMeshBoundsMax()
        );

        Telemetry telemetry = new Telemetry();
        // Rasterize input polygon soup.
        Heightfield solid = JmeRecastVoxelization.buildSolidHeightfield(geomProvider, builderCfg, telemetry);

        JmeRecastBuilder rcBuilder = new JmeRecastBuilder();
        RecastBuilderResult rcResult = rcBuilder.build(
                builderCfg.borderSize,
                builderCfg.buildMeshDetail,
                geomProvider,
                recastConfig,
                solid,
                telemetry
        );

        System.out.println("Telemetry:");
        telemetry.print();

        NavMeshDataCreateParams params = getNavMeshCreateParams(geomProvider, rcResult);

        updateAreaAndFlags(params);

        MeshData meshData = NavMeshBuilder.createNavMeshData(params);

        navMeshRenderer.drawMeshByArea(meshData, true);

        navigationMesh = new NavMesh(meshData, recastConfig.maxVertsPerPoly, 0);
    }

    private NavMeshDataCreateParams getNavMeshCreateParams(JmeInputGeomProvider provider, RecastBuilderResult rcResult) {
        PolyMesh polyMesh = rcResult.getMesh();
        PolyMeshDetail detail = rcResult.getMeshDetail();
        NavMeshDataCreateParams params = new NavMeshDataCreateParams();

        for (int i = 0; i < polyMesh.npolys; ++i) {
            polyMesh.flags[i] = 1;
        }

        params.verts = polyMesh.verts;
        params.vertCount = polyMesh.nverts;
        params.polys = polyMesh.polys;
        params.polyAreas = polyMesh.areas;
        params.polyFlags = polyMesh.flags;
        params.polyCount = polyMesh.npolys;
        params.nvp = polyMesh.nvp;

        if (detail != null) {
            params.detailMeshes = detail.meshes;
            params.detailVerts = detail.verts;
            params.detailVertsCount = detail.nverts;
            params.detailTris = detail.tris;
            params.detailTriCount = detail.ntris;
        }

        params.walkableHeight = agentHeight;
        params.walkableRadius = agentRadius;
        params.walkableClimb = agentMaxClimb;
        params.bmin = polyMesh.bmin;
        params.bmax = polyMesh.bmax;
        params.cs = cellSize;
        params.ch = cellHeight;
        params.buildBvTree = true;

        params.offMeshConCount = provider.getOffMeshConnections().size();
        params.offMeshConVerts = new float[params.offMeshConCount * 6];
        params.offMeshConRad = new float[params.offMeshConCount];
        params.offMeshConDir = new int[params.offMeshConCount];
        params.offMeshConAreas = new int[params.offMeshConCount];
        params.offMeshConFlags = new int[params.offMeshConCount];
        params.offMeshConUserID = new int[params.offMeshConCount];

        for (int i = 0; i < params.offMeshConCount; i++) {
            OffMeshLink offMeshConn = provider.getOffMeshConnections().get(i);
            System.arraycopy(offMeshConn.verts, 0, params.offMeshConVerts, 6 * i, 6);
            params.offMeshConRad[i] = offMeshConn.radius;
            params.offMeshConDir[i] = offMeshConn.biDirectional ? NavMesh.DT_OFFMESH_CON_BIDIR : 0;
            params.offMeshConAreas[i] = offMeshConn.area;
            params.offMeshConFlags[i] = offMeshConn.flags;
            params.offMeshConUserID[i] = offMeshConn.userID;
        }

        return params;
    }

    private void setNavMeshModifiers(JmeInputGeomProvider m_geom, Node root) {

        root.depthFirstTraversal(new SceneGraphVisitorAdapter() {
            @Override
            public void visit(Geometry geometry) {

                if (geometry.getMaterial() == null || geometry.getMaterial().getName() == null) {
                    return;
                }

                String[] name = geometry.getMaterial().getName().split("_");
                NavMeshModifier mod = switch (name[0]) {
                    case "water" -> new NavMeshModifier(geometry, AREAMOD_WATER);
                    case "road" -> new NavMeshModifier(geometry, AREAMOD_ROAD);
                    case "grass" -> new NavMeshModifier(geometry, AREAMOD_GRASS);
                    case "door" -> new NavMeshModifier(geometry, AREAMOD_DOOR);
                    default -> new NavMeshModifier(geometry, AREAMOD_GROUND);
                };

                m_geom.addModification(mod);
                System.out.println(mod);
            }
        });
    }

    private void updateAreaAndFlags(NavMeshDataCreateParams params) {
        final int DT_TILECACHE_WALKABLE_AREA = 63;

        for (int i = 0; i < params.polyCount; ++i) {

            if (params.polyAreas[i] == DT_TILECACHE_WALKABLE_AREA) {
                params.polyAreas[i] = POLYAREA_TYPE_GROUND;
            }

            if (params.polyAreas[i] == POLYAREA_TYPE_GROUND
                    || params.polyAreas[i] == POLYAREA_TYPE_GRASS
                    || params.polyAreas[i] == POLYAREA_TYPE_ROAD) {
                params.polyFlags[i] = POLYFLAGS_WALK;

            } else if (params.polyAreas[i] == POLYAREA_TYPE_WATER) {
                params.polyFlags[i] = POLYFLAGS_SWIM;

            } else if (params.polyAreas[i] == POLYAREA_TYPE_DOOR) {
                params.polyFlags[i] = POLYFLAGS_WALK | POLYFLAGS_DOOR;
            }
        }
    }
}
