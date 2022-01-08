package checkers;

public class EvaluationMain
{
	public static final String INPUT_FILENAME = "input.txt";

	public static void main(String[] args) throws Exception
	{
		evaluate();
	}

	public static void evaluate() throws Exception
	{
		int caseNumber = 10;
		String directory = "src/hw2/case" + String.valueOf(caseNumber) + "/";
		String inputFilename = directory + INPUT_FILENAME;

		// Read problem from input
		Problem problem = new Problem(inputFilename);
		System.out.println(problem);

		// Evaluate state
		double value = Minimax.evaluateState(problem.state, problem.team, 0);
		System.out.println("Value = " + value);
	}
}