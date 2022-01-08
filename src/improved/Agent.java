package improved;

import java.util.List;

/**
 * The <code>Agent</code> class solves a given <code>Problem</code> and returns the textual representation of the action to perform.
 * 
 * @author Mohammad Alali
 * @see AgentTimer
 * @see Problem
 * @see Minimax
 */
public final class Agent
{
	/**
	 * Don't let anyone instantiate this class.
	 */
	private Agent()
	{}

	/**
	 * Solves the given <code>problem</code> and returns a textual representation of the action to perform.
	 * 
	 * @param problem the problem to solve
	 * @return the textual representation of the action to perform
	 */
	public static String solve(Problem problem)
	{
		if (problem.agentType == AgentType.Single)
			return singleMove(problem);
		else
		{
			CalibrationData.readFromFile();
			PlayData.readFromFile(problem);
			final String output = gameMove(problem);
			PlayData.writeToFile();

			return output;
		}
	}

	/**
	 * Solves the given single-mode problem. If the problem contains no valid actions, this method will fail.
	 * 
	 * <p>
	 * Algorithm:
	 * <ol>
	 * <li>Generate all jump actions.</li>
	 * <li>Sort all jumps in descending order of chain length (longest becomes first).</li>
	 * <li>If a jump exists, return the first action.</li>
	 * <li>Otherwise, generate all move actions, and return the first move action.</li>
	 * </ol>
	 * </p>
	 * 
	 * @param problem the problem to solve
	 * @return the textual representation of the move action to perform
	 */
	private static String singleMove(Problem problem)
	{
		// Return the first jump we find (will be longest chain)
		final List<ChainJumpAction> jumpActions = problem.board.getJumpActions();
		if (jumpActions.size() > 0)
			return jumpActions.get(0).getText();

		// Return the first move we find
		final List<MoveAction> moveActions = problem.board.getMoveActions();
		return moveActions.get(0).getText();
	}

	/**
	 * Solves the given game-mode problem. If the problem contains no valid actions, this method will fail.
	 * 
	 * <p>
	 * Algorithm:
	 * <ol>
	 * <li>Generate all jump actions.</li>
	 * <li>Sort all jumps in descending order of chain length (longest becomes first).</li>
	 * <li>If only one jump exists, return that jump since its a forced jump.</li>
	 * <li>Also, if the remaining time is too short (less than {@link Constants#URGENCY_TIME}), the agent will revert to returning <b>ANY VALID</b> action.</li>
	 * <li>Otherwise, the agent will perform an <b>Iterative-Deepening Alpha-Beta Minimax</b> until reaching computed max depth, based on time constraints, all while keeping the returned of action of the
	 * last depth. Also, if any time, the play time is up (less than {@link Constants#URGENCY_TIME}), the agent will return whatever it has computed so far.</li>
	 * <li>However, if the agent did not manage to even compute any search and the time ran out, then it will revert to returning <b>ANY VALID</b> action.</li>
	 * </ol>
	 * </p>
	 * 
	 * @param problem the problem to solve
	 * @return the textual representation of the move action to perform
	 */
	private static String gameMove(Problem problem)
	{
		final StateBoard board = problem.board;

		// Optimization: use an opening move to save time
		final String openingMove = getOpeningMove(board);
		if (openingMove != null)
			return openingMove;

		// Get the jumps in descending chain length
		final List<ChainJumpAction> jumpActions = board.getJumpActions();

		// Optimization: Forced single jump - just do it, thinking about it wont make a difference
		if (jumpActions.size() == 1)
			return jumpActions.get(0).getText();

		// Optimization: extreme crunch so play anything!
		if (AgentTimer.getRemainingTime() <= Constants.URGENCY_TIME)
			return singleMove(problem);

		Action bestAction = null;
		final int maxDepth = AgentTimer.getSuggestedMaxDepth(board);
		for (int depth = 0; depth <= maxDepth; depth++)
		{
			final Action action = Minimax.search(board, board.isBlackTurn, depth);
			if (action != null)
				bestAction = action;
			if (AgentTimer.getRemainingTime() <= Constants.URGENCY_TIME)
				break;
		}

		// Fallback: did not manage to search for an action, then return ANY VALID action
		if (bestAction == null)
			return singleMove(problem);

		return bestAction.getText();
	}

	private static String getOpeningMove(StateBoard board)
	{
		if (!PlayData.initialized)
			return null;

		// The first move for Black
		if (PlayData.plysSinceStart == 0)
		{
			// Use the "Old Faithful" opening Move for Black
			if (board.isBlackTurn)
			{
				final boolean isBlackInPosition = (board.blacks & StateBoard.MASK_BLACK_START) == StateBoard.MASK_BLACK_START;
				final boolean isWhiteInPosition = (board.whites & StateBoard.MASK_WHITE_START) == StateBoard.MASK_WHITE_START;
				final boolean noKings = board.kings == 0L;
				if (isBlackInPosition && isWhiteInPosition && noKings)
					return "E f6 e5";
			}
		}
		// The first move for White
		else if (PlayData.plysSinceStart == 1)
		{
			// Use a popular response to whatever opening move Black played
			// There are 7 responses in total to Black
			if (!board.isBlackTurn)
			{
				final boolean isEmptyB6 = ((board.blacks >> 17) & 1L) == 0L;
				final boolean isEmptyD6 = ((board.blacks >> 19) & 1L) == 0L;
				final boolean isEmptyF6 = ((board.blacks >> 21) & 1L) == 0L;
				final boolean isEmptyH6 = ((board.blacks >> 23) & 1L) == 0L;

				final boolean isOccupiedA5 = ((board.blacks >> 24) & 1L) != 0L;
				final boolean isOccupiedC5 = ((board.blacks >> 26) & 1L) != 0L;
				final boolean isOccupiedE5 = ((board.blacks >> 28) & 1L) != 0L;
				final boolean isOccupiedG5 = ((board.blacks >> 30) & 1L) != 0L;

				if (isEmptyB6 && isOccupiedA5)
					return "E c3 b4";
				if (isEmptyB6 && isOccupiedC5)
					return "E e3 d4";
				if (isEmptyD6 && isOccupiedC5)
					return "E e3 d4";
				if (isEmptyD6 && isOccupiedE5)
					return "E c3 d4";
				if (isEmptyF6 && isOccupiedE5)
					return "E c3 d4";
				if (isEmptyF6 && isOccupiedG5)
					return "E e3 f4";
				if (isEmptyH6 && isOccupiedG5)
					return "E g3 f4";
			}
		}

		return null;
	}
}