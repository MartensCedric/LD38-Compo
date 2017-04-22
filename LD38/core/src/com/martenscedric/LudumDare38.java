package com.martenscedric;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import org.codetome.hexameter.core.api.*;
import org.codetome.hexameter.core.api.Point;
import rx.Observable;



public class LudumDare38 extends ApplicationAdapter {
	private final int WATER_TILES = 3;
	private final int GRID_WIDTH = 9;
	private final int GRID_HEIGHT = 9;
	private SpriteBatch batch;
	private PolygonSpriteBatch polyBatch;
	private HexagonalGrid<TileData> grid;
	private ShapeRenderer shapeRenderer;
	
	@Override
	public void create () {

		batch = new SpriteBatch();
		polyBatch = new PolygonSpriteBatch();

		HexagonalGridBuilder<TileData> builder = new HexagonalGridBuilder<TileData>()
				.setGridHeight(GRID_WIDTH)
				.setGridWidth(GRID_HEIGHT)
				.setGridLayout(HexagonalGridLayout.HEXAGONAL)
				.setOrientation(HexagonOrientation.FLAT_TOP)
				.setRadius(Gdx.graphics.getWidth()/16);

		grid = builder.build();
		initHexData();
		fillHexs();
		initInput();
		shapeRenderer = new ShapeRenderer();
		shapeRenderer.setAutoShapeType(true);

		AssetLoader.load();

		Hexagon<TileData> hex1 = grid.getByCubeCoordinate(CubeCoordinate.fromCoordinates(3, 5)).get();
		TileData data1 = hex1.getSatelliteData().get();
		data1.setTileType(TileType.WATER);

		Hexagon<TileData> hex2 = grid.getByCubeCoordinate(CubeCoordinate.fromCoordinates(2, 1)).get();
		TileData data2 = hex2.getSatelliteData().get();
		data2.setTileType(TileType.WATER);

		Hexagon<TileData> hex3 = grid.getByCubeCoordinate(CubeCoordinate.fromCoordinates(7, 0)).get();
		TileData data3 = hex3.getSatelliteData().get();
		data3.setTileType(TileType.WATER);

		Hexagon<TileData> hex4 = grid.getByCubeCoordinate(CubeCoordinate.fromCoordinates(4, 2)).get();
		TileData data4 = hex4.getSatelliteData().get();
		data4.setTileType(TileType.HOUSE);

	}

	@Override
	public void render () {
		Gdx.gl.glClearColor(66f/255f, 206f/255f, 244f/255f, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		Observable<Hexagon<TileData>> hexagons = grid.getHexagons();
		hexagons.forEach(hex -> {
			hex.getSatelliteData().get().draw(polyBatch, batch);
		});
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

				screenY = Gdx.graphics.getHeight() - screenY;
				Hexagon<TileData> data = grid.getByPixelCoordinate(screenX, screenY).get();
				System.out.println(data.getCubeCoordinate().toAxialKey());
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
    		data.setTileType(TileType.GRASS);
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
