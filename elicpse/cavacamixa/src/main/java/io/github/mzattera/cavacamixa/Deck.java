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

/**
 * 
 */

/**
 * 
 */
package io.github.mzattera.cavacamixa;

import java.util.Arrays;
import java.util.List;

/**
 * A 40 cards deck (already shuffled).
 */
public class Deck {

	int[] cards = new int[40];

	/**
	 * Creates the "initial" deck.
	 */
	public Deck() {
		int index = 0;
		for (int i = 0; i < 28; i++)
			cards[index++] = 0;
		for (int i = 0; i < 4; i++)
			cards[index++] = 1;
		for (int i = 0; i < 4; i++)
			cards[index++] = 2;
		for (int i = 0; i < 4; i++)
			cards[index++] = 3;
	}

	/**
	 * Constructor from String representation.
	 * 
	 * @param deck
	 */
	public Deck(String deck) {
		deck = deck.trim();
		if (deck.length() != cards.length)
			throw new IllegalArgumentException("Invalid deck configuration: " + deck);
		for (int i = 0; i < cards.length; ++i)
			cards[i] = deck.charAt(i) - '0';
	}

	/**
	 * Copy constructor.
	 * 
	 * @param other
	 */
	public Deck(Deck other) {
		System.arraycopy(other.cards, 0, cards, 0, 40);
	}

	/**
	 * Legacy.
	 * 
	 * @param other
	 */
	// TODO REMOVE
	protected Deck(List<Integer> deck) {
		for (int i = 0; i < deck.size(); i++) {
		    cards[i] = deck.get(i);
		}
	}

	/**
	 * @return Next deck in the sequence of all possible deck combinations, or null
	 *         if this is already last deck.
	 */
	public Deck next() {

		Deck result = new Deck(this);

		// Finds biggest i such that deck[i] < deck[i + 1]
		int i = 38;
		while (i >= 0 && (result.cards[i] >= result.cards[i + 1]))
			i--;
		if (i < 0)
			return null;

		// Finds biggest j such that j > i and deck[j] > deck[i]
		int j = 39;
		while (result.cards[j] <= result.cards[i])
			j--;

		swap(result.cards, i, j);
		reverse(result.cards, i + 1, 39);

		return result;
	}

	private static void swap(int[] deck, int i, int j) {
		int temp = deck[i];
		deck[i] = deck[j];
		deck[j] = temp;
	}

	private static void reverse(int[] deck, int start, int end) {
		while (start < end) {
			swap(deck, start++, end--);
		}
	}

	/**
	 * 
	 * @return This deck as a list of integers.
	 */
	public List<Integer> toList() {
		return Arrays.stream(cards).boxed().toList();
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for (int i : cards)
			sb.append(i);
		return sb.toString();
	}
}