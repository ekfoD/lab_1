package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.tiles.StaticTiledMapTile;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Array;
import com.mygdx.game.screens.EditingScreen;

import javax.swing.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Dovydas Girskas 5gr
 */
public class MapUtilities {
    private final static int BLOCK_DIMENSIONS = MyGdxGame.BLOCK_DIMENSIONS;
    public final static int COINS_LAYER = 0;
    public final static int LADDER_LAYER = 1;
    public final static int BLOCKS_LAYER = 2;
    public final static int SPIKES_LAYER = 3;
    public static int[][] readMapCoordsFromFile(String fileName) {
        int[][] map = null;

        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            // nuskaito
            List<int[]> rows = new ArrayList<>();
            String line;
            while ((line = br.readLine()) != null) {
                String[] values = line.split(",");
                int[] row = new int[values.length];
                for (int col = 0; col < values.length; col++) {
                    row[col] = Integer.parseInt(values[col]);
                }
                rows.add(row);
            }
            // Determine number of rows and columns
            int y = rows.size();
            int x = rows.get(0).length; // Assuming all rows have the same length

            // Create and fill the 2D array
            map = new int[y][x];
            for (int i = y - 1, g = 0; i >= 0; i--, g++) {              // somehow paflippinau??? lol lmao
                for (int j = 0; j < x; j++) {
                    map[i][j] = rows.get(g)[j];
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return map;
    }
    public static TextureRegion[] loadTilePhotos() {
        //Texture img = MyGdxGame.tilesheetTexture;
        Texture img = new Texture("tileSheets/levelTileSheet.png");


        // Calculate the number of tiles in both dimensions
        int numTilesX = img.getWidth() / BLOCK_DIMENSIONS;
        int numTilesY = img.getHeight() / BLOCK_DIMENSIONS;
        int numTiles = numTilesX * numTilesY;


        // Split the texture into a 2D array of TextureRegions
        TextureRegion[][] regions = TextureRegion.split(img, BLOCK_DIMENSIONS, BLOCK_DIMENSIONS);
        // cia bus array su fotkem
        TextureRegion[] tiles = new TextureRegion[numTiles];

        int index = 0;

        for (int y = 0; y < numTilesY; y++) {
            for (int x = 0; x < numTilesX; x++) {
                tiles[index++] = regions[y][x];
            }
        }

        return tiles;
    }
    public static TiledMap getMapFromFile (String fileName){
        // gaunu koordinates
        int[][] mapCoords = readMapCoordsFromFile(fileName);

        // gaunu seperate nuotraukas is tileSheet
        TextureRegion[] tilePhotos = loadTilePhotos();

        TiledMapTile tile;
        TiledMap tiledMap = new TiledMap();
        TextureRegion region;

        // Calculate map dimensions
        int mapHeight = mapCoords.length;
        int mapWidth = mapCoords[0].length;

        TiledMapTileLayer coinLayer = new TiledMapTileLayer(mapWidth, mapHeight, BLOCK_DIMENSIONS, BLOCK_DIMENSIONS);
        TiledMapTileLayer ladderLayer = new TiledMapTileLayer(mapWidth, mapHeight, BLOCK_DIMENSIONS, BLOCK_DIMENSIONS);
        TiledMapTileLayer blocksLayer = new TiledMapTileLayer(mapWidth, mapHeight, BLOCK_DIMENSIONS, BLOCK_DIMENSIONS);
        TiledMapTileLayer spikesLayer = new TiledMapTileLayer(mapWidth, mapHeight, BLOCK_DIMENSIONS, BLOCK_DIMENSIONS);

        // sitam loope pagal coord reiksme pridedi
        for (int y = 0; y < mapHeight; y++) {
            for (int x = 0; x < mapWidth; x++) {
                int tileId = mapCoords[y][x]; // gauni coord

                // gauni nuotrauka tos coord
                if (tileId == -1) {
                    continue;
                } else {
                    region = tilePhotos[tileId];
                }

                CustomCell cell = new CustomCell();

                // Add the tile to the map
                cell.setTile(new StaticTiledMapTile(region));
                cell.setTileId(tileId);
                switch (tileId) {
                    case 0:
                    case 1:
                    case 2:
                    case 3:
                    case 4:
                    case 5:
                    case 6:
                    case 7:
                    case 8:
                    case 9:
                    case 10:
                        blocksLayer.setCell(x, y, cell);
                        break;
                    case 11:
                    case 12:
                        ladderLayer.setCell(x, y, cell);
                        break;
                    case 13:
                        coinLayer.setCell(x, y, cell);
                        break;
                    case 14:
                    case 15:
                    case 16:
                    case 17:
                    case 18:
                        spikesLayer.setCell(x, y, cell);
                        break;
                    default:
                        throw new RuntimeException();
                }
            }
        }
        tiledMap.getLayers().add(coinLayer);
        tiledMap.getLayers().add(ladderLayer);
        tiledMap.getLayers().add(blocksLayer);
        tiledMap.getLayers().add(spikesLayer);
        return tiledMap;
    }
    public static int countCoinsInMap(TiledMap tiledMap) {
        TiledMapTileLayer layer = (TiledMapTileLayer) tiledMap.getLayers().get(COINS_LAYER);

        int cellCount = 0;
        int width = layer.getWidth();
        int height = layer.getHeight();

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                TiledMapTileLayer.Cell cell = layer.getCell(x, y);
                if (cell != null) {
                    cellCount++;
                }
            }
        }
        return cellCount;
    }
    public static void saveMapToFolder(Table map) {
        // sitas laikys mapa
        StringBuilder stringBuilder = new StringBuilder();

        // visus table cells laiko
        Array<Cell> cells = map.getCells();

        // prideda "grid"
        for (int row = 0; row < EditingScreen.NUM_ROWS; row++) {
            for (int col = 0; col < EditingScreen.NUM_COLS; col++) {

                Cell cell = cells.get(col + row * EditingScreen.NUM_COLS);
                Image img = (Image)cell.getActor();

                // gauti somehow bloko index ir appendinti stringBuilderi ir + kablelis
                if ((col + 1) == EditingScreen.NUM_COLS) {
                    stringBuilder.append((int) img.getUserObject());
                    stringBuilder.append('\n');
                } else {
                    stringBuilder.append((int) img.getUserObject());
                    stringBuilder.append(',');
                }
            }
        }

        // is to string builderio extractinam stringa
        String mapInCsvFormat = stringBuilder.toString();


        // kad issavintu tinkamu vardu
        FileHandle folderHandle = new FileHandle("customLevels");
        int mapIndex = folderHandle.list().length;
        String filePath = "customLevels/level_" + (mapIndex + 1) + ".csv";
        folderHandle = Gdx.files.local(filePath);
        folderHandle.writeString(mapInCsvFormat,false);
    }
    public static Table loadMapFromFolder() {
        Table mapTable = new Table();
        int[][] mapInArray = readMapCoordsFromFile(userSelectedFile());     // user selection
        TextureRegion[] tiles = loadTilePhotos();

        for (int row = EditingScreen.NUM_ROWS - 1; row >= 0; row--) {
            for (int col = 0; col < EditingScreen.NUM_COLS; col++) {
                Image img;
                if (mapInArray[row][col] == -1) {
                    img = new Image();
                } else
                    img = new Image(tiles[mapInArray[row][col]]);
                img.setUserObject(mapInArray[row][col]);
                mapTable.add(img).size(64);
            }
            mapTable.row();
        }
        return mapTable;
    }

    private static String userSelectedFile() {
        String filePath = "levels/level_1.csv";     // default value
        JFileChooser fileChooser = new JFileChooser("C:\\Users\\dovyd\\Desktop\\all\\UNIVERAS\\(PRAKT) objektinis programavimas\\game\\assets");
        int result = fileChooser.showOpenDialog(null); // Show open file dialog

        // Check if the user selected a file
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            filePath = selectedFile.getAbsolutePath(); // gauni path ir ja grazini
        }

        return filePath;
    }
}
