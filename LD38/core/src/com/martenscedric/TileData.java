package com.martenscedric;

import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.PolygonRegion;
import com.badlogic.gdx.graphics.g2d.PolygonSprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.EarClippingTriangulator;
import com.badlogic.gdx.utils.ShortArray;
import org.codetome.hexameter.core.api.Hexagon;
import org.codetome.hexameter.core.api.Point;
import org.codetome.hexameter.core.api.contract.SatelliteData;
import org.codetome.hexameter.core.api.defaults.DefaultSatelliteData;

/**
 * Created by Cedric on 2017-04-21.
 */
public class TileData extends DefaultSatelliteData {
    private TileType tileType;
    private PolygonSprite sprite;
    private Hexagon<TileData> parent;

    public TileData(Hexagon<TileData> parent)
    {
        this.parent = parent;
    }

    public PolygonSprite getSprite() {
        return sprite;
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

    public TileType getTileType() {
        return tileType;
    }

    public void setTileType(TileType tileType) {
        this.tileType = tileType;
    }
}
