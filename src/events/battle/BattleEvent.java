package events.battle;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

import data.BattleData;
import data.Card;
import data.DataManager;
import data.Deck;
import data.GameData;
import events.Event;
import events.EventSource;
import events.EventUtil;
import events.SceneEvent;
import events.battle.BattleEffect;


public class BattleEvent extends SceneEvent {
	private GameData gameData;
	private DataManager dm;
	public BattleData batData;
	public EventSource firstPlayer = null;
	public Card attackCard = null;
	public Card targetMinion = null;
	public boolean oppoTarget = false;
	public boolean myTarget = false;
	public boolean waitingForTarget = false;
	public Deck myDeck, oppoDeck;
	public boolean myWin = false;
	public boolean oppoWin = false;
	public String target = "";
	public Random r = new Random();
	public boolean myMinion = false;
	public boolean oppoMinion = false;
	public int targetIndex;
	public boolean isMinion = false;
	public int attackCardIndex;
	public boolean blockThere = false;
	public Card waitingCard = null;
	public boolean draw = false;
	public boolean testFlag = false;
	
	/**
	 * Constructs a BattleEvent object
	 * @param gameData the initial data for each game
	 * @param dm the data manager
	 */
	public BattleEvent(GameData gameData, DataManager dm) {
		this.gameData = gameData;
		this.dm = dm;
		this.batData = gameData.getBattleData();
		batData.myTurn = false;
		batData.oppoTurn = false;
	}
	
	/**
	 * Constructs a BattleEvent object
	 * @param gameData the initial data for each game
	 * @param player the data manager
	 */
	public BattleEvent(GameData gameData, EventSource player) {
		this.gameData = gameData;
		this.batData = gameData.getBattleData();
		this.firstPlayer = player;
	}
	
	public void start() {
		Event event;
		initBattleData();
		if (batData.single) {
			int turn = r.nextInt(2);
			if (turn == 0) {
				gameData.meFirst = true;
			} else {
				gameData.meFirst = false;
			}
			gameData.deckSeed1 = r.nextLong();
			gameData.deckSeed2 = r.nextLong();
		}
		if (gameData.meFirst == true) {
			dm.loadDeck(0, 2);
			dm.loadDeck(1, 1);
		} else {
			dm.loadDeck(0, 1);
			dm.loadDeck(1, 2);
		}
		myDeck = batData.getMyDeck();
		oppoDeck = batData.getoppoDeck();
		if (!testFlag) {
			randomizeDeck(myDeck, gameData.deckSeed1);
			randomizeDeck(oppoDeck, gameData.deckSeed2);
		} else {
			for(int i = 0; i < myDeck.size(); i++) {
				if (i - 1 >= 0) {
					if (myDeck.getCard(i - 1).getName().equals(myDeck.getCard(i).getName())) {
						myDeck.removeCard(i);
						i--;
					}
				}
			}
			
			int val;
			for(int i = 1; i < myDeck.getCards().size(); i++) {
				val = myDeck.getCard(i).getCost();
				for(int j = 0; j < i; j++) {
					if(val < myDeck.getCard(j).getCost()) {
						Collections.swap(myDeck.getCards(), i, j);
						val = myDeck.getCard(i).getCost();
					}
				}
			}
			
			for(int i = 0; i < oppoDeck.size(); i++) {
				if (i - 1 >= 0) {
					if (oppoDeck.getCard(i - 1).getName().equals(oppoDeck.getCard(i).getName())) {
						oppoDeck.removeCard(i);
						i--;
					}
				}
			}
			
			for(int i = 1; i < oppoDeck.getCards().size(); i++) {
				val = oppoDeck.getCard(i).getCost();
				for(int j = 0; j < i; j++) {
					if(val < oppoDeck.getCard(j).getCost()) {
						Collections.swap(oppoDeck.getCards(), i, j);
						val = oppoDeck.getCard(i).getCost();
					}
				}
			}
		}
		//sort the random ai's deck in order of small to large cost
		//so that it is slightly more likely to use minions as affordable
		if(batData.single && !testFlag) {
			//full order
//			int val;
//			for(int i = 1; i < oppoDeck.getCards().size(); i++) {
//				val = oppoDeck.getCard(i).getCost();
//				for(int j = 0; j < i; j++) {
//					if(val < oppoDeck.getCard(j).getCost()) {
//						Collections.swap(oppoDeck.getCards(), i, j);
//						val = oppoDeck.getCard(i).getCost();
//					}
//				}
//			}
//			
			//Slightly random shuffle order
			ArrayList<Card> top = new ArrayList<Card>();
			ArrayList<Card> middle = new ArrayList<Card>();
			ArrayList<Card> bottom = new ArrayList<Card>();
			for(int i = 0; i < oppoDeck.getCards().size();i++) {
				if(oppoDeck.getCard(i).getCost() < 3) {
					top.add(oppoDeck.getCard(i));
				} else if(oppoDeck.getCard(i).getCost() > 6) {
					bottom.add(oppoDeck.getCard(i));
				} else {
					middle.add(oppoDeck.getCard(i));
				}
			}
			top.addAll(middle);
			top.addAll(bottom);
			oppoDeck = new Deck(oppoDeck.getDeckID(),top);
			
			System.out.println(oppoDeck.getCards());
		}
		
		drawCard(myDeck, batData.myHand, 3, EventSource.Player);
		drawCard(oppoDeck, batData.oppoHand, 3, EventSource.Opponent);

		if (gameData.meFirst == true) {
			oppoRoundMana(0);
			myRoundStart(EventSource.Player);
			myRoundMana(1);
			myRoundDraw(EventSource.Player);
			myRoundAction();
			batData.myTurn = true;
		} else {
			myRoundMana(0);
			oppoRoundStart(EventSource.Opponent);
			oppoRoundMana(1);
			oppoRoundDraw(EventSource.Opponent);
			oppoRoundAction();
			batData.oppoTurn = true;
		}

		while (!gameData.sysGameEnd && gameData.currentScene == GameData.BATTLE_PAGE) {
			EventUtil.sleep(100);
			event = takeEvent();
			if (event != null) {
				if (event.getMessage().equals("giveUp")) {
					giveUp(event.getEventSource());
					break;
				} else if (batData.myTurn) {
					if (event.getEventSource() == EventSource.Player) {
						if (event.getMessage().equals("selectCard")) {
							selectCard(event.getArgs()[0], event.getEventSource());
						} else if (event.getMessage().equals("selectMyMinion")) {
							selectMyMinion(event.getArgs()[0], event.getEventSource());
						} else if (event.getMessage().equals("selectOppoMinion")) {
							selectOppoMinion(event.getArgs()[0], event.getEventSource());
						} else if (event.getMessage().equals("selectMyFace")) {
							selectMyFace(event.getEventSource());
						} else if (event.getMessage().equals("selectOppoFace")) {
							selectOppoFace(event.getEventSource());
						} else if (event.getMessage().equals("nextRound")) {
							nextRound(event.getEventSource());
						}
					}
				} else if (batData.oppoTurn) {
					if (event.getEventSource() == EventSource.Opponent) {
						if (event.getMessage().equals("selectCard")) {
							selectCard(event.getArgs()[0], event.getEventSource());
						} else if (event.getMessage().equals("selectMyMinion")) {
							selectMyMinion(event.getArgs()[0], event.getEventSource());
						} else if (event.getMessage().equals("selectOppoMinion")) {
							selectOppoMinion(event.getArgs()[0], event.getEventSource());
						} else if (event.getMessage().equals("selectMyFace")) {
							selectMyFace(event.getEventSource());
						} else if (event.getMessage().equals("selectOppoFace")) {
							selectOppoFace(event.getEventSource());
						} else if (event.getMessage().equals("nextRound")) {
							nextRound(event.getEventSource());
						}
					}
				}
			}
		}
	}
	
	/**
	 * Given a deck, shuffle it deterministically
	 * @param deck the deck to be randomized
	 * @param seed the seed used for randomization
	 */
	private void randomizeDeck(Deck deck, long seed) {
		Collections.shuffle(deck.getCards(), new Random(seed));
	}
	
	/**
	 * Resets the battleData to default values
	 */
	private void initBattleData() {
		batData.myHP = 40;
		batData.myMana = 0;
		batData.myMaxTurnMana = 0;
		batData.myConcede = false;
		batData.myFaceSelectable = false;
		batData.myTurn = false;
		batData.myHand = new ArrayList<Card>();
		batData.myMinions = new ArrayList<Card>();
		batData.myFatigue = 1;
		batData.battleEnd = false;
		batData.turn = 1;
		batData.waitingForTarget = false;

		batData.round = 0;
		batData.startOfTurn = new ArrayList<BattleEffect>();
		batData.endOfTurn = new ArrayList<BattleEffect>();

		batData.oppoHP = 40;
		batData.oppoMana = 0;
		batData.oppoMaxTurnMana = 0;
		batData.oppoConcede = false;
		batData.oppoFaceSelectable = false;
		batData.oppoTurn = false;
		batData.oppoHand = new ArrayList<Card>();
		batData.oppoMinions = new ArrayList<Card>();
		batData.oppoFatigue = 1;
	}
	
	/**
	 * makes the selectable opponent minions highlighted
	 */
	private void oppoRoundAction() {
		for (Card card : batData.oppoHand) {
			if (batData.oppoMana >= card.getCost())
				card.selectable = true;
		}
		for (Card minion : batData.oppoMinions)
			minion.selectable = true;
	}
	
	/**
	 * makes the selectable player minions highlighted
	 */
	private void myRoundAction() {
		if (gameData.debug)
			System.out.println("::I can move now");
		for (Card card : batData.myHand) {
			if (batData.myMana >= card.getCost())
				card.selectable = true;
		}
		for (Card minion : batData.myMinions)
			minion.selectable = true;
	}

	/**
	 * Draw a card from the opponent deck to their hand
	 * @param event the event source
	 */
	private void oppoRoundDraw(EventSource event) {
		drawCard(batData.oppoDeck, batData.oppoHand, 1, event);
	}

	/**
	 * Draw a card from the player deck to their hand
	 * @param event the event source
	 */
	private void myRoundDraw(EventSource event) {
		if (gameData.debug)
			System.out.println("::I draw 1 card");
		drawCard(batData.myDeck, batData.myHand, 1, event);
	}
	
	/**
	 * Increase the opponents mana pool
	 * @param i a given integer
	 */
	private void oppoRoundMana(int i) {
		if (gameData.debug)
			System.out.println("::Opponent gets " + i + " mana");
		if (batData.oppoMaxTurnMana < batData.maxMana) {
			batData.oppoMaxTurnMana += i;
		}
		batData.oppoMana = batData.oppoMaxTurnMana;

	}

	/**
	 * Increase the players mana pool
	 * @param i a given integer
	 */
	private void myRoundMana(int i) {
		if (gameData.debug)
			System.out.println("::I get " + i + " mana");
		if (batData.myMaxTurnMana < batData.maxMana) {
			batData.myMaxTurnMana += i;
		}
		batData.myMana = batData.myMaxTurnMana;
	}

	/**
	 * the start of turn effects for the given event source
	 * @param event the event source
	 */
	private void oppoRoundStart(EventSource event) {
		if (gameData.debug)
			System.out.println("::Opponent turn start!");
		startTurnEffect(event);

	}

	/**
	 * the start of turn effects for the given event source
	 * @param event the event source
	 */
	private void myRoundStart(EventSource event) {
		if (gameData.debug)
			System.out.println("::My turn start!");
		startTurnEffect(event);
	}

	/**
	 * the end of turn effect call for the given source
	 * @param event the event source
	 */
	private void oppoRoundEnd(EventSource event) {
		for (Card card : batData.oppoHand)
			card.selectable = false;
		for (Card minion : batData.oppoMinions)
			minion.selectable = false;
		endTurnEffect(event);

		if (gameData.debug)
			System.out.println("::Opponent round end");
	}

	/**
	 * the end of turn effect call for the given source
	 * @param event the event source
	 */
	private void myRoundEnd(EventSource event) {
		for (Card card : batData.myHand)
			card.selectable = false;
		for (Card minion : batData.myMinions)
			minion.selectable = false;
		endTurnEffect(event);

		if (gameData.debug)
			System.out.println("::My round end");
	}

	/**
	 * the end of turn effect method
	 * @param event the event source
	 */
	private void endTurnEffect(EventSource event) {
		if (batData.endOfTurn.size() > 0) {
			for (int i = 0; i < batData.endOfTurn.size(); i++) {
				if (event == batData.endOfTurn.get(i).getEventSource()) {
					getEffect(batData.endOfTurn.get(i).getCard(), event);
				}
			}
		}

	}

	/**
	 * the start of turn effect method
	 * @param event the event source
	 */
	private void startTurnEffect(EventSource event) {
		if (batData.startOfTurn.size() > 0) {
			for (int i = 0; i < batData.startOfTurn.size(); i++) {
				if (event == batData.startOfTurn.get(i).getEventSource()) {
					getEffect(batData.startOfTurn.get(i).getCard(), event);
				}
			}
		}

	}

	/**
	 * Runs the code behind each effect in question if it matches
	 * @param card the card the effect belongs to
	 * @param event the event source
	 */
	private void getEffect(Card card, EventSource event) {
		BattleEffect effect = null;
		switch (card.logicalEffect) {
		case "":
			break;
		case "deal X damage to a random enemy minion at the end of your turn":
			card.logicalEffect = "deal X damage to a random enemy minion";
			effect = new BattleEffect(card, card.logicalEffect, event);
			batData.endOfTurn.add(effect);
			break;
		case "deal X damage":
			if (!waitingForTarget) {
				waitingForTarget = true;
				batData.waitingForTarget = true;
			} else {
				if (target.equals("MyFace")) {
					myHealthCheck(card.quantity1);
					target = "";
				} else if (target.equals("OppoFace")) {
					oppoHealthCheck(card.quantity1);
					target = "";
				} else {
					if (oppoMinion) {
						dealMinionDamage(targetMinion, targetIndex, card.quantity1, "Op");
					} else if (myMinion) {
						dealMinionDamage(targetMinion, targetIndex, card.quantity1, "My");
					}
				}
				waitingForTarget = false;
				batData.waitingForTarget = false;
			}
			break;
		case "gain X/X at the end of your turn":
			card.logicalEffect = "gain X/X";
			effect = new BattleEffect(card, card.logicalEffect, event);
			batData.endOfTurn.add(effect);
			break;
		case "gain X/X at the start of each turn":
			card.logicalEffect = "gain X/X";
			effect = new BattleEffect(card, card.logicalEffect, event);
			batData.startOfTurn.add(effect);
			break;
		case "gain X/X":
			giveMinionBuff(card, card.quantity1, card.quantity2);
			break;
		case "nullify a minion":
			if (!waitingForTarget) {
				if (batData.myMinions.size() > 0 || batData.oppoMinions.size() > 0) {
					waitingForTarget = true;
					batData.waitingForTarget = true;
					isMinion = true;
				}
			} else {
				targetMinion.setEffect("--NULLIFIED-- ");
				targetMinion.logicalEffect = "";
				removeFromEffectQ(card);
				waitingForTarget = false;
				batData.waitingForTarget = false;
			}
			break;
		case "deal X damage to a random enemy minion ":
			if (event == EventSource.Player) {
				if (batData.oppoMinions.size() > 0) {
					int randNum = r.nextInt(batData.oppoMinions.size());
					dealMinionDamage(batData.oppoMinions.get(randNum), randNum, card.quantity1, "Op");
				}
			} else {
				if (batData.myMinions.size() > 0) {
					int randNum = r.nextInt(batData.myMinions.size());
					dealMinionDamage(batData.myMinions.get(randNum), randNum, card.quantity1, "My");
				}
			}
			break;
		case "give X/X to a minion":
			if (!waitingForTarget) {
				if (batData.myMinions.size() > 0 || batData.oppoMinions.size() > 0) {
					waitingForTarget = true;
					batData.waitingForTarget = true;
					isMinion = true;
				}
			} else {
				giveMinionBuff(targetMinion, card.quantity1, card.quantity2);
				waitingForTarget = false;
				batData.waitingForTarget = false;
			}
			break;
		case "give X/X to a friendly minion":
			if (!waitingForTarget) {
				if (event == EventSource.Player) {
					if (batData.myMinions.size() > 0) {
						myMinion = true;
						waitingForTarget = true;
						batData.waitingForTarget = true;
						isMinion = true;
					}
				} else {
					if (batData.oppoMinions.size() > 0) {
						oppoMinion = true;
						waitingForTarget = true;
						batData.waitingForTarget = true;
						isMinion = true;
					}
				}
			} else {
				giveMinionBuff(targetMinion, card.quantity1, card.quantity2);
				waitingForTarget = false;
				batData.waitingForTarget = false;
			}
			break;
		case "destroy all minions":
			if (batData.myMinions.size() > 0) {
				for (int i = 0; i < batData.myMinions.size(); i++) {
					targetMinion = batData.myMinions.get(i);
					batData.myMinions.remove(i);
					if (targetMinion.keyword1 == Card.EFFECT_DEADLINE
							|| targetMinion.keyword2 == Card.EFFECT_DEADLINE) {
						getEffect(targetMinion, EventSource.Player);
					}
					targetMinion.selectable = false;
				}
			}
			if (batData.oppoMinions.size() > 0) {
				for (int i = 0; i < batData.oppoMinions.size(); i++) {
					targetMinion = batData.oppoMinions.get(i);
					batData.oppoMinions.remove(i);
					if (targetMinion.keyword1 == Card.EFFECT_DEADLINE
							|| targetMinion.keyword2 == Card.EFFECT_DEADLINE) {
						getEffect(targetMinion, EventSource.Opponent);
					}
					targetMinion.selectable = false;
				}
			}
			break;
		case "draw X cards":
			if (event == EventSource.Player) {
				drawCard(batData.myDeck, batData.myHand, card.quantity1, event);
			} else {
				drawCard(batData.oppoDeck, batData.oppoHand, card.quantity1, event);
			}
			break;
		case "draw a card":
			if (event == EventSource.Player) {
				drawCard(batData.myDeck, batData.myHand, 1, event);
			} else {
				drawCard(batData.oppoDeck, batData.oppoHand, 1, event);
			}
			break;
		case "deal X damage to your opponent":
			if (event == EventSource.Player) {
				oppoHealthCheck(card.quantity1);
			} else {
				myHealthCheck(card.quantity1);
			}
			break;
		case "restore X health to all friendly minions":
			if (event == EventSource.Player) {
				if (batData.myMinions.size() > 0) {
					for (int i = 0; i < batData.myMinions.size(); i++) {
						restoreMinionHealth(batData.myMinions.get(i), card.quantity1);
					}
				}
			} else {
				if (batData.oppoMinions.size() > 0) {
					for (int i = 0; i < batData.oppoMinions.size(); i++) {
						restoreMinionHealth(batData.myMinions.get(i), card.quantity1);
					}
				}
			}
			break;
		case "restore X health to all friendly minions at the end of your turn":
			card.logicalEffect = "restore X health to all friendly minions";
			effect = new BattleEffect(card, card.logicalEffect, event);
			batData.endOfTurn.add(effect);
			break;
		case "nullify all enemy minions":
			if (event == EventSource.Player) {
				if (batData.oppoMinions.size() > 0) {
					for (int i = 0; i < batData.oppoMinions.size(); i++) {
						batData.oppoMinions.get(i).setEffect("--NULLIFIED-- ");
						batData.oppoMinions.get(i).logicalEffect = "";
						removeFromEffectQ(card);
					}
				}
			} else {
				if (batData.myMinions.size() > 0) {
					for (int i = 0; i < batData.myMinions.size(); i++) {
						batData.myMinions.get(i).setEffect("--NULLIFIED-- ");
						batData.myMinions.get(i).logicalEffect = "";
						removeFromEffectQ(card);
					}
				}
			}
			break;
		case "increase the mana pool by X for this turn":
			if (event == EventSource.Player) {
				if (batData.maxMana > batData.myMana + card.quantity1) {
					batData.myMana = batData.myMana + card.quantity1;
				} else {
					batData.myMana = batData.maxMana;
				}
			} else {
				if (batData.maxMana > batData.oppoMana + card.quantity1) {
					batData.oppoMana = batData.oppoMana + card.quantity1;
				} else {
					batData.oppoMana = batData.maxMana;
				}
			}
			break;
		case "restore X health to all minions":
			if (batData.myMinions.size() > 0) {
				for (int i = 0; i < batData.myMinions.size(); i++) {
					restoreMinionHealth(batData.myMinions.get(i), card.quantity1);
				}
			}
			if (batData.oppoMinions.size() > 0) {
				for (int i = 0; i < batData.oppoMinions.size(); i++) {
					restoreMinionHealth(batData.myMinions.get(i), card.quantity1);
				}
			}
			break;
		case "swap attack/health of a minion":
			if (!waitingForTarget) {
				if (batData.myMinions.size() > 0 || batData.oppoMinions.size() > 0) {
					waitingForTarget = true;
					batData.waitingForTarget = true;
					isMinion = true;
				}
			} else {
				int x = targetMinion.getMinion().getAttack();
				int y = targetMinion.getMinion().getDefence();
				targetMinion.getMinion().setAttack(y);
				targetMinion.getMinion().setDefence(x);
				if (targetMinion.getMinion().isDestroyed()) {
					if (oppoMinion) {
						batData.oppoMinions.remove(targetIndex);
					} else {
						batData.myMinions.remove(targetIndex);
					}
					if (targetMinion.keyword1 == Card.EFFECT_DEADLINE
							|| targetMinion.keyword2 == Card.EFFECT_DEADLINE) {
						getEffect(targetMinion, EventSource.Opponent);
					}
					targetMinion.selectable = false;
				}
				waitingForTarget = false;
				batData.waitingForTarget = false;
			}
			break;
		case "restore X health":
			if (!waitingForTarget) {
				waitingForTarget = true;
				batData.waitingForTarget = true;
			} else {
				if (target.equals("MyFace")) {
					if (batData.maxHP < (batData.myHP + card.quantity1)) {
						batData.myHP = batData.maxHP;
					} else {
						batData.myHP += card.quantity1;
					}
					target = "";
				} else if (target.equals("OppoFace")) {
					if (batData.maxHP < (batData.oppoHP + card.quantity1)) {
						batData.oppoHP = batData.maxHP;
					} else {
						batData.oppoHP += card.quantity1;
					}
					target = "";
				} else {
					restoreMinionHealth(targetMinion, card.quantity1);
				}
				waitingForTarget = false;
				batData.waitingForTarget = false;
			}
			break;
		case "deal X damage to all minions":
			if (batData.myMinions.size() > 0) {
				for (int i = 0; i < batData.myMinions.size(); i++) {
					if (dealMinionDamage(batData.myMinions.get(i), i, card.quantity1, "My")) {
						i--;
					}
				}
			}
			if (batData.oppoMinions.size() > 0) {
				for (int i = 0; i < batData.oppoMinions.size(); i++) {
					if (dealMinionDamage(batData.oppoMinions.get(i), i, card.quantity1, "Op")) {
						i--;
					}
				}
			}
			break;
		case "deal X damage to all enemy minions":
			if (event == EventSource.Player) {
				if (batData.oppoMinions.size() > 0) {
					for (int i = 0; i < batData.oppoMinions.size(); i++) {
						if (dealMinionDamage(batData.oppoMinions.get(i), i, card.quantity1, "Op")) {
							i--;
						}
					}
				}
			} else {
				if (batData.myMinions.size() > 0) {
					for (int i = 0; i < batData.myMinions.size(); i++) {
						if (dealMinionDamage(batData.myMinions.get(i), i, card.quantity1, "My")) {
							i--;
						}
					}
				}
			}
			break;
		case "give X/X to all minions":
			if (batData.myMinions.size() > 0) {
				for (int i = 0; i < batData.myMinions.size(); i++) {
					giveMinionBuff(batData.myMinions.get(i), card.quantity1, card.quantity2);
				}
			}
			if (batData.oppoMinions.size() > 0) {
				for (int i = 0; i < batData.oppoMinions.size(); i++) {
					giveMinionBuff(batData.oppoMinions.get(i), card.quantity1, card.quantity2);
				}
			}
			break;
		case "restore X health to a minion":
			if (!waitingForTarget) {
				if (batData.myMinions.size() > 0 || batData.oppoMinions.size() > 0) {
					waitingForTarget = true;
					batData.waitingForTarget = true;
					isMinion = true;
				}
			} else {
				restoreMinionHealth(targetMinion, card.quantity1);
				waitingForTarget = false;
				batData.waitingForTarget = false;
			}
			break;
		case "restore X health to a friendly minion":
			if (!waitingForTarget) {
				if (event == EventSource.Player) {
					if (batData.myMinions.size() > 0) {
						myMinion = true;
						waitingForTarget = true;
						batData.waitingForTarget = true;
						isMinion = true;
					}
				} else {
					if (batData.oppoMinions.size() > 0) {
						oppoMinion = true;
						waitingForTarget = true;
						batData.waitingForTarget = true;
						isMinion = true;
					}
				}
			} else {
				restoreMinionHealth(targetMinion, card.quantity1);
				waitingForTarget = false;
				batData.waitingForTarget = true;
			}

			break;
		case "destroy a random enemy minion ":
			if (event == EventSource.Player) {
				if (batData.oppoMinions.size() > 0) {
					int randNum = r.nextInt(batData.oppoMinions.size());
					targetMinion = batData.myMinions.get(randNum);
					batData.oppoMinions.remove(randNum);
					if (targetMinion.keyword1 == Card.EFFECT_DEADLINE
							|| targetMinion.keyword2 == Card.EFFECT_DEADLINE) {
						getEffect(targetMinion, EventSource.Opponent);
					}
					targetMinion.selectable = false;
				}
			} else {
				if (batData.myMinions.size() > 0) {
					int randNum = r.nextInt(batData.myMinions.size());
					targetMinion = batData.myMinions.get(randNum);
					batData.myMinions.remove(randNum);
					if (targetMinion.keyword1 == Card.EFFECT_DEADLINE
							|| targetMinion.keyword2 == Card.EFFECT_DEADLINE) {
						getEffect(targetMinion, EventSource.Player);
					}
					targetMinion.selectable = false;
				}
			}
			break;
		case "destroy a minion":
			if (!waitingForTarget) {
				if (batData.myMinions.size() > 0 || batData.oppoMinions.size() > 0) {
					waitingForTarget = true;
					batData.waitingForTarget = true;
					isMinion = true;
				}
			} else {
				if (oppoMinion) {
					batData.oppoMinions.remove(targetIndex);
					if (targetMinion.keyword1 == Card.EFFECT_DEADLINE
							|| targetMinion.keyword2 == Card.EFFECT_DEADLINE) {
						getEffect(targetMinion, EventSource.Opponent);
					}
					targetMinion.selectable = false;
				} else {
					batData.myMinions.remove(targetIndex);
					if (targetMinion.keyword1 == Card.EFFECT_DEADLINE
							|| targetMinion.keyword2 == Card.EFFECT_DEADLINE) {
						getEffect(targetMinion, EventSource.Player);
					}
					targetMinion.selectable = false;
					waitingForTarget = false;
					batData.waitingForTarget = false;
				}
			}
			break;
		case "deal X damage to an enemy minion":
			if (!waitingForTarget) {
				if (event == EventSource.Player) {
					if (batData.oppoMinions.size() > 0) {
						waitingForTarget = true;
						batData.waitingForTarget = true;
						isMinion = true;
						oppoMinion = true;
					}
				} else if (event == EventSource.Opponent) {
					if (batData.myMinions.size() > 0) {
						waitingForTarget = true;
						batData.waitingForTarget = true;
						isMinion = true;
						myMinion = true;
					}
				}
			} else {
				if (oppoMinion) {
					dealMinionDamage(targetMinion, targetIndex, card.quantity1, "Op");
				} else {
					dealMinionDamage(targetMinion, targetIndex, card.quantity1, "My");
				}
				waitingForTarget = false;
				batData.waitingForTarget = false;
			}
			break;
		case "deal X damage to all characters":
			charHealthCheck(card.quantity1);
			if (batData.myMinions.size() > 0) {
				for (int i = 0; i < batData.myMinions.size(); i++) {
					if (dealMinionDamage(batData.myMinions.get(i), i, card.quantity1, "My")) {
						i--;
					}
				}
			}
			if (batData.oppoMinions.size() > 0) {
				for (int i = 0; i < batData.oppoMinions.size(); i++) {
					if (dealMinionDamage(batData.oppoMinions.get(i), i, card.quantity1, "Op")) {
						i--;
					}
				}
			}
			break;
		case "force a minion to deal damage to itself":
			if (!waitingForTarget) {
				if (batData.myMinions.size() > 0 || batData.oppoMinions.size() > 0) {
					waitingForTarget = true;
					batData.waitingForTarget = true;
					isMinion = true;
				}
			} else {
				if (oppoMinion) {
					dealMinionDamage(targetMinion, targetIndex, targetMinion.getAttack(), "Op");
				} else {
					dealMinionDamage(targetMinion, targetIndex, targetMinion.getAttack(), "My");
				}
				waitingForTarget = false;
				batData.waitingForTarget = false;
			}
			break;
		case "draw a card and deal its damage to your target":
			if (!waitingForTarget) {
				waitingForTarget = true;
				batData.waitingForTarget = true;
			} else {
				int damage = 0;
				if (event == EventSource.Player) {
					if (batData.myDeck.size() > 0) {
						damage = batData.myDeck.getCard(0).getCost();
					}
				} else if (event == EventSource.Opponent) {
					if (batData.oppoDeck.size() > 0) {
						damage = batData.oppoDeck.getCard(0).getCost();
					}
				}
				if (target.equals("MyFace")) {
					myHealthCheck(damage);
					target = "";
				} else if (target.equals("OppoFace")) {
					oppoHealthCheck(damage);
					target = "";
				} else {
					if (oppoMinion) {
						dealMinionDamage(targetMinion, targetIndex, damage, "Op");
					} else if (myMinion) {
						dealMinionDamage(targetMinion, targetIndex, damage, "My");
					}
				}
				if (event == EventSource.Player) {
					drawCard(batData.myDeck, batData.myHand, 1, event);
				} else if (event == EventSource.Opponent) {
					drawCard(batData.oppoDeck, batData.oppoHand, 1, event);
				}
				waitingForTarget = false;
				batData.waitingForTarget = false;
			}
			break;
		}

	}

	/**
	 * Remove the card from both effect queues
	 * @param card a playing card
	 */
	private void removeFromEffectQ(Card card) {
		if (batData.endOfTurn.size() > 0) {
			for (int i = 0; i < batData.endOfTurn.size(); i++) {
				if (card.equals(batData.endOfTurn.get(i).getCard())) {
					batData.endOfTurn.remove(i);
					break;
				}
			}
		}
		if (batData.startOfTurn.size() > 0) {
			for (int i = 0; i < batData.startOfTurn.size(); i++) {
				if (card.equals(batData.startOfTurn.get(i).getCard())) {
					batData.startOfTurn.remove(i);
					break;
				}
			}
		}

	}

	/**
	 * Deal minion to a specified card and check whether it has died
	 * @param card the card
	 * @param index the location of the card in the designated list
	 * @param i the value of damage done
	 * @param s a string to notify the user of the card's owner
	 * @return true if the minion is destroyed
	 */
	private boolean dealMinionDamage(Card card, int index, int i, String s) {
		if (s.equals("Op")) {
			card.getMinion().setDefence(card.getMinion().getDefence() - i);
			if (batData.oppoMinions.get(index).getMinion().isDestroyed()) {
				batData.oppoMinions.remove(index);
				if (card.keyword1 == Card.EFFECT_DEADLINE || card.keyword2 == Card.EFFECT_DEADLINE) {
					getEffect(card, EventSource.Opponent);
				}
				card.selectable = false;
				return true;
			}
		} else if (s.equals("My")) {
			card.getMinion().setDefence(card.getMinion().getDefence() - i);
			if (batData.myMinions.get(index).getMinion().isDestroyed()) {
				batData.myMinions.remove(index);
				if (card.keyword1 == Card.EFFECT_DEADLINE || card.keyword2 == Card.EFFECT_DEADLINE) {
					getEffect(card, EventSource.Player);
				}
				card.selectable = false;
				return true;
			}
		}
		return false;

	}

	/**
	 * Give a minion card a increase in stats
	 * @param card the card to be changed
	 * @param i the attack change
	 * @param j the defence change
	 */
	private void giveMinionBuff(Card card, int i, int j) {
		card.getMinion().setAttack(card.getMinion().getAttack() + i);
		card.getMinion().setMaxAttack(card.getMinion().getMaxAttack() + i);
		card.getMinion().setDefence(card.getMinion().getDefence() + j);
		card.getMinion().setMaxDefence(card.getMinion().getMaxDefence() + j);
	}

	/**
	 * Restore the health of a minion
	 * @param card the card to be affected
	 * @param i the value used in the change
	 */
	private void restoreMinionHealth(Card card, int i) {
		if (card.getMinion().getMaxDefence() > (card.getMinion().getDefence() + i)) {
			card.getMinion().setDefence(card.getMinion().getDefence() + i);
		} else {
			card.getMinion().setDefence(card.getMinion().getMaxDefence());
		}
	}

	/**
	 * Deal damage and check the health of the opponent
	 * @param value the value used in the change
	 */
	private void oppoHealthCheck(int value) {
		batData.oppoHP -= value;
		if (batData.oppoHP <= 0) {
			myWin = true;
			endBattle();
		}
	}

	/** 
	 * Deal damage and check the health of the player
	 * @param value the value used in the change
	 */
	private void myHealthCheck(int value) {
		batData.myHP -= value;
		if (batData.myHP <= 0) {
			oppoWin = true;
			endBattle();
		}
	}

	/**
	 * Deal damage to both players and check their health
	 * @param value the value used in the change
	 */
	private void charHealthCheck(int value) {
		batData.myHP -= value;
		batData.oppoHP -= value;
		if (batData.myHP <= 0) {
			oppoWin = true;
		}
		if (batData.oppoHP <= 0) {
			myWin = true;
		}
		if (myWin && oppoWin) {
			myWin = false;
			oppoWin = false;
			draw = true;
			endBattle();
		} else if (myWin || oppoWin) {
			endBattle();
		}

	}

	/**
	 * Reset all the values to how they should be initially
	 */
	private void resetTurnValues() {
		waitingCard = null;
		waitingForTarget = false;
		attackCard = null;
		targetMinion = null;
		oppoTarget = false;
		myTarget = false;
		oppoMinion = false;
		myMinion = false;
		target = "";
		blockThere = false;

	}

	/**
	 * Called to end the event and send data to the data manager for collection
	 */
	private void endBattle() {
		batData.battleEnd = true;
		batData.myTurn = false;
		batData.oppoTurn = false;
		if (draw == true) {
			dm.gamePlayed(gameData.getCharactor().getName(), false);
		} else {
			if (myWin) {
				dm.gamePlayed(gameData.getCharactor().getName(), true);
			} else {
				dm.gamePlayed(gameData.getCharactor().getName(), false);
			}
		}
		gameData.currentScene = GameData.HOME_PAGE;
	}

	/**
	 * Adds card to the board while checking for its keyword effects
	 * @param card the card in question
	 */
	private void playMyCard(Card card) {
		batData.myMinions.add(card);
		if (card.keyword1 == Card.EFFECT_RUSH || card.keyword2 == Card.EFFECT_RUSH) {
			card.selectable = true;
		} else {
			card.selectable = false;
		}
		batData.myMana -= card.getCost();

	}

	/**
	 * Adds card to the board while checking for its keyword effects
	 * @param card the card in question
	 */
	private void playOppoCard(Card card) {
		batData.oppoMinions.add(card);
		if (card.keyword1 == Card.EFFECT_RUSH || card.keyword2 == Card.EFFECT_RUSH) {
			card.selectable = true;
		} else {
			card.selectable = false;
		}
		batData.oppoMana -= card.getCost();

	}

	/**
	 * Updates the available cards to the user
	 * @param source the event source
	 */
	private void updateSelectableHand(EventSource source) {
		if (source == EventSource.Player) {
			for (Card card : batData.myHand)
				if (batData.myMana >= card.getCost())
					card.selectable = true;
				else
					card.selectable = false;
		} else {
			for (Card card : batData.oppoHand)
				if (batData.oppoMana >= card.getCost())
					card.selectable = true;
				else
					card.selectable = false;
		}
	}

	/**
	 * Allows selection of attack for a player minion
	 * @param value the boolean used
	 * @param s A string to determine the card used
	 */
	private void myTargetSelection(boolean value, String s) {
		if (s.equals("minion")) {
			for (Card minion : batData.myMinions) {
				if (minion.keyword1 == Card.EFFECT_BLOCK || minion.keyword2 == Card.EFFECT_BLOCK) {
					blockThere = true;
				}
			}
			if (blockThere) {
				for (Card minion : batData.myMinions) {
					if (minion.keyword1 == Card.EFFECT_BLOCK || minion.keyword2 == Card.EFFECT_BLOCK) {
						minion.selectable = value;
					} else {
						minion.selectable = false;
					}
					batData.myFaceSelectable = false;
				}
				blockThere = false;
			} else {
				for (Card minion : batData.myMinions) {
					minion.selectable = value;
				}
				batData.myFaceSelectable = value;
			}
		} else {
			for (Card minion : batData.oppoMinions) {
				minion.selectable = value;
			}
			batData.oppoFaceSelectable = value;
		}
	}

	/**
	 * Allows selection of attack for a opponent minion
	 * @param value the boolean used
	 * @param s A string to determine the card used
	 */
	private void oppoTargetSelection(boolean value, String s) {
		if (s.equals("minion")) {
			for (Card minion : batData.oppoMinions) {
				if (minion.keyword1 == Card.EFFECT_BLOCK || minion.keyword2 == Card.EFFECT_BLOCK) {
					blockThere = true;
				}
			}
			if (blockThere) {
				for (Card minion : batData.oppoMinions) {
					if (minion.keyword1 == Card.EFFECT_BLOCK || minion.keyword2 == Card.EFFECT_BLOCK) {
						minion.selectable = value;
					} else {
						minion.selectable = false;
					}
					batData.oppoFaceSelectable = false;
				}
				blockThere = false;
			} else {
				for (Card minion : batData.oppoMinions) {
					minion.selectable = value;
				}
				batData.oppoFaceSelectable = value;
			}
		} else {
			for (Card minion : batData.oppoMinions) {
				minion.selectable = value;
			}
			batData.oppoFaceSelectable = value;
		}
	}
	
	/**
	 * Changes the current round
	 * @param eventSource the users event source
	 */
	public void nextRound(EventSource eventSource) {
		if (waitingForTarget) {
			batData.errorMessage = "You still need to select a target";
		} else {
			if (eventSource == EventSource.Player) {
				myRoundEnd(eventSource);
				batData.myTurn = false;
				batData.oppoTurn = true;
				oppoRoundStart(EventSource.Opponent);
				oppoRoundMana(1);
				oppoRoundDraw(eventSource);
				oppoRoundAction();
			} else if (eventSource == EventSource.Opponent) {
				oppoRoundEnd(eventSource);
				batData.oppoTurn = false;
				batData.myTurn = true;
				myRoundStart(EventSource.Player);
				myRoundMana(1);
				myRoundDraw(eventSource);
				myRoundAction();
				batData.turn++;
			}
			resetTurnValues();
		}

	}
	
	/**
	 * Allows selection of player's face
	 * @param event the attacker's event source
	 */
	public void selectMyFace(EventSource event) {
		if (waitingForTarget) {
			if (isMinion) {
				batData.errorMessage = "You need to select a minion";
			} else {
				if (!waitingCard.isMinion()) {
					target = "MyFace";
					getEffect(waitingCard, event);
					if (event == EventSource.Player) {
						batData.myMana -= waitingCard.getCost();
					} else {
						batData.oppoMana -= waitingCard.getCost();
					}
				} else {
					target = "MyFace";
					getEffect(waitingCard, event);
					if (event == EventSource.Player) {
						playMyCard(waitingCard);
					} else {
						playOppoCard(waitingCard);
					}
				}
			}
		} else {
			if (myTarget) {
				myTargetSelection(false, "minion");
				myHealthCheck(attackCard.getAttack());
				myTarget = false;
				batData.myTarget = false;
			} else {
				batData.errorMessage = "You cannot attack your own face";
			}
		}
		updateSelectableHand(event);
	}

	/**
	 * Allows selection of opponent's face
	 * @param event the attacker's event source
	 */
	public void selectOppoFace(EventSource event) {
		if (waitingForTarget) {
			if (isMinion) {
				batData.errorMessage = "You need to select a minion";
			} else {
				if (!waitingCard.isMinion()) {
					target = "OppoFace";
					getEffect(waitingCard, event);
					if (event == EventSource.Player) {
						batData.myMana -= waitingCard.getCost();
					} else {
						batData.oppoMana -= waitingCard.getCost();
					}
				} else {
					target = "OppoFace";
					getEffect(waitingCard, event);
					if (event == EventSource.Player) {
						playMyCard(waitingCard);
					} else {
						playOppoCard(waitingCard);
					}
				}
			}
		} else {
			if (oppoTarget) {
				oppoTargetSelection(false, "minion");
				oppoHealthCheck(attackCard.getAttack());
				oppoTarget = false;
				batData.oppoTarget = false;
			} else {
				batData.errorMessage = "You cannot attack your own face";
			}
		}
		updateSelectableHand(event);
	}
	
	/**
	 * Allows the selection of a card in the users hand
	 * @param ind the index string used
	 * @param event the event source
	 */
	public void selectCard(String ind, EventSource event) {
		int index = Integer.valueOf(ind);
		if (waitingForTarget) {
			batData.errorMessage = "You cannot target a card in your hand";
		} else if (event == EventSource.Player) {
			Card card = batData.myHand.get(index);
			if (card.selectable) {
				if (card.isMinion()) {
					if (batData.myMinions.size() != batData.maxMinOnBoard) {
						batData.myHand.remove(index);
						if (!(card.keyword1 == Card.EFFECT_DEADLINE) && !(card.keyword2 == Card.EFFECT_DEADLINE)) {
							getEffect(card, event);
						}
						if (!waitingForTarget) {
							playMyCard(card);
						} else {
							waitingCard = card;
						}
					}
				} else {
					batData.myHand.remove(index);
					getEffect(card, event);
					if (!waitingForTarget) {
						card.selectable = false;
						batData.myMana -= card.getCost();
					} else {
						waitingCard = card;
					}
				}
			}
		} else {
			Card card = batData.oppoHand.get(index);
			if (card.selectable) {
				if (card.isMinion()) {
					if (batData.oppoMinions.size() != batData.maxMinOnBoard) {
						batData.oppoHand.remove(index);
						if (!(card.keyword1 == Card.EFFECT_DEADLINE) && !(card.keyword2 == Card.EFFECT_DEADLINE)) {
							getEffect(card, event);
						}
						if (!waitingForTarget) {
							playOppoCard(card);
						} else {
							waitingCard = card;
						}
					}
				} else {
					batData.oppoHand.remove(index);
					getEffect(card, event);
					if (!waitingForTarget) {
						card.selectable = false;
						batData.oppoMana -= card.getCost();
					} else {
						waitingCard = card;
					}
				}
			}
		}
		updateSelectableHand(event);
	}

	/**
	 * Allows selection of the opponent's minions
	 * @param ind the index string given
	 * @param event the event source
	 */
	public void selectOppoMinion(String ind, EventSource event) {
		int index = Integer.valueOf(ind);
		Card minionCard = batData.oppoMinions.get(index);
		if (waitingForTarget) {
			if (myMinion) {
				batData.errorMessage = "Please select a friendly minion";
			} else {
				targetMinion = minionCard;
				targetIndex = index;
				oppoMinion = true;
				getEffect(waitingCard, event);
				if (waitingCard.isMinion()) {
					if (event == EventSource.Player) {
						playMyCard(waitingCard);
					} else {
						playOppoCard(waitingCard);
					}
				} else {
					if (event == EventSource.Player) {
						batData.myMana -= waitingCard.getCost();
					} else {
						batData.oppoMana -= waitingCard.getCost();
					}
				}
				isMinion = false;
				oppoMinion = false;
			}
		} else {
			if (oppoTarget) {
				if (attackCard.isMinion()) {
					if (minionCard.selectable) {
						minionCard.getMinion().setDefence(minionCard.getMinion().getDefence() - attackCard.getAttack());
						attackCard.setDefence(attackCard.getDefence() - minionCard.getMinion().getAttack());
						if (minionCard.getMinion().isDestroyed()) {
							batData.oppoMinions.remove(index);
							if (minionCard.keyword1 == Card.EFFECT_DEADLINE
									|| minionCard.keyword2 == Card.EFFECT_DEADLINE) {
								getEffect(minionCard, EventSource.Opponent);
								removeFromEffectQ(minionCard);
							}
							minionCard.getMinion().selectable = false;
						}
						if (attackCard.getMinion().isDestroyed()) {
							batData.myMinions.remove(attackCardIndex);
							if (attackCard.keyword1 == Card.EFFECT_DEADLINE
									|| attackCard.keyword2 == Card.EFFECT_DEADLINE) {
								getEffect(attackCard, event);
								removeFromEffectQ(attackCard);
							}
							attackCard.selectable = false;
						}
						oppoTarget = false;
						batData.oppoTarget = false;
						oppoTargetSelection(false, "minion");
					} else {
						batData.errorMessage = "You cannot attack this minion";
					}
				}
			} else if (!myTarget) {
				if (minionCard.selectable) {
					attackCard = minionCard;
					attackCardIndex = index;
					minionCard.selectable = false;
					myTarget = true;
					batData.myTarget = true;
					myTargetSelection(true, "minion");
				}
			} else {
				batData.errorMessage = "This minion cannot attack yet";
			}
		}
		updateSelectableHand(event);
	}
	
	/**
	 * Allows selection of the player's minions
	 * @param ind the index string given
	 * @param event the event source
	 */
	public void selectMyMinion(String ind, EventSource event) {
		int index = Integer.valueOf(ind);
		Card minionCard = batData.myMinions.get(index);
		if (waitingForTarget) {
			if (oppoMinion) {
				batData.errorMessage = "Please select a friendly minion";
			} else {
				targetMinion = minionCard;
				targetIndex = index;
				myMinion = true;
				getEffect(waitingCard, event);
				if (waitingCard.isMinion()) {
					if (event == EventSource.Player) {
						playMyCard(waitingCard);
					} else {
						playOppoCard(waitingCard);
					}
				} else {
					if (event == EventSource.Player) {
						batData.myMana -= waitingCard.getCost();
					} else {
						batData.oppoMana -= waitingCard.getCost();
					}
				}
				isMinion = false;
				myMinion = false;
			}
		} else {
			if (myTarget) {
				if (attackCard.isMinion()) {
					if (minionCard.selectable) {
						minionCard.getMinion().setDefence(minionCard.getMinion().getDefence() - attackCard.getAttack());
						attackCard.setDefence(attackCard.getDefence() - minionCard.getMinion().getAttack());
						if (minionCard.getMinion().isDestroyed()) {
							batData.myMinions.remove(index);
							if (minionCard.keyword1 == Card.EFFECT_DEADLINE
									|| minionCard.keyword2 == Card.EFFECT_DEADLINE) {
								getEffect(minionCard, EventSource.Player);
								removeFromEffectQ(minionCard);
							}
							minionCard.getMinion().selectable = false;
						}
						if (attackCard.getMinion().isDestroyed()) {
							batData.oppoMinions.remove(attackCardIndex);
							if (attackCard.keyword1 == Card.EFFECT_DEADLINE
									|| attackCard.keyword2 == Card.EFFECT_DEADLINE) {
								getEffect(attackCard, event);
								removeFromEffectQ(attackCard);
							}
							attackCard.selectable = false;
						}
						myTarget = false;
						batData.myTarget = false;
						myTargetSelection(false, "minion");
					} else {
						batData.errorMessage = "You cannot attack this minion";
					}
				}
			} else if (!oppoTarget) {
				if (minionCard.selectable) {
					attackCard = minionCard;
					attackCardIndex = index;
					minionCard.selectable = false;
					oppoTarget = true;
					batData.oppoTarget = true;
					oppoTargetSelection(true, "minion");
				} else {
					batData.errorMessage = "This minion cannot attack yet";
				}
			}
		}
		updateSelectableHand(event);
	}
	
	/**
	 * Allows the user to exit the battle but at the cost of losing
	 * @param event the event source
	 */
	private void giveUp(EventSource event) {
		if (event == EventSource.Player) {
			if (gameData.debug)
				System.out.println("::I concede");
			batData.myConcede = true;
			oppoWin = true;
			myWin = false;
			draw = false;
		} else {
			if (gameData.debug)
				System.out.println("::Opponent concedes");
			batData.oppoConcede = true;
			myWin = true;
			oppoWin = false;
			draw = false;
		}
		endBattle();
	}
	
	/**
	 * Move n given cards from a given deck to a given hand
	 * @param deck the user's deck
	 * @param hand the user's hand
	 * @param n the number of cards to draw
	 * @param event the event source of the user
	 */
	public void drawCard(Deck deck, ArrayList<Card> hand, int n, EventSource event) {
		for (int i = 0; i < n; i++) {
			if (deck.size() != 0) {
				if (hand.size() < batData.handMax) {
					hand.add(deck.getCard(0));
				}
				deck.removeCard(0);
			} else {
				if (event == EventSource.Player) {
					myHealthCheck(batData.myFatigue);
					batData.myFatigue++;
				} else {
					oppoHealthCheck(batData.oppoFatigue);
					batData.oppoFatigue++;
				}
			}
		}
	}

	/**
	 * Returns the value of meFirst in battleData
	 * @param value the boolean value to be set
	 * @return the value of meFirst
	 */
	public boolean getmeFirst() {
		return gameData.meFirst;
	}

	/**
	 * Allows the changing of the boolean meFirst in battleData
	 * @param value the value to change meFirst to
	 */
	public void setmeFirst(boolean value) {
		gameData.meFirst = value;
	}

}