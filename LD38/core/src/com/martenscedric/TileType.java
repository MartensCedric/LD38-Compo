package com.martenscedric;

/**
 * Created by Cedric on 2017-04-21.
 */
public enum TileType
{
    GRASS("Grass", 1, 0x11FF38FF),
    WATER("Water", 1, 0x4286F4FF),
    SAND("Sand", 0, 0xe8d17fFF),
    FOREST("Forest", 2, 0x284919FF);


    private String name;
    private int multiplier;
    private int color;

    TileType(String name, int multiplier, int color)
    {
        this.name = name;
        this.multiplier = multiplier;
        this.color = color;
    }

    public String getName() {
        return name;
    }

    public int getMultiplier() {
        return multiplier;
    }

    public int getColor() {
        return color;
    }
}
