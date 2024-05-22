package com.mygdx.game;

import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;

/**
 * @author Dovydas Girskas 5gr
 */
public class CustomCell extends TiledMapTileLayer.Cell {
    private int tileId;

    public void setTileId(int tileId) {
        this.tileId = tileId;
    }

    public int getTileId() {
        return tileId;
    }
}
