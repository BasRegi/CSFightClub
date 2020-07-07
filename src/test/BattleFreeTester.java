package test;

import java.util.ArrayList;
import java.util.Scanner;

import ai.AIInterface;
import data.BattleData;
import data.Card;
import data.DataManager;
import data.GameData;
import events.EventManager;
import events.EventTrigger;
import events.EventUtil;
import gui.GUImodel;
import gui.GUIview;

public class BattleFreeTester extends AIInterface{

	private static GameData gameData;
	private static BattleData batData;
	private static EventTrigger eventTrigger;
	private static GUImodel model;
	private static GUIview view;
	private static ArrayList<Card> computerSelectableMinion = new ArrayList<Card>();
	private static ArrayList<Card> humanSelectableMinion = new ArrayList<Card>();
	private static ArrayList<Card> computerSelectableCard = new ArrayList<Card>();
	private static Scanner in = new Scanner(System.in);

	public static void main(String[] args) {
		System.out.println("In this tester, you will control AI in the CMD, Human in GUI");
		gameData = new GameData();
		batData = gameData.getBattleData();
		DataManager dataManager = new DataManager(gameData);
		EventManager eventManager = new EventManager(gameData, dataManager);
		eventTrigger = eventManager.getEventTrigger();
		
		System.out.println("Loading Cards from database");
		dataManager.loadCards();
		
		gameData.sysAIReady = true;
		
		model = new GUImodel();
		view = new GUIview(gameData, eventTrigger, model);
		EventUtil.sleep(1000);
		new Thread(new Runnable(){
			public void run(){gameData.currentScene = GameData.BATTLE_PAGE;eventManager.startScene(GameData.BATTLE_PAGE);}
		}).start();
		EventUtil.sleep(1000);
		
		(new BattleFreeTester(gameData, eventTrigger)).start();
		BattleGuiTester.batData = batData;
		BattleGuiTester.model = model;
		(new BattleGuiTester()).start();
	}

	public BattleFreeTester(GameData gameData, EventTrigger eventTrigger) {
		super(gameData, eventTrigger);
		this.gameData = gameData;
		this.eventTrigger = eventTrigger;
	}
	
	@Override
	public void run() {
		BattleData battleData = gameData.getBattleData();
		while (!battleData.myTurn && !battleData.oppoTurn) EventUtil.sleep(50);
		System.out.println("You may start to control battleData");
		
		while (!battleData.battleEnd) {
			EventUtil.sleep(50);
			if (battleData.oppoTurn) {
				showBoardStatus();
				showOppoInfo();
				showSelectableStuff();
				showOptions();
				System.out.println(batData.errorMessage);
				String input;
				switch (getNumberInput(5)){
				case -1: // input==q
					eventTrigger.aiInput("giveUp");
					eventTrigger.aiInput("exit");
					break;
				case 0: // TODO select card
					if (computerSelectableCard.size()==0) System.out.println("No card available...");
					int cardIndex = getNumberInput(computerSelectableCard.size());
					eventTrigger.aiInput("selectCard", String.valueOf(
							batData.oppoHand.indexOf(computerSelectableCard.get(cardIndex))));
					break;
				case 1: // TODO select Minion
					System.out.print("0.human / 1.computer    INPUT : ");
					input = in.nextLine();
					if (input.equals("0")){
						if (humanSelectableMinion.size()==0) {
							System.out.println("No minion available...");
							break;
						}
						int minionIndex = getNumberInput(humanSelectableMinion.size());
						eventTrigger.aiInput("selectMyMinion", String.valueOf(
								batData.oppoMinions.indexOf(humanSelectableMinion.get(minionIndex))));
					} else if (input.equals("1")){
						if (computerSelectableMinion.size()==0) {
							System.out.println("No minion available...");
							break;
						}
						int minionIndex = getNumberInput(computerSelectableMinion.size());
						eventTrigger.aiInput("selectOppoMinion", String.valueOf(
								batData.oppoMinions.indexOf(computerSelectableMinion.get(minionIndex))));
					}
					break;
				case 2: // TODO select face
					System.out.print("0.human / 1.computer    INPUT : ");
					input = in.nextLine();
					if (input.equals("0")){
						eventTrigger.aiInput("selectMyFace");
					} else if (input.equals("1")){
						eventTrigger.aiInput("selectOppoFace");
					}
					break;
				case 3: // TODO Pass
					eventTrigger.aiInput("nextRound");
					break;
				case 4: // TODO Surrender
					eventTrigger.aiInput("giveUp");
					break;
				}
			}
		}
	}

	private static void showOppoInfo(){
		System.out.println("\nCopmuter    Health:"+batData.oppoHP+"\tMana:"+batData.oppoMana);
		System.out.println("\nPlayer      Health:"+batData.myHP+"\tMana:"+batData.myMana);
		for (int i=0; i<batData.oppoMinions.size(); i++){
			System.out.println("Minion("+i+")\t"+batData.oppoMinions.get(i)+"\n");
		}
		for (int i=0; i<batData.oppoHand.size(); i++){
			System.out.println("Hand("+i+")\t"+batData.oppoHand.get(i)+"\n");
		}
	}
	
	private static void showBoardStatus(){
		System.out.println("============= Board Status =============");
		System.out.println("human deck                    computer deck");
		System.out.println(batData.myDeck.size()+"                      "+batData.oppoDeck.size());
		System.out.println("\nhuman minions              computer minions");
		for (int i=0; i<Math.max(batData.myMinions.size(), batData.oppoMinions.size()); i++){
			if (i<batData.myMinions.size()) System.out.print(batData.myMinions.get(i)+"\t\t\t");
			else System.out.print("\t\t\t\t\t\t");
			if (i<batData.oppoMinions.size()) System.out.print(batData.oppoMinions.get(i));
		}
	}
	
	private static void showSelectableStuff() {
		computerSelectableMinion.clear();
		for (Card minionCard : batData.oppoMinions){
			if (minionCard.selectable) computerSelectableMinion.add(minionCard);
		}
		if (!computerSelectableMinion.isEmpty()){
			System.out.println("== Computer Selectable Minions ==");
			for (int i=0; i<computerSelectableMinion.size(); i++){
				System.out.println(i+"\t:\t"+computerSelectableMinion.get(i));
			}
		}else{
			System.out.println("No Selectable Computer Minions");
		}
		humanSelectableMinion.clear();
		for (Card minionCard : batData.myMinions){
			if (minionCard.selectable) humanSelectableMinion.add(minionCard);
		}
		if (!humanSelectableMinion.isEmpty()) {
			System.out.println("==    Human Selectable Minions    ==");
			for (int i=0; i<humanSelectableMinion.size(); i++){
				System.out.println(i+"\t:\t"+humanSelectableMinion.get(i));
			}
		} else {
			System.out.println("No selectable Human Minion");
		}
		
		computerSelectableCard.clear();
		for (Card card : batData.oppoHand){
			if (card.selectable) computerSelectableCard.add(card);
		}
		if (!computerSelectableCard.isEmpty()) {
			System.out.println("==     Computer Selectable Cards     ==");
			for (int i=0; i<computerSelectableCard.size(); i++){
				System.out.println(i+"\t:\t"+computerSelectableCard.get(i));
			}
		} else {
			System.out.println("No Selectable Card");
		}
		
		if (batData.myFaceSelectable) System.out.println("!! You may select human face");
		if (batData.oppoFaceSelectable) System.out.println("!! You may select your computer face");
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
