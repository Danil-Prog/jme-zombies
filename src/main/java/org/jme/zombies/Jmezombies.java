package org.jme.zombies;

import com.github.stephengold.wrench.LwjglAssetLoader;
import com.jme3.app.SimpleApplication;
import com.jme3.asset.TextureKey;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.font.BitmapText;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.material.Material;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Sphere;
import com.jme3.system.AppSettings;
import com.jme3.texture.Texture;
import org.jme.zombies.game.GameContext;
import org.jme.zombies.game.entity.EntityFactory;
import org.jme.zombies.game.listeners.BallCollisionListener;
import org.jme.zombies.game.system.AIMovementSystem;
import org.jme.zombies.game.system.InputListenerSystem;
import org.jme.zombies.game.system.PlayerMovementSystem;
import org.recast4j.detour.NavMesh;

public class Jmezombies extends SimpleApplication {

    private final BulletAppState bulletAppState;

    public Jmezombies(BulletAppState bulletAppState) {
        this.bulletAppState = bulletAppState;
    }

    public static void main(String[] args) {
        var bulletAppState = new BulletAppState();
        Jmezombies app = new Jmezombies(bulletAppState);

        AppSettings settings = new AppSettings(true);
        settings.setTitle("Jmezombies");
        settings.setFullscreen(false);
        settings.setWindowSize(1366, 768);

        app.setSettings(settings);

        app.start();
    }

    private Material stoneMaterial;

    private static final Sphere ball;

    static {
        ball = new Sphere(32, 32, 0.4f, true, false);
        ball.setTextureMode(Sphere.TextureMode.Projected);
    }

    @Override
    public void simpleInitApp() {
        assetManager.registerLoader(LwjglAssetLoader.class,
                "3ds", "3mf", "blend", "bvh", "dae", "fbx", "glb", "gltf",
                "lwo", "meshxml", "mesh.xml", "obj", "ply", "stl");

        EntityFactory.assetManager = assetManager;

        stateManager.attach(bulletAppState);

        GameContext gameContext = new GameContext(this);

        initInputs();
        initMaterials();
        initCrossHairs();

        EntityFactory.bulletAppState = bulletAppState;

        gameContext.initialize();

        NavMesh navMesh = gameContext.getTerrainFactory().getNavMeshTerrain();

        AIMovementSystem aiMovementSystem = new AIMovementSystem(navMesh);
        InputListenerSystem inputListenerSystem = new InputListenerSystem();
        PlayerMovementSystem playerMovementSystem = new PlayerMovementSystem();

        stateManager.attach(aiMovementSystem);
        stateManager.attach(inputListenerSystem);
        stateManager.attach(playerMovementSystem);

        bulletAppState.setDebugEnabled(false);
        bulletAppState.getPhysicsSpace().addCollisionListener(new BallCollisionListener(this));
    }

    private void initInputs() {
        inputManager.addMapping("shoot", new MouseButtonTrigger(MouseInput.BUTTON_LEFT));
        inputManager.addListener(actionListener, "shoot");
    }

    final private ActionListener actionListener = (name, keyPressed, tpf) -> {
        if (name.equals("shoot") && !keyPressed) {
            makeCannonBall();
        }
    };

    public void initMaterials() {
        stoneMaterial = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        TextureKey textureKey = new TextureKey("Textures/Terrain/Rock/Rock.PNG");
        textureKey.setGenerateMips(true);
        Texture texture = assetManager.loadTexture(textureKey);
        stoneMaterial.setTexture("ColorMap", texture);
    }

    public void makeCannonBall() {
        Geometry geometry = new Geometry("ball", ball);
        geometry.setMaterial(stoneMaterial);
        geometry.setShadowMode(RenderQueue.ShadowMode.CastAndReceive);

        rootNode.attachChild(geometry);

        geometry.setLocalTranslation(cam.getLocation().add(cam.getDirection()));

        RigidBodyControl bodyControl = new RigidBodyControl(0.1f);
        geometry.addControl(bodyControl);

        bodyControl.setPhysicsLocation(cam.getLocation().add(cam.getDirection().mult(1.5f)));
        bodyControl.setLinearVelocity(cam.getDirection().mult(25));

        bulletAppState.getPhysicsSpace().add(bodyControl);
    }

    protected void initCrossHairs() {
        float x = (float) settings.getWidth() / 2;
        float y = (float) settings.getHeight() / 2;

        setDisplayStatView(false);

        BitmapText cursor = new BitmapText(guiFont);

        cursor.setSize(guiFont.getCharSet().getRenderedSize() * 2);
        cursor.setText("+");
        cursor.setLocalTranslation(x, y, 0);

        guiNode.attachChild(cursor);
    }
}

