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
 * This class can play a game of cavacamixa.
 * 
 * @author Massimiliano "Maxi" Zattera
 */
public class Player {

	// Deck containing only "nulls"
	private final static List<Integer> EMPTY_DECK = new ArrayList<>(40);
	static {
		for (int i = 0; i < 28; ++i)
			EMPTY_DECK.add(0);
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

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static GameStats play(DeckConfig cfg) {

		GameStats stats = new GameStats(cfg);
		List<Integer> cards = buildDeck(cfg);

		int player = 0;
		int penalty = 0;
		List[] deck = new ArrayList[2];
		deck[0] = new ArrayList<>(cards.subList(0, 20));
		deck[1] = new ArrayList<>(cards.subList(20, 40));
		List<Integer> pile = new ArrayList<>(40);

		// All configurations in the game so far; this is to detect infinite games
		List<int[]> stati = new ArrayList<>(1000);
		stati.add(toIntArray(deck[0], deck[1]));

		while (true) { // Game loop

			if (deck[player].size() == 0) {
				stats.playerLost(player);
				return stats;
			}

			// Play card
			int played = (int) deck[player].remove(0);
			pile.add(played);
			stats.cardPlayed();

			if (played != 0) { // Played a "penalty card"
				penalty = played;
				stats.penaltyCardPlayed();
				player = ++player & 1;
			} else { // Played normal card
				if (penalty != 0) { // Player was responding to a penalty card
					if (--penalty == 0) { // Player lost this hand
						player = ++player & 1;
						deck[player].addAll(pile);
						pile.clear();
						stats.handWon();

						// Check if we were already in this configuration
						int[] status = toIntArray(deck[0], deck[1]);
						if (stati.contains(status)) {
							// Yes, infinite game
							if (player == 0) {
								stats.getDeckConfig().isInfinite(true);
								return stats;
							}
						} else {
							// No, memorize this configuration
							stati.add(status);
						}

					}
				} else { // Was not responding to a penalty
					player = ++player & 1;
				}
			}
		}
	}

	private static int[] toIntArray(List<Integer> list1, List<Integer> list2) {
		int[] r = new int[41];
		int i = 0;
		for (int l : list1)
			r[i++] = l;
		r[i++] = '-';
		for (int l : list2)
			r[i++] = l;
		return r;
	}
}
