package checkers;

import checkers.gui.Checker;

public class GameTestMain
{
	public static void main(String[] args)
	{
		System.out.println(Checker.state);

		Checker.agentPlay();
//		playRandom();

		System.out.println(Checker.state);
	}

	private static void playRandom()
	{
		StateActions stateActions = Checker.state.getActions();
		BaseAction action = null;
		if (stateActions.jumpActions.size() > 0)
			action = stateActions.jumpActions.get(0);
		else if (stateActions.moveActions.size() > 0)
			action = stateActions.moveActions.get(0);

		if (action != null)
		{
			Checker.state.executeAction(action);
			System.out.println("Action:\n" + action.getText());
			System.out.println(Checker.state);

			Checker.state.undoAction(action);
			System.out.println("Undoing action");
			System.out.println(Checker.state);
		}
	}
}
