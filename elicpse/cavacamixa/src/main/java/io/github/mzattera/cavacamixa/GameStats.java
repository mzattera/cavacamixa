package io.github.mzattera.cavacamixa;

/**
 * Holds statistics fro one game.
 * 
 * @author Massimiliano "Maxi" Zattera
 */
public class GameStats {

	private final DeckConfig deckConfig;

	/**
	 * @return The deck for this game
	 */
	public DeckConfig getDeckConfig() {
		return deckConfig;
	}

	private int cardsPlayed = 0;

	/**
	 * 
	 * @return Number of cards played.
	 */
	public int getCardsPlayed() {
		return cardsPlayed;
	}

	/**
	 * Signals a card was played.
	 * 
	 * @return the incremented number of cards played.
	 */
	public int cardPlayed() {
		return ++cardsPlayed;
	}

	private int penaltyCardsPlayed = 0;

	/**
	 * 
	 * @return Number of "penalty cards" (aces, 2s & 3s) played.
	 */
	public int getPenaltyCardsPlayed() {
		return penaltyCardsPlayed;
	}

	/**
	 * Signals a "penalty cards" (aces, 2s & 3s) was played.
	 * 
	 * @return the incremented number of face cards played
	 */
	public int penaltyCardPlayed() {
		return ++penaltyCardsPlayed;
	}

	private int hands = 0;

	/**
	 * 
	 * @return Number of "hands" won (1 hand is won when a player wins the cards on
	 *         the table).
	 */
	public int getHands() {
		return hands;
	}

	/**
	 * Signals a hand was won.
	 * 
	 * @return the incremented number of hands won.
	 */
	public int handWon() {
		return ++hands;
	}

	private int losingPlayer = 0;

	/**
	 * 
	 * @return The player who lost (0-1 - 0 being the player that starts the game).
	 */
	public int getLosingPlayer() {
		return losingPlayer;
	}

	/**
	 * 
	 * @return the winning player (0-1 - 0 being the player that starts the game).
	 */
	public int getWinningPlayer() {
		return (losingPlayer == 0 ? 1 : 0);
	}

	/**
	 * Signals that a player lost the game.
	 * 
	 * @param player the player who lost (0-1 - 0 being the player that starts the
	 *               game).
	 * @return the losing player.
	 */
	public int playerLost(int player) {
		return losingPlayer = player;
	}

	public GameStats(DeckConfig cfg) {
		this.deckConfig = cfg;
	}

	@Override
	public String toString() {
		return "GameStats [deckConfig=" + deckConfig + ", cardsPlayed=" + cardsPlayed + ", penaltyCardsPlayed="
				+ penaltyCardsPlayed + ", hands=" + hands + ", winningPlayer=" + getWinningPlayer() + "]";
	}
}
