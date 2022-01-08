package improved;
import java.io.FileWriter;

public final class homework
{
	public static void main(String[] args) throws Exception
	{
		// Start the agent timer
		AgentTimer.start();

		// Read problem from input file
		final Problem problem = new Problem(Constants.FILENAME_INPUT);
		AgentTimer.setProblem(problem);

		// Solve problem
		final String output = Agent.solve(problem);

		// Write solution to output
		try (final FileWriter writer = new FileWriter(Constants.FILENAME_OUTPUT))
		{
			writer.write(output);
		}
	}
}