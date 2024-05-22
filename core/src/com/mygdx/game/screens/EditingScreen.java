package com.mygdx.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.mygdx.game.MapUtilities;
import com.mygdx.game.MyGdxGame;
import com.mygdx.game.RainProgram;
import com.mygdx.game.ShaderUtilities;

/**
 * @author Dovydas Girskas 5gr
 */
public class EditingScreen implements Screen {

    private MyGdxGame game;
    private Stage stage;
    private RainProgram rain;
    private float elapsedTime;
    private int selectedTileIndex;
    private Texture selectedTexture;
    private Texture transparentTexture;
    private Table gridInput;
    private TextureRegion emptyTextureRegion;
    public static final int NUM_ROWS = MyGdxGame.WORLD_HEIGHT / MyGdxGame.BLOCK_DIMENSIONS;
    public static final int NUM_COLS = MyGdxGame.WORLD_WIDTH / MyGdxGame.BLOCK_DIMENSIONS;
    private Integer[][] map;
    private Stack stack;
    private ScrollPane scrollPane;


    public EditingScreen(MyGdxGame game) {
        this.game = game;

        emptyTextureRegion = new TextureRegion(new Texture("extras/selectionTile.png"));

        stack = new Stack();  // kad sujungtum inp table su grid table

        // transparent pic
        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(0, 0, 0, 0);
        pixmap.fill();
        transparentTexture = new Texture(pixmap);
        pixmap.dispose();

        stage = new Stage(new ScreenViewport());
        rain = new RainProgram((OrthographicCamera) stage.getCamera());
        Gdx.input.setInputProcessor(stage); //  visi input keliauja sitam screen
    }

    @Override
    public void show() {
        // Create a table that fills the screen. Everything else will go inside this table.

        stage.addActor(getButtonsTable());
        stage.addActor(getTilesTable());

        // grid stuff
        gridInput = getUserInputTable();

        stack.add(getGrid());
        stack.add(gridInput);

        scrollPane = new ScrollPane(stack);  // nes sitas dalykas movable window padaro
        scrollPane.setPosition(60, 80);
        scrollPane.setSize(1000, 600);

        stage.addActor(scrollPane);
    }

    @Override
    public void render(float delta) {
        elapsedTime += delta;
        // clear the screen ready for next set of images to be drawn
        Gdx.gl.glClearColor(255f, 255f, 255f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        ShaderUtilities.showAnimatedBackground(elapsedTime, Color.valueOf("656565"), Color.valueOf("333333"));
        game.batch.begin();
        rain.render(game.batch);
        game.batch.end();

        // tell our stage to do actions and draw itself
        stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 30f));
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    /**
     *
     * @return grazina lentele
     */
    private Table getGrid() {
        Table gridTable = new Table();
        gridTable.setFillParent(true);
        gridTable.padTop(50);

        // prideda "grid"
        for (int row = 0; row < NUM_ROWS; row++) {
            for (int col = 0; col < NUM_COLS; col++) {
                Image emptyCell = new Image(emptyTextureRegion);
                emptyCell.sizeBy(64);
                gridTable.add(emptyCell).size(64);
            }
            gridTable.row();
        }
        gridTable.setWidth(NUM_COLS * MyGdxGame.BLOCK_DIMENSIONS);
        gridTable.setHeight(NUM_ROWS * MyGdxGame.BLOCK_DIMENSIONS);
        return gridTable;
    }

    /**
     *
     * @return grazina input lentele
     */
    private Table getUserInputTable() {
        Table inpTable = new Table();

        inpTable.setFillParent(true); // Table fills the entire stage
        inpTable.padTop(50);

        for (int row = 0; row < NUM_ROWS; row++) {
            for (int col = 0; col < NUM_COLS; col++) {
                Image emptyCell = new Image();
                emptyCell.setUserObject((int)-1);

                // listeneris
                emptyCell.addListener(new ClickListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        Image clickedCell = (Image) event.getTarget();

                        if (selectedTileIndex != 18 && selectedTexture != null && getButton() == Input.Buttons.LEFT) {
                            clickedCell.setDrawable(new TextureRegionDrawable(selectedTexture));
                            clickedCell.setUserObject(selectedTileIndex);
                        } else if (getButton() == Input.Buttons.LEFT) {
                            clickedCell.setDrawable(new TextureRegionDrawable(transparentTexture));
                            clickedCell.setUserObject((int)-1);
                        }
                    }
                });

                inpTable.add(emptyCell).size(64);
            }
            inpTable.row();
        }
        return inpTable;
    }

    private void renderLoadedTable(Table map) {
        TextureRegion[] tilePhotos = MapUtilities.loadTilePhotos();

        for (int row = 0; row < NUM_ROWS; row++) {
            for (int col = 0; col < NUM_COLS; col++) {
                // gauni indexID ir nuotrauka corresponding indexID
                Cell loadedCell = map.getCells().get(col + row * NUM_COLS);
                int loadedValue = (int) loadedCell.getActor().getUserObject();

                // idedi img ir i img idedi ta setUserObject (jei -1 kas yra oras, tada idedi emptytextureRegion)
                Image img;
                if (loadedValue == -1) {
                   img = new Image(emptyTextureRegion);
                } else {
                    img = new Image(tilePhotos[loadedValue]);
                }
                img.setUserObject(loadedValue);

                // gauni dabartinio grid input cell kuria keisi
                Cell appCell = gridInput.getCells().get(col + row * NUM_COLS);

                if (appCell != null && appCell.getActor() instanceof Image) {
                    Image existingImage = (Image) appCell.getActor();
                    // pakeiti viska
                    existingImage.setDrawable(img.getDrawable());
                    existingImage.setUserObject(loadedValue);
                }
            }
        }
        // updatini scrollPane
        scrollPane.layout();
    }
    private Table getTilesTable() {
        Table tilesTable = new Table();
        tilesTable.setFillParent(true);
        int elemCounter = 0, blockIndex = 0;

        FileHandle[] tileFiles = Gdx.files.internal("tileSheets/lvl tileSheet seperate imgs").list();
        for (FileHandle file : tileFiles) {
            Texture texture = new Texture(file);
            Image tileImage = new Image(texture);

            // huh? nes inner method...? idk
            int finalBlockIndex = blockIndex;

            tileImage.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    selectedTileIndex = finalBlockIndex;
                    selectedTexture = texture;
                }
            });

            // del selection;
            ++blockIndex;

            // del grazesnio issidestymo tiles
            ++elemCounter;
            tilesTable.align(Align.right).add(tileImage).size(64).pad(10);
            if (elemCounter > 3) {
                tilesTable.row();
                elemCounter = 0;
            }

        }
        return tilesTable;
    }
    private Table getButtonsTable() {
        Table buttonsTable = new Table();
        buttonsTable.setFillParent(true);
        Skin skin = new Skin(Gdx.files.internal("skins/mainSkin.json"));
        TextButton menu = new TextButton("Menu", skin);
        TextButton loadLevel = new TextButton("Load", skin);
        TextButton saveLevel = new TextButton("Save", skin);

        buttonsTable.top().add(menu).pad(10);
        buttonsTable.top().add(loadLevel).pad(10);
        buttonsTable.top().add(saveLevel).pad(10);

        // create button listeners
        menu.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                game.changeScreen(MyGdxGame.MENU);
            }
        });

        loadLevel.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Table map = MapUtilities.loadMapFromFolder();
                renderLoadedTable(map);
            }
        });

        saveLevel.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                // somehow save to customLevels folder ir based on kiek ten failu taip pavadinti + 1
                MapUtilities.saveMapToFolder(gridInput);
            }
        });
        return buttonsTable;
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

    }
}
