package com.martenscedric;

/**
 * Created by Cedric on 2017-04-22.
 */
public enum  BuildingType
{
    NONE("None", "You should never see this message", 0),
    HOUSE("Worker house", "Provides a worker to every adjacent tile. Needs an adjacent food source.", -1),
    FARM("Farm", "Provides food to every adjacent tile. Cannot be placed next to another farm.", -1),
    MINE("Mine", "Provides minerals to every adjacent tiles. Needs a worker", 1),
    WIND("Wind turbine", "Produces energy to every adjacent tile. Needs a worker.", 1),
    FACTORY("Factory", "Produces consumer goods to every adjacent tile. Needs a worker, energy and minerals", 2),
    BANK("Bank", "Produces money", 3);

    private String name;
    private String desc;
    private int score;

    BuildingType(String name, String desc, int score)
    {
        this.name = name;
        this.desc = desc;
        this.score = score;
    }

    public String getName() {
        return name;
    }

    public String getDesc() {
        return desc;
    }

    public int getScore() {
        return score;
    }
}
