package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.maps.tiled.TiledMap;

import java.io.File;

/**
 * @author Dovydas Girskas 5gr
 */
public class LevelUtilities {
    private TiledMap[] levels;
    private TiledMap currentLevel;
    public static int levelsIndex;
    public int levelAmount;
    public LevelUtilities(String folderName) {
        levelAmount = getHowManyLevelsAreThere(folderName);
        levels = new TiledMap[levelAmount];
        levels = loadLevels(folderName);

        levelsIndex = 0;
    }
    public static int getHowManyLevelsAreThere(String folderName) {
        FileHandle folder = Gdx.files.internal(folderName);
        FileHandle[] files = folder.list();
        return files.length;
    }
    private TiledMap[] loadLevels(String folderName) {
        for (int i = 0; i < levelAmount; ++i) {
            levels[i] = MapUtilities.getMapFromFile(folderName + "/level_" + (i + 1) + ".csv");
        }
        return levels;
    }
    public TiledMap getCurrentLevel(String folderName) {
        return MapUtilities.getMapFromFile(folderName + "/level_" + (levelsIndex) + ".csv");
    }
    public TiledMap loadNextLevel() {
        ++levelsIndex;
        currentLevel = levels[(levelsIndex - 1)];
        return currentLevel;
    }
}
