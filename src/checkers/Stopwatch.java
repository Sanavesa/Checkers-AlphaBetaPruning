package checkers;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;

public final class Stopwatch
{
	private static final double NANOSECONDS_PER_SECOND = 1_000_000_000.0;
	private final ThreadMXBean threadTimer;
	private long startTime = 0;
	private long threadId = -1;
	private boolean started = false;

	public Stopwatch()
	{
		threadTimer = ManagementFactory.getThreadMXBean();
		threadTimer.setThreadCpuTimeEnabled(true);
	}

	public final void start(long threadId)
	{
		started = true;
		this.threadId = threadId;
		startTime = threadTimer.getThreadCpuTime(threadId);
	}

	public final double elapsedTime()
	{
		if (!started)
			return 0;
		long endTime = threadTimer.getThreadCpuTime(threadId);
		started = false;
		return (endTime - startTime) / NANOSECONDS_PER_SECOND;
	}
}