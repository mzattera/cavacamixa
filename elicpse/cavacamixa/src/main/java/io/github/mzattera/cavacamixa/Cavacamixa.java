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

/**
 * Main class to start the application from CLI.
 * 
 * @author Massimiliano "Maxi" Zattera
 */
public class Cavacamixa {

	public static final int BATCH_SIZE = 1_000_000_000;

	/**
	 * @param args
	 */
	public static void main(String[] args) throws IOException {
		try {
			// TODO play single game (from config or deck description) and print moves
			if (args.length < 2) {

				// Makes sure output folder exists and can be written
				File saveFolder;
				if (args.length == 0) {
					saveFolder = new File(".");
				} else {
					saveFolder = new File(args[0]);
				}
				if (!saveFolder.canWrite() || !saveFolder.isDirectory())
					throw new IOException("Cannot acccess folder: " + saveFolder.getCanonicalPath());

				System.out.println("Playing games forever. Save folder: " + saveFolder.getCanonicalPath() + "\n");

				// Runs games forever
				new ParallelExecutor(saveFolder, BATCH_SIZE).run();

			} else if (args.length == 2) {
				if (args[0].equals("-p")) { // Plays one game from a deck description

				}
			} else {
				printUsage();
				System.exit(-1);
			}
		} catch (Exception e) {
			e.printStackTrace(System.err);
			System.exit(-1);
		}
	}

	private static void printUsage() {
		System.out.println(Cavacamixa.class.getCanonicalName() + "<saveFolder>");
		System.out.println("\tPlays games forever saving longest game and recovery point in <saveFolder>.");
		System.out.println("\tIf <saveFolder> contains a recovery point, it starts playing from there.");
		System.out.println("\t<saveFolder> must exists and writable.");
	}
}
