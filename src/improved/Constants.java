package improved;
import java.util.Random;

public final class Constants
{
	/**
	 * Don't let anyone instantiate this class.
	 */
	private Constants()
	{}

	/////// Filenames ///////

	/**
	 * Filename of the input file that contains the problem.
	 * 
	 * @see Problem
	 */
	public static final String FILENAME_INPUT = "input.txt";

	/**
	 * Filename of the output file that contains the chosen action.
	 * 
	 * @see Problem
	 */
	public static final String FILENAME_OUTPUT = "output.txt";

	/**
	 * Filename of the calibration file.
	 * 
	 * @see hw2.calibration
	 * @see CalibrationData
	 */
	public static final String FILENAME_CALIBRATION = "calibration.txt";

	/**
	 * Filename of the temporary input calibration file used to measure file I/O.
	 * 
	 * @see hw2.calibration
	 * @see CalibrationData
	 */
	public static final String FILENAME_CALIBRATION_TEMP_INPUT = "temp_calibration_input.txt";

	/**
	 * Filename of the playdata file.
	 * 
	 * @see PlayData
	 */
	public static final String FILENAME_PLAYDATA = "playdata.txt";

	/////// Agent Timer ///////

	/**
	 * Time threshold for when the agent defaults to <b>ANY VALID</b> action. This value is also used by <code>Minimax</code> in its cutoff test.
	 * 
	 * @see AgentTimer
	 * @see Agent
	 * @see Minimax
	 */
	public static final double URGENCY_TIME = 5;

	/**
	 * Time threshold for when the agent using <code>Minimax</code> begins considering lower depths from the calibration data. This works in tandem with {@link #CRUNCH_ALLOWANCE} and
	 * {@link #CRUNCH_MAX_DEPTH}.
	 * 
	 * @see AgentTimer
	 * @see Agent
	 * @see CalibrationData
	 * @see Minimax
	 */
	public static final double CRUNCH_TIME = 10;

	/**
	 * The percentage of remaining time (0-1) of how much to spend when crunching using <code>Minimax</code>. This works in tandem with {@link #CRUNCH_TIME} and {@link #CRUNCH_MAX_DEPTH}.
	 * 
	 * @see AgentTimer
	 * @see Agent
	 * @see Minimax
	 */
	public static final double CRUNCH_ALLOWANCE = 0.2;

	/**
	 * The maximum depth allowed when the agent using <code>Minimax</code> is crunching. This works in tandem with {@link #CRUNCH_TIME} and {@link #CRUNCH_ALLOWANCE}.
	 * 
	 * @see AgentTimer
	 * @see Agent
	 * @see Minimax
	 */
	public static final int CRUNCH_MAX_DEPTH = 5;

	/**
	 * The minimum depth for the agent using <code>Minimax</code> in normal scenarios. This works in tandem with {@link #MAX_DEPTH} and {@link #DEPTH_FACTOR}.
	 * 
	 * @see AgentTimer
	 * @see Agent
	 * @see Minimax
	 */
	public static final int MIN_DEPTH = 6;

	/**
	 * The maximum depth for the agent using <code>Minimax</code> in normal scenarios. This works in tandem with {@link #MIN_DEPTH} and {@link #DEPTH_FACTOR}.
	 * 
	 * @see AgentTimer
	 * @see Agent
	 * @see Minimax
	 */
	public static final int MAX_DEPTH = 10;

	/**
	 * Tunes how fast to interpolate the suggested depth for <code>Minimax</code> in normal scenarios. Values greater than 1 favor {@link #MIN_DEPTH} more, whereas values lower than 1 favor
	 * {@link #MAX_DEPTH}.
	 * 
	 * @see AgentTimer
	 * @see Agent
	 * @see Minimax
	 */
	public static final double DEPTH_FACTOR = 1;

	//////// State Evaluation ///////

	/**
	 * The value of capturing a regular pawn. This is used by the <code>Minimax</code> algorithm to evalaute a given state board.
	 * 
	 * @see Minimax
	 * @see StateBoard
	 */
	public static final double EVAL_CAPTURE_PAWN_WEIGHT = 3;

	/**
	 * The value of capturing a king. This is used by the <code>Minimax</code> algorithm to evalaute a given state board.
	 * 
	 * @see Minimax
	 * @see StateBoard
	 */
	public static final double EVAL_CAPTURE_KING_WEIGHT = 5;

	/**
	 * The value of possessing a king. This is used by the <code>Minimax</code> algorithm to evalaute a given state board.
	 * 
	 * @see Minimax
	 * @see StateBoard
	 */
	public static final double EVAL_PAWN_KING_WEIGHT = 3;

	/**
	 * The value of possessing a home-row pawn. This is used by the <code>Minimax</code> algorithm to evalaute a given state board.
	 * 
	 * @see Minimax
	 * @see StateBoard
	 */
	public static final double EVAL_PAWN_HOME_ROW_WEIGHT = 1.5;

	/**
	 * The value of potential king pawn which is 1 row away. This is not limited to unblocked pawns, however. This is used by the <code>Minimax</code> algorithm to evalaute a given state board.
	 * 
	 * @see Minimax
	 * @see StateBoard
	 */
	public static final double EVAL_PAWN_ALMOST_KING_WEIGHT = 1.5;

	/**
	 * The value of possessing a pawn that is <b>not</b> in the home-row, <b>not</b> a potential king, and <b>not</b> a king. This is used by the <code>Minimax</code> algorithm to evalaute a given state
	 * board.
	 * 
	 * @see Minimax
	 * @see StateBoard
	 */
	public static final double EVAL_PAWN_WEIGHT = 1;

	/**
	 * The benefit when my team controls the center. This is used by the <code>Minimax</code> algorithm to evalaute a given state board.
	 * 
	 * @see Minimax
	 * @see StateBoard
	 */
	public static final int EVAL_POSITION_CENTER_BONUS = 2;

	/**
	 * The benefit when my team has less blocked pawns. This is used by the <code>Minimax</code> algorithm to evalaute a given state board.
	 * 
	 * @see Minimax
	 * @see StateBoard
	 */
	public static final int EVAL_FEWER_BLOCKED_PAWNS_BONUS = 2;

	/**
	 * The benefit when my team has more pawns than the opponent. This is used in tandem with {@link #EVAL_TRADE_REQ}. This is used by the <code>Minimax</code> algorithm to evalaute a given state board.
	 * 
	 * @see Minimax
	 * @see StateBoard
	 */
	public static final double EVAL_TRADE_WEIGHT = 3;

	/**
	 * The difference in number of pawns needed to add the trade weight. This is used in tandem with {@link #EVAL_TRADE_WEIGHT}. This is used by the <code>Minimax</code> algorithm to evalaute a given
	 * state board.
	 * 
	 * @see Minimax
	 * @see StateBoard
	 */
	public static final int EVAL_TRADE_REQ = 2;

	/**
	 * The terminal value of a draw game. This is used by the <code>Minimax</code> algorithm to evalaute a given state board.
	 * 
	 * @see Minimax
	 * @see StateBoard
	 */
	public static final double EVAL_DRAW_WEIGHT = 2;

	/**
	 * The terminal value of a won game. This is used by the <code>Minimax</code> algorithm to evalaute a given state board.
	 * 
	 * @see Minimax
	 * @see StateBoard
	 */
	public static final double EVAL_WIN_WEIGHT = 100;

	/**
	 * The terminal value of a lost game. This is used by the <code>Minimax</code> algorithm to evalaute a given state board.
	 * 
	 * @see Minimax
	 * @see StateBoard
	 */
	public static final double EVAL_LOSE_WEIGHT = -100;

	/**
	 * The magnitude of randomness added at the end of evaluation to ensure <code>Agent</code> is not deterministic. This is used by the <code>Minimax</code> algorithm to evalaute a given state board.
	 * 
	 * @see Minimax
	 * @see StateBoard
	 */
	public static final double EVAL_RANDOMNESS_WEIGHT = 0.01;

	/////// Utility ///////

	/**
	 * The conversion factor from seconds to nanoseconds.
	 */
	public static final double SEC_TO_NANO = 1E9;

	/**
	 * The conversion factor from nanoseconds to seconds.
	 */
	public static final double NANO_TO_SEC = 1E-9;

	/**
	 * An application-wide random number generator.
	 */
	public static final Random RANDOM = new Random();
}