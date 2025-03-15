package io.github.mzattera.cavacamixa;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Plays many games in parallel and saves the last configuration tried.
 * 
 * @author Massimiliano "Maxi" Zattera
 */
public class ParallelExecutor {

	private class Runner implements Runnable {
		@Override
		public void run() {
			DeckConfig cfg = null;
			try {
				while ((cfg = onStart()) != null) { // Run till we have configs to test
					onFinish(Player.play(cfg));
				}
			} catch (Exception e) {
				if (cfg != null)
					onError(cfg, e);
			}
		}
	}

	private DeckConfig current = null;
	private GameStats longestGame = null;
	private final long maxGames;
	private long games = 0;

	public ParallelExecutor(DeckConfig first, long maxGames) {
		// TODO Read latest config we tested (checkpoint) and start from there
		current = first;
		this.maxGames = maxGames;
	}

	/**
	 * Invoked by runners when they want to start a new game.
	 * 
	 * @return Configuration of the game to try, or null if all games have been
	 *         tried already.
	 */
	public synchronized DeckConfig onStart() {
		if (games++ < maxGames) {
			DeckConfig result = current;
			current = current.next();
			return result;
		} else {
			return null;
		}
	}

	/**
	 * Invoked by runners when a game is finished.
	 * 
	 * @param gameStats Game statistics.
	 */
	public synchronized void onFinish(GameStats gameStats) {
		if (longestGame == null)
			longestGame = gameStats;
		else if (gameStats.getCardsPlayed() > longestGame.getCardsPlayed())
			longestGame = gameStats;

		if (longestGame == gameStats) {
			// TODO save this checkpoint
			// TODO if the game is infinite also print it out
			System.out.println(longestGame.toString());
		}
	}

	/**
	 * Invoked when an error occurs. It ends current run.
	 * 
	 * 
	 * @param cfg Configuration for the game
	 * @param e   Exception that occurred
	 */
	public synchronized void onError(DeckConfig cfg, Exception e) {
		System.out.println("================================");
		System.out.println("Error running this deck configuration: " + cfg + "\n");
		e.printStackTrace();
		System.out.println("================================");
		System.out.flush();

		System.exit(-1);
	}

	/**
	 * Runs this executor, playing all the games it has to play.
	 * 
	 * @param threads Number of threads to use for parallel execution. Use -1 to use
	 *                a thread per processor.
	 * 
	 * @return The non-infinite game with the longest duration.
	 */
	public GameStats run() {
		return run(-1);
	}

	/**
	 * Runs this executor, playing all the games it has to play.
	 * 
	 * @param threads Number of threads to use for parallel execution. Use -1 to use
	 *                a thread per processor.
	 * 
	 * @return The non-infinite game with the longest duration.
	 */
	public GameStats run(int threads) {
		if (threads == -1)
			threads = Runtime.getRuntime().availableProcessors();
		System.out.println("Threads: " + threads);
		ExecutorService ex = Executors.newFixedThreadPool(threads);
		for (int i = 0; i < threads; ++i)
			ex.execute(new Runner());
		ex.shutdown();

		try {
			ex.awaitTermination(Long.MAX_VALUE, TimeUnit.DAYS); // Waits for all threads to stop
			// TODO Save current, which will be the first config to try next time
			System.out.println("\nNext configuration to test: " + current + "\n");
			return longestGame;
		} catch (Exception e) {
			return null;
		}
	}

	public static void main(String[] args) {
		ParallelExecutor ex = new ParallelExecutor(new DeckConfig(), 50_000_000);
		GameStats stats = ex.run();
		System.out.println("================================");
		System.out.println("BEST configuration: " + stats + "\n");
	}
}
