/*
 * Copyright 2023-2025 Massimiliano "Maxi" Zattera
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

package io.github.mzattera.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Utility class for handling files.
 * 
 * @author Massimiliano "Maxi" Zattera
 *
 */
public final class FileUtil {

	private FileUtil() {
	}

	/**
	 * Reads content of a file, assumed to be a UTF-8 string.
	 * 
	 * @param fileName
	 * @return
	 * @throws IOException
	 */
	public static String readFile(String fileName) throws IOException {
		return readFile(new File(fileName));
	}

	/**
	 * Reads content of a file, assumed to be a UTF-8 string.
	 * 
	 * @param fileName
	 * @return
	 * @throws IOException
	 */
	public static String readFile(File f) throws IOException {
		Path path = f.toPath();
		StringBuilder content = new StringBuilder();
		try (BufferedReader br = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
			String line;
			while ((line = br.readLine()) != null) {
				content.append(line).append(System.lineSeparator());
			}
		}
		return content.toString();
	}

	/**
	 * Reads content of a stream, assuming it contains a UTF-8 string.
	 */
	public static String readStream(InputStream stream) throws IOException {
		StringBuilder content = new StringBuilder();
		try (InputStreamReader in = new InputStreamReader(stream, StandardCharsets.UTF_8);
				BufferedReader br = new BufferedReader(in)) {
			String line;
			while ((line = br.readLine()) != null) {
				content.append(line).append(System.lineSeparator());
			}
		}
		return content.toString();
	}

	/**
	 * Write text to given file, in UTF-8 encoding.
	 * 
	 * @param fileName
	 * @param text
	 * @throws IOException
	 * 
	 */
	public static void writeFile(String fileName, String text) throws IOException {
		writeFile(new File(fileName), text);
	}

	/**
	 * Write text to given file, in UTF-8 encoding.
	 * 
	 * @param fileName
	 * @param text
	 * @throws IOException
	 * 
	 */
	public static void writeFile(File file, String text) throws IOException {
		try (BufferedWriter writer = new BufferedWriter(
				new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8))) {
			writer.write(text);
			writer.flush();
		}
	}
}
