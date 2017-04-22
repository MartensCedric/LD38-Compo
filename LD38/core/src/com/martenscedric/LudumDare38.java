package com.martenscedric;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.GdxRuntimeException;
import org.codetome.hexameter.core.api.*;
import org.codetome.hexameter.core.api.Point;
import org.codetome.hexameter.core.backport.Optional;
import rx.Observable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


public class LudumDare38 extends ApplicationAdapter {
	private final int WATER_TILES = 3;
	private final int GRID_WIDTH = 9;
	private final int GRID_HEIGHT = 9;
	private final int MENU_PADDING_Y = 10;

	private SpriteBatch batch;
	private PolygonSpriteBatch polyBatch;
	private HexagonalGrid<TileData> grid;
	private ShapeRenderer shapeRenderer;
	private TileType currentCursorSelect = null;
	private ShaderProgram invalidPlacement;
	private ShaderProgram unlawfulPlacement;
	private ShaderProgram okPlacement;
	private List<Texture> menuTextures;
	private String scoreText = "SCORE : %d";
	private int score = 0;
	
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

		String vertexShader = Gdx.files.internal("defaultvertex.vs").readString();
		String redShader = Gdx.files.internal("redtrans.fs").readString();
		invalidPlacement = new ShaderProgram(vertexShader, redShader);
		if (!invalidPlacement.isCompiled()) throw new GdxRuntimeException("Couldn't compile shader: " + invalidPlacement.getLog());

		String yellowShader = Gdx.files.internal("yellowtrans.fs").readString();
		unlawfulPlacement = new ShaderProgram(vertexShader, yellowShader);
		if (!unlawfulPlacement.isCompiled()) throw new GdxRuntimeException("Couldn't compile shader: " + unlawfulPlacement.getLog());

		String okShader = Gdx.files.internal("slightlytrans.fs").readString();
		okPlacement = new ShaderProgram(vertexShader, okShader);
		if (!okPlacement.isCompiled()) throw new GdxRuntimeException("Couldn't compile shader: " + okPlacement.getLog());
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

		Hexagon<TileData> hex5 = grid.getByCubeCoordinate(CubeCoordinate.fromCoordinates(4, 3)).get();
		TileData data5 = hex5.getSatelliteData().get();
		data5.setTileType(TileType.WIND);

		Hexagon<TileData> hex6 = grid.getByCubeCoordinate(CubeCoordinate.fromCoordinates(3, 3)).get();
		TileData data6 = hex6.getSatelliteData().get();
		data6.setTileType(TileType.FARM);

		Hexagon<TileData> hex7 = grid.getByCubeCoordinate(CubeCoordinate.fromCoordinates(5, 2)).get();
		TileData data7 = hex7.getSatelliteData().get();
		data7.setTileType(TileType.MINE);

		menuTextures = new ArrayList<>();
		menuTextures.add(AssetLoader.assetManager.get("house.png", Texture.class));
		menuTextures.add(AssetLoader.assetManager.get("farm.png", Texture.class));
		menuTextures.add(AssetLoader.assetManager.get("mine.png", Texture.class));
		menuTextures.add(AssetLoader.assetManager.get("wind.png", Texture.class));
		menuTextures.add(AssetLoader.assetManager.get("factory.png", Texture.class));
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
		calculateScore();
		AssetLoader.getFont().draw(batch, String.format(scoreText, score), 5,25);
		batch.end();
		shapeRenderer.begin();
		renderHexs();
		shapeRenderer.end();
		drawMenu();
		drawCursorSelect();

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
				Optional<Hexagon<TileData>> dataOpt = grid.getByPixelCoordinate(screenX, screenY);
				if(dataOpt.isPresent())
				{
					Hexagon<TileData> data = dataOpt.get();
					if(currentCursorSelect != null && data.getSatelliteData().get().getTileType() == TileType.GRASS
							&& isLegal(data.getSatelliteData().get()))
					{
						data.getSatelliteData().get().setTileType(currentCursorSelect);
						currentCursorSelect = null;
					}
					System.out.println(data.getCubeCoordinate().toAxialKey() +  " " + data.getSatelliteData().get().getTileType().getName());
				}else{
					currentCursorSelect = getMenuItem(screenX, screenY);
				}

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

	private void drawMenu()
	{

		shapeRenderer.begin();
		shapeRenderer.set(ShapeRenderer.ShapeType.Filled);
		shapeRenderer.rect(Gdx.graphics.getWidth() - 80, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight(),
				Color.valueOf("#42f498"), Color.valueOf("#42f498"), Color.valueOf("#42f498"), Color.valueOf("#42f498"));
		shapeRenderer.set(ShapeRenderer.ShapeType.Line);
		shapeRenderer.line(Gdx.graphics.getWidth() - 80, 0, Gdx.graphics.getWidth() - 80, Gdx.graphics.getHeight(), Color.BLACK, Color.BLACK);

		shapeRenderer.end();

		batch.begin();
		for(int i = 0; i < menuTextures.size(); i++)
		{
			batch.draw(menuTextures.get(i),
					Gdx.graphics.getWidth() - 40 - menuTextures.get(i).getWidth()/2, Gdx.graphics.getHeight() - (menuTextures.get(i).getHeight() + MENU_PADDING_Y + i * 75),
					menuTextures.get(i).getWidth(), menuTextures.get(i).getHeight());
		}
		batch.end();
	}

	private void drawCursorSelect()
	{
		if(currentCursorSelect !=null)
		{

			Texture texture = null;
			switch (currentCursorSelect)
			{
				case HOUSE:
					texture = AssetLoader.assetManager.get("house.png", Texture.class);
					break;
				case FARM:
					texture = AssetLoader.assetManager.get("farm.png", Texture.class);
					break;
				case MINE:
					texture = AssetLoader.assetManager.get("mine.png", Texture.class);
					break;
				case WIND:
					texture = AssetLoader.assetManager.get("wind.png", Texture.class);
					break;
				case FACTORY:
					texture = AssetLoader.assetManager.get("factory.png", Texture.class);
					break;
			}

			Optional<Hexagon<TileData>> dataOpt = grid.getByPixelCoordinate(Gdx.input.getX(), Gdx.graphics.getHeight() - Gdx.input.getY());

			if(dataOpt.isPresent())
			{
				Hexagon<TileData> data = dataOpt.get();
				batch.begin();
				if(data.getSatelliteData().get().getTileType() == TileType.GRASS)
					batch.setShader(isLegal(data.getSatelliteData().get()) ? okPlacement : unlawfulPlacement);
				else
					batch.setShader(invalidPlacement);

				batch.draw(texture, (float) (data.getCenterX() - texture.getWidth()/2), (float) (data.getCenterY() - texture.getHeight()/2));
				batch.setShader(null);
				batch.end();
			}
		}
	}

	private TileType getMenuItem(int mouseX, int mouseY)
	{
		//The entire code of this game is shit but this is probably the worst method
		for(int i = 0; i < menuTextures.size(); i++)
		{
			if(Utils.isInside(mouseX, mouseY, Gdx.graphics.getWidth() - 40 - menuTextures.get(i).getWidth()/2, Gdx.graphics.getHeight() - (menuTextures.get(i).getHeight() + MENU_PADDING_Y + i * 75),
					Gdx.graphics.getWidth() - 40 + menuTextures.get(i).getWidth()/2, Gdx.graphics.getHeight() - (MENU_PADDING_Y + i * 75)))
			{
				switch (i)
				{
					case 0:
						return TileType.HOUSE;
					case 1:
						return TileType.FARM;
					case 2:
						return TileType.MINE;
					case 3:
						return TileType.WIND;
					case 4:
						return TileType.FACTORY;
				}
			}

		}
		return null;
	}

	private boolean isLegal(TileData data)
	{
		TileType type = currentCursorSelect;
		Collection<Hexagon<TileData>> neighbors = grid.getNeighborsOf(data.getParent());
		boolean farm = false;
		boolean worker = false;
		boolean energy = false;
		boolean mineral = false;
		switch (type)
		{
			case FARM:
				for(Hexagon<TileData> tile : neighbors)
				{
					if(tile.getSatelliteData().get().getTileType() == TileType.FARM)
						return false;
				}
				return true;
			case HOUSE:

				for(Hexagon<TileData> tile : neighbors)
				{
					if(tile.getSatelliteData().get().getTileType() == TileType.FARM)
						return true;
				}
				return false;
			case WIND:
				for(Hexagon<TileData> tile : neighbors)
				{
					if(tile.getSatelliteData().get().getTileType() == TileType.HOUSE)
						return true;
				}
				return false;

			case MINE:
				for(Hexagon<TileData> tile : neighbors)
				{
					if(tile.getSatelliteData().get().getTileType() == TileType.HOUSE)
						worker = true;
				}
				return worker;
			case FACTORY:
				for(Hexagon<TileData> tile : neighbors)
				{
					if(tile.getSatelliteData().get().getTileType() == TileType.HOUSE)
						worker = true;

					if(tile.getSatelliteData().get().getTileType() == TileType.WIND)
						energy = true;

					if(tile.getSatelliteData().get().getTileType() == TileType.MINE)
						mineral = true;
				}
				return worker && energy && mineral;
		}


		return true;
	}

	private void calculateScore()
	{
		final int[] tempScore = {0};
		Observable<Hexagon<TileData>> hexagons = grid.getHexagons();
		hexagons.forEach(hex -> {
			TileType type = hex.getSatelliteData().get().getTileType();
			switch (type)
			{
				case FARM:
					tempScore[0]--;
				break;
				case MINE:
					tempScore[0]++;
					break;
				case FACTORY:
					tempScore[0]+=2;
				break;
			}
		});
		score = tempScore[0];
	}

	@Override
	public void dispose () {
		batch.dispose();
	}
}
