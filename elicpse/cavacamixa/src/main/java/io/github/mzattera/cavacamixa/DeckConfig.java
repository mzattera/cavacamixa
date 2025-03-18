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

import java.util.ArrayList;
import java.util.List;

/**
 * Describes one deck; it shows where to insert the 12 "penalty cards" (aces, 2s
 * & 3s) inside a deck beginning with 28 nulls, to build the final deck.
 *
 * @author Massimiliano "Maxi" Zattera
 */
public class DeckConfig {

	// Deck containing only "nulls"
	private final static List<Integer> EMPTY_DECK = new ArrayList<>(40);
	static {
		for (int i = 0; i < 28; ++i)
			EMPTY_DECK.add(0);
	}

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

	public static DeckConfig fromDeck(String deck) {
		DeckConfig result = new DeckConfig();

		String t = deck.trim();
		if (t.length() != 41)
			throw new IllegalArgumentException("Invalid deck format: " + deck);
		t = t.replaceAll("\\-", "");
		if (t.length() != 40)
			throw new IllegalArgumentException("Invalid deck format: " + deck);

		StringBuilder sb = new StringBuilder(t);
		for (int seed = 3; seed >= 0; --seed) {
			for (char value = '3'; value >= '1'; --value) {
				int pos = sb.lastIndexOf(value + "");
				if (pos==-1)
					throw new IllegalArgumentException("Invalid deck format: " + deck);
				result.p[seed * 3 + value - '1'] = pos;
				sb.deleteCharAt(pos);
			}
		}

		return result;
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

	/**
	 * Builds a 40-card deck based on a given configuration.
	 * 
	 * @param cfg the deck configuration
	 * @return A 40-card deck
	 */
	public static List<Integer> buildDeck(DeckConfig cfg) {
		List<Integer> deck = new ArrayList<>(EMPTY_DECK);
		for (int seed = 0; seed < 4; ++seed) {
			for (int value = 1; value <= 3; ++value) {
				int i = cfg.get(seed, value);
				if (i == deck.size())
					deck.add(value);
				else
					deck.add(i, value);
			}
		}
		return deck;
	}

	/**
	 * Prints a deck.
	 */
	public static void printDeck(List<Integer> deck) {
		for (int i : deck)
			System.out.print(i + " ");
		System.out.println();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null)
			return false;

		if (obj instanceof DeckConfig) {
			DeckConfig other = (DeckConfig) obj;
			// TODO URGENT there are different configurations leading to same deck.....
			return buildDeck(this).equals(buildDeck(other));
		} else {
			return false;
		}
	}

	@Override
	public int hashCode() {
		return p.hashCode();
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

	public static void main(String args[]) {
		DeckConfig cfg = new DeckConfig("[22,15,22,15,5,23,3,0,0,0,0,0]");
		Deck deck = new Deck(buildDeck(cfg));
		System.out.println(deck.toString());
	}
}
