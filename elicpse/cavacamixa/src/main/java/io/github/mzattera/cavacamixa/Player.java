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
	private static List<Integer> buildDeck(DeckConfig cfg) {
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
		// TODO will it be faster using .subList() only (re-using cards)
		deck[0] = new ArrayList<>(cards.subList(0, 20));
		deck[1] = new ArrayList<>(cards.subList(20, 40));
		List<Integer> pile = new ArrayList<>(40);

		while (true) { // Game loop
			
			// TODO URGENT detect infinite games
			
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
				if (penalty != 0) { // Player was responding to a face card
					if (--penalty == 0) { // Player lost this hand
						player = ++player & 1;
						deck[player].addAll(pile);
						pile.clear();
						stats.handWon();
					}
				} else {
					player = ++player & 1;
				}
			}
		}
	}
}
