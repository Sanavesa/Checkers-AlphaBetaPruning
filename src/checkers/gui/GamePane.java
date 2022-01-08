package checkers.gui;

import checkers.GameState;
import checkers.Minimax;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.layout.GridPane;
import javafx.util.Duration;

public class GamePane extends GridPane
{
	private final RootPane rootPane;
	private final GameCell[][] cells;
	private final Timeline agentTimeline;
	private final Timeline playerTimeline;

	public GamePane(RootPane rootPane)
	{
		this.rootPane = rootPane;
		cells = new GameCell[8][8];
		setupBoard();
		update();

		agentTimeline = new Timeline();
		agentTimeline.setCycleCount(Timeline.INDEFINITE);
		agentTimeline.getKeyFrames().add(new KeyFrame(Duration.millis(1000), e -> agentPlay()));
		agentTimeline.play();

		playerTimeline = new Timeline();
		playerTimeline.setCycleCount(Timeline.INDEFINITE);
		playerTimeline.getKeyFrames().add(new KeyFrame(Duration.millis(200), e -> playerTick()));
		playerTimeline.play();
	}

	public void update()
	{
		rootPane.turnLabel.setText(Checker.state.currentTurn + "'s turn.");
		rootPane.stateValueLabel.setText("State Value: " + Minimax.evaluateState(Checker.state, Checker.AGENT_TEAM, 0));
		rootPane.agentTimeLabel.setText("Agent Time ( " + Checker.AGENT_TEAM + "): " + Checker.AGENT_TIME);
		rootPane.playerTimeLabel.setText("Player Time ( " + Checker.PLAYER_TEAM + "): " + Checker.PLAYER_TIME);

		for (int r = 0; r < 8; r++)
		{
			for (int c = 0; c < 8; c++)
			{
				cells[r][c].update();
			}
		}
	}

	private void setupBoard()
	{
		setVgap(0);
		setHgap(0);

		int i = 0;
		for (int row = 0; row < 8; row++)
		{
			for (int column = 0; column < 8; column++)
			{
				boolean isDarkCell = (i % 2 == 1);
				GameCell cell = new GameCell(this, row, column, isDarkCell);
				cells[row][column] = cell;
				add(cell, column, row);
				i++;
			}
			i++;
		}
	}

	private void playerTick()
	{
		if (Checker.state.currentTurn == Checker.PLAYER_TEAM)
		{
			Checker.PLAYER_TIME -= 0.2;
			update();
		}

		GameState gameState = Checker.state.getGameState();
		if (gameState.isOver())
		{
			playerTimeline.stop();
			rootPane.turnLabel.setText(gameState.toString());
			return;
		}

		if (Checker.AGENT_TIME <= 0)
		{
			playerTimeline.stop();
			rootPane.turnLabel.setText("Agent ran out of time!");
			return;
		}

		if (Checker.PLAYER_TIME <= 0)
		{
			playerTimeline.stop();
			rootPane.turnLabel.setText("Player ran out of time!");
			return;
		}
	}

	private void agentPlay()
	{
		GameState gameState = Checker.state.getGameState();
		if (gameState.isOver())
		{
			agentTimeline.stop();
			rootPane.turnLabel.setText(gameState.toString());
			return;
		}

		if (Checker.AGENT_TIME <= 0)
		{
			agentTimeline.stop();
			rootPane.turnLabel.setText("Agent ran out of time!");
			return;
		}

		if (Checker.PLAYER_TIME <= 0)
		{
			agentTimeline.stop();
			rootPane.turnLabel.setText("Player ran out of time!");
			return;
		}

		if (Checker.state.currentTurn == Checker.AGENT_TEAM)
		{
			agentTimeline.pause();
			Checker.agentPlay();
			update();
			agentTimeline.play();
		}
	}
}