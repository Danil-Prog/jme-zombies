package org.jme.zombies;

import com.github.stephengold.wrench.LwjglAssetLoader;
import com.jme3.app.SimpleApplication;
import com.jme3.bullet.BulletAppState;
import com.jme3.font.BitmapText;
import com.jme3.system.AppSettings;
import org.jme.zombies.game.listeners.BulletCollisionListener;
import org.jme.zombies.game.listeners.InputListener;
import org.jme.zombies.game.listeners.ShootListener;
import org.jme.zombies.game.states.EntityState;
import org.jme.zombies.game.states.NavigationMeshAppState;
import org.jme.zombies.game.states.WorldAppState;
import org.jme.zombies.game.system.AIMovementSystem;
import org.jme.zombies.game.system.DetachingSystem;
import org.jme.zombies.game.system.HealthBarSystem;
import org.jme.zombies.game.system.PlayerMovementSystem;

public class GameApplication extends SimpleApplication {

    private final BulletAppState bulletAppState;

    public GameApplication(BulletAppState bulletAppState) {
        this.bulletAppState = bulletAppState;
    }

    public static void main(String[] args) {
        var bulletAppState = new BulletAppState();
        var game = new GameApplication(bulletAppState);

        AppSettings settings = new AppSettings(true);
        settings.setTitle("Defenders of Castle");
        settings.setFullscreen(true);
        settings.setWindowSize(1600, 900);

        game.setSettings(settings);
        game.start();
    }

    @Override
    public void simpleInitApp() {
        assetManager.registerLoader(LwjglAssetLoader.class,
                "3ds", "3mf", "blend", "bvh", "dae", "fbx", "glb", "gltf",
                "lwo", "meshxml", "mesh.xml", "obj", "ply", "stl");

        // Initialize default system
        stateManager.attach(bulletAppState);
        stateManager.attach(new WorldAppState());
        stateManager.attach(new NavigationMeshAppState());
        stateManager.attach(new EntityState());
        stateManager.attach(new ShootListener());
        stateManager.attach(new InputListener());

        initCrossHairs();

        // Initialize system from ECS
        stateManager.attach(new AIMovementSystem());
        stateManager.attach(new PlayerMovementSystem());
        stateManager.attach(new DetachingSystem());
        stateManager.attach(new HealthBarSystem());

        var ballCollisionListener = new BulletCollisionListener(this);

        bulletAppState.setDebugEnabled(false);
        bulletAppState.getPhysicsSpace().addCollisionListener(ballCollisionListener);
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

