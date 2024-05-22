package com.mygdx.game;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.viewport.Viewport;

// GameCam.translate() => prideda tuos x/y/z jau prie esamos kameros location
// GameCam.position.set() => literally settina position
/**
 * @author Dovydas Girskas 5gr
 */
public class CameraUtilities {
    private OrthographicCamera gameCam;
    private Viewport gamePort;
    private Player player;
    public CameraUtilities(OrthographicCamera gameCam, Viewport gamePort, Player player) {
        this.gameCam = gameCam;
        this.gamePort = gamePort;
        this.player = player;
    }
    public void updateCameraBounds() {
        // nustatai kad kamera ant zaidejo butu
        gameCam.position.set(player.playerX, player.playerY, 0);

        // nustatai kad kamera neiseitu is pasaulio ribu
        gameCam.position.x = MathUtils.clamp(gameCam.position.x, gamePort.getWorldWidth() / 2, MyGdxGame.WORLD_WIDTH - gamePort.getWorldWidth() / 2);
        gameCam.position.y = MathUtils.clamp(gameCam.position.y, gamePort.getWorldHeight() / 2, MyGdxGame.WORLD_HEIGHT - gamePort.getWorldHeight() / 2);
    }
}
