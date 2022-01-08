package checkers;

public class MoveAction extends BaseAction
{
	public final Pawn pawn;
	public final int rowFrom;
	public final int columnFrom;
	public final int rowTo;
	public final int columnTo;

	private boolean actionExecuted;
	private boolean snapshotPawnWasKing;
	private int snapshotStatePlys;

	public MoveAction(Pawn pawn, int rowFrom, int columnFrom, int rowTo, int columnTo)
	{
		super();
		this.pawn = pawn;
		this.rowFrom = rowFrom;
		this.columnFrom = columnFrom;
		this.rowTo = rowTo;
		this.columnTo = columnTo;

		actionExecuted = false;
		snapshotPawnWasKing = false;
		snapshotStatePlys = 0;
	}

	@Override
	public String getText()
	{
		return Converter.moveOutput(rowFrom, columnFrom, rowTo, columnTo);
	}

	@Override
	public String getDebugText()
	{
		// Move Black King Pawn from [rowFrom, columnFrom] (POS) to [rowTo, columnTo] (POS)

		String pos1 = "";
		try
		{
			pos1 = Converter.positionOutput(rowFrom, columnFrom);
		}
		catch (Exception e)
		{
		}

		String pos2 = "";
		try
		{
			pos2 = Converter.positionOutput(rowTo, columnTo);
		}
		catch (Exception e)
		{
		}

		StringBuilder builder = new StringBuilder();
		builder.append("Move ").append(pawn.team);
		if (pawn.isKing)
			builder.append(" King");
		builder.append(" Pawn from ");

		builder.append("[").append(rowFrom).append(", ").append(columnFrom).append("]");
		if (!pos1.isEmpty())
			builder.append(" (").append(pos1).append(")");

		builder.append(" to ");
		builder.append("[").append(rowTo).append(", ").append(columnTo).append("]");
		if (!pos2.isEmpty())
			builder.append(" (").append(pos2).append(")");

		return builder.toString();
	}

	@Override
	public void execute(State state)
	{
		if (!isValidAction(state))
			return;

		if (actionExecuted)
		{
			error(state, "Cannot execute action as it has already been executed previously.");
			return;
		}

		// Capture snapshot
		snapshotPawnWasKing = pawn.isKing;
		snapshotStatePlys = state.plysSinceLastCrownOrCapture;

		// Update board
		state.board[rowFrom][columnFrom] = null;
		state.board[rowTo][columnTo] = pawn;

		// Crown pawn if needed
		if (!pawn.isKing && rowTo == State.getKingsRow(pawn.team.opponent()))
		{
			pawn.isKing = true;
			state.plysSinceLastCrownOrCapture = 0;
		}

		actionExecuted = true;
	}

	@Override
	public void undo(State state)
	{
		if (!actionExecuted)
		{
			error(state, "Cannot undo as the action was never executed.");
			return;
		}

		// Update board
		state.board[rowFrom][columnFrom] = pawn;
		state.board[rowTo][columnTo] = null;

		// Retrieve snapshot
		pawn.isKing = snapshotPawnWasKing;
		state.plysSinceLastCrownOrCapture = snapshotStatePlys;

		actionExecuted = false;
	}

	@Override
	public boolean isValidAction(State state)
	{
		if (pawn == null)
		{
			error(state, "Pawn is null.");
			return false;
		}

		if (State.isOutOfBounds(rowFrom, columnFrom))
		{
			error(state, "Source position was out of bounds");
			return false;
		}

		if (State.isOutOfBounds(rowTo, columnTo))
		{
			error(state, "Destination position was out of bounds.");
			return false;
		}

		if (state.board[rowFrom][columnFrom] == null)
		{
			error(state, "Source position was empty.");
			return false;
		}

		if (!state.board[rowFrom][columnFrom].equals(pawn))
		{
			error(state, "Pawn is not located at source position.");
			return false;
		}

		if (state.board[rowTo][columnTo] != null)
		{
			error(state, "Destination position was not empty.");
			return false;
		}

		if (rowFrom == rowTo || columnFrom == columnTo)
		{
			error(state, "Invalid from-to positions.");
			return false;
		}

		int forward = State.getForward(pawn.team);
		int moveForward = (int) Math.signum(rowTo - rowFrom);
		if (!pawn.isKing && forward != moveForward)
		{
			error(state, "Forward of move does not match expected forward.");
			return false;
		}

		int distance = manhattan(rowFrom, columnFrom, rowTo, columnTo);
		if (distance > 2)
		{
			error(state, "Distance of move is greater than 2.");
			return false;
		}

		return true;
	}
}