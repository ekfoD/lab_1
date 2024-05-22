package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

/**
 * @author Dovydas Girskas 5gr
 */
public class RainProgram {
    private Array<Vector2> raindrops;
    private Texture raindropTexture;
    private OrthographicCamera gameCam;
    private final int SPAWN_RADIUS = 300;   // kiek + spawnins lietu

    public RainProgram(OrthographicCamera gameCam) {
        this.gameCam = gameCam;
        raindrops = new Array<>();
        raindropTexture = new Texture("extras/raindrop.png"); // Load a raindrop texture
        spawnRaindrops();
    }

    public void render(SpriteBatch batch) {
        for (Vector2 raindrop : raindrops) {
            batch.draw(raindropTexture, raindrop.x, raindrop.y);
        }
        updateRaindrops();
    }

    public void resize(int width, int height) {
        // respawnina nes ekrano dydis pasikeicia
        raindrops.clear();
        spawnRaindrops();
    }

    private void spawnRaindrops() {
        // atspawnina randomly
        float cameraLeft = gameCam.position.x - gameCam.viewportWidth / 2 - SPAWN_RADIUS;
        float cameraRight = gameCam.position.x + gameCam.viewportWidth / 2 + SPAWN_RADIUS;
        float cameraBottom = gameCam.position.y - gameCam.viewportHeight / 2 - SPAWN_RADIUS;
        float cameraTop = gameCam.position.y + gameCam.viewportHeight / 2 + SPAWN_RADIUS;

        for (int i = 0; i < (gameCam.viewportWidth / 3); i++) {
            float x = MathUtils.random(cameraLeft, cameraRight - raindropTexture.getWidth());
            float y = MathUtils.random(cameraBottom, cameraTop);
            raindrops.add(new Vector2(x, y));
        }
    }

    private void updateRaindrops() {
        // apacion leidzia lasiukus
        for (Vector2 raindrop : raindrops) {
            raindrop.y -= 200 * Gdx.graphics.getDeltaTime();

            // jei pasiekia apacia resettina
            if (raindrop.y + raindropTexture.getHeight() < gameCam.position.y - gameCam.viewportHeight / 2 - SPAWN_RADIUS) {
                raindrop.y = gameCam.position.y + gameCam.viewportHeight / 2 + SPAWN_RADIUS;
                raindrop.x = MathUtils.random(gameCam.position.x - gameCam.viewportWidth / 2 - SPAWN_RADIUS,
                        gameCam.position.x + gameCam.viewportWidth / 2 + SPAWN_RADIUS - raindropTexture.getWidth());
            }
        }
    }

    public void dispose() {
        raindropTexture.dispose();
    }
}

