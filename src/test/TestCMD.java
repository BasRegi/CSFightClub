package test;
import java.util.ArrayList;
import java.util.Scanner;

import data.BattleData;
import data.Card;
import data.GameData;
import data.LobbyData;
import data.Minion;
import events.EventTrigger;
import events.EventUtil;

public class TestCMD extends Thread {
	private GameData gameData;
	private BattleData batData;
	private EventTrigger eventTrigger;
	private Scanner in = new Scanner(System.in);
	
	private ArrayList<Card> oppoSelectableMinion = new ArrayList<Card>();
	private ArrayList<Card> mySelectableMinion = new ArrayList<Card>();
	private ArrayList<Card> mySelectableCard = new ArrayList<Card>();
	
	private boolean registerReady = false;
	private boolean loginReady = false;
	
	public TestCMD(GameData gameData, EventTrigger eventTrigger){
		this.gameData = gameData;
		this.batData = gameData.getBattleData();
		this.eventTrigger = eventTrigger;
	}
	
	public void run(){
		System.out.println("test CMD running");
		
		while (!gameData.sysGameEnd){
			switch (gameData.currentScene)  {
			case GameData.WELCOME_PAGE:
				welcome();
				break;
			case GameData.HOME_PAGE:
				home();
				break;
			case GameData.BATTLE_PAGE:
				battle();
				break;
			case GameData.LOBBY_PAGE:
				lobby();
				break;
			case GameData.CANVAS_PAGE:
				canvs();
				break;
			case GameData.COLLECTION_PAGE:
				collection();
				break;
			case GameData.CHARACTOR_PAGE:
				charactor();
				break;
			case GameData.STORE_PAGE:
				store();
				break;
			}
			if (gameData.sysGameEnd) System.out.println("Game should end!");
		}
	}
	
	private void welcome() {
		System.out.println("################################################");
		System.out.println("# Welcome Page                                 #");
		System.out.println("################################################");
		System.out.println("Input 'q' anytime if you wanna exit");
		System.out.print("Username : ");
		String username = in.nextLine();
		if (username.equals("q")) eventTrigger.myInput("exit");
		else eventTrigger.myInput("changeBoxUsername", username);
		EventUtil.sleep(200);
		System.out.print("Password : ");
		String password = in.nextLine();
		if (password.equals("q")) eventTrigger.myInput("exit");
		else eventTrigger.myInput("changeBoxPassword", password);
		EventUtil.sleep(200);
		System.out.println("Press <Enter> to login (or input anything to register)");
		String input = in.nextLine();
		if (input.equals("q")) eventTrigger.myInput("exit");
		else if (input.equals("")) eventTrigger.myInput("login", username, password);
		else eventTrigger.myInput("register", username, password);
		//System.out.println("Checking");
		gameData.sysLockGUI = true;
		while (gameData.sysLockGUI) {
			EventUtil.sleep(500);
			//System.out.print(".");
		}
		if (gameData.currentScene == GameData.WELCOME_PAGE){
			System.out.println("The username or the password is incorrect");
		}
	}
	
	private void home() {
		System.out.println("################################################");
		System.out.println("# Home Page                                    #");
		System.out.println("################################################");
		System.out.println("# Input 'q' anytime if you wanna exit          #");
		System.out.println("# 0. Single Battle                             #");
		System.out.println("# 1. Lobby                                     #");
		System.out.println("# 2. Collection                                #");
		System.out.println("# 3. Canvas                                    #");
		System.out.println("# 4. Settings                                  #");
		System.out.println("# 5. Exit                                      #");
		System.out.println("################################################");
		System.out.print("Input : ");
		String input = in.nextLine();
		if (input.equals("q") || input.equals("5")) {
			eventTrigger.myInput("exit");
		} else if (input.equals("1")){
			System.out.print("HOST : ");
			String host = in.nextLine();
			if (host.equals("q")) {
				eventTrigger.myInput("exit");
				EventUtil.sleep(500);
				return;
			}
			System.out.print("PORT : ");
			String port = in.nextLine();
			if (port.equals("q")) {
				eventTrigger.myInput("exit");
				EventUtil.sleep(500);
				return;
			}
			eventTrigger.myInput("clickLobby", host, port);
		} else if (input.equals("2")) {
			eventTrigger.myInput("clickCollection" );
		} else if (input.equals("3")) {
			eventTrigger.myInput("clickCanvas" );
		} else if (input.equals("4")) {
			eventTrigger.myInput("clickSettings" );
		} else if (input.equals("0")) {
			eventTrigger.myInput("clickSingle");
		}
		EventUtil.sleep(500);
	}

	private void battle() {
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
			showBoardStatus();
			while (!batData.battleEnd) {
				EventUtil.sleep(1000);
				if (batData.myTurn) {
					while (batData.myTurn) {
						showMyInfo();
						showSelectableStuff();
						showOptions();
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
								eventTrigger.myInput("selectMinion", String.valueOf(
										batData.myMinions.indexOf(mySelectableMinion.get(minionIndex))));
							} else if (input.equals("1")){
								if (oppoSelectableMinion.size()==0) {
									System.out.println("No minion available...");
									break;
								}
								int minionIndex = getNumberInput(oppoSelectableMinion.size());
								eventTrigger.myInput("selectMinion", String.valueOf(
										batData.oppoMinions.indexOf(oppoSelectableMinion.get(minionIndex))));
							}
							break;
						case 2: // TODO select face
							System.out.print("0.my / 1.oppo    INPUT : ");
							input = in.nextLine();
							if (input.equals("0")){
								if (!batData.myFaceSelectable) {
									System.out.println("You can not touch your face");
									break;
								}
								eventTrigger.myInput("selectMyFace");
							} else if (input.equals("1")){
								if (!batData.oppoFaceSelectable) {
									System.out.println("You can not touch your opponent's face");
									break;
								}
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
						EventUtil.sleep(50);
					} else {
				
						// TODO show some info of me
						// TODO show all oppo info, cards on his hand, minions, and options
						// TODO choose option, select card or minions to do something
						
						// FUCK just nextRound...
						eventTrigger.aiInput("nextRound");
						break;
					}
				}
			}
		}
		
	}

		private void showBoardStatus(){
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
		
		private void showMyInfo(){
			System.out.println("\nOpponent    Health:"+batData.oppoHP+"\tMana:"+batData.oppoMana);
			System.out.println("\nPlayer      Health:"+batData.myHP+"\tMana:"+batData.myMana);
			for (int i=0; i<batData.myMinions.size(); i++){
				System.out.println("Minion("+i+")\t"+batData.myHand.get(i)+"\n");
			}
			for (int i=0; i<batData.myHand.size(); i++){
				System.out.println("Hand("+i+")\t"+batData.myHand.get(i)+"\n");
			}
		}
		
		private void showSelectableStuff() {
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
		
		private void showOptions(){
			System.out.println("==== Options ====");
			System.out.println(" 0. Select Card");
			System.out.println(" 1. Select Minion");
			System.out.println(" 2. Select (My or opponent's)Face");
			System.out.println(" 3. Pass");
			System.out.println(" 4. Surrender");
		}

	private void lobby() {
		System.out.println("########################################");
		System.out.println("# Lobby                                #");
		System.out.println("########################################");
		LobbyData lobData = gameData.getLobbyData();
		int msgTotal = lobData.messageHistory.size();
		while (gameData.currentScene == GameData.LOBBY_PAGE) {
			while (msgTotal < lobData.messageHistory.size()) {
				int msgHistorySize = lobData.messageHistory.size();
				for (int i=msgTotal; i<msgHistorySize; i++){
					System.out.println(lobData.messageHistory.get(i));
				}
				msgTotal = lobData.messageHistory.size();
			}
			EventUtil.sleep(500);
			System.out.print("MESSAGE : ");
			String message = in.nextLine();
			if (message.equals("q")) {
				eventTrigger.myInput("back");
				while (gameData.currentScene == GameData.LOBBY_PAGE) EventUtil.sleep(50);
				break;
			}
			System.out.print("RECEIVE : ");
			String receiver = in.nextLine();
			if (receiver.equals("q")) {
				eventTrigger.myInput("back");
				while (gameData.currentScene == GameData.LOBBY_PAGE) EventUtil.sleep(50);
				break;
			}
			if (receiver.equals("")) receiver = null;
			eventTrigger.myInput("sendMessage", receiver, message);
		}
		
	}

	private void canvs() {
		// TODO Auto-generated method stub
		
	}

	private void collection() {
		// TODO Auto-generated method stub
		
	}

	private void charactor() {
		// TODO Auto-generated method stub
		
	}

	private void store() {
		// TODO Auto-generated method stub
		
	} 
	
	private int getNumberInput(int n) {
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
