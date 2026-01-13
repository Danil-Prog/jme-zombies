package org.jme.zombies.game.terrain;

import com.jme3.asset.AssetManager;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.light.AmbientLight;
import com.jme3.light.DirectionalLight;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.post.FilterPostProcessor;
import com.jme3.post.filters.FXAAFilter;
import com.jme3.recast4j.recast.NavMeshAssetManager;
import com.jme3.renderer.ViewPort;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.shadow.DirectionalLightShadowFilter;
import com.jme3.shadow.EdgeFilteringMode;
import com.jme3.util.SkyFactory;
import org.jme.zombies.Jmezombies;
import org.jme.zombies.game.Context;
import org.recast4j.detour.NavMesh;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Objects;

public class TerrainFactory implements Context {

    private final BulletAppState bulletAppState;
    private final Node rootNode;
    private final AssetManager assetManager;
    private final ViewPort viewPort;

    private NavMesh navMeshTerrain;

    public TerrainFactory(Jmezombies game) {
        this.bulletAppState = game.getStateManager().getState(BulletAppState.class);
        this.rootNode = game.getRootNode();
        this.assetManager = game.getAssetManager();
        this.viewPort = game.getViewPort();
    }

    public void buildTerrainEnvironment() {
        Spatial world = assetManager.loadModel("Models/Terrain.obj");

        world.setLocalTranslation(0, -1f, 0);
        world.setShadowMode(RenderQueue.ShadowMode.CastAndReceive);

        RigidBodyControl worldRigidBodyControl = new RigidBodyControl(0.0f);
        world.addControl(worldRigidBodyControl);

        bulletAppState.getPhysicsSpace().add(worldRigidBodyControl);

        rootNode.attachChild(world);

        environment();

        navMeshFromFile();
        addLighting();
    }

    public NavMesh getNavMeshTerrain() {
        return this.navMeshTerrain;
    }

    private void navMeshFromFile() {
        try {
            ClassLoader classLoader = getClass().getClassLoader();
            URL resource = classLoader.getResource("NavMesh/Terrain2.navmesh");

            File file = new File(Objects.requireNonNull(resource).getFile());
            this.navMeshTerrain = NavMeshAssetManager.load(file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
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

        // Set the viewport's background color to light blue.
        ColorRGBA skyColor = new ColorRGBA(0.1f, 0.2f, 0.4f, 1f);
        viewPort.setBackgroundColor(skyColor);

        AmbientLight al = new AmbientLight();
        al.setName("Global");
        rootNode.addLight(al);

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
}
