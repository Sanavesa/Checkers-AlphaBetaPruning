package checkers;

public class ActionsMain
{
	public static final String INPUT_FILENAME = "input.txt";

	public static void main(String[] args) throws Exception
	{
		evaluate();
	}

	public static void evaluate() throws Exception
	{
		int caseNumber = 19;
		String directory = "src/hw2/case" + String.valueOf(caseNumber) + "/";
		String inputFilename = directory + INPUT_FILENAME;

		// Read problem from input
		Problem problem = new Problem(inputFilename);
		System.out.println(problem);

		StateActions stateActions = problem.state.getActions();

		System.out.println("Move Actions (" + stateActions.moveActions.size() + "):");
		for (MoveAction moveAction : stateActions.moveActions)
			System.out.println("\t" + moveAction.getDebugText());

		System.out.println();
		
		System.out.println("Jump Actions (" + stateActions.jumpActions.size() + "):");
		for (ChainJumpAction chainJumpAction : stateActions.jumpActions)
		{
			System.out.println("\t" + chainJumpAction.getDebugText().replaceAll("\n", "\n\t"));
		}
	}
}