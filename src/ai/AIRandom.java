package ai;

import java.util.ArrayList;
import java.util.Random;

import data.BattleData;
import data.GameData;
import events.EventTrigger;
import events.EventUtil;

public class AIRandom extends AIInterface {

	private Random randomGenerator;
	GameData gameData;
	BattleData battleData;

	public AIRandom(GameData gameData, EventTrigger eventTrigger) {
		super(gameData, eventTrigger);
		this.gameData = gameData;
		battleData = gameData.getBattleData();
	}

	@Override
	public void run() {
		System.out.println("Random AI Start!");
		int count = 0;
		while (!battleData.myTurn && !battleData.oppoTurn)
			EventUtil.sleep(800);
		System.out.println("Random AI Start to think!");
		while (!battleData.battleEnd && gameData.currentScene == GameData.BATTLE_PAGE) {
			EventUtil.sleep(1000);
			// still needs to be done (selectTarget)
			
			if (battleData.oppoTurn) {
				randomGenerator = new Random();
				/*
				 * ArrayList<Integer> selectableHand = new ArrayList<Integer>();
				 * for (int i = 0; i < battleData.oppoHand.size(); i++) if
				 * (battleData.oppoHand.get(i).selectable)
				 * selectableHand.add(i);
				 * 
				 * ArrayList<Integer> selectableOppoMinions = new
				 * ArrayList<Integer>(); for (int i = 0; i <
				 * battleData.oppoMinions.size(); i++) if
				 * (battleData.oppoMinions.get(i).selectable)
				 * selectableOppoMinions.add(i);
				 * 
				 * ArrayList<Integer> selectableMyMinions = new
				 * ArrayList<Integer>(); for (int j = 0; j <
				 * battleData.myMinions.size(); j++) if
				 * (battleData.myMinions.get(j).selectable)
				 * selectableMyMinions.add(j);
				 */
				int range = 0;
				int randomNum = 0;
				if (battleData.waitingForTarget) {
					range = 4;
					randomNum = randomGenerator.nextInt(range);
					System.out.println(randomNum + " while waiting");
				} else if (battleData.myTarget) {
					randomNum = randomGenerator.nextInt(2) + 2;
				} else {
					range = 6;
					count++;
					if(count>6) {
						randomNum = 5;
					} else {
						randomNum = randomGenerator.nextInt(range);
						System.out.println("Normal choice " + randomNum);
					}
				}
				
				switch (randomNum) {
				case 0:
					// if (selectableOppoMinions.size()==0) break;
					// int oppoMinionsRange = selectableOppoMinions.size();
					if (battleData.oppoMinions.size() == 0)
						break;
					Integer randomOppoMinion = randomGenerator.nextInt(battleData.oppoMinions.size());
					System.out.println(battleData.oppoMinions.get(randomOppoMinion));
					getEventTrigger().aiInput("selectOppoMinion", randomOppoMinion.toString());
					break;
				case 1:
					if (battleData.oppoFaceSelectable) {
						getEventTrigger().aiInput("selectOppoFace");
					}
					break;
				case 2:
					if (battleData.myFaceSelectable) {
						getEventTrigger().aiInput("selectMyFace");
					}
					break;
				case 3:
					// if (selectableMyMinions.size()==0) break;
					// int myMinionsRange = selectableMyMinions.size();
					if (battleData.myMinions.size() == 0)
						break;
					Integer randomMyMinions = randomGenerator.nextInt(battleData.myMinions.size());
					System.out.println(battleData.myMinions.get(randomMyMinions));
					getEventTrigger().aiInput("selectMyMinion", randomMyMinions.toString());
					break;
				case 4:
					// if (selectableHand.size()==0) break;
					// int handRange = selectableHand.size();
					if (battleData.oppoHand.size() == 0)
						break;
					Integer randomHand = randomGenerator.nextInt(battleData.oppoHand.size());
					System.out.println(battleData.oppoHand.get(randomHand));
					getEventTrigger().aiInput("selectCard", randomHand.toString());
					break;
				case 5:
					// int randomNumber = randomGenerator.nextInt(10);
					// if (randomNumber!=0) break;
					System.out.println("I dont want to think anymore......................");
					getEventTrigger().aiInput("nextRound");
					count = 0;
					break;
				}
			}

		}
		System.out.println("Random AI die");
	}
}
