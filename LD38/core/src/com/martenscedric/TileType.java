package com.martenscedric;

/**
 * Created by Cedric on 2017-04-21.
 */
public enum TileType
{
    GRASS("Grass", "An empty area"),
    WATER("Water", "Adjacent farms get a +1 range bonus"),
    HOUSE("Worker house", "Provides a worker to every adjacent tile"),
    FARM("Farm", "Provides food to every adjacent tile and produces a small amount of resources. Needs a worker"),
    MINE("Mine", "Produces a large amount of resources. Needs a worker and energy"),
    WIND("Wind turbine", "Produces energy to every adjacent tile"),
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
