package io.github.mzattera.cavacamixa;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import io.github.mzattera.util.FileUtil;

/**
 * Plays many games in parallel and saves the last configuration tried.
 * 
 * @author Massimiliano "Maxi" Zattera
 */
public class ParallelExecutor {

	public static final String SAVE_FILE_NAME = "cavacamixa_checkpoint.txt";

	private static final String LONGEST_FILE_NAME = "cavacamixa_longest_game.txt";

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

	// Next config to play
	private DeckConfig current = null;

	private GameStats longestGame = null;
	private final long batchSize;
	private long games = 0;

	private final File saveFolder;

	/**
	 * 
	 * @param saveFolder Folder where to save check point and longest game.
	 * @param batchSize  Size of a batch, after which check point is saved.
	 * @throws IOException If configuration folder cannot be read.
	 */
	public ParallelExecutor(File saveFolder, int batchSize) throws IOException {
		this.saveFolder = saveFolder;
		File saveCfg = new File(saveFolder, SAVE_FILE_NAME);
		if (saveCfg.exists()) {
			current = readCheckPoint(saveCfg);
		} else {
			current = new DeckConfig();
		}
		this.batchSize = batchSize;
	}

	private void writeCheckPoint(DeckConfig cfg) throws IOException {
		FileUtil.writeFile(new File(saveFolder, SAVE_FILE_NAME), cfg.toString());
	}

	private DeckConfig readCheckPoint(File saveCfg) throws IOException {
		return new DeckConfig(FileUtil.readFile(new File(saveFolder, SAVE_FILE_NAME)));
	}

	/**
	 * Invoked by runners when they want to start a new game.
	 * 
	 * @return Configuration of the game to try, or null if all games have been
	 *         tried already.
	 */
	public synchronized DeckConfig onStart() {
		if (games++ < batchSize) {
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

		if (gameStats.getDeckConfig().isInfinite()) {
			// Found an infinite game
			// TODO store this separately
			System.out.println("=== INFINITE GAME FOUND!!! ========================");
			System.out.println(gameStats.toString());
			System.out.println("===================================================");

		} else {

			if (longestGame == null)
				longestGame = gameStats;
			else if (gameStats.getCardsPlayed() > longestGame.getCardsPlayed())
				longestGame = gameStats;

			if (longestGame == gameStats) {
				try {
					System.out.println("Found longer game: " + gameStats);
					FileUtil.writeFile(new File(saveFolder, LONGEST_FILE_NAME), gameStats.toString());
				} catch (IOException e) {
					onError(gameStats.getDeckConfig(), e);
				}
			}
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
		System.err.println("================================");
		System.err.println("Error running this deck configuration: " + cfg + "\n");
		e.printStackTrace(System.err);
		System.err.println("================================");
		System.err.flush();

		System.exit(-1);
	}

	/**
	 * Runs forever, saving check points at each batch.
	 * 
	 * @throws IOException
	 */
	public void run() throws IOException {
		System.out.println("Resuming playing from deck configuration: " + current);
		while (true) {
			runBatch();
			writeCheckPoint(current);
		}
	}

	/**
	 * Runs this executor, playing one batch of games.
	 * 
	 * @param threads Number of threads to use for parallel execution. Use -1 to use
	 *                a thread per processor.
	 * 
	 * @return The non-infinite game with the longest duration.
	 */
	private GameStats runBatch() {
		return runBatch(-1);
	}

	/**
	 * Runs this executor, playing all the games it has to play.
	 * 
	 * @param threads Number of threads to use for parallel execution. Use -1 to use
	 *                a thread per processor.
	 * 
	 * @return The non-infinite game with the longest duration.
	 */
	private GameStats runBatch(int threads) {
		games=0;
		if (threads == -1)
			threads = Runtime.getRuntime().availableProcessors();
		ExecutorService ex = Executors.newFixedThreadPool(threads);
		for (int i = 0; i < threads; ++i)
			ex.execute(new Runner());
		ex.shutdown();

		try {
			ex.awaitTermination(Long.MAX_VALUE, TimeUnit.DAYS); // Waits for all threads to stop
			return longestGame;
		} catch (Exception e) {
			return null;
		}
	}
}
