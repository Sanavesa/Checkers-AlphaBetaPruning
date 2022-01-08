package checkers;

import java.util.Objects;

public class JumpAction extends BaseAction
{
	public final Pawn pawn;
	public final Pawn victim;
	public final int rowFrom;
	public final int columnFrom;
	public final int rowTo;
	public final int columnTo;
	public final int rowVictim;
	public final int columnVictim;

	private boolean actionExecuted;
	private boolean snapshotPawnWasKing;
	private int snapshotStatePlys;

	public JumpAction(Pawn pawn, Pawn victim, int rowFrom, int columnFrom, int rowTo, int columnTo)
	{
		super();
		this.pawn = pawn;
		this.victim = victim;
		this.rowFrom = rowFrom;
		this.columnFrom = columnFrom;
		this.rowTo = rowTo;
		this.columnTo = columnTo;
		rowVictim = (rowFrom + rowTo) / 2;
		columnVictim = (columnFrom + columnTo) / 2;

		actionExecuted = false;
		snapshotPawnWasKing = false;
		snapshotStatePlys = 0;
	}

	@Override
	public String getText()
	{
		return Converter.jumpOutput(rowFrom, columnFrom, rowTo, columnTo);
	}

	@Override
	public String getDebugText()
	{
		// Jump Black King Pawn from [rowFrom, columnFrom] (POS) to [rowTo, columnTo] (POS), capturing White King Pawn at [rowVictim, columnVictim] (POS)

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

		String pos3 = "";
		try
		{
			pos3 = Converter.positionOutput(rowVictim, columnVictim);
		}
		catch (Exception e)
		{
		}

		StringBuilder builder = new StringBuilder();
		builder.append("Jump ").append(pawn.team);
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

		builder.append(", capturing ").append(victim.team);
		if (victim.isKing)
			builder.append(" King");
		builder.append(" Pawn at ");
		builder.append("[").append(rowVictim).append(", ").append(columnVictim).append("]");
		if (!pos3.isEmpty())
			builder.append(" (").append(pos3).append(")");

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
		state.board[rowVictim][columnVictim] = null;
		state.board[rowTo][columnTo] = pawn;

		// Crown pawn if needed
		if (!pawn.isKing && rowTo == State.getKingsRow(pawn.team.opponent()))
			pawn.isKing = true;

		state.plysSinceLastCrownOrCapture = 0;
		
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
		state.board[rowVictim][columnVictim] = victim;
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

		if (victim == null)
		{
			error(state, "Victim is null.");
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

		if (State.isOutOfBounds(rowVictim, columnVictim))
		{
			error(state, "Victim position was out of bounds.");
			return false;
		}

		// TODO: Does not work if part of chained jump
//		if (state.board[rowFrom][columnFrom] == null)
//		{
//			error(state, "Source position was empty.");
//			return false;
//		}

		// TODO: Does not work if part of chained jump
//		if (!state.board[rowFrom][columnFrom].equals(pawn))
//		{
//			error(state, "Pawn is not located at source position.");
//			return false;
//		}

		if (state.board[rowVictim][columnVictim] == null)
		{
			error(state, "Victim position was empty.");
			return false;
		}

		if (!state.board[rowVictim][columnVictim].equals(victim))
		{
			error(state, "Victim is not located at victim position.");
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
			error(state, "Forward of jump does not match expected forward.");
			return false;
		}

		int distance = manhattan(rowFrom, columnFrom, rowTo, columnTo);
		if (distance > 4)
		{
			error(state, "Distance of jump is greater than 4.");
			return false;
		}

		return true;
	}

	@Override
	public int hashCode()
	{
		return Objects.hash(columnFrom, columnTo, columnVictim, pawn, rowFrom, rowTo, rowVictim, victim);
	}

	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		JumpAction other = (JumpAction) obj;
		return columnFrom == other.columnFrom && columnTo == other.columnTo && columnVictim == other.columnVictim && Objects.equals(pawn, other.pawn) && rowFrom == other.rowFrom && rowTo == other.rowTo && rowVictim == other.rowVictim
				&& Objects.equals(victim, other.victim);
	}
}