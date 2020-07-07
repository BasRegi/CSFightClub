package test;

import java.util.ArrayList;
import java.util.Scanner;

import ai.AIRandom;
import data.BattleData;
import data.Card;
import data.DataManager;
import data.GameData;
import events.EventManager;
import events.EventTrigger;
import events.EventUtil;

public class BattleTest {
	
	private static GameData gameData;
	private static BattleData batData;
	private static EventTrigger eventTrigger;
	private static Scanner in = new Scanner(System.in);
	
	private static ArrayList<Card> oppoSelectableMinion = new ArrayList<Card>();
	private static ArrayList<Card> mySelectableMinion = new ArrayList<Card>();
	private static ArrayList<Card> mySelectableCard = new ArrayList<Card>();

	public static void main(String[] args) {
		System.out.println("===============  Battle Test ==============");
		
		System.out.println("Initialising...");
		gameData = new GameData();
		batData = gameData.getBattleData();
		DataManager dataManager = new DataManager(gameData);
		EventManager eventManager = new EventManager(gameData, dataManager);
		eventTrigger = eventManager.getEventTrigger();
		
		// Load Cards data
		if (gameData.debug) System.out.println("Loading Cards from database");
		dataManager.loadCards();
		
		// AI?
		System.out.println("Do you want to test the AI Random?");
		System.out.println("0. Yes    1. No");
		int input = getNumberInput(2);
		if (input == 0) {
			gameData.sysAIReady = true;
			AIRandom ai = new AIRandom(gameData, eventTrigger);
			new Thread(new Runnable(){
				public void run(){ai.start();}
			}).start();
		}
		
		// Start Battle Stuff
		gameData.currentScene = GameData.BATTLE_PAGE;
		new Thread(new Runnable(){
			public void run(){eventManager.startScene(GameData.BATTLE_PAGE);}
		}).start();
		
		battle();
	}
	
	private static void battle() {
		System.out.println("########################################");
		System.out.println("# Battle                               #");
		System.out.println("########################################");
		batData.battleEnd = false;
		
		//wait for battle initialisation
		System.out.print("Loading");
		while (!batData.myTurn && !batData.oppoTurn) {
			System.out.print(".");
			EventUtil.sleep(500);
		}
		System.out.println("\nBattle Start!");
		
		while (!batData.battleEnd) {
			EventUtil.sleep(1000);
			while (!batData.battleEnd) {
				EventUtil.sleep(1000);
				if (batData.myTurn) {
					while (batData.myTurn) {
						showBoardStatus();
						showMyInfo();
						showSelectableStuff();
						showOptions();
						showBoardStatus();
						System.out.println();
						System.out.println(batData.errorMessage);
						String input;
						switch (getNumberInput(5)){
						case -1: // input==q
							eventTrigger.myInput("giveUp");
							eventTrigger.myInput("exit");
							break;
						case 0: // TODO select card
							if (mySelectableCard.size()==0) System.out.println("No card available...");
							int cardIndex = getNumberInput(mySelectableCard.size());
							eventTrigger.myInput("selectCard", String.valueOf(
									batData.myHand.indexOf(mySelectableCard.get(cardIndex))));
							break;
						case 1: // TODO select Minion
							System.out.print("0.my / 1.oppo    INPUT : ");
							input = in.nextLine();
							if (input.equals("0")){
								if (mySelectableMinion.size()==0) {
									System.out.println("No minion available...");
									break;
								}
								int minionIndex = getNumberInput(mySelectableMinion.size());
								eventTrigger.myInput("selectMyMinion", String.valueOf(
										batData.myMinions.indexOf(mySelectableMinion.get(minionIndex))));
							} else if (input.equals("1")){
								if (oppoSelectableMinion.size()==0) {
									System.out.println("No minion available...");
									break;
								}
								int minionIndex = getNumberInput(oppoSelectableMinion.size());
								eventTrigger.myInput("selectOppoMinion", String.valueOf(
										batData.oppoMinions.indexOf(oppoSelectableMinion.get(minionIndex))));
							}
							break;
						case 2: // TODO select face
							System.out.print("0.my / 1.oppo    INPUT : ");
							input = in.nextLine();
							if (input.equals("0")){
//								if (!batData.myFaceSelectable) {
//									System.out.println("You can not touch your face");
//									break;
//								}
								eventTrigger.myInput("selectMyFace");
							} else if (input.equals("1")){
//								if (!batData.oppoFaceSelectable) {
//									System.out.println("You can not touch your opponent's face");
//									break;
//								}
								eventTrigger.myInput("selectOppoFace");
							}
							break;
						case 3: // TODO Pass
							eventTrigger.myInput("nextRound");
							break;
						case 4: // TODO Surrender
							eventTrigger.myInput("giveUp");
							break;
						}
						EventUtil.sleep(1000);
					}
					break;
				} else if (batData.oppoTurn ){
					// TODO if AI is ready, just wait
					if (gameData.sysAIReady){
						while (batData.oppoTurn && !batData.myTurn) EventUtil.sleep(50);
					} else {
				
						// TODO show some info of me
						// TODO show all oppo info, cards on his hand, minions, and options
						// TODO choose option, select card or minions to do something
						
						System.out.println("AI is not working..........................");
						eventTrigger.aiInput("nextRound");
						break;
					}
				}
			}
		}
		
	}

		private static void showBoardStatus(){
			System.out.println("============= Board Status =============");
			System.out.println("my deck                    opponent deck");
			System.out.println(batData.myDeck.size()+"                      "+batData.oppoDeck.size());
			System.out.println("\nmy minions              opponent minions");
			for (int i=0; i<Math.max(batData.myMinions.size(), batData.oppoMinions.size()); i++){
				if (i<batData.myMinions.size()) System.out.print(batData.myMinions.get(i)+"\t\t\t");
				else System.out.print("\t\t\t\t\t\t");
				if (i<batData.oppoMinions.size()) System.out.print(batData.oppoMinions.get(i));
			}
		}
		
		private static void showMyInfo(){
			System.out.println("\nOpponent    Health:"+batData.oppoHP+"\tMana:"+batData.oppoMana);
			System.out.println("\nPlayer      Health:"+batData.myHP+"\tMana:"+batData.myMana);
			for (int i=0; i<batData.myMinions.size(); i++){
				System.out.println("Minion("+i+")\t"+batData.myMinions.get(i)+"\n");
			}
			for (int i=0; i<batData.myHand.size(); i++){
				System.out.println("Hand("+i+")\t"+batData.myHand.get(i)+"\n");
			}
		}
		
		private static void showSelectableStuff() {
			oppoSelectableMinion.clear();
			for (Card minionCard : batData.oppoMinions){
				if (minionCard.selectable) oppoSelectableMinion.add(minionCard);
			}
			if (!oppoSelectableMinion.isEmpty()){
				System.out.println("== Opponent Selectable Minions ==");
				for (int i=0; i<oppoSelectableMinion.size(); i++){
					System.out.println(i+"\t:\t"+oppoSelectableMinion.get(i));
				}
			}else{
				System.out.println("No Selectable Opponent Minions");
			}
			
			mySelectableMinion.clear();
			for (Card minionCard : batData.myMinions){
				if (minionCard.selectable) mySelectableMinion.add(minionCard);
			}
			if (!mySelectableMinion.isEmpty()) {
				System.out.println("==    My Selectable Minions    ==");
				for (int i=0; i<mySelectableMinion.size(); i++){
					System.out.println(i+"\t:\t"+mySelectableMinion.get(i));
				}
			} else {
				System.out.println("No selectable Minion");
			}
			
			mySelectableCard.clear();
			for (Card card : batData.myHand){
				if (card.selectable) mySelectableCard.add(card);
			}
			if (!mySelectableCard.isEmpty()) {
				System.out.println("==     My Selectable Cards     ==");
				for (int i=0; i<mySelectableCard.size(); i++){
					System.out.println(i+"\t:\t"+mySelectableCard.get(i));
				}
			} else {
				System.out.println("No Selectable Card");
			}
			
			if (batData.myFaceSelectable) System.out.println("!! You may select your face");
			if (batData.oppoFaceSelectable) System.out.println("!! You may select your opponent's face");
		}
		
		private static void showOptions(){
			System.out.println("==== Options ====");
			System.out.println(" 0. Select Card");
			System.out.println(" 1. Select Minion");
			System.out.println(" 2. Select (My or opponent's)Face");
			System.out.println(" 3. Pass");
			System.out.println(" 4. Surrender");
		}

		private static int getNumberInput(int n) {
			if (n<=0) return -1;
			while (true){
				System.out.print("INPUT (0.."+(n-1)+") : ");
				String input = in.nextLine();
				if (input == "q") return -1;
				try{
					int choice = Integer.valueOf(input);
					if (choice>=0 && choice<n) return choice;
				}catch (Exception e){
					System.out.println("  INVALID INPUT!! ");
				}
			}
		}
}
