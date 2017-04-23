package com.martenscedric.desktop;

import com.badlogic.gdx.Files;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.martenscedric.LudumDare38;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.width = 800;
		config.height = 800;
		config.resizable = false;
		config.title = "HexCity";
		config.addIcon("icons/icon-128.png", Files.FileType.Internal);
		config.addIcon("icons/icon-32.png", Files.FileType.Internal);
		config.addIcon("icons/icon-16.png", Files.FileType.Internal);
		new LwjglApplication(new LudumDare38(), config);
	}
}
