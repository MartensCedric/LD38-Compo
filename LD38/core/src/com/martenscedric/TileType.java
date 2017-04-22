package com.martenscedric;

/**
 * Created by Cedric on 2017-04-21.
 */
public enum TileType
{
    GRASS("Grass", "An empty area"),
    WATER("Water", "Wet"),
    HOUSE("Worker house", "Provides a worker to every adjacent tile"),
    FARM("Farm", "Provides food to every adjacent tile and produces a small amount of resources."),
    MINE("Mine", "Provides minerals to every adjacent tiles. Needs a worker"),
    WIND("Wind turbine", "Produces energy to every adjacent tile"),
    FACTORY("Factory", "Produces consumer goods to every adjacent tile. Needs a worker, energy and minerals"),
    BANK("Bank", "Produces money");

    private String name;
    private String desc;

    TileType(String name, String desc)
    {
        this.name = name;
        this.desc = desc;
    }

    public String getName() {
        return name;
    }

    public String getDesc() {
        return desc;
    }
}
