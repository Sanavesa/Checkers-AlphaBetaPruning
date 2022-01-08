package checkers;

import java.io.File;
import java.io.FileWriter;
import java.nio.file.Files;

public class Main
{
	public static final String INPUT_FILENAME = "input.txt";
	public static final String OUTPUT_FILENAME = "output.txt";
	public static final String SOLUTION_FILENAME = "solution.txt";

	public static void main(String[] args) throws Exception
	{
		long start = System.nanoTime();
		start();
		long end = System.nanoTime();
		System.out.println("\nTook " + (end - start) / (1_000_000_000.0) + " secs");
	}

	public static void start() throws Exception
	{
		int caseNumber = 16;
		String directory = "src/hw2/case" + String.valueOf(caseNumber) + "/";
		String inputFilename = directory + INPUT_FILENAME;

		// Read problem from input
		Problem problem = new Problem(inputFilename);
		System.out.println(problem);

		// Solve problem
		String output = Agent.solve(problem);

		System.out.println("\nOutput:\n" + output);

		// Write solution to output
		String outputFilename = directory + OUTPUT_FILENAME;
		try (FileWriter writer = new FileWriter(outputFilename))
		{
			writer.write(output);
		}

		// Expected solution
		String solutionFilename = directory + SOLUTION_FILENAME;
		File solutionFile = new File(solutionFilename);
		if (solutionFile.exists())
		{
			String solution = Files.readString(solutionFile.toPath());
			System.out.println("\nExpected Output:\n" + solution);
		}
	}
}