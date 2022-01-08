package checkers.gui;

import checkers.BaseAction;
import checkers.Pawn;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;

public class GameCell extends StackPane
{
	public static final int CELL_SIZE = 48;
	public static final int CIRCLE_SIZE = 32;
	public static final Color LIGHT_CELL_COLOR = Color.rgb(232, 217, 188);
	public static final Color DARK_CELL_COLOR = Color.rgb(168, 118, 87);
	public static final Color SELECTION_COLOR = Color.rgb(214, 212, 66);

	private static GameCell selectedCell = null;

	public final GamePane gamePane;
	public final Rectangle rect;
	public final Rectangle selectionRect;
	public final Circle circle;
	public final Text text;
	public final int row;
	public final int column;

	public GameCell(GamePane gamePane, int row, int column, boolean isDarkCell)
	{
		this.gamePane = gamePane;

		rect = new Rectangle(CELL_SIZE, CELL_SIZE, (isDarkCell) ? DARK_CELL_COLOR : LIGHT_CELL_COLOR);
		selectionRect = new Rectangle(CELL_SIZE, CELL_SIZE, SELECTION_COLOR);
		circle = new Circle(CIRCLE_SIZE / 2, CIRCLE_SIZE / 2, CIRCLE_SIZE / 2, Color.TRANSPARENT);
		text = new Text("");

		this.row = row;
		this.column = column;

		getChildren().addAll(rect, selectionRect, circle, text);

		setup();
	}

	public void update()
	{
		selectionRect.setOpacity(0);
		if (selectedCell == this)
			selectionRect.setOpacity(1);

		Pawn pawn = Checker.state.board[row][column];
		char c = (pawn == null) ? '.' : pawn.getBoardChar();
		if (c == '.')
		{
			circle.setFill(Color.TRANSPARENT);
			text.setFill(Color.TRANSPARENT);
		}
		else if (c == 'w')
		{
			circle.setFill(Color.WHITE);
			text.setFill(Color.TRANSPARENT);
		}
		else if (c == 'W')
		{
			circle.setFill(Color.WHITE);
			text.setFill(Color.BLACK);
			text.setText("K");
		}
		else if (c == 'b')
		{
			circle.setFill(Color.BLACK);
			text.setFill(Color.TRANSPARENT);
		}
		else if (c == 'B')
		{
			circle.setFill(Color.BLACK);
			text.setFill(Color.WHITE);
			text.setText("K");
		}
	}

	private boolean isCellSelectable()
	{
		Pawn pawn = Checker.state.board[row][column];
		if (pawn == null)
			return false;
		return pawn.team == Checker.state.currentTurn && pawn.team == Checker.PLAYER_TEAM;
	}

	private void setup()
	{
		rect.setPickOnBounds(true);
		rect.setId("board-cell");

		selectionRect.setMouseTransparent(true);
		selectionRect.setOpacity(0);
		selectionRect.setId("board-cell");

		circle.setMouseTransparent(true);

		text.setFill(Color.TRANSPARENT);
		text.setStyle("-fx-font-size: 32px;");
		text.setMouseTransparent(true);

		rect.setOnMouseClicked(e ->
		{
			Pawn pawn = Checker.state.board[row][column];
			char c = (pawn == null) ? '.' : pawn.getBoardChar();
			boolean isEmptyCell = (c == '.');

			if (selectedCell == null)
			{
				if (isCellSelectable())
					selectedCell = this;
			}
			else
			{
				if (isEmptyCell)
				{
					BaseAction action = Checker.createAction(selectedCell.row, selectedCell.column, row, column);
					if (action != null && action.isValidAction(Checker.state) && Checker.PLAYER_TIME > 0)
					{
						System.out.println("[Player - " + Checker.PLAYER_TEAM + "] played:\n\t" + action.getText().replaceAll("\n", "\n\t"));
						Checker.state.executeAction(action);
						selectedCell = null;
						gamePane.update();
					}
					else
					{
						if (action == null)
							System.out.println("[Player - " + Checker.PLAYER_TEAM + "] null action.");
						else
							System.out.println("[Player - " + Checker.PLAYER_TEAM + "] invalid action:\n\t" + action.getText().replaceAll("\n", "\n\t"));
					}
				}
				else
				{
					if (selectedCell == this)
						selectedCell = null;
					else if (isCellSelectable())
						selectedCell = this;
					else
						selectedCell = null;
				}
			}

			gamePane.update();
		});
	}
}