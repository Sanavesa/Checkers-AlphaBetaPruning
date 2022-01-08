package checkers.gui;

import java.io.FileNotFoundException;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class VisualMain extends Application
{
	public static void main(String[] args) throws FileNotFoundException
	{
		launch(args);
		Checker.service.shutdownNow();
	}

	@Override
	public void start(Stage stage) throws Exception
	{
		RootPane root = new RootPane();
		Scene scene = new Scene(root);
		stage.setScene(scene);
		stage.show();
	}
}