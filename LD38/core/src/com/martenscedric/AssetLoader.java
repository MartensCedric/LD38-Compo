package com.martenscedric;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.SoundLoader;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;

import java.util.HashMap;

/**
 * Created by Cedric on 2017-04-22.
 */
public class AssetLoader
{
    public static AssetManager assetManager = new AssetManager();
    public static HashMap<String, Sound> sounds = new HashMap<>();
    private static BitmapFont font = null;

    public static void load()
    {
        assetManager.load("house.png", Texture.class);
        assetManager.load("wind.png", Texture.class);
        assetManager.load("farm.png", Texture.class);
        assetManager.load("mine.png", Texture.class);
        assetManager.load("factory.png", Texture.class);
        assetManager.load("market.png", Texture.class);
        assetManager.load("bank.png", Texture.class);
        assetManager.load("rocket.png", Texture.class);
        assetManager.load("cloud.png", Texture.class);
        sounds.put("click", Gdx.audio.newSound(Gdx.files.internal("sound/click.wav")));
        sounds.put("bad", Gdx.audio.newSound(Gdx.files.internal("sound/bad.wav")));
        assetManager.finishLoading();
    }

    public static BitmapFont getFont() {
        if (font == null) {
            FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("VCROSDMono.ttf"));
            FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
            parameter.size = 30;
            parameter.borderWidth = 1;
            parameter.color = Color.BLACK;
            parameter.shadowOffsetX = 2;
            parameter.shadowOffsetY = 2;
            parameter.shadowColor = new Color(0, 1f, 0, 0.5f);
            font = generator.generateFont(parameter);
            generator.dispose();
        }
        return font;
    }
}
