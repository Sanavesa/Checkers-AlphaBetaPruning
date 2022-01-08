package checkers;

import java.util.Random;

public class Minimax
{
	private static final Random rand = new Random(1337);

	private static int clamp(int value, int min, int max)
	{
		if (value <= min)
			return min;
		else if (value >= max)
			return max;
		return value;
	}

	public static int getSuggestedMaxDepth(State state)
	{
		int pieces = 0;
		for (int r = 0; r < 8; r++)
			for (int c = 0; c < 8; c++)
				if (state.board[r][c] != null)
					pieces++;

		int maxDepth = clamp(30 - pieces, 10, 20); // Has lookahead of 10 to 20
		return maxDepth;
	}

	public static BaseAction search(State state, Team myTeam, int depth)
	{
		double bestValue = Double.NEGATIVE_INFINITY;
		BaseAction bestAction = null;
		StateActions actions = state.getActions();

		for (BaseAction action : actions.jumpActions)
		{
			state.executeAction(action);
			double value = minValue(state, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, myTeam, depth - 1);
			state.undoAction(action);
			if (value > bestValue)
			{
				bestValue = value;
				bestAction = action;
			}
		}

		if (actions.jumpActions.size() == 0)
		{
			for (BaseAction action : actions.moveActions)
			{
				state.executeAction(action);
				double value = minValue(state, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, myTeam, depth - 1);
				state.undoAction(action);
				if (value > bestValue)
				{
					bestValue = value;
					bestAction = action;
				}
			}
		}

		return bestAction;
	}

	private static double maxValue(State state, double alpha, double beta, Team myTeam, int depth)
	{
		if (cutoffTest(state, myTeam, depth))
			return evaluateState(state, myTeam, depth);

		StateActions actions = state.getActions();

		for (BaseAction action : actions.jumpActions)
		{
			state.executeAction(action);
			alpha = Math.max(alpha, minValue(state, alpha, beta, myTeam, depth - 1));
			state.undoAction(action);
			if (alpha >= beta)
				return beta;
		}

		if (actions.jumpActions.size() == 0)
		{
			for (BaseAction action : actions.moveActions)
			{
				state.executeAction(action);
				alpha = Math.max(alpha, minValue(state, alpha, beta, myTeam, depth - 1));
				state.undoAction(action);
				if (alpha >= beta)
					return beta;
			}
		}

		return alpha;
	}

	private static double minValue(State state, double alpha, double beta, Team myTeam, int depth)
	{
		if (cutoffTest(state, myTeam, depth))
			return evaluateState(state, myTeam, depth);

		StateActions actions = state.getActions();

		for (BaseAction action : actions.jumpActions)
		{
			state.executeAction(action);
			beta = Math.min(beta, maxValue(state, alpha, beta, myTeam, depth - 1));
			state.undoAction(action);
			if (beta <= alpha)
				return alpha;
		}

		if (actions.jumpActions.size() == 0)
		{
			for (BaseAction action : actions.moveActions)
			{
				state.executeAction(action);
				beta = Math.min(beta, maxValue(state, alpha, beta, myTeam, depth - 1));
				state.undoAction(action);
				if (beta <= alpha)
					return alpha;
			}
		}

		return beta;
	}

	private static boolean cutoffTest(State state, Team myTeam, int depth)
	{
		return depth <= 0 || state.getGameState() != GameState.Ongoing;
	}

	public static double evaluateState(State state, Team myTeam, int depth)
	{
		final double capturePawnWeight = 2;
		final double captureKingWeight = 5;
		final double kingWeight = 2;
		final double pawnWeight = 1;
		final double tradeWeight = 3; // if i have more pawns, i have an advantage
		final double homeRowWeight = 1; // if i have more pawns in the home row, i'm a bit better
		final double drawWeight = -5;
		final double winWeight = 100;
		final double loseWeight = -100;

		// Will assume I'm black and opponent is white; will multiply -1 at end if its the opposite
		double value = 0;

		GameState gameOverState = state.getGameState();
		if (gameOverState == GameState.Ongoing)
		{
			// My Pawns - Opponent Pawns
			int blackPieces = 0;
			int whitePieces = 0;
			int blackHomeRowPieces = 0;
			int whiteHomeRowPieces = 0;
			for (int r = 0; r < 8; r++)
			{
				for (int c = 0; c < 8; c++)
				{
					Pawn pawn = state.board[r][c];
					if (pawn != null)
					{
						double pawnValue = (pawn.isKing) ? kingWeight : pawnWeight;
						if (pawn.team == Team.Black)
						{
							value += pawnValue;
							blackPieces++;
							if (r == 0)
								blackHomeRowPieces++;
						}
						else
						{
							value -= pawnValue;
							whitePieces++;
							if (r == 7)
								whiteHomeRowPieces++;
						}
					}
				}
			}

			// If I have 2 or more pieces, then trades are good for me.
			if (blackPieces - whitePieces >= 2)
				value += tradeWeight;
			else
				value -= tradeWeight;

			// Compare home rows
			value += homeRowWeight * (blackHomeRowPieces - whiteHomeRowPieces);

			// If I have jumps, its good for me!
			// Evaluate the jumps!
			StateActions actions = state.getActions();
			double bestJumpActionValue = 0;
			for (ChainJumpAction chainJumpAction : actions.jumpActions)
			{
				double chainValue = 0;
				for (JumpAction x : chainJumpAction.chain)
					chainValue += (x.victim.isKing) ? captureKingWeight : capturePawnWeight;
				bestJumpActionValue = Math.max(bestJumpActionValue, chainValue);
			}
			if (state.currentTurn == Team.Black)
				value += bestJumpActionValue;
			else
				value -= bestJumpActionValue;

			// Randomness factor to break ties in equivalent states
			// TODO: Re-add after bugs disappear
			value += rand.nextDouble() * 0.25;
//			value += Math.random() * 0.25;
		}
		else if (gameOverState == GameState.Draw)
			value += drawWeight;
		else if (gameOverState == GameState.BlackWin)
			value += winWeight;
		else if (gameOverState == GameState.WhiteWin)
			value += loseWeight;

		// Symmetry
		if (myTeam != Team.Black)
			value *= -1;
		if (state.currentTurn != myTeam)
			value *= -1;

		return value;
	}
}