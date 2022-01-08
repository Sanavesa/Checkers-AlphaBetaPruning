package checkers;

public class State
{
	public Pawn[][] board;
	public Team currentTurn;
	public int plysSinceLastCrownOrCapture;

	public static State createEmpty()
	{
		return new State();
	}

	public static State createInitial()
	{
		State state = new State();
		int i = 0;
		for (int r = 0; r < 8; r++)
		{
			for (int c = 0; c < 8; c++)
			{
				Pawn pawn = null;
				if (i % 2 == 1 && (r <= 2 || r >= 5))
				{
					Team team = (r <= 4) ? Team.Black : Team.White;
					pawn = new Pawn(false, team);
				}

				state.board[r][c] = pawn;
				i++;
			}
			i++;
		}

		return state;
	}

	public static boolean isOutOfBounds(int row, int column)
	{
		return (row < 0 || column < 0 || row >= 8 || column >= 8);
	}

	public static int getKingsRow(Team team)
	{
		return (team == Team.Black) ? 0 : 7;
	}

	public static int getForward(Team team)
	{
		return (team == Team.Black) ? 1 : -1;
	}

	private State()
	{
		board = new Pawn[8][8];
		currentTurn = Team.Black;
		plysSinceLastCrownOrCapture = 0;
	}

	// TODO: Remove later
	private void scream()
	{
		int black = 0;
		int white = 0;
		for (int r = 0; r < 8; r++)
		{
			for (int c = 0; c < 8; c++)
			{
				Pawn pawn = board[r][c];
				if (pawn != null)
				{
					if (pawn.team == Team.Black)
						black++;
					else
						white++;
				}
			}
		}

		if (black > 12 || white > 12)
		{
			System.err.println("STOP RIGHT THERE. " + black + " blacks and " + white + " whites");
			System.exit(-1);
		}
	}

	public void executeAction(BaseAction action)
	{
		scream();

		// TODO: Remove later
//		System.out.println("Executing " + action.getDebugText());
//		System.out.println("Execute was: " + this);

		plysSinceLastCrownOrCapture++;
		action.execute(this);
		switchTurn();

//		System.out.println("Execute became: " + this);
	}

	public void undoAction(BaseAction action)
	{
		scream();

		// TODO: Remove later
//		System.out.println("Undoing " + action.getDebugText());
//		System.out.println("Undo was: " + this);

		action.undo(this);
		switchTurn();

//		System.out.println("Undo became: " + this);
	}

	// TODO: Cache this and only recalculate when dirtied by execute/undo
	public StateActions getActions()
	{
		return new StateActions(this);
	}

	public GameState getGameState()
	{
		if (plysSinceLastCrownOrCapture >= 100)
			return GameState.Draw;

		int blackCount = 0;
		int whiteCount = 0;
		for (int r = 0; r < 8; r++)
		{
			for (int c = 0; c < 8; c++)
			{
				Pawn pawn = board[r][c];
				if (pawn != null)
				{
					if (pawn.team == Team.Black)
						blackCount++;
					else
						whiteCount++;
				}
			}
		}

		if (blackCount == 0)
			return GameState.WhiteWin;

		if (whiteCount == 0)
			return GameState.BlackWin;

		StateActions actions = new StateActions(this);
		long currentTeamMoves = actions.jumpActions.size() + actions.moveActions.size();
		if (currentTeamMoves == 0)
		{
			if (currentTurn == Team.Black)
				return GameState.WhiteWin;

			if (currentTurn == Team.White)
				return GameState.BlackWin;
		}

		return GameState.Ongoing;
	}

	@Override
	public String toString()
	{
		StringBuilder builder = new StringBuilder();
		builder.append("State:");
		builder.append("\tCurrent Turn: ").append(currentTurn).append("\n");
		builder.append("\tBoard:\n");
		for (int r = 0; r < 8; r++)
		{
			builder.append("\t\t");
			for (int c = 0; c < 8; c++)
			{
				Pawn pawn = board[r][c];
				if (pawn == null)
					builder.append(".");
				else
					builder.append(pawn.getBoardChar());
			}
			builder.append("\n");
		}

		return builder.toString();
	}

	private void switchTurn()
	{
		currentTurn = (currentTurn == Team.Black) ? Team.White : Team.Black;
	}
}