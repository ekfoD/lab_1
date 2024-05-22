package com.mygdx.game.screens;

import com.badlogic.gdx.Screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.mygdx.game.LevelUtilities;
import com.mygdx.game.MyGdxGame;
import com.mygdx.game.RainProgram;
import com.mygdx.game.ShaderUtilities;

/**
 * @author Dovydas Girskas 5gr
 */
public class MainMenuScreen implements Screen {

    private MyGdxGame game;
    private Stage stage;
    private RainProgram rain;
    private float elapsedTime;
    public MainMenuScreen(MyGdxGame game){
        this.game = game;
        // create stage and set it as input processor
        stage = new Stage(new ScreenViewport());

        rain = new RainProgram((OrthographicCamera) stage.getCamera());
        Gdx.input.setInputProcessor(stage); //  tells the screen to send any input from the user to the stage so it can respond.
    }

    @Override
    public void show() {
        // Create a table that fills the screen. Everything else will go inside this table.
        Table table = new Table();
        table.setFillParent(true);
        stage.addActor(table);

        Skin skin = new Skin(Gdx.files.internal("skins/mainSkin.json"));

        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/KodeMono-Bold.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 40; // Set the font size
        BitmapFont myFont = generator.generateFont(parameter);
        generator.dispose(); // Dispose the generator when you're done with it


        //create Text field
        Label title = new Label("Shadow boy", new Label.LabelStyle(myFont, Color.BLACK));
        title.setFontScale(2f);
        //create buttons
        TextButton newGame = new TextButton("New Game", skin);
        TextButton playCustom = new TextButton("Play Custom", skin);
        TextButton editor = new TextButton("Editor", skin);
        TextButton exit = new TextButton("Exit", skin);

        //add buttons to table
        table.add(title).expandX().colspan(2).center().padBottom(30).row(); // colspan(2) to span the title across two columns
        table.row();
        table.add(newGame).center().uniformX().expandX();
        table.row().pad(10, 0, 10, 0);
        table.add(playCustom).center().uniformX().expandX();
        table.row();
        table.add(editor).center().uniformX().expandX();
        table.row().pad(10, 0, 10, 0);
        table.add(exit).center().uniformX().expandX();

        // create button listeners
        newGame.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                game.changeScreen(MyGdxGame.PLAY);
            }
        });

        playCustom.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (LevelUtilities.getHowManyLevelsAreThere("customLevels") != 0)
                    game.changeScreen(MyGdxGame.PLAY_CUSTOM);
            }
        });

        editor.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                game.changeScreen(MyGdxGame.EDITOR);
            }
        });

        exit.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Gdx.app.exit();
            }
        });
    }

    @Override
    public void render(float delta) {
        elapsedTime += delta;
        // screen go brrr
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
        // change the stage's viewport when teh screen size is changed
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void pause() {
        // TODO Auto-generated method stub

    }

    @Override
    public void resume() {
        // TODO Auto-generated method stub

    }

    @Override
    public void hide() {
        // TODO Auto-generated method stub

    }

    @Override
    public void dispose() {
        // dispose of assets when not needed anymore
        stage.dispose();
    }

}