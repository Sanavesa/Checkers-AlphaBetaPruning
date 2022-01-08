package improved;

/**
 * The <code>AgentTimer</code> class solves a given <code>Problem</code> and returns the textual representation of the action to perform.
 * 
 * @author Mohammad Alali
 * @see AgentTimer
 * @see Problem
 * @see Minimax
 */
public final class AgentTimer
{
	/**
	 * Don't let anyone instantiate this class.
	 */
	private AgentTimer()
	{}

	private static Problem problem = null;
	private static long startTime = 0;

	/**
	 * Starts the time. Preferably called on the program's startup.
	 */
	public static void start()
	{
		startTime = System.nanoTime();
	}

	/**
	 * Sets the <code>problem</code> the agent is attempting to solve.
	 * 
	 * @param problem the problem the agent is solving
	 * @see Input.Problem
	 */
	public static void setProblem(Problem problem)
	{
		AgentTimer.problem = problem;
	}

	/**
	 * Calculates the total remaining time, in seconds, for the agent's play time.
	 * 
	 * @return the remaining time in seconds
	 */
	public static double getRemainingTime()
	{
		return problem.playTime - (System.nanoTime() - startTime) * Constants.NANO_TO_SEC;
	}

	/**
	 * Calculates a max depth for the <code>Minimax</code> search that accomodates for the given time constraints.
	 * <p>
	 * Algorithm:
	 * <ol>
	 * <li>If remaining time is short (less than {@link Constants#CRUNCH_TIME}), then return a depth based on <code>CalibrationData</code> that only uses up {@link Constants#CRUNCH_ALLOWANCE} percent of
	 * the remaining time, up to a maximum depth of {@link Constants#MAX_DEPTH}. If no <code>CalibrationData</code> was found, it will revert to the max depth.</li>
	 * <li>Otherwise, use the number of pieces in the board as a guide on how deep one can go. Less pieces = higher depth. This is controlled by {@link Constants#MIN_DEPTH}, {@link Constants#MAX_DEPTH},
	 * and {@link Constants#DEPTH_FACTOR}. </il>
	 * </ol>
	 * </p>
	 * 
	 * @param board the current <code>board</code> the agent is solving
	 * @return the suggested max depth
	 * @see Minimax
	 * @see CalibrationData
	 */
	public static int getSuggestedMaxDepth(StateBoard board)
	{
		if (getRemainingTime() <= Constants.CRUNCH_TIME)
			return getCrunchDepth();

		final int pieces = Long.bitCount(board.whites | board.blacks);
		final int maxDepth = clamp((int) (30 - pieces * Constants.DEPTH_FACTOR), Constants.MIN_DEPTH, Constants.MAX_DEPTH); // Has lookahead of 10 to 20
		return maxDepth;
	}

	/**
	 * Returns a depth that accomodates for the severe time constraints imposed.
	 * 
	 * @return a time-saving depth
	 */
	private static int getCrunchDepth()
	{
		if (!CalibrationData.initialized)
			return Constants.CRUNCH_MAX_DEPTH;

		// We want a depth of only 20% of the remaining time, max depth of 7.
		final double allowance = getRemainingTime() * Constants.CRUNCH_ALLOWANCE;
		int bestDepth = 0;
		int size = CalibrationData.depthTimes.length;
		for (int i = 0; i < size; i++)
		{
			if (CalibrationData.depthTimes[i] > allowance)
				break;
			bestDepth = i;
		}

		return Math.min(bestDepth, Constants.CRUNCH_MAX_DEPTH);
	}

	/**
	 * Utility function to return <code>value</code> clamped to the given range.
	 * 
	 * @param value the value to clamp
	 * @param min   the min value
	 * @param max   the max value
	 * @return the clamped value
	 */
	private static int clamp(int value, int min, int max)
	{
		if (value <= min)
			return min;
		else if (value >= max)
			return max;
		return value;
	}
}