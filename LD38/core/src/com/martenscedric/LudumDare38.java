package com.martenscedric;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import org.codetome.hexameter.core.api.*;
import org.codetome.hexameter.core.api.Point;
import rx.Observable;
import rx.functions.Action1;


public class LudumDare38 extends ApplicationAdapter {
	private SpriteBatch batch;
	private HexagonalGrid grid;
	private ShapeRenderer shapeRenderer;
	
	@Override
	public void create () {
		batch = new SpriteBatch();

		HexagonalGridBuilder builder = new HexagonalGridBuilder()
				.setGridHeight(9)
				.setGridWidth(9)
				.setGridLayout(HexagonalGridLayout.HEXAGONAL)
				.setOrientation(HexagonOrientation.FLAT_TOP)
				.setRadius(Gdx.graphics.getWidth()/16);

		grid = builder.build();
		shapeRenderer = new ShapeRenderer();
		shapeRenderer.setAutoShapeType(true);
	}

	@Override
	public void render () {
		Gdx.gl.glClearColor(1, 1, 1, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		batch.begin();

		batch.end();
		shapeRenderer.begin();

		Observable<Hexagon> hexagons = grid.getHexagons();
		hexagons.forEach(hexagon -> {
            Point p0 = (Point) hexagon.getPoints().toArray()[0];
            Point pLast = (Point) hexagon.getPoints().toArray()[hexagon.getPoints().size() - 1];
            shapeRenderer.line((float)p0.getCoordinateX(), (float)p0.getCoordinateY(),
                    (float)pLast.getCoordinateX(), (float)pLast.getCoordinateY(),
					Color.BLACK, Color.BLACK);
            for(int i = 1; i < hexagon.getPoints().size(); i++)
            {
                Point current = (Point)hexagon.getPoints().toArray()[i];
                Point precedent = (Point)hexagon.getPoints().toArray()[i - 1];
                shapeRenderer.line((float)current.getCoordinateX(), (float)current.getCoordinateY(),
                        (float)precedent.getCoordinateX(), (float)precedent.getCoordinateY(),
                        Color.BLACK, Color.BLACK);
            }
        });
		shapeRenderer.end();
	}
	
	@Override
	public void dispose () {
		batch.dispose();

	}
}
