
package com.mygdx.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;
import com.mygdx.game.screens.*;

/**
 * @author Dovydas Girskas 5gr
 */

public class MyGdxGame extends Game {
	public static final int MAIN_LEVEL_AMOUNT = 2;
	public static final int BLOCK_DIMENSIONS = 32;
	public static final int CAMERA_HEIGHT = 10 * BLOCK_DIMENSIONS;
	public static final int CAMERA_WIDTH = 20 * BLOCK_DIMENSIONS;
	public static final int WORLD_HEIGHT = 20 * BLOCK_DIMENSIONS;
	public static final int WORLD_WIDTH = 80 * BLOCK_DIMENSIONS;
	private MainMenuScreen mainMenuScreen;
	private PlayScreen playScreen;
	private PlayScreen playCustomScreen;
	private EditingScreen editingScreen;
	private EndingScreen endingScreen;

	public final static int MENU = 0;
	public final static int PLAY = 1;
	public final static int PLAY_CUSTOM = 2;
	public final static int EDITOR = 3;
	public final static int ENDGAME = 4;
	public SpriteBatch batch; // this thing laiko per visa zaidima visus tavo assets n stuff


	@Override
	public void create () {
		// to rendering tool instance nustatai
		batch = new SpriteBatch();
		// set screen nustato koki ekrana dabar matysi
		changeScreen(MENU);
	}

	@Override
	public void render () {
		// programos flow mazdaug toks => sitos klases create metodas nustato ekrana kuris dabar aktyvus
		// sitos klases render() tada aktyvuojasi kas perduoda kontrole base.render()
		// ir parent klaseje kontrole perduodama aktyvios scenos klases render() metodui (siuo atveju PlayScreen)
		// jai baigus dirbti, controle atgal sitos klases render() metodui. ir loopas krc

		// super keyword kaip c# base. parent klases metoda callini
		// siuo atveju cia butu kad callina ScreenAdapter render() metoda
		// o tas metodas yra default rendering behavior for the screen
		// (clear the screen, update stage, render stage etc.)
		super.render();
	}

	public void changeScreen(int screen){
		switch(screen){
			case MENU:
				mainMenuScreen = new MainMenuScreen(this);
				this.setScreen(mainMenuScreen);
				break;
			case PLAY:
				playScreen = new PlayScreen(this, "levels");
				this.setScreen(playScreen);
				break;
			case PLAY_CUSTOM:
				playCustomScreen = new PlayScreen(this, "customLevels");
				this.setScreen(playCustomScreen);
				break;
			case EDITOR:
				editingScreen = new EditingScreen(this);
				this.setScreen(editingScreen);
				break;
		}
	}

	@Override
	public void dispose () {
	}
}
