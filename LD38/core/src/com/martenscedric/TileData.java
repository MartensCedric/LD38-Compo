package com.martenscedric;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.math.EarClippingTriangulator;
import com.badlogic.gdx.utils.ShortArray;
import org.codetome.hexameter.core.api.Hexagon;
import org.codetome.hexameter.core.api.Point;
import org.codetome.hexameter.core.api.defaults.DefaultSatelliteData;

/**
 * Created by Cedric on 2017-04-21.
 */
public class TileData extends DefaultSatelliteData {
    private TileType tileType;
    private BuildingType buildingType;
    private PolygonSprite sprite;
    private Texture texture;
    private Hexagon<TileData> parent;
    private int drawCalls = 0;
    private boolean tileFlipped = false;

    public TileData(Hexagon<TileData> parent)
    {
        this.parent = parent;
        setTileType(TileType.GRASS);
        setBuilding(BuildingType.NONE);
    }

    public Texture getTexture() {
        return texture;
    }

    public Hexagon<TileData> getParent() {
        return parent;
    }

    public void setTexture(Texture texture) {
        this.texture = texture;
    }

    public PolygonSprite getSprite() {
        return sprite;
    }

    public BuildingType getBuildingType() {
        return buildingType;
    }

    public void setColor(int color) {
        Pixmap pix = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pix.setColor(color);
        pix.fill();
        Texture tex = new Texture(pix);
        TextureRegion region = new TextureRegion(tex);

        Point p1 = (Point) parent.getPoints().toArray()[0];
        Point p2 = (Point) parent.getPoints().toArray()[1];
        Point p3 = (Point) parent.getPoints().toArray()[2];
        Point p4 = (Point) parent.getPoints().toArray()[3];
        Point p5 = (Point) parent.getPoints().toArray()[4];
        Point p6 = (Point) parent.getPoints().toArray()[5];

        float[] vertices = new float[]{(
                float) p1.getCoordinateX(), (float)p1.getCoordinateY(),
                (float)p2.getCoordinateX(), (float)p2.getCoordinateY(),
                (float)p3.getCoordinateX(), (float)p3.getCoordinateY(),
                (float) p4.getCoordinateX(), (float)p4.getCoordinateY(),
                (float)p5.getCoordinateX(), (float)p5.getCoordinateY(),
                (float)p6.getCoordinateX(), (float)p6.getCoordinateY()};
        EarClippingTriangulator triangulator = new EarClippingTriangulator();
        ShortArray triangleIndices = triangulator.computeTriangles(vertices);
        PolygonRegion polygonRegion = new PolygonRegion(region, vertices, triangleIndices.toArray());
        sprite = new PolygonSprite(polygonRegion);
    }

    public void setTileType(TileType tileType)
    {
        this.tileType = tileType;
        setColor(tileType.getColor());
    }

    public TileType getTileType() {
        return tileType;
    }

    public void setBuilding(BuildingType buildingType) {
        this.buildingType = buildingType;

        switch (buildingType)
        {
            case HOUSE:
                setTexture(AssetLoader.assetManager.get("house.png", Texture.class));
                break;
            case WIND:
                setTexture(AssetLoader.assetManager.get("wind.png", Texture.class));
                break;
            case FARM:
                setTexture(AssetLoader.assetManager.get("farm.png", Texture.class));
                break;
            case MINE:
                setTexture(AssetLoader.assetManager.get("mine.png", Texture.class));
                break;
            case FACTORY:
                setTexture(AssetLoader.assetManager.get("factory.png", Texture.class));
                break;
            case NONE:
                setTexture(null);
                break;
        }
    }

    public void draw(PolygonSpriteBatch poly, SpriteBatch batch)
    {
        if (buildingType == BuildingType.NONE)
        {
            renderTileColor(poly);
        }else{
            renderTileColor(poly);
            if(buildingType == buildingType.WIND)
            {
                tileFlipped = drawCalls < 30;
                renderTileSprite(batch, tileFlipped);

            }else{
                renderTileSprite(batch);
            }
        }
        drawCalls = ++drawCalls % 60;
    }


    private void renderTileColor(PolygonSpriteBatch poly)
    {
        poly.begin();
        sprite.draw(poly);
        poly.end();
    }

    private void renderTileSprite(SpriteBatch batch)
    {
        renderTileSprite(batch, false);
    }

    private void renderTileSprite(SpriteBatch batch, boolean tileFlipped)
    {
        batch.begin();
        batch.draw(texture, (float) parent.getCenterX() - texture.getWidth()/2, (float) (parent.getCenterY() - texture.getHeight()/2),
                (float)texture.getWidth(), (float)texture.getHeight(), 0, 0,
                texture.getWidth(), texture.getHeight(), tileFlipped, false);
        batch.end();
    }
}
