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
