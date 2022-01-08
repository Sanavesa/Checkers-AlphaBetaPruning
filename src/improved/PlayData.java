package improved;
import java.io.File;
import java.io.FileWriter;
import java.util.Scanner;

/**
 * The <code>PlayData</code> class contains necessary persistent data for the <code>Agent</code> class to determine the best action to perform in the remaining alloted time. The filename used is
 * defined by {@link Constants#FILENAME_PLAYDATA}.
 * 
 * <p>
 * The format of the play data file is a single integer indicating the number of plys since start of the game.
 * </p>
 * 
 * @author Mohammad Alali
 * @see Agent
 */
public final class PlayData
{
	/**
	 * Don't let anyone instantiate this class.
	 */
	private PlayData()
	{}

	/**
	 * The number of plys since the start of the game. If no plys have been played, a fallback value of <code>0</code> or <code>1</code> is returned (depends on team), or when loading the play data was
	 * unsuccessful.
	 */
	public static int plysSinceStart = 0;

	/**
	 * A flag indicating the play data file was read successfully.
	 */
	public static boolean initialized = false;

	/**
	 * Attemps to read the play data file at {@link Constants#FILENAME_PLAYDATA} and parse its contents.
	 * 
	 * <p>
	 * The format of the play data file is a single integer indicating the number of plys since start of the game.
	 * </p>
	 * 
	 * @param problem the given problem in order to to calculate the plys
	 */
	public static void readFromFile(Problem problem)
	{
		final File file = new File(Constants.FILENAME_PLAYDATA);
		if (file.exists())
		{
			try (final Scanner scanner = new Scanner(file))
			{
				plysSinceStart = scanner.nextInt();
				initialized = true;
			}
			catch (final Exception e)
			{
				// An error occured while reading, so to prevent the error propagating, we shall set it to ply 3 (to surpass all opening moves checks).
				plysSinceStart = 3;
				initialized = false;
			}
		}
		else
		{
			// File does not exist. It must be our first move.
			// If its black's turn, then its the first ply, otherwise its the second ply
			plysSinceStart = problem.board.isBlackTurn ? 0 : 1;
			initialized = true;
		}
	}

	public static void writeToFile()
	{
		final File file = new File(Constants.FILENAME_PLAYDATA);
		try (final FileWriter writer = new FileWriter(file))
		{
			writer.write(Integer.toString(plysSinceStart + 2));
		}
		catch (final Exception e)
		{

		}
	}
}