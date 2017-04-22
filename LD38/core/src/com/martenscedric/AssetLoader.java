package com.martenscedric;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;

/**
 * Created by Cedric on 2017-04-22.
 */
public class AssetLoader
{
    public static AssetManager assetManager = new AssetManager();

    public static void load()
    {
        assetManager.load("house.png", Texture.class);
        assetManager.load("wind.png", Texture.class);
        assetManager.finishLoading();
    }
}
