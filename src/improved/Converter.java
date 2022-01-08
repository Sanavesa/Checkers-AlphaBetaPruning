package improved;
/**
 * The <code>Converter</code> class contains methods to convert the row,column position to the actual board position representation used, such as a1 or e3.
 * 
 * @author Mohammad Alali
 */
public final class Converter
{
	/**
	 * Don't let anyone instantiate this class.
	 */
	private Converter()
	{}

	/**
	 * A table lookup to convert between row index to the board's textual position representation.
	 */
	private static final String[] ROW_CONVERSION = { "8", "7", "6", "5", "4", "3", "2", "1" };

	/**
	 * A table lookup to convert between column index to the board's textual position representation.
	 */
	private static final String[] COLUMN_CONVERSION = { "a", "b", "c", "d", "e", "f", "g", "h" };

	/**
	 * Returns the move output format as specified in the homework, such as <i>E a1 b2</i>.
	 * 
	 * @param rowFrom    the start position's row
	 * @param columnFrom the start position's column
	 * @param rowTo      the destination position's row
	 * @param columnTo   the destination position's column
	 * @return the converted move output format
	 * @see MoveAction
	 */
	public static String moveOutput(int rowFrom, int columnFrom, int rowTo, int columnTo)
	{
		return "E " + positionOutput(rowFrom, columnFrom) + " " + positionOutput(rowTo, columnTo);
	}

	/**
	 * Returns the jump output format as specified in the homework, such as <i>J a1 c3</i>. If this is a chain of jumps, they are separated by a newline.
	 * 
	 * @param rowFrom    the start position's row
	 * @param columnFrom the start position's column
	 * @param rowTo      the destination position's row
	 * @param columnTo   the destination position's column
	 * @return the converted jump output format
	 * @see JumpAction
	 * @see ChainJumpAction
	 */
	public static String jumpOutput(int rowFrom, int columnFrom, int rowTo, int columnTo)
	{
		return "J " + positionOutput(rowFrom, columnFrom) + " " + positionOutput(rowTo, columnTo);
	}

	/**
	 * Returns the position format as specified in the homework, such as <i>g5</i>.
	 * 
	 * @param row    the position's row
	 * @param column the position's column
	 * @return the converted position format
	 * @see MoveAction
	 * @see JumpAction
	 * @see ChainJumpAction
	 */
	public static String positionOutput(int row, int column)
	{
		return COLUMN_CONVERSION[column] + ROW_CONVERSION[row];
	}
}