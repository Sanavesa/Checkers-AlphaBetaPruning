package checkers;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class StateActions
{
	public final State state;
	public final List<MoveAction> moveActions;
	public final List<ChainJumpAction> jumpActions;

	public StateActions(State state)
	{
		this.state = state;
		moveActions = new ArrayList<>();
		jumpActions = new ArrayList<>();
		populateAllActions();
		jumpActions.sort((a, b) -> -Integer.compare(a.chain.size(), b.chain.size())); // Sort by descending chain length
	}

	private void populateAllActions()
	{
		int forward = State.getForward(state.currentTurn);
		for (int row = 0; row < 8; row++)
		{
			for (int column = 0; column < 8; column++)
			{
				Pawn pawn = state.board[row][column];
				if (pawn != null && pawn.team == state.currentTurn)
				{
					checkActionsAt(pawn, row, column, forward, -1);
					checkActionsAt(pawn, row, column, forward, 1);
					if (pawn.isKing)
					{
						checkActionsAt(pawn, row, column, -forward, -1);
						checkActionsAt(pawn, row, column, -forward, 1);
					}
				}
			}
		}
	}

	private void checkActionsAt(Pawn pawn, int row, int column, int deltaRow, int deltaColumn)
	{
		int diagonalRow = row + deltaRow;
		int diagonalColumn = column + deltaColumn;
		if (State.isOutOfBounds(diagonalRow, diagonalColumn))
			return;

		Pawn diagonalPawn = state.board[diagonalRow][diagonalColumn];

		// Diagonal cell is empty
		if (diagonalPawn == null)
		{
			// Pawn can move there
			moveActions.add(new MoveAction(pawn, row, column, diagonalRow, diagonalColumn));
		}
		// Diagonal cell is occupied by hostile
		else if (pawn.team.opponent() == diagonalPawn.team)
		{
			int behindDiagonalRow = diagonalRow + deltaRow;
			int behindDiagonalColumn = diagonalColumn + deltaColumn;
			if (State.isOutOfBounds(behindDiagonalRow, behindDiagonalColumn))
				return;

			Pawn behindDiagonalPawn = state.board[behindDiagonalRow][behindDiagonalColumn];

			// Cell behind diagonal cell is empty
			if (behindDiagonalPawn == null)
			{
				// Pawn can jump there
				ChainJumpAction chainJumpAction = new ChainJumpAction();
				JumpAction jumpAction = new JumpAction(pawn, diagonalPawn, row, column, behindDiagonalRow, behindDiagonalColumn);
				chainJumpAction.chain.add(jumpAction);

				List<ChainJumpAction> allChainJumpActions = new ArrayList<>();
				allChainJumpActions.add(chainJumpAction);

				// Remove pawn temporarily so it can detect all cycle chain jumps
				state.board[row][column] = null;

				findAllChains(allChainJumpActions, jumpAction);

				// Re-add pawn
				state.board[row][column] = pawn;

				jumpActions.addAll(allChainJumpActions);
			}
		}
	}

	private void findAllChains(List<ChainJumpAction> list, JumpAction jump)
	{
		List<ChainJumpAction> chainJumpsEndingWithGivenJump = list.stream().filter(p -> p.endsWith(jump)).collect(Collectors.toList());
		if (chainJumpsEndingWithGivenJump.isEmpty())
			return;

		List<JumpAction> successiveJumps = findSuccessiveJumps(jump);
		if (successiveJumps.isEmpty())
			return;

		list.removeAll(chainJumpsEndingWithGivenJump);

		for (ChainJumpAction chainJumpAction : chainJumpsEndingWithGivenJump)
		{
			boolean anyValid = false;
			for (JumpAction successiveJump : successiveJumps)
			{
				if (chainJumpAction.isValidAddition(successiveJump))
				{
					anyValid = true;
					ChainJumpAction extendedChainJump = chainJumpAction.copy();
					extendedChainJump.chain.add(successiveJump);
					list.add(extendedChainJump);
				}
			}

			if (!anyValid)
				list.add(chainJumpAction);
		}

		for (JumpAction successiveJump : successiveJumps)
			findAllChains(list, successiveJump);
	}

	private List<JumpAction> findSuccessiveJumps(JumpAction jump)
	{
		// Don't include jumps that return back to where we came from
		List<JumpAction> jumps = new ArrayList<>();

		int forward = State.getForward(jump.pawn.team);
		int row = jump.rowTo;
		int column = jump.columnTo;

		JumpAction j1 = getJumpAt(jump.pawn, row, column, forward, -1);
		if (j1 != null && !(j1.rowTo == jump.rowFrom && j1.columnTo == jump.columnFrom))
			jumps.add(j1);

		JumpAction j2 = getJumpAt(jump.pawn, row, column, forward, 1);
		if (j2 != null && !(j2.rowTo == jump.rowFrom && j2.columnTo == jump.columnFrom))
			jumps.add(j2);

		if (jump.pawn.isKing)
		{
			JumpAction j3 = getJumpAt(jump.pawn, row, column, -forward, -1);
			if (j3 != null && !(j3.rowTo == jump.rowFrom && j3.columnTo == jump.columnFrom))
				jumps.add(j3);

			JumpAction j4 = getJumpAt(jump.pawn, row, column, -forward, 1);
			if (j4 != null && !(j4.rowTo == jump.rowFrom && j4.columnTo == jump.columnFrom))
				jumps.add(j4);
		}

		return jumps;
	}

	private JumpAction getJumpAt(Pawn pawn, int row, int column, int deltaRow, int deltaColumn)
	{
		int diagonalRow = row + deltaRow;
		int diagonalColumn = column + deltaColumn;
		if (State.isOutOfBounds(diagonalRow, diagonalColumn))
			return null;

		Pawn diagonalPawn = state.board[diagonalRow][diagonalColumn];

		// Diagonal cell is occupied by hostile
		if (diagonalPawn != null && pawn.team.opponent() == diagonalPawn.team)
		{
			int behindDiagonalRow = diagonalRow + deltaRow;
			int behindDiagonalColumn = diagonalColumn + deltaColumn;
			if (State.isOutOfBounds(behindDiagonalRow, behindDiagonalColumn))
				return null;

			Pawn behindDiagonalPawn = state.board[behindDiagonalRow][behindDiagonalColumn];

			// Cell behind diagonal cell is empty
			if (behindDiagonalPawn == null)
			{
				// Pawn can jump there
				return new JumpAction(pawn, diagonalPawn, row, column, behindDiagonalRow, behindDiagonalColumn);
			}
		}

		return null;
	}
}