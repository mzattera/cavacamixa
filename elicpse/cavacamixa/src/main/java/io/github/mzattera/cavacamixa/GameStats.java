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
 * Holds statistics for one game.
 * 
 * @author Massimiliano "Maxi" Zattera
 */
public class GameStats {

	private final Deck deck;

	/**
	 * @return The deck for this game
	 */
	public Deck getDeck() {
		return deck;
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

	private boolean infinite = false;

	/**
	 * 
	 * @return True if this is an infinite game.
	 */
	public boolean isInfinite() {
		return infinite;
	}

	/**
	 * Sets whether this game is infinite or not.
	 * 
	 * @param v True if this is an infinite game.
	 * @return v
	 */
	public boolean isInfinite(boolean v) {
		return infinite = v;
	}

	public GameStats(Deck cfg) {
		this.deck = cfg;
	}

	@Override
	public String toString() {
		return "GameStats [deckConfig=" + deck + ", cardsPlayed=" + cardsPlayed + ", penaltyCardsPlayed="
				+ penaltyCardsPlayed + ", hands=" + hands + ", winningPlayer=" + getWinningPlayer() + "]";
	}
}
