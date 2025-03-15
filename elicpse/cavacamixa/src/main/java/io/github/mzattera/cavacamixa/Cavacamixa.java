/**
 * 
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

	public static final int BATCH_SIZE = 3;

	/**
	 * @param args
	 */
	public static void main(String[] args) throws IOException {
		try {
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

				System.out.println("Playing games forever. Save folder: " + saveFolder.getCanonicalPath());
				
				// Runs games forever
				while (true) {					
					ParallelExecutor ex = new ParallelExecutor (saveFolder,BATCH_SIZE);
					while (true) {
						ex.run();
					}					
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
