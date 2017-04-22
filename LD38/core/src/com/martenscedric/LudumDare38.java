package com.martenscedric;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.EarClippingTriangulator;
import com.badlogic.gdx.utils.ShortArray;
import org.codetome.hexameter.core.api.*;
import org.codetome.hexameter.core.api.Point;
import rx.Observable;
import rx.functions.Action1;


public class LudumDare38 extends ApplicationAdapter {
	private SpriteBatch batch;
	private PolygonSpriteBatch polyBatch;
	private HexagonalGrid<TileData> grid;
	private ShapeRenderer shapeRenderer;
	
	@Override
	public void create () {

		batch = new SpriteBatch();
		polyBatch = new PolygonSpriteBatch();

		HexagonalGridBuilder<TileData> builder = new HexagonalGridBuilder<TileData>()
				.setGridHeight(9)
				.setGridWidth(9)
				.setGridLayout(HexagonalGridLayout.HEXAGONAL)
				.setOrientation(HexagonOrientation.FLAT_TOP)
				.setRadius(Gdx.graphics.getWidth()/16);

		grid = builder.build();
		initHexData();
		fillHexs();
		initInput();
		shapeRenderer = new ShapeRenderer();
		shapeRenderer.setAutoShapeType(true);
	}

	@Override
	public void render () {
		Gdx.gl.glClearColor(1, 1, 1, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		polyBatch.begin();
		Observable<Hexagon<TileData>> hexagons = grid.getHexagons();
		hexagons.forEach(hex -> {
			TileData data = hex.getSatelliteData().get();
			data.getSprite().draw(polyBatch);
		});
		polyBatch.end();
		batch.begin();

		batch.end();
		shapeRenderer.begin();
		renderHexs();
		shapeRenderer.end();
	}

	private void initInput()
	{
		Gdx.input.setInputProcessor(new InputProcessor() {
			@Override
			public boolean keyDown(int keycode) {
				return false;
			}

			@Override
			public boolean keyUp(int keycode) {
				return false;
			}

			@Override
			public boolean keyTyped(char character) {
				return false;
			}

			@Override
			public boolean touchDown(int screenX, int screenY, int pointer, int button) {

				if(!Gdx.input.isButtonPressed(Input.Buttons.LEFT))
					return false;
				Hexagon<TileData> data = grid.getByPixelCoordinate(screenX, screenY).get();
				System.out.println(data.getCenterX() + " " + data.getCenterY());
				return true;
			}

			@Override
			public boolean touchUp(int screenX, int screenY, int pointer, int button) {
				return false;
			}

			@Override
			public boolean touchDragged(int screenX, int screenY, int pointer) {
				return false;
			}

			@Override
			public boolean mouseMoved(int screenX, int screenY) {
				return false;
			}

			@Override
			public boolean scrolled(int amount) {
				return false;
			}
		});
	}

	private void initHexData()
	{
		Observable<Hexagon<TileData>> hexagons = grid.getHexagons();
		hexagons.forEach(hex -> {
			hex.setSatelliteData(new TileData(hex));
		});
	}

	private void fillHexs()
	{
		Observable<Hexagon<TileData>> hexagons = grid.getHexagons();
		hexagons.forEach(hex -> {
			TileData data = hex.getSatelliteData().get();
    		data.setColor(0x11FF38FF);
        });
	}

	private void renderHexs()
	{
		Observable<Hexagon<TileData>> hexagons = grid.getHexagons();
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
	}
	@Override
	public void dispose () {
		batch.dispose();
	}
}
