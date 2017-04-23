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
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.GdxRuntimeException;
import org.codetome.hexameter.core.api.*;
import org.codetome.hexameter.core.api.Point;
import org.codetome.hexameter.core.backport.Optional;
import rx.Observable;
import rx.functions.Action1;

import javax.xml.soap.Text;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


public class LudumDare38 extends ApplicationAdapter {

	private final int GRID_WIDTH = 9;
	private final int GRID_HEIGHT = 9;
	private final int MENU_PADDING_Y = 10;

	private SpriteBatch batch;
	private PolygonSpriteBatch polyBatch;
	private HexagonalGrid<TileData> grid;
	private ShapeRenderer shapeRenderer;
	private BuildingType currentCursorSelect = null;
	private ShaderProgram invalidPlacement;
	private ShaderProgram unlawfulPlacement;
	private ShaderProgram okPlacement;
	private List<Texture> menuTextures;
	private String scoreText = "SCORE : %d";
	private int score = 0;
	private Stage stage;
	private WidgetGroup group;
	private TextButton btnReset;
	private TextButton labelToolTip;
	
	@Override
	public void create () {

		stage = new Stage();
		group = new WidgetGroup();
		stage.addActor(group);
		createToolTip();
		createResetButton();
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

		initGrid();

		menuTextures = new ArrayList<>();
		menuTextures.add(AssetLoader.assetManager.get("house.png", Texture.class));
		menuTextures.add(AssetLoader.assetManager.get("farm.png", Texture.class));
		menuTextures.add(AssetLoader.assetManager.get("mine.png", Texture.class));
		menuTextures.add(AssetLoader.assetManager.get("wind.png", Texture.class));
		menuTextures.add(AssetLoader.assetManager.get("factory.png", Texture.class));
		menuTextures.add(AssetLoader.assetManager.get("market.png", Texture.class));
		menuTextures.add(AssetLoader.assetManager.get("bank.png", Texture.class));
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
		BuildingType hoverItem = getMenuItem(Gdx.input.getX(), Gdx.graphics.getHeight() - Gdx.input.getY());
		if(hoverItem != null)
			displayToolTip(hoverItem);
		else
		{
			labelToolTip.setVisible(false);
		}
		stage.act();
		stage.draw();
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
					if(currentCursorSelect != null && data.getSatelliteData().get().getTileType() != TileType.WATER
							&& isLegal(data.getSatelliteData().get()))
					{
						data.getSatelliteData().get().setBuilding(currentCursorSelect);
						currentCursorSelect = null;
					}
				}else{
					if(Utils.isInside(screenX, screenY,
							btnReset.getX(), btnReset.getY(),
							btnReset.getX() + btnReset.getWidth(), btnReset.getY() + btnReset.getHeight()))
					{
						currentCursorSelect = null;
						clearGrid();
						initGrid();
					}else{
						currentCursorSelect = getMenuItem(screenX, screenY);
					}
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
				case MARKET:
					texture = AssetLoader.assetManager.get("market.png", Texture.class);
					break;
				case BANK:
					texture = AssetLoader.assetManager.get("bank.png", Texture.class);
					break;
			}

			Optional<Hexagon<TileData>> dataOpt = grid.getByPixelCoordinate(Gdx.input.getX(), Gdx.graphics.getHeight() - Gdx.input.getY());

			if(dataOpt.isPresent())
			{
				Hexagon<TileData> data = dataOpt.get();
				batch.begin();
				if(data.getSatelliteData().get().getTileType() != TileType.WATER)
					batch.setShader(isLegal(data.getSatelliteData().get()) ? okPlacement : unlawfulPlacement);
				else
					batch.setShader(invalidPlacement);

				batch.draw(texture, (float) (data.getCenterX() - texture.getWidth()/2), (float) (data.getCenterY() - texture.getHeight()/2));
				batch.setShader(null);
				batch.end();
			}
		}
	}

	private BuildingType getMenuItem(int mouseX, int mouseY)
	{
		//The entire code of this game is shit but this is probably the worst method
		for(int i = 0; i < menuTextures.size(); i++)
		{
			if(Utils.isInside(mouseX, mouseY, Gdx.graphics.getWidth() - 40 - menuTextures.get(i).getWidth()/2, Gdx.graphics.getHeight() - (menuTextures.get(i).getHeight() + MENU_PADDING_Y + i * 75),
					Gdx.graphics.getWidth() - 40 + menuTextures.get(i).getWidth()/2, Gdx.graphics.getHeight() - (MENU_PADDING_Y + i * 75)))
			{
				return BuildingType.values()[i + 1];
			}

		}
		return null;
	}

	private boolean isLegal(TileData data)
	{
		BuildingType type = currentCursorSelect;
		Collection<Hexagon<TileData>> neighbors = grid.getNeighborsOf(data.getParent());
		boolean farm = false;
		boolean worker = false;
		boolean energy = false;
		boolean mineral = false;
		boolean consumerGoods = false;
		boolean trade = false;
		boolean wealth = false;
		switch (type)
		{
			case FARM:
				for(Hexagon<TileData> tile : neighbors)
				{
					if(tile.getSatelliteData().get().getBuildingType() == BuildingType.FARM)
						return false;
				}
				return true;
			case HOUSE:

				for(Hexagon<TileData> tile : neighbors)
				{
					if(tile.getSatelliteData().get().getBuildingType() == BuildingType.FARM)
						return true;
				}
				return false;
			case WIND:
				for(Hexagon<TileData> tile : neighbors)
				{
					if(tile.getSatelliteData().get().getBuildingType() == BuildingType.HOUSE)
						return true;
				}
				return false;

			case MINE:
				for(Hexagon<TileData> tile : neighbors)
				{
					if(tile.getSatelliteData().get().getBuildingType() == BuildingType.HOUSE)
						worker = true;
				}
				return worker;
			case FACTORY:
				for(Hexagon<TileData> tile : neighbors)
				{
					if(tile.getSatelliteData().get().getBuildingType() == BuildingType.HOUSE)
						worker = true;

					if(tile.getSatelliteData().get().getBuildingType() == BuildingType.WIND)
						energy = true;

					if(tile.getSatelliteData().get().getBuildingType() == BuildingType.MINE)
						mineral = true;
				}
				return worker && energy && mineral;
			case MARKET:
				for(Hexagon<TileData> tile : neighbors)
				{
					if(tile.getSatelliteData().get().getBuildingType() == BuildingType.HOUSE)
						worker = true;

					if(tile.getSatelliteData().get().getBuildingType() == BuildingType.WIND)
						energy = true;

					if(tile.getSatelliteData().get().getBuildingType() == BuildingType.FACTORY)
						consumerGoods = true;
				}
				return worker && energy && consumerGoods;
			case BANK:
				for(Hexagon<TileData> tile : neighbors)
				{
					if(tile.getSatelliteData().get().getBuildingType() == BuildingType.HOUSE)
						worker = true;

					if(tile.getSatelliteData().get().getBuildingType() == BuildingType.WIND)
						energy = true;

					if(tile.getSatelliteData().get().getBuildingType() == BuildingType.MARKET)
						trade = true;

					if(tile.getSatelliteData().get().getBuildingType() == BuildingType.MINE)
						mineral = true;
				}
				return worker && trade && energy && mineral;
		}


		return true;
	}

	private void calculateScore()
	{
		final int[] tempScore = {0};
		Observable<Hexagon<TileData>> hexagons = grid.getHexagons();
		hexagons.forEach(hex -> {
			TileType tileType = hex.getSatelliteData().get().getTileType();
			BuildingType buildingType = hex.getSatelliteData().get().getBuildingType();
			tempScore[0]+= buildingType.getScore() * tileType.getMultiplier();
		});
		score = tempScore[0];
	}

	private void displayToolTip(BuildingType type)
	{
		labelToolTip.setText(type.getName() + "\n\n" + "Modifies score by " + type.getScore() + "\n\n" + type.getDesc());
		labelToolTip.setX(Gdx.input.getX() - 125);
		labelToolTip.setY(Gdx.graphics.getHeight() - Gdx.input.getY() - labelToolTip.getLabel().getHeight());
		labelToolTip.setHeight(labelToolTip.getLabel().getHeight());
		labelToolTip.setVisible(true);
	}

	private void createToolTip()
	{
		Skin skin = new Skin();
		Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
		pixmap.setColor(Color.WHITE);
		pixmap.fill();
		skin.add("white", new Texture(pixmap));
		skin.add("default", new BitmapFont());

		TextButton.TextButtonStyle textButtonStyle = new TextButton.TextButtonStyle();
		textButtonStyle.up = skin.newDrawable("white", new Color(0, 0, 0, 1));
		textButtonStyle.font = skin.getFont("default");
		skin.add("default", textButtonStyle);

		labelToolTip = new TextButton("TEST", skin);
		labelToolTip.setX(5);
		labelToolTip.setY(5);
		labelToolTip.setWidth(125);
		labelToolTip.setVisible(false);
		labelToolTip.getLabel().setWrap(true);
		labelToolTip.setHeight(labelToolTip.getLabel().getHeight());
		group.addActor(labelToolTip);
	}

	private void createResetButton()
	{
		Skin skin = new Skin();
		Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
		pixmap.setColor(Color.WHITE);
		pixmap.fill();
		skin.add("white", new Texture(pixmap));
		skin.add("default", new BitmapFont());

		TextButton.TextButtonStyle textButtonStyle = new TextButton.TextButtonStyle();
		textButtonStyle.up = skin.newDrawable("white", new Color(0, 0, 0, 1));
		textButtonStyle.font = skin.getFont("default");
		skin.add("default", textButtonStyle);

		btnReset = new TextButton("RESET", skin);
		btnReset.setX(5);
		btnReset.setY(Gdx.graphics.getHeight() - 25);
		btnReset.setWidth(60);
		btnReset.setVisible(true);
		group.addActor(btnReset);
	}

	private void clearGrid()
	{
		Observable<Hexagon<TileData>> hexagons = grid.getHexagons();
		hexagons.forEach(hex -> {
            hex.getSatelliteData().get().setBuilding(BuildingType.NONE);
            hex.getSatelliteData().get().setTileType(TileType.GRASS);
        });
	}

	private void initGrid()
	{
		//Hardcoded cancer (Map settings)
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
		data4.setBuilding(BuildingType.HOUSE);

		Hexagon<TileData> hex5 = grid.getByCubeCoordinate(CubeCoordinate.fromCoordinates(4, 3)).get();
		TileData data5 = hex5.getSatelliteData().get();
		data5.setBuilding(BuildingType.WIND);

		Hexagon<TileData> hex6 = grid.getByCubeCoordinate(CubeCoordinate.fromCoordinates(3, 3)).get();
		TileData data6 = hex6.getSatelliteData().get();
		data6.setBuilding(BuildingType.FARM);

		Hexagon<TileData> hex7 = grid.getByCubeCoordinate(CubeCoordinate.fromCoordinates(5, 2)).get();
		TileData data7 = hex7.getSatelliteData().get();
		data7.setBuilding(BuildingType.MINE);

		Hexagon<TileData> hex8 = grid.getByCubeCoordinate(CubeCoordinate.fromCoordinates(1, 4)).get();
		TileData data8 = hex8.getSatelliteData().get();
		data8.setTileType(TileType.SAND);

		Hexagon<TileData> hex9 = grid.getByCubeCoordinate(CubeCoordinate.fromCoordinates(6, 3)).get();
		TileData data9 = hex9.getSatelliteData().get();
		data9.setTileType(TileType.SAND);

		Hexagon<TileData> hex10 = grid.getByCubeCoordinate(CubeCoordinate.fromCoordinates(5, -1)).get();
		TileData data10 = hex10.getSatelliteData().get();
		data10.setTileType(TileType.SAND);

		Hexagon<TileData> hex11 = grid.getByCubeCoordinate(CubeCoordinate.fromCoordinates(4, -2)).get();
		TileData data11 = hex11.getSatelliteData().get();
		data11.setTileType(TileType.FOREST);

		Hexagon<TileData> hex12 = grid.getByCubeCoordinate(CubeCoordinate.fromCoordinates(0, 6)).get();
		TileData data12 = hex12.getSatelliteData().get();
		data12.setTileType(TileType.FOREST);

		Hexagon<TileData> hex13 = grid.getByCubeCoordinate(CubeCoordinate.fromCoordinates(8, 2)).get();
		TileData data13 = hex13.getSatelliteData().get();
		data13.setTileType(TileType.FOREST);
	}

	@Override
	public void dispose () {
		batch.dispose();
	}
}
