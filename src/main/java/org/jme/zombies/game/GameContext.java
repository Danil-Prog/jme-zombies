package org.jme.zombies.game;

import org.jme.zombies.Jmezombies;
import org.jme.zombies.game.entity.EntityFactory;
import org.jme.zombies.game.terrain.TerrainFactory;

public class GameContext {

    private final TerrainFactory terrainFactory;

    public GameContext(Jmezombies game) {
        this.terrainFactory = new TerrainFactory(game);
    }

    public void initialize() {

        EntityFactory.createPlayer();
        EntityFactory.createEnemy(0f, 0f);

        terrainFactory.buildTerrainEnvironment();
    }

    public TerrainFactory getTerrainFactory() {
        return terrainFactory;
    }
}
