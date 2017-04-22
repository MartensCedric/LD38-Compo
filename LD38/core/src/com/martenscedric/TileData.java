package com.martenscedric;

import org.codetome.hexameter.core.api.contract.SatelliteData;

/**
 * Created by Cedric on 2017-04-21.
 */
public class TileData implements SatelliteData
{
    private TileType tileType;

    public TileType getTileType() {
        return tileType;
    }

    public void setTileType(TileType tileType) {
        this.tileType = tileType;
    }

    @Override
    public boolean isPassable() {
        return true;
    }

    @Override
    public void setPassable(boolean passable) {

    }

    @Override
    public boolean isOpaque() {
        return true;
    }

    @Override
    public void setOpaque(boolean opaque) {

    }

    @Override
    public double getMovementCost() {
        return 0;
    }

    @Override
    public void setMovementCost(double movementCost) {

    }
}
