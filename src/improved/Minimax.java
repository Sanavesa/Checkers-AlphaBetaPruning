package improved;

import java.util.List;

/**
 * The <code>Minimax</code> class is an implementation of the <b>Alpha-Beta Minimax</b> algorithm for the game of English Checkers.
 * 
 * @author Mohammad Alali
 * @see Action
 * @see StateBoard
 */
public final class Minimax
{
	/**
	 * Don't let anyone instantiate this class.
	 */
	private Minimax()
	{}

	/**
	 * The original team of the maximizing player.
	 */
	private static boolean isOnBlackTeam = false;

	/**
	 * Initiates a <b>Alpha-Beta Minimax</b> search for the specified team with the specified depth.
	 * 
	 * @param board       the initial board state to search from
	 * @param isBlackTurn the team of the maximizing player
	 * @param depth       the max depth of the search
	 * @return the action computed by the search
	 */
	public static Action search(StateBoard board, boolean isBlackTurn, int depth)
	{
		System.out.println("depth = " + depth);
		Minimax.isOnBlackTeam = isBlackTurn;

		double bestValue = Double.NEGATIVE_INFINITY;
		Action bestAction = null;

		// First, process jumps
		final List<ChainJumpAction> jumpActions = board.getJumpActions();
		final int jumpActionsSize = jumpActions.size();
		if (jumpActionsSize > 0)
		{
			for (int i = 0; i < jumpActionsSize; i++)
			{
				// Abort if short on time
				if (AgentTimer.getRemainingTime() <= Constants.URGENCY_TIME)
					break;

				final ChainJumpAction jumpAction = jumpActions.get(i);
				board.executeAction(jumpAction);
				final double value = recursive(board, depth - 1, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, false);
				board.undoAction(jumpAction);
				if (value >= bestValue)
				{
					bestValue = value;
					bestAction = jumpAction;
				}
			}
		}
		// Otherwise, process moves
		else
		{
			final List<MoveAction> moveActions = board.getMoveActions();
			final int moveActionsSize = moveActions.size();
			for (int i = 0; i < moveActionsSize; i++)
			{
				// Abort if short on time
				if (AgentTimer.getRemainingTime() <= Constants.URGENCY_TIME)
					break;

				final MoveAction moveAction = moveActions.get(i);
				board.executeAction(moveAction);
				final double value = recursive(board, depth - 1, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, false);
				board.undoAction(moveAction);
				System.out.println(moveAction.getText() + " = " + value);
				if (value >= bestValue)
				{
					bestValue = value;
					bestAction = moveAction;
				}
			}
		}

		System.out.println("Best = " + bestAction.getText() + "\n");
		return bestAction;
	}

	/**
	 * Represents the recursive part of the <b>Alpha-Beta Minimax</b> algorithm. It has been condensed into a single method with a <code>maximizingPlayer</code> flag.
	 * 
	 * @param board            the current state of the board
	 * @param depth            the remaining depth
	 * @param alpha            the current alpha value
	 * @param beta             the current beta value
	 * @param maximizingPlayer the current player's turn
	 * @return the value of the subtree from this board state
	 */
	private static double recursive(StateBoard board, int depth, double alpha, double beta, boolean maximizingPlayer)
	{
		if (cutoffTest(board, depth))
			return evaluateState(board, depth);

		if (maximizingPlayer)
		{
			// First, process jumps
			final List<ChainJumpAction> jumpActions = board.getJumpActions();
			final int jumpActionsSize = jumpActions.size();
			if (jumpActionsSize > 0)
			{
				for (int i = 0; i < jumpActionsSize; i++)
				{
					final ChainJumpAction jumpAction = jumpActions.get(i);
					board.executeAction(jumpAction);
					alpha = Math.max(alpha, recursive(board, depth - 1, alpha, beta, false));
					board.undoAction(jumpAction);

					// beta cutoff
					if (alpha >= beta)
						break;
				}
			}
			// Otherwise, process moves
			else
			{
				final List<MoveAction> moveActions = board.getMoveActions();
				final int moveActionsSize = moveActions.size();
				for (int i = 0; i < moveActionsSize; i++)
				{
					final MoveAction moveAction = moveActions.get(i);
					board.executeAction(moveAction);
					alpha = Math.max(alpha, recursive(board, depth - 1, alpha, beta, false));
					board.undoAction(moveAction);

					// Beta cutoff
					if (alpha >= beta)
						break;
				}
			}

			return alpha;
		}
		else
		{
			// First, process jumps
			final List<ChainJumpAction> jumpActions = board.getJumpActions();
			final int jumpActionsSize = jumpActions.size();
			if (jumpActionsSize > 0)
			{
				for (int i = 0; i < jumpActionsSize; i++)
				{
					final ChainJumpAction jumpAction = jumpActions.get(i);
					board.executeAction(jumpAction);
					beta = Math.min(beta, recursive(board, depth - 1, alpha, beta, true));
					board.undoAction(jumpAction);

					// alpha cutoff
					if (beta <= alpha)
						break;
				}
			}
			// Otherwise, process moves
			else
			{
				final List<MoveAction> moveActions = board.getMoveActions();
				final int moveActionsSize = moveActions.size();
				for (int i = 0; i < moveActionsSize; i++)
				{
					final MoveAction moveAction = moveActions.get(i);
					board.executeAction(moveAction);
					beta = Math.min(beta, recursive(board, depth - 1, alpha, beta, true));
					board.undoAction(moveAction);

					// alpha cutoff
					if (beta <= alpha)
						break;
				}
			}

			return beta;
		}
	}

	/**
	 * Determines whether the given board state is a terminal node, either via the game state conditions or by the given depth. Also, this will consider the remaining time and abort when the remaining
	 * time is less than {@link Constants#URGENCY_TIME}.
	 * 
	 * @param board the current board state
	 * @param depth the remaining depth
	 * @return true if to terminate search at from that board, false otherwise
	 */
	private static boolean cutoffTest(StateBoard board, int depth)
	{
		return depth <= 0 || board.getGameState() != GameState.Ongoing || AgentTimer.getRemainingTime() <= Constants.URGENCY_TIME;
	}

	/**
	 * Returns an evaluation of the given board state based on numerous factors, which are:
	 * <ol>
	 * <li>Find the best jump I can execute.</li>
	 * <li>Execute that jump.</li>
	 * <li>Calculate the remaining pieces differential.</li>
	 * <li>If I have more pieces, add the trading weight.</li>
	 * <li>Calculate the number of blocked actions differential.</li>
	 * <li>Value center pieces more than non-center pieces.</li>
	 * <li>Undo that jump.</li>
	 * <li>Add a hint of randomness.</li>
	 * </ol>
	 * 
	 * @param board the current board state
	 * @param depth the remaining depth
	 * @return an evaluation of the board state
	 */
	private static double evaluateState(StateBoard board, int depth)
	{
		/*-
		 * Evaluation:
		 * 
		 * If the game state is terminal, proceed with specified weights that scale the shallower the depth (winning in 3 moves > winning in 10 moves).
		 * 
		 * Otherwise:
		 * 
		 * 1. Find the best jump I can execute.
		 * 2. Execute that jump
		 * 3. Calculate the remaining pieces differential
		 * 4. If I have more pieces, add the trading weight
		 * 5. Calculate the number of blocked actions differential
		 * 6. Value center pieces more than non-center pieces.
		 * 7. Undo that jump
		 * 8. Add a hint of randomness
		 */

		// basically used for terminal states, where shallower states are scaled much more
		// i.e, winning in 3 moves is better than winning in 30 moves
		final double terminalStateDepthMultiplier = Math.max(1, depth);
		final GameState gameState = board.getGameState();
		double value = 0;

		if (gameState == GameState.Ongoing)
		{
			// 3. Calculate the remaining pieces differential
//			int blackHomeRowPieces = Long.bitCount(board.blacks & StateBoard.MASK_BLACK_HOME_ROW);
//			int blackPotentialKings = Long.bitCount(board.blacks & ~board.kings & StateBoard.MASK_BLACK_POTENTIAL_KINGS_ROW);
			int blackKings = Long.bitCount(board.blacks & board.kings);
			int blackPawns = Long.bitCount(board.blacks);

//			int whiteHomeRowPieces = Long.bitCount(board.whites & StateBoard.MASK_WHITE_HOME_ROW);
//			int whitePotentialKings = Long.bitCount(board.whites & ~board.kings & StateBoard.MASK_WHITE_POTENTIAL_KINGS_ROW);
			int whiteKings = Long.bitCount(board.whites & board.kings);
			int whitePawns = Long.bitCount(board.whites);

			// 3. Calculate the remaining pieces differential
			// Compare pieces count
			value += (blackPawns - whitePawns) * Constants.EVAL_PAWN_WEIGHT;
//			if (blackPawns + whitePawns >= 12)
//				value += (blackHomeRowPieces - whiteHomeRowPieces) * Constants.EVAL_PAWN_HOME_ROW_WEIGHT;
//			value += (blackPotentialKings - whitePotentialKings) * Constants.EVAL_PAWN_ALMOST_KING_WEIGHT;
			value += (blackKings - whiteKings) * Constants.EVAL_PAWN_KING_WEIGHT;

			// 4. If I have more pieces, add the trading weight
			// If I have 2 or more pieces, then trades are good for me.
//			final int blackTotalPawns = blackPawns + blackHomeRowPieces + blackPotentialKings + blackKings;
//			final int whiteTotalPawns = whitePawns + whiteHomeRowPieces + whitePotentialKings + whiteKings;
//			if (blackTotalPawns - whiteTotalPawns >= Constants.EVAL_TRADE_REQ)
//				value += Constants.EVAL_TRADE_WEIGHT;
//			else if (whiteTotalPawns - blackTotalPawns >= Constants.EVAL_TRADE_REQ)
//				value -= Constants.EVAL_TRADE_WEIGHT;

			// 5. Calculate the number of blocked actions differential
//			value += Math.signum(board.getNumBlockedPawnsDifferential()) * Constants.EVAL_FEWER_BLOCKED_PAWNS_BONUS;

			// 6. Value center pieces more than non-center pieces.
//			int blackCenterPieces = Long.bitCount(board.blacks & StateBoard.MASK_CENTER_MID);
//			int whiteCenterPieces = Long.bitCount(board.whites & StateBoard.MASK_CENTER_MID);
//			if (blackPawns + whitePawns >= 12)
//				value += Math.signum(blackCenterPieces - whiteCenterPieces) * Constants.EVAL_POSITION_CENTER_BONUS;

			// Negate value if original team is not black
			if (!isOnBlackTeam)
				value *= -1;
		}
		else if (gameState == GameState.Draw)
		{
			value = Constants.EVAL_DRAW_WEIGHT * terminalStateDepthMultiplier;
		}
		else if (gameState == GameState.BlackWin)
		{
			if (isOnBlackTeam)
				value += Constants.EVAL_WIN_WEIGHT * terminalStateDepthMultiplier;
			else
				value += Constants.EVAL_LOSE_WEIGHT * terminalStateDepthMultiplier;
		}
		else if (gameState == GameState.WhiteWin)
		{
			if (isOnBlackTeam)
				value += Constants.EVAL_LOSE_WEIGHT * terminalStateDepthMultiplier;
			else
				value += Constants.EVAL_WIN_WEIGHT * terminalStateDepthMultiplier;
		}

		// 8. Add a hint of randomness
		// Randomness factor to break ties in equivalent states
		value += (0.5 - Constants.RANDOM.nextDouble()) * Constants.EVAL_RANDOMNESS_WEIGHT * 2;

		return value;
	}
}