package com.martenscedric;

/**
 * Created by Cedric on 2017-04-22.
 */
public enum  BuildingType
{
    NONE("None", "You should never see this message", 0),
    HOUSE("Worker house", "Provides a worker to every adjacent tile. Needs an adjacent farm.", -1),
    FARM("Farm", "Provides food to every adjacent tile. Cannot be placed next to another farm.", -1),
    MINE("Mine", "Provides minerals to every adjacent tiles. Needs an adjacent worker", 1),
    WIND("Wind turbine", "Produces energy to every adjacent tile. Needs an adjacent worker.", 1),
    FACTORY("Factory", "Produces consumer goods to every adjacent tile. Needs an adjacent worker, wind turbine and minerals", 2),
    MARKET("Market", "Produces trade to every adjacent tile. Needs an adjacent worker, wind turbine and a factory", 3),
    BANK("Bank", "Produces money to every adjacent tile. Needs an adjacent worker, market, minerals and wind turbine", 5);

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
