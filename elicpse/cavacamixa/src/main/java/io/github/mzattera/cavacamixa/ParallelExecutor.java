/*
 * Copyright 2025 Massimiliano "Maxi" Zattera
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.github.mzattera.cavacamixa;

import java.io.File;
import java.io.IOException;
import java.util.UUID;
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
			Deck cfg = null;
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
	private Deck current = new Deck();

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
		File saveFile = new File(saveFolder, SAVE_FILE_NAME);
		if (saveFile.exists()) {
			readCheckPoint(saveFile);
		}
		this.batchSize = batchSize;
	}

	private void writeCheckPoint() throws IOException {
		FileUtil.writeFile(new File(saveFolder, SAVE_FILE_NAME), current + "\n" + longestGame.getDeck());
		System.out.println("Checkpoint...");
	}

	private void readCheckPoint(File saveFile) throws IOException {
		String cp = FileUtil.readFile(saveFile);
		String[] cpp = cp.trim().split("\\n");
		if (cpp.length != 2)
			throw new IllegalArgumentException("Invalid checkpoint file");
		current = new Deck(cpp[0]);
		longestGame = Player.play(new Deck(cpp[1]));
	}

	/**
	 * Invoked by runners when they want to start a new game.
	 * 
	 * @return Configuration of the game to try, or null if all games have been
	 *         tried already.
	 */
	public synchronized Deck onStart() {
		if (games++ < batchSize) {
			if (current == null)
				onError(null, new IllegalArgumentException("No other decks to try!"));
			Deck result = current;
			current = current.next();
			return result;
		} else { // Batch completed
			return null;
		}
	}

	/**
	 * Invoked by runners when a game is finished.
	 * 
	 * @param stats Game statistics.
	 */
	public synchronized void onFinish(GameStats stats) {

		if (stats.isInfinite()) {
			// Found an infinite game
			System.out.println("=== INFINITE GAME FOUND!!! ========================");
			System.out.println(stats.toString());
			System.out.println("===================================================");
			try {
				FileUtil.writeFile(new File(saveFolder, "cavacamixa_infinite_game"+UUID.randomUUID()+".txt"), stats.toString());
			} catch (IOException e) {
				System.err.println("Cannot save, continuing...");
			}

		} else {

			if (longestGame == null)
				longestGame = stats;
			else if (stats.getCardsPlayed() > longestGame.getCardsPlayed())
				longestGame = stats;

			if (longestGame == stats) {
				try {
					System.out.println("Found longer game: " + stats);
					FileUtil.writeFile(new File(saveFolder, LONGEST_FILE_NAME), stats.toString());
				} catch (IOException e) {
					onError(stats.getDeck(), e);
				}
			}
		}
	}

	/**
	 * Invoked when an error occurs. It ends current run.
	 * 
	 * 
	 * @param deck Deck used in the game
	 * @param e    Exception that occurred
	 */
	public synchronized void onError(Deck deck, Exception e) {
		System.err.println("================================");
		System.err.println("Error running this deck configuration: " + deck + "\n");
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
		System.out.println("longest game so far: " + longestGame);
		System.out.println();
		while (true) {
			runBatch();
			writeCheckPoint();
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
		games = 0;
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
