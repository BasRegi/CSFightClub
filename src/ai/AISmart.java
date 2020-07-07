package ai;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

import data.BattleData;
import data.Card;
import data.GameData;
import events.EventTrigger;
import events.EventUtil;

public class AISmart extends AIInterface {

	public AISmart(GameData gameData, EventTrigger eventTrigger) {
		super(gameData, eventTrigger);
	}

	GameData gameData = new GameData();
	BattleData battleData = gameData.getBattleData();

	public ArrayList<Card> sortByAttack(ArrayList<Card> deck) {

		for (int i = 0; i < deck.size(); i++) {
			for (int j = i + 1; j <= deck.size(); j++) {
				if (deck.get(i).getAttack() > deck.get(j).getAttack())
					Collections.swap(deck, i, j);
			}
		}

		return deck;

	}

	public ArrayList<Card> sortByDefence(ArrayList<Card> deck) {

		for (int i = 0; i < deck.size(); i++) {
			for (int j = i + 1; j <= deck.size(); j++) {
				if (deck.get(i).getDefence() > deck.get(j).getDefence())
					Collections.swap(deck, i, j);
			}
		}

		return deck;

	}

	public int getMyCredits() {

		int credits = battleData.myHP * 3; // 1 HP point -> 3 credits
		for (int i = 0; i < battleData.myHand.size(); i++)
			credits = credits + battleData.myHand.get(i).getAttack() * 3
					+ battleData.myHand.get(i).getDefence() * 3;

		return credits;

	}

	public int getOppoCredits() {

		int credits = battleData.oppoHP * 3; // 1 HP point -> 3 credits
		for (int i = 0; i < battleData.oppoHand.size(); i++)
			credits = credits + battleData.oppoHand.get(i).getAttack() * 3
					+ battleData.oppoHand.get(i).getDefence() * 3;

		return credits;

	}

	// evalState function will do myCredits/oppoCredits and according to
	// this ratio will use switch statement to determine the next best move

	@Override
	public void run() {

		while (!battleData.battleEnd) {
			EventUtil.sleep(50);

			if (battleData.oppoTurn) {

				ArrayList<Card> selectableOppoMinions = new ArrayList<Card>();
				for (int k = 0; k < battleData.myMinions.size(); k++)
					if (battleData.myMinions.get(k).selectable)
						selectableOppoMinions.add(battleData.myMinions.get(k));

				ArrayList<Card> defSelectableOppoMinions = sortByDefence(selectableOppoMinions);
				ArrayList<Card> attSelectableOppoMinions = sortByAttack(selectableOppoMinions);

				int myCredits = getMyCredits();
				int oppoCredits = getOppoCredits();
				double ratio = oppoCredits / myCredits;

				if (ratio < 1) {
					Integer bestCurrentCard = 0;
					getEventTrigger().aiInput("selectOppoMinion", bestCurrentCard.toString());
					defSelectableOppoMinions.remove(bestCurrentCard);
				} else {
					Integer bestCurrentCard = 0;
					getEventTrigger().aiInput("selectOppoMinion", bestCurrentCard.toString());
					attSelectableOppoMinions.remove(bestCurrentCard);
				}
			}
		}
	}
}
