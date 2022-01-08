package checkers;

import java.io.FileWriter;
import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public class measure
{
	private static final long WAIT_TIME = 4500;
	private static final ExecutorService service = Executors.newSingleThreadExecutor();

	public static void main(String[] args) throws Exception
	{
//		timingPhase();

		StopwatchCPU stopwatch = new StopwatchCPU();
		long start = System.nanoTime();

		double result = timingPhase();
//		double result = timingPhaseNoExceptions();

		// result and linux CPU time are off by 0.15s (0.5s if missed deadline)
		// result < cpuTime, but within 0.01
		// precision #.## seconds

		final double cpuTime = stopwatch.elapsedTime();
		final double realTime = (System.nanoTime() - start) / 1_000_000_000.0;

		try (FileWriter writer = new FileWriter("output.txt"))
		{
			writer.write("Result = " + result + "\n");
			writer.write("Total Real Time: " + realTime + "\n");
			writer.write("Total CPU Time: " + cpuTime + "\n");
		}
	}

	private static int longTask(int x)
	{
		if (x <= 1)
			return x;
		return (longTask(x - 1) % 10 + longTask(x - 2) % 10) % 10;
	}

	private static double timingPhase()
	{
		final Future<Double> futureTask = service.submit(() ->
		{
			final ThreadMXBean threadTimer = ManagementFactory.getThreadMXBean();
			final long start = threadTimer.getCurrentThreadCpuTime();
			longTask(43);
			final long end = threadTimer.getCurrentThreadCpuTime();
			return (end - start) / 1_000_000_000.0;
		});

		try
		{
			double result = futureTask.get(WAIT_TIME, TimeUnit.MILLISECONDS);
			return result;
		}
		catch (Exception e)
		{

		}
		finally
		{
			futureTask.cancel(true);
			service.shutdown();
		}

		return -1;
	}

	static double r = 0;
	private static double timingPhaseNoExceptions()
	{
		final ThreadMXBean threadTimerOut = ManagementFactory.getThreadMXBean();
		final long startOut = threadTimerOut.getCurrentThreadCpuTime();

		final Future<?> futureTask = service.submit(() ->
		{
			final ThreadMXBean threadTimer = ManagementFactory.getThreadMXBean();
			final long start = threadTimer.getCurrentThreadCpuTime();
			longTask(43);
			final long end = threadTimer.getCurrentThreadCpuTime();
			r = (end - start) / 1_000_000_000.0;
		});

		while (!futureTask.isDone() || ((threadTimerOut.getCurrentThreadCpuTime() - startOut) / 1_000_000.0) < WAIT_TIME)
		{
			// wait.
		}

		futureTask.cancel(true);
		service.shutdown();
		
		r += (threadTimerOut.getCurrentThreadCpuTime() - startOut) / 1_000_000_000.0;

		return r;
	}
	
	private static class StopwatchCPU
	{
		private static final double NANOSECONDS_PER_SECOND = 1000000000;

		private final ThreadMXBean threadTimer;
		private final Map<Long, Double> startTimes;

		public StopwatchCPU()
		{
			threadTimer = ManagementFactory.getThreadMXBean();
			long[] allThreadIds = threadTimer.getAllThreadIds();
			startTimes = new HashMap<>(allThreadIds.length);
			for (long id : allThreadIds)
			{
				double threadElapsed = threadTimer.getThreadCpuTime(id) / NANOSECONDS_PER_SECOND;
				startTimes.put(id, threadElapsed);
			}
		}

		public double elapsedTime()
		{
			long[] allThreadIds = threadTimer.getAllThreadIds();
			double elapsed = 0;
			for (long id : allThreadIds)
			{
				double startTime = startTimes.getOrDefault(id, 0.0);
				double endTime = threadTimer.getThreadCpuTime(id) / NANOSECONDS_PER_SECOND;
				elapsed += endTime - startTime;
			}
			return elapsed;
		}
	}
}

