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

/**
 * Describes one deck; it shows where to insert the 12 "penalty cards" (aces, 2s
 * & 3s) inside a deck beginning with 28 nulls, to build the final deck.
 *
 * @author Massimiliano "Maxi" Zattera
 */
public class DeckConfig {

	// Insertion points for the "face" cards
	private int[] p = new int[12];

	/**
	 * @param seed  0-3 (bastoni, denari, coppe, spade)
	 * @param value 1-3 (card face value 1=ace)
	 * @return Where to insert given card in the deck
	 */
	public int get(int seed, int value) {
		return p[seed * 3 + value - 1];
	}

	private boolean isInfinite = false;

	/**
	 * 
	 * @return True if this configuration defines an infinite game.
	 */
	public boolean isInfinite() {
		return isInfinite;
	}

	/**
	 * 
	 * @param isInfinite True to mark this configuration as infinite game.
	 * 
	 * @return True if this configuration defines an infinite game.
	 */
	public boolean isInfinite(boolean isInfinite) {
		return (this.isInfinite = isInfinite);
	}

	public DeckConfig() {
	}

	/**
	 * Constructor from String representation.
	 * 
	 * @param cfg
	 */
	public DeckConfig(String cfg) {
		cfg = cfg.trim();
		if (!cfg.startsWith("[") || !cfg.endsWith("]"))
			throw new IllegalArgumentException("Invalid deck configuration: " + cfg);
		String[] s = cfg.substring(1, cfg.length() - 1).split(",");
		if (s.length != p.length)
			throw new IllegalArgumentException("Invalid deck configuration: " + cfg);
		for (int i = 0; i < p.length; ++i)
			p[i] = Integer.parseInt(s[i]);
	}

	/**
	 * Call this repeatedly to obtain all possible decks.
	 *
	 * @return Next variation of a deck, or null if there is no next version
	 */
	public DeckConfig next() {
		DeckConfig result = new DeckConfig();

		for (int i = 0; i < 12; ++i) {
			if (p[i] < (28 + i)) { // This card can be put in a later position in the deck; move it
				result.p[i] = p[i] + 1;
				for (++i; i < 12; ++i) // Copy rest and exit
					result.p[i] = p[i];
				return result;
			} else { // Reset position and move another card
				result.p[i] = 0;
			}
		}
		return null; // Cannot move any card
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append('[');
		for (int i = 0; i < 11; ++i)
			sb.append(p[i]).append(',');
		sb.append(p[11]).append(']');
		return sb.toString();
	}
}
