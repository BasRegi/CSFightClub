package data;

import events.battle.BattleEffect;

/**
 * The class to store ALL data about a card and capture its logical effect on a battle
 * @author rxk592
 * 
 */
public class Card {
	
	public static final int EFFECT_NULL = 0;   //Nothing before colon
	public static final int EFFECT_ITERATE = 1;  //trivial
	public static final int EFFECT_BLOCK = 2;
	public static final int EFFECT_DEADLINE = 3;
	public static final int EFFECT_RUSH = 4;
	public static final int EFFECT_FIRSTCALL = 5;
	
	public static final int ACTION_NULL = 20;   //No action, shouldn't occur
	public static final int ACTION_DAMAGE = 21; //e.g. Deal X damage
	public static final int ACTION_GAIN = 23;   //e.g. Gain +3/+2
	public static final int ACTION_NULLIFY = 24;  //uncertain, but used
	public static final int ACTION_DESTROY = 25; //wipe out minion or board
	public static final int ACTION_DRAW = 26; //default is cards, e.g. draw x cards 
	public static final int ACTION_RESTORE = 28; //Health up
	public static final int ACTION_GIVE = 32; //New effect given to minion
	
	public static final int TARGET_NULL = 40;  //No target given, use default
	public static final int TARGET_OPPONENT = 41; //own face
	public static final int TARGET_MINION = 42; //Assume this would mean user can just pick any of minions on board
	public static final int TARGET_ENEMYMIN = 43; //User picks any enemy minion or his face
	public static final int TARGET_RANDENEMYMIN = 44; //Random enemy minion
	public static final int TARGET_ALLENEMYMIN = 45; //All enemy minions
	public static final int TARGET_FRIENDLYMIN = 46;     //User picks an ally minion
	public static final int TARGET_RANDFRIENDLYMIN = 47; //Random ally minion
	public static final int TARGET_ALLFRIENDLYMIN = 48; //All ally minions
	public static final int TARGET_ALLMINION = 49;  //All minions
	public static final int TARGET_SELF = 50;
	
	public static final int TIME_NULL = 70;  //Use default, probably immediate
	public static final int TIME_MYSTART = 71;   //Trivial
	public static final int TIME_OPPSTART = 72;
	public static final int TIME_EACHSTART = 73;
	public static final int TIME_MYEND = 74;
	public static final int TIME_OPPEND = 75;
	public static final int TIME_EACHEND = 76;

	
	private int id;
	private String name;
	private String set;
	private int cost;
	private Minion minion;
	private String effect;
	private String dbEffect;
	
	public boolean selectable = false;
	public BattleEffect waitingEffect;
	
	public String logicalEffect = new String();
	public int keyword1, keyword2, action, quantity1, quantity2, target, time;
	
	
	/**
	 * Setup a new Minion card
	 * @param i the id of the card
	 * @param n the name of the card
	 * @param s the set the card is in
	 * @param c the cost of using the card
	 * @param a the attack value of the minion
	 * @param d the defence value of the minion
	 * @param e the effect the card has on the battle
	 */
	public Card(int i, String n, String s, int c, int a, int d, String e) {
		id = i;
		name = n;
		set = s;
		cost = c;
		minion = new Minion(name, a, d);
		dbEffect = e;
		effect = e;
		pullLogic();
	}
	
	/**
	 * Setup a new hex card
	 * @param i the id of the card
	 * @param n the name of the card
	 * @param s the set the card is in
	 * @param c the cost of using the card
	 * @param e the effect the card has on the battle
	 */
	public Card(int i, String n, String s, int c, String e) {
		id = i;
		name = n;
		set = s;
		cost = c;
		minion = new Minion();
		dbEffect = e;
		effect = e;
		pullLogic();
	}
	
	/**
	 * Take the effect string for the card, and identify the words used to
	 * logically store the impact it has on a battle. 
	 * NO LONGER CARE, but still identifies keywords, pulls logicalEffect, and identifies
	 * any quantities
	 */
	private void pullLogic() {
		String eff = new String();
		if (effect.charAt(0) == ':') { //No keyword effects
			eff = "";
			logicalEffect = effect.substring(1).toLowerCase(); //remove leading colon, lower case
			effect = effect.substring(1); //remove leading colon
		} else if (!effect.contains(":")) { //'<keyword>''
			eff = effect.toLowerCase();
		} else { //'<keywords>: <effect>'
			eff = effect.substring(0, effect.indexOf(':')).toLowerCase(); //before colon is keywords
			logicalEffect = effect.substring(effect.indexOf(':') + 1).toLowerCase(); //after is effect
		}

		
		boolean firstAdded = false;
		boolean secondAdded = false;
		if(!eff.equals("")) { //keywords
			if (eff.contains("iterate")) { //has iterate effect
				if (!firstAdded) { //not already added a keyword (impossible to have this time but applies to others)
					keyword1 = EFFECT_ITERATE; //set
					firstAdded = true; //flag
				}
				else { //already added
					keyword2 = EFFECT_ITERATE; //set second field
					secondAdded = true; //flag
				}
			}
			if (eff.contains("block")) {
				if (!firstAdded) {
					keyword1 = EFFECT_BLOCK;
					firstAdded = true;
				}
				else {
					keyword2 = EFFECT_BLOCK;
					secondAdded = true;
				}
			}
			if (eff.contains("deadline")) {
				if (!firstAdded) {
					keyword1 = EFFECT_DEADLINE;
					firstAdded = true;
				}
				else {
					keyword2 = EFFECT_DEADLINE;
					secondAdded = true;
				}
			}
			if (eff.contains("rush")) {
				if (!firstAdded) {
					keyword1 = EFFECT_RUSH;
					firstAdded = true;
				}
				else {
					keyword2 = EFFECT_RUSH;
					secondAdded = true;
				}
			}
			if (eff.contains("first call")) {
				if (!firstAdded) {
					keyword1 = EFFECT_FIRSTCALL;
					firstAdded = true;
				}
				else {
					keyword2 = EFFECT_FIRSTCALL;
					secondAdded = true;
				}
			}
			if (eff.contains("no effect")) {
				keyword1 = EFFECT_NULL;
				keyword2 = EFFECT_NULL;
			}
		} else {
			keyword1 = EFFECT_NULL;
			keyword2 = EFFECT_NULL;
		}
		if (!secondAdded) {
			keyword2 = EFFECT_NULL;
		}
		
		//Mostly useless from now on as unused apart from quantity setting (commented)
		firstAdded = false;
		secondAdded = false;
		if(logicalEffect.contains("deal")) {
			action = ACTION_DAMAGE;
			firstAdded = true;
		}
		if(logicalEffect.contains("gain")) {
			action = ACTION_GAIN;
			try {
				quantity1 = Integer.parseInt("" + logicalEffect.charAt(logicalEffect.indexOf("gain")+6)); //set attack increase
				quantity2 = Integer.parseInt("" + logicalEffect.charAt(logicalEffect.indexOf("gain")+9)); //set defence increase
				if(logicalEffect.charAt(logicalEffect.indexOf("gain")+5) == '-') //make negative if that is intended
					quantity1 *= -1;
				if(logicalEffect.charAt(logicalEffect.indexOf("gain")+8) == '-')
					quantity2 *= -1;
				logicalEffect = logicalEffect.replace("+", ""); //remove symbols from logical effect so reads '... X/X ..'
				logicalEffect = logicalEffect.replace("-", "");
			} catch (NumberFormatException e) {
				System.out.println("ERROR IN EXTRACTING GAIN VAL");
			}
			firstAdded = true;
		}
		if(logicalEffect.contains("nullify")) {
			action = ACTION_NULLIFY;
			firstAdded = true;
		}
		if(logicalEffect.contains("destroy")) {
			action = ACTION_DESTROY;
			firstAdded = true;
		}
		if(logicalEffect.contains("draw")) {
			action = ACTION_DRAW;
			firstAdded = true;
		}
		if(logicalEffect.contains("restore")) {
			action = ACTION_RESTORE;
			firstAdded = true;
		}
		if(logicalEffect.contains("give")) {
			action = ACTION_GIVE;
			try {
				quantity1 = Integer.parseInt("" + logicalEffect.charAt(logicalEffect.indexOf("give")+6)); //see gain above
				quantity2 = Integer.parseInt("" + logicalEffect.charAt(logicalEffect.indexOf("give")+9));
				if(logicalEffect.charAt(logicalEffect.indexOf("give")+5) == '-')
					quantity1 *= -1;
				if(logicalEffect.charAt(logicalEffect.indexOf("give")+8) == '-')
					quantity2 *= -1;
				logicalEffect = logicalEffect.replace("+", "");
				logicalEffect = logicalEffect.replace("-", "");
			} catch (NumberFormatException e) {
				System.out.println("ERROR IN EXTRACTING GAIN VAL");
			}
			firstAdded = true;
		}
		if(!firstAdded)
			action = ACTION_NULL;
		
		firstAdded = false;
		secondAdded = false;
		if(logicalEffect.contains("opponent")) {
			target = TARGET_OPPONENT;
			firstAdded = true;
		}
		if(logicalEffect.contains("minion")) {
			if(logicalEffect.contains("enemy")) {
				if(logicalEffect.contains("random")) {
					target = TARGET_RANDENEMYMIN;
				}
				else if (logicalEffect.contains("all")) {
					target = TARGET_ALLENEMYMIN;
				} else {
					target = TARGET_ENEMYMIN;
				}
			} else if (logicalEffect.contains("friendly")) {
				if(logicalEffect.contains("random")) {
					target = TARGET_RANDFRIENDLYMIN;
				}
				else if (logicalEffect.contains("all")) {
					target = TARGET_ALLFRIENDLYMIN;
				} else {
					target = TARGET_FRIENDLYMIN;
				}
			} else if (logicalEffect.contains("all")) {
				target = TARGET_ALLMINION;
			} else {
				target = TARGET_MINION;
			}
			firstAdded = true;
		}
		if(logicalEffect.contains("yourself")) {
			target = TARGET_SELF;
			firstAdded = true;
		}
		if(!firstAdded)
			target = TARGET_NULL;
		
		firstAdded = false;
		secondAdded = false;
		if(logicalEffect.contains("turn")) {
			if(logicalEffect.contains("opponent")) {
				if(logicalEffect.contains("start")) {
					time = TIME_OPPSTART;
				} else if(logicalEffect.contains("end")) {
					time = TIME_OPPEND;
				}
			} else if(logicalEffect.contains("your")) {
				if(logicalEffect.contains("start")) {
					time = TIME_MYSTART;
				} else if(logicalEffect.contains("end")) {
					time = TIME_MYEND;
				}
			} else if(logicalEffect.contains("each")) {
				if(logicalEffect.contains("start")) {
					time = TIME_EACHSTART;
				} else if(logicalEffect.contains("end")) {
					time = TIME_EACHEND;
				}
			}
			firstAdded = true;
		}
		if(!firstAdded) {
			time = TIME_NULL;
		}
		
		if(!(logicalEffect.contains("gain") || logicalEffect.contains("give"))) { //not already set
			quantity1 = getQuant(logicalEffect); //find it
		}
		
		for(int p = 0; p < logicalEffect.length(); p++) { //for each character
			if(Character.isDigit(logicalEffect.charAt(p))) { //if it is a digit
				if(logicalEffect.charAt(p-1) == 'X') { //if we have already set position flag for digit
					logicalEffect = logicalEffect.substring(0,p) + logicalEffect.substring(p+1); //remove character
				} else { //otherwise
					logicalEffect = logicalEffect.substring(0,p) + 'X' + logicalEffect.substring(p+1); //set position flag
				}
			}
		}
		logicalEffect = logicalEffect.trim(); //remove leading white space
	}

	/**
	 * Given a string, find a numerical character sequence and return the int represented
	 * @param s the string to get the int out of
	 * @return any value in the string, returning -1 if nothing found
	 */
	private int getQuant(String s) {
		boolean found = false;
		int i = 0;
		String n = new String();
		while((!found) && i < s.length()) { //for every character until numerical sequence found
			if(Character.isDigit(s.charAt(i))) { //if character is number
				n = n + s.charAt(i); //add character to number string
				if(s.charAt(i+1) == ' ') { //until we hit a space
					found = true;
				}
			}
			i++;
		}
		if(found) { //got a result
			return Integer.parseInt(n); //get value
		} else { 
			return -1; //default failure value
		}
	}

	/**
	 * Get the unique id of the card as stored in the database
	 * @return the id
	 */
	public int getId() {
		return id;
	}

	/**
	 * Retrieve the name of the card
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * Retrieve the set that the card is in
	 * @return the set (e.g. Programming / Robotics)
	 */
	public String getSet() {
		return set;
	}

	/**
	 * Retrieve the mana cost of using the card in a battle
	 * @return the cost as an int
	 */
	public int getCost() {
		return cost;
	}

	/**
	 * Retrieve the full unformatted effect string of the card
	 * as it is stored in the database or in offline save files
	 * @return the full effect
	 */
	public String getDBEffect() {
		return dbEffect;
	}
	
	/**
	 * Retrieve the formatted effect string for displaying the card
	 * @return the effect
	 */
	public String getEffect() {
		return effect;
	}
	
	/**
	 * Where the card is a minion card, retrieve the current attack value
	 * @return the current attack level of the minion
	 */
	public int getAttack() {
		return minion.getAttack();
	}
	
	/**
	 * Where the card is a minion card, retrieve the maximum attack value it can have
	 * @return the maximum attack level of the minion
	 */
	public int getMaxAttack() {
		return minion.getMaxAttack();
	}
	
	/**
	 * Where the card is a minion card, retrieve the current defence value
	 * @return the current defence level of the minion
	 */
	public int getDefence() {
		return minion.getDefence();
	}
	
	/**
	 * Where the card is a minion card, retrieve the maximum defence value it can have
	 * @return the maximum defence level of the minion
	 */
	public int getMaxDefence() {
		return minion.getMaxDefence();
	}
	
	/**
	 * Where the card is a minion card, retrieve all data about a minion
	 * @return a Minion object with all data as stored within the Card
	 */
	public Minion getMinion() {
		return minion;
	}
	
	/**
	 * Change the effect stored on a card
	 * @param s the string to prepend to the card's current effect
	 */
	public void setEffect(String s) {
		effect = s + effect;
	}

	/**
	 * Change the current attack level of a Minion
	 * @param a the new attack level
	 */
	public void setAttack(int a) {
		minion.setAttack(a);
	}
	
	/**
	 * Change the maximum attack level a minion can have
	 * @param a the new maximum level
	 */
	public void setMaxAttack(int a) {
		minion.setMaxAttack(a);
	}
	
	/**
	 * Change the current defence level of a minion
	 * @param d the new defence level
	 */
	public void setDefence(int d) {
		minion.setDefence(d);
	}
	
	/**
	 * Change the maximum defence level a minion can have
	 * @param d the new maximum level
	 */
	public void setMaxDefence(int d) {
		minion.setMaxDefence(d);
	}
	
	/**
	 * Retrieve whether the Card is a Minion card, or just a hex
	 * @return true if it is a minion, false if it is a hex
	 */
	public boolean isMinion() {
		return !minion.isNull();
	}
	
	public String toString() {
		if (!isMinion()) {
			return ("HEX Card: ID = " + id + "; Name = " + name + "; Set = " + set + "; Cost = " + cost + ".\nEffect is " + effect);
		} else {
			return ("MINION Card: ID = " + id + "; Name = " + name + "; Set = " + set + "; Cost = " + cost + "; Attack = " + getAttack() + "; Defence = " + getDefence() + ".\nEffect is " + effect);
		}
	}
}
