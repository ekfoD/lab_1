package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.mygdx.game.scenes.Hud;
import com.mygdx.game.screens.PlayScreen;

import java.awt.*;
import java.util.logging.Level;

/**
 * @author Dovydas Girskas 5gr
 */
public class Player extends Sprite {
    public Rectangle player_rect;   // collision
    public TiledMap tiledMap;  // kad gauti layerius
    private TiledMapTileLayer layer; // kad issisaugoti kazkoki layeri
    private CustomCell cell; // kad is layerio kazkoki tile imti
    private CustomCell anotherCell;
    private int tileX;  // kad gauti tiksliai tile X coordinates
    private int tileY;  // kad gauti tiksliai tile Y coordinates
    public static final int PLAYER_RECT_WIDTH = 18;    // player rect collision matmenys
    public static final int PLAYER_RECT_HEIGHT = 30;   // player rect collision matmenys
    private static final int LADDER_UPPER_PART_INDEX = 11; // kopeciu patikrinimui
    private static final int SPIKES_SMALL_INDEX = 17;
    private static final float SPEED = 100f;
    private static final float JUMP_VELOCITY = 3.5f;
    private static final float PLAYER_WEIGTH = 1f;
    public static final float GRAVITY = -10.4f;
    public static final float MAX_VELOCITY = 7f;
    public float velocityY = 0f;
    public static final float STARTING_PLAYER_X = 100f;
    public static final float STARTING_PLAYER_Y = 100f;
    public float playerX;
    public float playerY;
    // del animations
    private MyGdxGame game;
    private PlayScreen playScreen;
    private AnimationHandler animationHandler;
    private PlayerState playerState;
    private Boolean isDeathAnimationComplete;
    private boolean isPlayerFacingLeft;
    // del collectibles
    public int coinsCollected;
    public int howManyCoins;
    // del coins ir spikes collision box
    private Rectangle triggerBox;

    public Player(Texture texture, TiledMap tiledMap, MyGdxGame game, PlayScreen playScreen) {
        super(texture);

        playerX = STARTING_PLAYER_X;
        playerY = STARTING_PLAYER_Y;

        // kad access to sprites
        this.game = game;
        // kad access to restart
        this.playScreen = playScreen;

        // animations
        animationHandler = new AnimationHandler(game, this);
        isPlayerFacingLeft = false;
        isDeathAnimationComplete = false;

        // getting map + collision box
        this.tiledMap = tiledMap;
        howManyCoins = MapUtilities.countCoinsInMap(tiledMap);
        player_rect = new Rectangle((int) playerX, (int) playerY, PLAYER_RECT_WIDTH, PLAYER_RECT_HEIGHT);

        // coins
        coinsCollected = 0;
    }
    public void updatePlayerMovement(float delta) {

        if (Gdx.input.isKeyPressed(Input.Keys.N))
            playScreen.nextLevel();

        // jei RIP
        if (isPlayerTouchingSpikes(playerX, playerY) || isPlayerRunningOutOfTheMap(playerX, playerY + MyGdxGame.BLOCK_DIMENSIONS)) {
            playerState = PlayerState.DYING;

            isDeathAnimationComplete = animationHandler.playDeathAnimation(delta, playerState, isPlayerFacingLeft);
            if (isDeathAnimationComplete) {
                animationHandler.secondStateTime = 0f;  // jei multiple restarts buvo
                playScreen.restartLevel();
            }
            else {
                return; // kad mirdamas negaletu judeti zaidejas
            }
        }

        // jei pinigeli liecia
        playerCoinsCollectionHandler();

        // pauze!
        if (Gdx.input.isKeyPressed(Input.Keys.ESCAPE))
            game.changeScreen(MyGdxGame.MENU);

        // animations
        playerState = PlayerState.IDLE;
        if (isPlayerOnLadder(playerX, playerY))
            playerState = PlayerState.CLIMBING_IDLE;

        // movement + collisions
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT) && !isPlayerRunningOutOfTheMap(playerX - (SPEED * delta), playerY)) {
            playerState = PlayerState.MOVING_LEFT; // animations
            isPlayerFacingLeft = true;

            if (isThereAWallCheckFromTwoPoints(playerX - (SPEED * delta), playerY, playerX - (SPEED * delta), playerY + PLAYER_RECT_HEIGHT))
                moveClosestToWall(playerX, playerY, playerX, playerY + PLAYER_RECT_HEIGHT, -1, 0);
            else
                playerX -= SPEED * delta;
        }

        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT) && !isPlayerRunningOutOfTheMap(playerX + (SPEED * delta), playerY)) {
            playerState = PlayerState.MOVING_RIGHT; // animations
            isPlayerFacingLeft = false;

            if (isThereAWallCheckFromTwoPoints(playerX + PLAYER_RECT_WIDTH + (SPEED * delta), playerY, playerX + PLAYER_RECT_WIDTH + (SPEED * delta), playerY + PLAYER_RECT_HEIGHT))
                moveClosestToWall(playerX + PLAYER_RECT_WIDTH, playerY, playerX + PLAYER_RECT_WIDTH, playerY + PLAYER_RECT_HEIGHT, 1, 0);
            else
                playerX += SPEED * delta;
        }

        // jump ir climbing calculations
        if (Gdx.input.isKeyPressed(Input.Keys.UP) && !isPlayerRunningOutOfTheMap(playerX, playerY + (SPEED * delta))) {
            // kopecios part
            if (isPlayerOnLadder(playerX, playerY)) {
                playerState = PlayerState.CLIMBING; // animations
                if (isThereAWallCheckFromTwoPoints(playerX, playerY + PLAYER_RECT_HEIGHT + (SPEED * delta), playerX + PLAYER_RECT_WIDTH, playerY + PLAYER_RECT_HEIGHT + (SPEED * delta)))
                    moveClosestToWall(playerX, playerY + PLAYER_RECT_HEIGHT, playerX + PLAYER_RECT_WIDTH, playerY + PLAYER_RECT_HEIGHT, 0, 1);
                else
                    playerY += SPEED * delta;
            }
            // jumping part
            else {
                // up animations handlina gravity dalyje
                if (isPlayerOnGround(playerX, playerY - 1) ||       // or jis buvo ant kopeciu ir yra ant paskutinio sloto kopeciu
                        (isPlayerOnLadder(playerX, playerY - 1) && !isPlayerOnLadder(playerX, playerY))) {
                    velocityY = 0;
                    velocityY += JUMP_VELOCITY * PLAYER_WEIGTH;
                }
                else if (!isPlayerOnGround(playerX, playerY - 1) && velocityY > 0) {
                    velocityY += JUMP_VELOCITY * PLAYER_WEIGTH * delta;
                }
            }
        }

        if (Gdx.input.isKeyPressed(Input.Keys.DOWN) && (isPlayerOnLadder(playerX + 10, playerY - 1) || isPlayerOnLadder(playerX + PLAYER_RECT_WIDTH - 10, playerY - 1))
        && !isPlayerRunningOutOfTheMap(playerX, playerY - (SPEED * delta))) {
            playerState = PlayerState.CLIMBING;// animations
            if (isThereAWallCheckFromTwoPoints(playerX, playerY - (SPEED * delta), playerX + PLAYER_RECT_WIDTH, playerY - (SPEED * delta)))
                moveClosestToWall(playerX, playerY, playerX + PLAYER_RECT_WIDTH, playerY, 0, -1);
            else
                playerY -= SPEED * delta;
        }

        // gravity
        if (!isPlayerOnLadder(playerX, playerY)) { // gravity works only if player is not on ladder
            if (!isThereAWallCheckFromTwoPoints(playerX, playerY - (SPEED * delta), playerX + PLAYER_RECT_WIDTH, playerY - (SPEED * delta)) && velocityY <= 0)
                playerState = PlayerState.FALLING;
            if (!isThereAWallCheckFromTwoPoints(playerX, playerY - (SPEED * delta), playerX + PLAYER_RECT_WIDTH, playerY - (SPEED * delta)) && velocityY > 0)
                playerState = PlayerState.JUMPING;
            gravityUpdate(delta);
        }
        else
            velocityY = 0;

        // offsettina pati sprite nuo collision staciakampio to ir prideda velocityY
        this.setPosition(playerX - 6, playerY);

        // animation rendering
        animationHandler.animationPlayer(delta, playerState, isPlayerFacingLeft);
    }
    private CustomCell getCell (TiledMapTileLayer layer, float playerX, float playerY) {
        tileX = (int) (playerX / MyGdxGame.BLOCK_DIMENSIONS);
        tileY = (int) (playerY / MyGdxGame.BLOCK_DIMENSIONS);
        return (CustomCell) layer.getCell(tileX, tileY);            // del kodo supaprastinimo
    }
    private boolean isThereAWallCheckFromTwoPoints(float leftPlayerX, float leftPlayerY, float rightPlayerX, float rightPlayerY) {
        if (isThereAWall(leftPlayerX, leftPlayerY) ||
                isThereAWall(rightPlayerX, rightPlayerY)) {
            return true;
        }
        return false;
    }
    private boolean isThereAWall(float potentialPlayerX, float potentialPlayerY) {
        layer = (TiledMapTileLayer) tiledMap.getLayers().get(MapUtilities.BLOCKS_LAYER);    // gaunu blocks layeri

        if (getCell(layer, potentialPlayerX, potentialPlayerY) != null)
            return true;
        return false;
    }
    private boolean isPlayerOnLadder(float playerX, float playerY) {
        layer = (TiledMapTileLayer) tiledMap.getLayers().get(MapUtilities.LADDER_LAYER);    // gaunu ladder layeri

        // kaires puses blokas
        cell = getCell(layer, playerX + 10, playerY);

        // desines puses blokas
        anotherCell = getCell(layer, playerX + PLAYER_RECT_WIDTH - 10, playerY);

        // ladder virsaus neskaitau kaip kopeciu. custom cell overwrite tik del to kad getTileId() veiktu
        if ((cell != null && cell.getTileId() != LADDER_UPPER_PART_INDEX) || (anotherCell != null && anotherCell.getTileId() != LADDER_UPPER_PART_INDEX))
                return true;
        return false;
}
    private boolean isPlayerOnGround(float playerX, float playerY) {
        layer = (TiledMapTileLayer) tiledMap.getLayers().get(MapUtilities.BLOCKS_LAYER);    // gaunu ladder layeri

        // kaire puse
        tileX = (int) (playerX / MyGdxGame.BLOCK_DIMENSIONS);
        tileY = (int) Math.floor((playerY / MyGdxGame.BLOCK_DIMENSIONS));
        cell = (CustomCell) layer.getCell(tileX, tileY);

        // desine puse
        tileX = (int) ((playerX + PLAYER_RECT_WIDTH) / MyGdxGame.BLOCK_DIMENSIONS);
        tileY = (int) Math.floor((playerY / MyGdxGame.BLOCK_DIMENSIONS));
        anotherCell = (CustomCell) layer.getCell(tileX, tileY);

        // jei kaireje zaidejo puseje apacioj ar desineje zaid. pus. apacioje blokas vadinasi jis ant zemes
        if (cell != null || anotherCell != null) {
            return true;
        }
        return false;
    }
    private boolean isPlayerTouchingSpikes(float playerX, float playerY) {
        layer = (TiledMapTileLayer) tiledMap.getLayers().get(MapUtilities.SPIKES_LAYER);    // spikes layeeeer
        player_rect.setLocation((int) playerX, (int) playerY);

        // kaire puse
        cell = getCell(layer, playerX + 3, playerY);
        // desine puse
        anotherCell = getCell(layer, playerX + PLAYER_RECT_WIDTH - 3, playerY);

        if ((cell != null && cell.getTileId() == SPIKES_SMALL_INDEX) || (anotherCell != null && anotherCell.getTileId() == SPIKES_SMALL_INDEX))
            triggerBox = new Rectangle((((int)(playerX + 3) / MyGdxGame.BLOCK_DIMENSIONS) * MyGdxGame.BLOCK_DIMENSIONS) + 7, (int)(playerY / MyGdxGame.BLOCK_DIMENSIONS) * MyGdxGame.BLOCK_DIMENSIONS, 18, 9);
        else if (cell != null || anotherCell != null)
            triggerBox = new Rectangle(((int)(playerX / MyGdxGame.BLOCK_DIMENSIONS) * MyGdxGame.BLOCK_DIMENSIONS) + 7, (int)(playerY / MyGdxGame.BLOCK_DIMENSIONS) * MyGdxGame.BLOCK_DIMENSIONS, 32, 16);

        // jei netikrinciau tu cells, butu nullPointerReferenceError
        if ((cell != null || anotherCell != null) && player_rect.intersects(triggerBox))
            return true;
        return false;
    }
    private boolean isPlayerRunningOutOfTheMap(float potentialPlayerX, float potentialPlayerY) {
        if (potentialPlayerX < 0 || potentialPlayerX > MyGdxGame.WORLD_WIDTH)
            return true;
        else if (potentialPlayerY < 0)
            return true;
        return false;
    }
    private void moveClosestToWall(float firstXCheck, float firstYCheck, float secondXCheck, float secondYCheck, float x, float y) {
        for (int i = 0; i < 8; i++) {
            if (!isThereAWall(firstXCheck + x, firstYCheck + y) &&
                    !isThereAWall(secondXCheck + x, secondYCheck + y)) {
                playerX += x;
                playerY += y;
                firstXCheck += x;   // kad updatintusi checkai
                firstYCheck += y;
                secondXCheck += x;
                secondYCheck += y;
            } else {
                break;
            }
        }
    }
    private void gravityUpdate(float delta) {
        // nesinori kad unlimited didetu velocity
        velocityY += (velocityY < MAX_VELOCITY) ? GRAVITY * delta * PLAYER_WEIGTH : MAX_VELOCITY;

        if (isThereAWallCheckFromTwoPoints(playerX, playerY + PLAYER_RECT_HEIGHT + velocityY, playerX + PLAYER_RECT_WIDTH, playerY + PLAYER_RECT_HEIGHT + velocityY) ||
                isThereAWallCheckFromTwoPoints(playerX, playerY + velocityY, playerX + PLAYER_RECT_WIDTH, playerY + velocityY)) {
            if (velocityY < 0) {
                moveClosestToWall(playerX, playerY, playerX + PLAYER_RECT_WIDTH, playerY, 0, -1);
            }
            velocityY = 0;
        }
        else
            playerY += velocityY;
    }
    private void playerCoinsCollectionHandler() {
        layer = (TiledMapTileLayer) tiledMap.getLayers().get(MapUtilities.COINS_LAYER);
        float tempPlayerX = 0, tempPlayerY = 0;

        // is visu 4 tasku patikrinu ar aplinkui nera pinigelio ir ar playeris jo neliecia
        if (getCell(layer, playerX, playerY) != null) {
            tempPlayerX = playerX;
            tempPlayerY = playerY;
        }
        else if (getCell(layer, playerX + PLAYER_RECT_WIDTH, playerY) != null) {
            tempPlayerX = playerX + PLAYER_RECT_WIDTH;
            tempPlayerY = playerY;
        }
        else if (getCell(layer, playerX, playerY + PLAYER_RECT_WIDTH) != null) {
            tempPlayerX = playerX;
            tempPlayerY = playerY + PLAYER_RECT_WIDTH;
        }
        else if (getCell(layer, playerX + PLAYER_RECT_WIDTH, playerY + PLAYER_RECT_WIDTH) != null) {
            tempPlayerX = playerX + PLAYER_RECT_WIDTH;
            tempPlayerY = playerY + PLAYER_RECT_WIDTH;
        }

        // PINIGELIS RASTAS. suriiiiink. jei visi pinigeliai tai varyk i kita lygi surinkti daugiau pinigeliuuu
        if (tempPlayerX != 0 && tempPlayerY != 0) {
            triggerBox = new Rectangle((((int)(tempPlayerX / MyGdxGame.BLOCK_DIMENSIONS) * MyGdxGame.BLOCK_DIMENSIONS) + 7), (((int)(tempPlayerY / MyGdxGame.BLOCK_DIMENSIONS)) * MyGdxGame.BLOCK_DIMENSIONS) + 7, 20, 20);

            if (player_rect.intersects(triggerBox)) {
                ++coinsCollected;
                Hud.addCoin();
                layer.setCell((int)tempPlayerX / MyGdxGame.BLOCK_DIMENSIONS, (int) tempPlayerY / MyGdxGame.BLOCK_DIMENSIONS, null);

                if (coinsCollected >= howManyCoins)
                    playScreen.nextLevel();
            }
        }
    }
    public void handleRestart(TiledMap tiledMap) {
        this.tiledMap = tiledMap;
        coinsCollected = 0;
        playerX = STARTING_PLAYER_X;
        playerY = STARTING_PLAYER_Y;
    }

}
