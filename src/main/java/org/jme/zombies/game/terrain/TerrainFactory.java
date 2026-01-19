package org.jme.zombies.game.terrain;

import com.jme3.asset.AssetManager;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.light.AmbientLight;
import com.jme3.light.DirectionalLight;
import com.jme3.light.LightList;
import com.jme3.light.PointLight;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.post.FilterPostProcessor;
import com.jme3.post.filters.FXAAFilter;
import com.jme3.recast4j.geom.InputGeomProviderBuilder;
import com.jme3.recast4j.geom.JmeInputGeomProvider;
import com.jme3.recast4j.geom.JmeRecastBuilder;
import com.jme3.recast4j.geom.JmeRecastVoxelization;
import com.jme3.recast4j.geom.NavMeshModifier;
import com.jme3.recast4j.geom.OffMeshLink;
import com.jme3.recast4j.geom.Telemetry;
import com.jme3.recast4j.recast.NavMeshDebugRenderer;
import com.jme3.recast4j.recast.RecastConfigBuilder;
import com.jme3.renderer.ViewPort;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.SceneGraphVisitorAdapter;
import com.jme3.scene.Spatial;
import com.jme3.shadow.DirectionalLightShadowFilter;
import com.jme3.shadow.EdgeFilteringMode;
import com.jme3.util.SkyFactory;
import org.jme.zombies.Jmezombies;
import org.jme.zombies.game.Context;
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

public class TerrainFactory implements Context {

    private final BulletAppState bulletAppState;
    private final Node rootNode;
    private final AssetManager assetManager;
    private final ViewPort viewPort;
    private final Spatial worldMap;
    private final NavMeshDebugRenderer navMeshRenderer;

    private Node worldNode;
    private NavMesh navMeshTerrain;

    private final static float agentRadius = 0.3f;
    private final static float agentHeight = 1.7f;
    private final static float agentMaxClimb = 0.3f; // > 2*ch
    private final static float cellSize = 0.1f;      // cs=r/2
    private final static float cellHeight = 0.5f;    // ch=cs/2 but not < .1f

    public TerrainFactory(Jmezombies game) {
        this.bulletAppState = game.getStateManager().getState(BulletAppState.class);
        this.rootNode = game.getRootNode();
        this.assetManager = game.getAssetManager();
        this.viewPort = game.getViewPort();
        this.worldMap = assetManager.loadModel("Scenes/Map.gltf");

        navMeshRenderer = new NavMeshDebugRenderer(assetManager);
    }

    public void buildTerrainEnvironment() {
        worldNode = new Node("worldNode");
        worldNode.setShadowMode(RenderQueue.ShadowMode.CastAndReceive);

        worldMap.setLocalTranslation(0, -2, 0);

        RigidBodyControl worldRigidBodyControl = new RigidBodyControl(0f);
        worldMap.addControl(worldRigidBodyControl);

        bulletAppState.getPhysicsSpace().add(worldRigidBodyControl);

        LightList lightList = worldNode.getLocalLightList();

        lightList.forEach(light -> {
            System.out.println(light.getName());
        });

        worldNode.attachChild(worldMap);

        rootNode.attachChild(worldNode);

        environment();

        buildSoloModified();
        addLighting();
    }

    public NavMesh getNavMeshTerrain() {
        return this.navMeshTerrain;
    }

    public Node getWorldNode() {
        return this.worldNode;
    }

    /**
     * Initialization world environment (sky).
     */
    private void environment() {
        Spatial sky = SkyFactory.createSky(
                assetManager,
                assetManager.loadTexture("Textures/Sky/Lagoon/lagoon_west.jpg"),
                assetManager.loadTexture("Textures/Sky/Lagoon/lagoon_east.jpg"),
                assetManager.loadTexture("Textures/Sky/Lagoon/lagoon_north.jpg"),
                assetManager.loadTexture("Textures/Sky/Lagoon/lagoon_south.jpg"),
                assetManager.loadTexture("Textures/Sky/Lagoon/lagoon_up.jpg"),
                assetManager.loadTexture("Textures/Sky/Lagoon/lagoon_down.jpg")
        );

        rootNode.attachChild(sky);
    }

    private void addLighting() {
        rootNode.setShadowMode(RenderQueue.ShadowMode.CastAndReceive);

        ColorRGBA skyColor = new ColorRGBA(0.1f, 0.2f, 0.4f, 1f);
        viewPort.setBackgroundColor(skyColor);

        DirectionalLight directionalLight = new DirectionalLight();
        directionalLight.setName("Sun");
        directionalLight.setDirection(new Vector3f(-7f, -3f, -5f).normalizeLocal());
        rootNode.addLight(directionalLight);

        // Render shadows based on the directional light.
        DirectionalLightShadowFilter shadowFilter = new DirectionalLightShadowFilter(assetManager, 2_048, 3);
        shadowFilter.setLight(directionalLight);
        shadowFilter.setShadowIntensity(0.4f);
        shadowFilter.setShadowZExtend(256);
        shadowFilter.setEdgeFilteringMode(EdgeFilteringMode.PCFPOISSON);

        FilterPostProcessor fpp = new FilterPostProcessor(assetManager);
        fpp.addFilter(shadowFilter);
        fpp.addFilter(new FXAAFilter());

        viewPort.addProcessor(fpp);
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
                .withAgentMaxSlope(45f)
                .withEdgeMaxLen(2.4f) // r*8
                .withEdgeMaxError(1.3f) // 1.1 - 1.5
                .withDetailSampleDistance(8.0f) // increase if exception
                .withDetailSampleMaxError(8.0f) // increase if exception
                .withVertsPerPoly(3)
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

        navMeshTerrain = new NavMesh(meshData, recastConfig.maxVertsPerPoly, 0);
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

        params.walkableHeight = TerrainFactory.agentHeight;
        params.walkableRadius = TerrainFactory.agentRadius;
        params.walkableClimb = TerrainFactory.agentMaxClimb;
        params.bmin = polyMesh.bmin;
        params.bmax = polyMesh.bmax;
        params.cs = TerrainFactory.cellSize;
        params.ch = TerrainFactory.cellHeight;
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
                System.out.println("setNavMeshArea " + mod);
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
