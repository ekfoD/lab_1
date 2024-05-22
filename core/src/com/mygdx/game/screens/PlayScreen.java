package com.mygdx.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.mygdx.game.*;
import com.mygdx.game.scenes.Hud;

/**
 * @author Dovydas Girskas 5gr
 */
public class PlayScreen implements Screen {
    private MyGdxGame game;                             // zaidimas
    private OrthographicCamera gameCam;                 // kamera
    private Viewport gamePort;                          // viewportas
    private float elapsedTime = 0f;                     // spalvom
    private Hud hud;                                    // HUD

    private CameraUtilities camUtilities;
    private MapUtilities mapUtilities;
    private LevelUtilities levelUtilities;
    private TiledMap tiledMap;                          // mano mapa laiko
    private OrthogonalTiledMapRenderer renderer;        //  renderina mapa
    public Player player;                              // player ref.
    private RainProgram rain;                       // raaaainnn aestheticssss
    private String folderName;
    public PlayScreen(MyGdxGame game, String folderName)
    {
        this.folderName = folderName; // del is kurio folderio loadinti leveli
        this.game = game;
        gameCam = new OrthographicCamera(game.CAMERA_WIDTH, game.CAMERA_HEIGHT);
        gamePort = new ExtendViewport(MyGdxGame.CAMERA_WIDTH, MyGdxGame.CAMERA_HEIGHT, MyGdxGame.WORLD_WIDTH, MyGdxGame.WORLD_HEIGHT, gameCam);

        // raaiiiinn
        rain = new RainProgram(gameCam);

        // map utilities
        mapUtilities = new MapUtilities();
        levelUtilities = new LevelUtilities(folderName);

        // mapa nustatai
        tiledMap = levelUtilities.loadNextLevel();
        renderer = new OrthogonalTiledMapRenderer(tiledMap);
        gamePort.getCamera().position.set(MyGdxGame.CAMERA_WIDTH / 2, MyGdxGame.CAMERA_HEIGHT / 2, 0);

        // Player class
        player = new Player(new Texture("animations/idle_0.png"), tiledMap, game, this);
        player.setPosition(player.STARTING_PLAYER_X, player.STARTING_PLAYER_Y); // Set initial position of player

        // Hud stuff
        hud = new Hud(game.batch, player, MapUtilities.countCoinsInMap(tiledMap), levelUtilities.levelAmount);

        // utilities/useful functions stuff
        camUtilities = new CameraUtilities(gameCam, gamePort, player);
    }

    @Override
    public void show() {
    }


    public void update(float delta) {
        camUtilities.updateCameraBounds();
        gameCam.update();
        renderer.setView(gameCam);
        player.updatePlayerMovement(delta);
        hud.update(delta);
    }

    public void restartLevel() {
        tiledMap.dispose();
        tiledMap = levelUtilities.getCurrentLevel(folderName);
        renderer.setMap(tiledMap);
        player.handleRestart(tiledMap);
        gameCam.update();
        hud.resetHud();
    }

    public void nextLevel() {
        tiledMap.dispose();
        if (LevelUtilities.levelsIndex == levelUtilities.levelAmount) // if levels are over
            loadMainMenu();
        else
        {
            tiledMap = levelUtilities.loadNextLevel();
            renderer.setMap(tiledMap);
            player.handleRestart(tiledMap);
            gameCam.update();
            hud.resetHud();
            hud.setCoinsAmount(MapUtilities.countCoinsInMap(tiledMap));
            player.howManyCoins = MapUtilities.countCoinsInMap(tiledMap);
        }
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(255, 255, 255, 1);      // kai isvalo visu pikseliu spalvas, sita spalva rodysis. kaip default color
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT); // glClear gali isclearinti specific buffers kalbant apie graphics context. siuo atveju pikseliu spalvas isvalo. tik spalvas!

        // color renderiiiing
        elapsedTime += delta;
//        ShaderUtilities.showAnimatedBackground(elapsedTime, Color.valueOf("666923"), Color.valueOf("3F5E36"));
        ShaderUtilities.showAnimatedBackground(elapsedTime, Color.valueOf("A9A9A9"), Color.valueOf("656565"));

        // mapa renderina
        renderer.render();

        // draw the player. pradziai setProjectionMatrix butinai, nes kitaip nuvaziuoja playeris
        game.batch.setProjectionMatrix(gameCam.combined);
        game.batch.begin(); // sito reikia nes tu viduje sukuri batch kuri efektyviai renderina nes SpriteBatch
            rain.render(game.batch);
            player.draw(game.batch);
        game.batch.end();   // ir cia baigiasi


        // input + movement + collision management
        update(delta);

        // spriteBatch yra kaip painting brush. jis yra rendering tool
        game.batch.setProjectionMatrix(hud.stage.getCamera().combined); // skliaustuose gauni vieta tikslia. kad tiksliai renderintu hud sita eilute
        hud.stage.draw();   // nupiesia hud dalykus ant virsaus
        // ir HUD kadangi extends Stage tai ten atskira turi sarysi su Sprite pieseju ir nereikia rasyti begin ir end
    }

    @Override
    public void resize(int width, int height) {
        // kad actually butu tas extend prikolas
        gamePort.update(width, height);
        // kad lietu paupdatintu kaip jis spawninas
        rain.resize(width, height);
        // kad hud paupdatintu
        hud.stage.getViewport().update(width, height, true);
        hud.updateFontSize();
    }

    public void loadMainMenu() {
        game.changeScreen(MyGdxGame.MENU);
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {
        rain.dispose();
    }
}
