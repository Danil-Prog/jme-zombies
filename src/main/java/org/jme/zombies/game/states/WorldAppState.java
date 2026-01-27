package org.jme.zombies.game.states;

import com.jme3.app.Application;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.asset.AssetManager;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.light.PointLight;
import com.jme3.math.ColorRGBA;
import com.jme3.post.FilterPostProcessor;
import com.jme3.post.filters.FogFilter;
import com.jme3.renderer.ViewPort;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.util.SkyFactory;
import org.jme.zombies.GameApplication;

public class WorldAppState extends AbstractAppState {

    private BulletAppState bulletAppState;
    private ViewPort viewPort;
    private AssetManager assetManager;
    private Node rootNode;
    private Node worldNode;

    private static final String WORLD_MODEL_NAME = "Scenes/Map.gltf";

    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        super.initialize(stateManager, app);

        var application = ((GameApplication) app);

        this.bulletAppState = stateManager.getState(BulletAppState.class);
        this.assetManager = application.getAssetManager();
        this.viewPort = application.getViewPort();
        this.rootNode = application.getRootNode();

        buildWorld();
        buildEnvironment();
        buildFilter();
    }

    public Node getWorldNode() {
        return worldNode;
    }

    private void buildWorld() {
        Spatial world = assetManager.loadModel(WORLD_MODEL_NAME);

        worldNode = new Node("worldNode");
        worldNode.setShadowMode(RenderQueue.ShadowMode.CastAndReceive);

        placePointLight(world);

        RigidBodyControl worldRigidBodyControl = new RigidBodyControl(0f);
        world.addControl(worldRigidBodyControl);

        bulletAppState.getPhysicsSpace().add(worldRigidBodyControl);

        worldNode.attachChild(world);

        rootNode.attachChild(worldNode);
    }

    private void placePointLight(Spatial world) {

        world.depthFirstTraversal(spatial -> {
            var name = spatial.getName();

            if (name.contains("FirePoint")) {
                var location = spatial.getWorldTranslation();

                PointLight fireLight = new PointLight();
                fireLight.setColor(new ColorRGBA(1.0f, 0.6f, 0.2f, 1.0f));
                fireLight.setRadius(100f);
                fireLight.setPosition(location);

                worldNode.addLight(fireLight);
            }
        });
    }

    /**
     * Initialization world environment (sky).
     */
    private void buildEnvironment() {
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

    private void buildFilter() {
        rootNode.setShadowMode(RenderQueue.ShadowMode.CastAndReceive);

        ColorRGBA skyColor = new ColorRGBA(0.1f, 0.2f, 0.4f, 1f);
        viewPort.setBackgroundColor(skyColor);

        FilterPostProcessor fpp = new FilterPostProcessor(assetManager);
        FogFilter fogFilter = new FogFilter();
        fogFilter.setFogColor(ColorRGBA.Gray);
        fogFilter.setFogDistance(35f);
        fogFilter.setFogDensity(0.5f);

        fpp.addFilter(fogFilter);

//        FilterPostProcessor fpp = new FilterPostProcessor(assetManager);
//        BloomFilter bloom = new BloomFilter();
//        bloom.setBloomIntensity(2.0f);
//        fpp.addFilter(bloom);

        viewPort.addProcessor(fpp);
    }
}
