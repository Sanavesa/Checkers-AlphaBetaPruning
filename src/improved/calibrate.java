package improved;
import java.io.File;
import java.io.FileWriter;

public class calibrate
{
	public static void main(String[] args)
	{
		try
		{
			final double[] solvingTime = measureSolve();

			// Format measurements
			final StringBuilder builder = new StringBuilder();
			builder.append(solvingTime.length);
			for (int i = 0; i < solvingTime.length; i++)
				builder.append(" ").append(solvingTime[i]);

			// Write measurements to calibration file
			try (final FileWriter writer = new FileWriter(Constants.FILENAME_CALIBRATION))
			{
				writer.write(builder.toString());
			}
		}
		catch (final Exception e)
		{
			// In case any errors occured, remove the calibration file incase it causes errors in homework.java
			new File(Constants.FILENAME_CALIBRATION).delete();
		}
		finally
		{
			// Delete the temporary files used to measure file I/O
			new File(Constants.FILENAME_CALIBRATION_TEMP_INPUT).delete();
		}
	}

	private static void writeCalibrationInputFile() throws Exception
	{
		final StringBuilder builder = new StringBuilder();
		builder.append("GAME\n");
		builder.append("BLACK").append("\n");
		builder.append("60.0\n");
		builder.append(".b.b.b.b\n");
		builder.append("b.b.b.b.\n");
		builder.append(".b.b.b.b\n");
		builder.append("........\n");
		builder.append("........\n");
		builder.append("w.w.w.w.\n");
		builder.append(".w.w.w.w\n");
		builder.append("w.w.w.w.");
		try (final FileWriter writer = new FileWriter(Constants.FILENAME_CALIBRATION_TEMP_INPUT))
		{
			writer.write(builder.toString());
		}
	}

	private static double[] measureSolve() throws Exception
	{
		writeCalibrationInputFile();
		final double[] result = new double[Constants.MAX_DEPTH + 1];

		Problem problem = new Problem(Constants.FILENAME_CALIBRATION_TEMP_INPUT);

		for (int depth = 0; depth < result.length; depth++)
		{
			AgentTimer.start();
			AgentTimer.setProblem(problem);

			final long startTime = System.nanoTime();
			Minimax.search(problem.board, problem.board.isBlackTurn, depth);
			final long endTime = System.nanoTime();
			result[depth] += (endTime - startTime) * Constants.NANO_TO_SEC;
		}

		// To simulate iterative deepening
		for (int i = 1; i < result.length; i++)
			result[i] += result[i - 1];

		return result;
	}
}