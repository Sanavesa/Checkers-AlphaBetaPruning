package checkers;

public final class Converter
{
	private static final String[] ROW_CONVERSION = { "8", "7", "6", "5", "4", "3", "2", "1" };
	private static final String[] COLUMN_CONVERSION = { "a", "b", "c", "d", "e", "f", "g", "h" };

	public static String moveOutput(int rowFrom, int columnFrom, int rowTo, int columnTo)
	{
		return "E " + positionOutput(rowFrom, columnFrom) + " " + positionOutput(rowTo, columnTo);
	}

	public static String jumpOutput(int rowFrom, int columnFrom, int rowTo, int columnTo)
	{
		return "J " + positionOutput(rowFrom, columnFrom) + " " + positionOutput(rowTo, columnTo);
	}

	public static String positionOutput(int row, int column)
	{
		return COLUMN_CONVERSION[column] + ROW_CONVERSION[row];
	}
}