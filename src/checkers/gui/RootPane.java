package checkers.gui;

import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

public class RootPane extends BorderPane
{
	public final Label turnLabel;
	public final Label stateValueLabel;
	public final Label agentTimeLabel;
	public final Label playerTimeLabel;
	public final GamePane gamePane;

	public RootPane()
	{
		super();

		getStylesheets().add("/checkers/gui/style.css");

		turnLabel = new Label("Turn: ??");
		stateValueLabel = new Label("State Value: ??");
		agentTimeLabel = new Label("Agent Time: ??");
		playerTimeLabel = new Label("Player Time: ??");
		gamePane = new GamePane(this);

		//VBox vbox = new VBox(turnLabel, stateValueLabel, agentTimeLabel, playerTimeLabel);
		VBox vbox = new VBox(turnLabel);
		setTop(vbox);
		setCenter(gamePane);
	}
}