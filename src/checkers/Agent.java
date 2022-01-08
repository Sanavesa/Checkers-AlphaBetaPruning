package checkers;

public class Agent
{
	public static String solve(Problem problem)
	{
		if (problem.agentType == AgentType.Single)
			return singleMove(problem);
		else
			return gameMove(problem);
	}

	private static String singleMove(Problem problem)
	{
		State state = problem.state;
		StateActions actions = state.getActions();

		BaseAction action;
		if (actions.jumpActions.size() > 0)
			action = actions.jumpActions.get(0);
		else
			action = actions.moveActions.get(0);

		state.executeAction(action);
		return action.getText();
	}

	private static String gameMove(Problem problem)
	{
		final State state = problem.state;
		final StateActions actions = state.getActions();

		// Optimization: Forced single jump - just do it, thinking about it wont make a difference
		if (actions.jumpActions.size() == 1)
		{
			BaseAction action = actions.jumpActions.get(0);
			state.executeAction(action);
			System.out.println("FORCED SINGLE JUMP.");
			return action.getText();
		}

		// Optimization: extreme crunch so play anything!
		if (problem.playTime <= 0.01)
		{
			System.out.println("CRUNCHING.");
			return singleMove(problem);
		}

		BaseAction action = null;

		long startTime = System.nanoTime();
		// Optimization: lower depth if crunching
		int depth = 7;
//		if (problem.playTime <= 0.2)
//			depth = 5;
//		else
//			depth = Minimax.getSuggestedMaxDepth(state);

		System.out.println("Going for DEPTH = " + depth);
		for (int i = 0; i <= depth; i++)
		{
			action = Minimax.search(state, problem.team, i);
			System.out.println("\tDepth " + i + " took " + (System.nanoTime() - startTime) / 1_000_000_000.0 + " seconds");
			startTime = System.nanoTime();
		}

		state.executeAction(action);
		return action.getText();
	}
}