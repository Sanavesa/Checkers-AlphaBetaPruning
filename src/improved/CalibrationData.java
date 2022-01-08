package improved;
import java.io.File;
import java.util.Scanner;

/**
 * The <code>CalibrationData</code> class contains the measurements produced by calibrating the code on the machine. This is used by the <code>AgentTimer</code> class to determine the best action to
 * perform in the remaining alloted time. The filename used is defined by {@link Constants#FILENAME_CALIBRATION}.
 * 
 * <p>
 * The format of the calibration file should be as such (all are <code>double</code> values separated by a space on a single line, except <code>N</code> is an integer):
 * <ul>
 * <code>timeForReading timeForWriting N timeForDepth0 timeForDepth1 ... timeForDepthN-1</code>
 * </ul>
 * </p>
 * 
 * @author Mohammad Alali
 * @see AgentTimer
 * @see Agent
 */
public final class CalibrationData
{
	/**
	 * Don't let anyone instantiate this class.
	 */
	private CalibrationData()
	{}

	/**
	 * A flag indicating whether the calibration file at {@link Constants#FILENAME_CALIBRATION} was read successfully or not.
	 */
	public static boolean initialized = false;

	/**
	 * The time in seconds to perform <code>Minimax</code> for each depth. Here, the index of the array is the depth. Fallback value of <code>null</code> when loading the calibration data was
	 * unsuccessful.
	 */
	public static double[] depthTimes = null;

	/**
	 * Attemps to read the calibration file at {@link Constants#FILENAME_CALIBRATION} and parse its contents.
	 * 
	 * <p>
	 * The format of the calibration file should be as such (all are <code>double</code> values separated by a space on a single line, except <code>N</code> is an integer):
	 * <ul>
	 * <code>N timeForDepth0 timeForDepth1 ... timeForDepthN-1</code>
	 * </ul>
	 * </p>
	 */
	public static void readFromFile()
	{
		final File file = new File(Constants.FILENAME_CALIBRATION);
		if (file.exists())
		{
			try (final Scanner scanner = new Scanner(file))
			{
				final int length = scanner.nextInt();
				depthTimes = new double[length];
				for (int i = 0; i < length; i++)
					depthTimes[i] = scanner.nextDouble();
				initialized = true;
			}
			catch (final Exception e)
			{
				initialized = false;
				depthTimes = null;
			}
		}
		else
		{
			initialized = false;
			depthTimes = null;
		}
	}
}