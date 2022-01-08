package improved;
import java.io.FileReader;
import java.util.Scanner;

/**
 * The <code>Problem</code> class contains the problem definition as per the homework. It is responsible for parsing the input file at {@link Constants#FILENAME_INPUT}.
 * 
 * @author Mohammad Alali
 * @see AgentType
 * @see StateBoard
 */
public final class Problem
{
	/**
	 * The type of problem to solve.
	 */
	public final AgentType agentType;

	/**
	 * The current state of the board.
	 */
	public final StateBoard board;

	/**
	 * The initial play time available for the program, in seconds.
	 */
	public final double playTime;

	/**
	 * Parses the given <code>filename</code> and returns a <code>Problem</code> instance if the format matches. The format is defined in the homework.
	 * 
	 * @param filename the name of the input file to parse
	 * @throws Exception if the format is incorrect or I/O exception
	 */
	public Problem(String filename) throws Exception
	{
		try (final Scanner scanner = new Scanner(new FileReader(filename)))
		{
			board = new StateBoard();

			agentType = AgentType.parse(scanner.nextLine());
			board.isBlackTurn = scanner.nextLine().equals("BLACK");
			playTime = scanner.nextFloat();
			scanner.nextLine(); // Move to next line

			for (int row = 0; row < 8; row++)
			{
				final String line = scanner.nextLine();
				for (int column = 0; column < 8; column++)
				{
					final char c = line.charAt(column);
					final int i = row * 8 + column;
					final long position = 1L << i;
					switch (c)
					{
						case 'B':
							board.kings |= position;
						case 'b':
							board.blacks |= position;
							break;
						case 'W':
							board.kings |= position;
						case 'w':
							board.whites |= position;
							break;
						default:
							break;
					}
				}
			}
		}
	}
}