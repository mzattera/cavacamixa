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

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * This class can play a game of cavacamixa.
 * 
 * @author Massimiliano "Maxi" Zattera
 */
public class Player {

	/**
	 * Store status of the game.
	 */
	private static class Status {

		private final List<Integer> deck0, deck1;

		public Status(int player, List<Integer> deck0, List<Integer> deck1) {
			if (player == 0) {
				this.deck0 = new ArrayList<>(deck0);
				this.deck1 = new ArrayList<>(deck1);
			} else {
				this.deck0 = new ArrayList<>(deck1);
				this.deck1 = new ArrayList<>(deck0);
			}
		}

		@Override
		public boolean equals(Object o) {
			if (this == o)
				return true;
			if (!(o instanceof Status))
				return false;
			Status other = (Status) o;
			return deck0.equals(other.deck0) && deck1.equals(other.deck1);
		}

		@Override
		public int hashCode() {
			return Objects.hash(deck0, deck1);
		}
	}

	/**
	 * Plays a game using given deck.
	 * 
	 * @param d
	 * @return
	 */
	public static GameStats play(Deck d) {
		return play(d, null);
	}

	/**
	 * Plays a game using given deck.
	 * 
	 * @param deck
	 * @param out  If not null, prints game moves using this stream.
	 * @return
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static GameStats play(Deck d, PrintStream out) {

		GameStats stats = new GameStats(d);

		// Check whether one player has only 0; in this case we skip, as we know longest
		// game will be less than 40 cards
		int i = 0;
		for (; i < 20; ++i)
			if (d.cards[i] != 0)
				break;
		if (i == 20)
			return stats;
		for (i = 20; i < 40; ++i)
			if (d.cards[i] != 0)
				break;
		if (i == 00)
			return stats;

		List<Integer> cards = d.toList();

		int player = 0;
		int penalty = 0;
		List[] deck = new ArrayList[2];
		deck[0] = new ArrayList<>(cards.subList(0, 20));
		deck[1] = new ArrayList<>(cards.subList(20, 40));
		List<Integer> pile = new ArrayList<>(40);

		// All configurations in the game so far; this is to detect infinite games
		Set<Status> stati = new HashSet<>();
		stati.add(new Status(player, deck[0], deck[1]));

		while (true) { // Game loop

			try {
				if (player == 0)
					out.println(player + " > " + deck[0] + " - " + deck[1] + " - " + pile);
				else
					out.println(player + " > " + deck[1] + " - " + deck[0] + " - " + pile);
			} catch (NullPointerException e) {
				// Faster than checking stream
			}

			if (deck[player].size() == 0) {
				stats.playerLost(player);
				return stats;
			}

			if (stati.size() > 5000)
				System.exit(-1);

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
						Status status = new Status(player, deck[0], deck[1]);
						if (stati.contains(status)) {
							stats.isInfinite(true);
							return stats;
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
}
