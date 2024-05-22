package com.mygdx.game.scenes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.viewport.*;
import com.mygdx.game.LevelUtilities;
import com.mygdx.game.Player;
/**
 * @author Dovydas Girskas 5gr
 */
public class Hud {
    // Stage yra container aktoriams. actors => is ju darai UI. jie reprezentuoja individual blocks (button, tekstas etc.)
    // jis pats turi priega prie spriteBatch ir yra atsakingas uz aktoriu rendering. gali ju hierarchija koreguoti ir pan.
    // jei i klase idedi stage field, tai jau gali accessines ta field renderint aktorius tame stage fielde ant ekrano
    public Stage stage;
    public Viewport viewport;       // hud atskira camera locked
    private Integer levelTimer;
    private float timeCount;
    private static Integer collectedCoins;
    private static Integer allCoins;
    private Integer level;
    private Integer levelAmount;
    Label countdownLabel;
    static Label coinScoreLabel;
    Label timeLabel;
    Label levelLabel;
    Label worldLabel;
    Label tokenLabel;
    private Player player;

    private static final float FONT_SIZE_PERCENTAGE = 0.04f;
    private int scaledFontSize;
    public Hud(SpriteBatch sb, Player player, int coins, int levelAmount) {
        this.player = player;
        levelTimer = 0;
        timeCount = 0;
        collectedCoins = 0;
        allCoins = coins;
        level = LevelUtilities.levelsIndex;
        this.levelAmount = levelAmount;

        scaledFontSize = (int) (Gdx.graphics.getWidth() * FONT_SIZE_PERCENTAGE);

        viewport = new ScreenViewport();
        stage = new Stage(viewport, sb);

        Table table = new Table();
        table.top();
        table.setFillParent(true); // kad kaip stage dydzio butu lentele

        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/KodeMono-Bold.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = scaledFontSize; // Set the font size
        BitmapFont myFont = generator.generateFont(parameter);
        generator.dispose(); // Dispose the generator when you're done with it

//        BitmapFont myFont = new BitmapFont(Gdx.files.internal("fonts/arialFont.fnt"));
        myFont.getRegion().getTexture().setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);

        countdownLabel = new Label(String.format("%3d", levelTimer), new Label.LabelStyle(myFont, Color.BLACK));
        coinScoreLabel = new Label(String.format("%d%c%-2d", collectedCoins, '/', allCoins), new Label.LabelStyle(myFont, Color.BLACK));
        levelLabel = new Label(String.format("%2d%c%-2d", level, '/', levelAmount), new Label.LabelStyle(myFont, Color.BLACK));
        timeLabel = new Label("time", new Label.LabelStyle(myFont, Color.BLACK));
        worldLabel = new Label("level", new Label.LabelStyle(myFont, Color.BLACK));
        tokenLabel = new Label("tokens", new Label.LabelStyle(myFont, Color.BLACK));

        table.add(worldLabel).expandX().padTop(10);
        table.add(timeLabel).expandX().padTop(10);
        table.add(tokenLabel).expandX().padTop(10);
        table.row();
        table.add(levelLabel).expandX();
        table.add(countdownLabel).expandX();
        table.add(coinScoreLabel).expandX();

        stage.addActor(table);
    }

    public void update(float dt) {
        // update time
        timeCount += dt;
        if (timeCount >= 1) {
            ++levelTimer;
            countdownLabel.setText(String.format("%3d", levelTimer));
            timeCount = 0;
        }

        // update level
        if (LevelUtilities.levelsIndex != level) {
            ++level;
            levelLabel.setText(String.format("%2d%c%-2d", level, '/', levelAmount));
        }
    }

    public void updateFontSize() {
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/KodeMono-Bold.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        scaledFontSize = (int)(Gdx.graphics.getWidth() * FONT_SIZE_PERCENTAGE);
        parameter.size = scaledFontSize; // Set the font size
        BitmapFont myFont = generator.generateFont(parameter);
        generator.dispose(); // Dispose the generator when you're done with it

//        Label.LabelStyle style = new Label.LabelStyle(myFont, Color.valueOf("E4E6EB"));
        Label.LabelStyle style = new Label.LabelStyle(myFont, Color.valueOf("404040"));

        countdownLabel.setStyle(style);
        levelLabel.setStyle(style);
        worldLabel.setStyle(style);
        coinScoreLabel.setStyle(style);
        tokenLabel.setStyle(style);
        timeLabel.setStyle(style);
    }
    public static void addCoin() {
        ++collectedCoins;
        coinScoreLabel.setText(String.format("%d%c%-2d", collectedCoins, '/', allCoins));
    }
    public void resetHud() {
        levelTimer = 0;
        countdownLabel.setText(String.format("%3d", levelTimer));
        collectedCoins = 0;
        coinScoreLabel.setText(String.format("%d%c%-2d", collectedCoins, '/', allCoins));
    }
    public void setCoinsAmount(int newCoinsAmount) {
        allCoins = newCoinsAmount;
        coinScoreLabel.setText(String.format("%d%c%-2d", collectedCoins, '/', allCoins));
    }
}
