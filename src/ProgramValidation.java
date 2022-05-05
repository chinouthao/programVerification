import java.io.*;
import java.util.*;

/**
 * 
 */

/**
 * @author cthao002
 *
 */
public class ProgramValidation {

	/**
	 * @param args
	 */
	private static ArrayList<String> orderList = new ArrayList<String>();
	private static ArrayList<String> leftSide = new ArrayList<String>();
	private static ArrayList<String> rightSide = new ArrayList<String>();
	private static ArrayList<String> operation = new ArrayList<String>();
	private static ArrayList<String> var = new ArrayList<String>();
	private static ArrayList<String> num = new ArrayList<String>();

	private static String condition = "";
	private static Boolean needCondition = false;
	private static String variable = "";
	private static String prePost = "Post";

	public static void main(String[] args) {
		// read fileName
		Scanner fileInput = new Scanner(System.in);
		System.out.println("Enter a file: ");
		String fileName = fileInput.nextLine();
		System.out.println("Does file need a condition (Y or N) : ");
		String haveCondition = fileInput.nextLine();
		if (haveCondition.equals("Y")) {
			needCondition = true;
			System.out.println("Enter Condition (pre x>2, y>3 or post x>2) : ");
			condition = fileInput.nextLine();
		}
		fileInput.close();
		// open and read file
		try {
			// open file
			File file = new File(fileName);
			// read file
			Scanner reader = new Scanner(file);
			while (reader.hasNextLine()) {
				// read and add line by line to orderList
				String data = reader.nextLine().replaceAll("\\s", "");

				orderList.add(data);

			}
			reader.close();
		} catch (FileNotFoundException e) {
			System.out.println("File Not Found.");
			e.printStackTrace();
		}

		if (needCondition == false) {
			//contains assert or print and making it the post condition
			if (orderList.get(orderList.size()-1).contains("assert")
					|| orderList.get(orderList.size()-1).contains("print")) {
				condition = postStatement(orderList.get(orderList.size() -1));
				System.out.println("Post-Condtion: " + condition);
				prePost = "Pre";
				orderList.remove(orderList.size()-1);
				Collections.reverse(orderList);
			}
			// pre condition (if assert and print isn't the last thing)
			if (orderList.get(0).contains("assert") && !orderList.get(orderList.size()-1).contains("assert")
					&& !orderList.get(orderList.size()-1).contains("print")) {
				condition = postStatement(orderList.get(0));
				System.out.println("Pre-Condtion: " + condition);
				orderList.remove(0);
			}
			else {
				System.out.println("can't find condition");
			}
		}

		else {
			if (condition.contains("pre")){
				condition = condition.replaceAll("\\s", "");
				condition = condition.replace("pre", "");
				System.out.println("Pre-Condtion: " + condition);
			}
			else {
				condition = condition.replaceAll("\\s", "");
				condition = condition.replace("post", "");
				prePost = "Pre";
				Collections.reverse(orderList);
				System.out.println("Post-Condtion: " + condition);
			}
		}

//		for (int i=0; i<orderList.size(); i++) {
//			System.out.println(orderList.get(i));
//		}

		solving(orderList, condition);

	}

	public static void solving(ArrayList<String> order, String cond) {
		//add condtion as first in leftSide, Operation, and rightSide
		statement(condition);
		var.add(leftSide.get(0));
		num.add(rightSide.get(0));
	


		//seperating every line of the code on '=' and adding it to side/operation
		for (int x = 0; x < orderList.size(); x++) {
			statement(orderList.get(x));
		}


		//System.out.println("Check orderList: " + orderList);
		//		//going through list and if the left side has the same as condition and replace the condition wiith the varaible

		for (int x = 0; x<orderList.size();x++) {
			//if current order contains already define variable
			for (int i = 0; i<var.size();i++) {
				if (leftSide.get(x+1).contains(var.get(i))){
					String temp = leftSide.get(x+1);
					temp = temp.replace(var.get(i), num.get(i));
					leftSide.set(x+1, temp);
				}
				if (rightSide.get(x+1).contains(var.get(i))){
					String temp = rightSide.get(x+1);
					temp = temp.replace(var.get(i), num.get(i));
					rightSide.set(x+1, temp);
				}
			}
//			System.out.println("LeftSide " + leftSide);
//			System.out.println("operation " + operation);
//			System.out.println("RightSide " + rightSide);
			int solved = solveCondition(leftSide.get(x+1), rightSide.get(x+1));
			var.add(variable);
			num.add(String.valueOf(solved));

		}
		
		
		System.out.println(orderList);
		for (int t = 1; t<var.size(); t++) {
			System.out.println(prePost + "Condition: "  + var.get(t) + operation.get(0) + num.get(t));

		}
	}

	public static void statement(String line) {
		if (line.contains("=")) {
			String[] k = line.split("=");
			operation.add("=");
			leftSide.add(k[0]);
			rightSide.add(k[1]);

		}
		else if (line.contains(">")) {
			String[] k = line.split(">");
			operation.add(">");
			leftSide.add(k[0]);
			rightSide.add(k[1]);
		}
		else if (line.contains("<")) {
			String[] k = line.split("<");
			operation.add("<");
			leftSide.add(k[0]);
			rightSide.add(k[1]);
		}
	}

	public static String postStatement(String line) {
		if (line.contains("print") || line.contains("assert")) {
			// removing all print and assert in postcondition
			String newLine = line.replaceAll("print", "");
			newLine = newLine.replace("(", "");
			newLine = newLine.replace(")", "");
			newLine = newLine.replaceAll("assert", "");
			return newLine;
		}
		return line;
	}

	public static int solve(ArrayList<String> left, ArrayList<String> right) {

		return 0;
	}

	public static boolean allNum(String side) {
		for (int j = 0; j < side.length(); j++) {
			if (!Character.isDigit(side.charAt(j))){
				return false;
			}
		}
		return true;
	}

	public static int solveCondition(String left, String right) {
		//find the side that has the variable
		String solveFor = left;
		String side = right;
		if (allNum(left)) {
			solveFor = right;
			side = left;
		}
		int value = 0;
		findVariable(solveFor);

		boolean once = false;
		//get all the numbers to one side
		for (int j=0; j<solveFor.length();j++) {
			String fullnum = "";
			//if the current charater is a number
			if (Character.isDigit(solveFor.charAt(0)) && once == false) {
				while(Character.isDigit(solveFor.charAt(j))){
					fullnum += solveFor.charAt(j);
					j++;
				}
				if (fullnum != "") {
					side = side + "-" + fullnum;
				}
				once=true;
			}
			fullnum = "";
			if (solveFor.charAt(j) == '-') {
				j++;
				while(j<solveFor.length() &&
						Character.isDigit(solveFor.charAt(j))){
					fullnum += solveFor.charAt(j);
					j++;

				}
				if (fullnum != "") {
					side = side + "+" + fullnum;
				}
			}
			fullnum = "";
			if (solveFor.charAt(j) == '+') {
				j++;
				while(j<solveFor.length() &&
						Character.isDigit(solveFor.charAt(j))){
					fullnum += solveFor.charAt(j);
					j++;
				}
				if (fullnum != "") {
					side = side + "-" + fullnum;
				}
			}
		}
		

		//solve for one side
		for (int j=0; j<side.length();j++) {
			//first num
			String fullNum = "";
			once = false;
			if (Character.isDigit(side.charAt(0))&& once == false) {
				while(Character.isDigit(side.charAt(j))){
					fullNum += side.charAt(j);
					j++;
				}
				once=true;
				value += Integer.parseInt(fullNum);
			}
			fullNum = "";
			if (side.charAt(j) == '-') {
				j++;
				while(j<side.length() &&
						Character.isDigit(side.charAt(j))){
					fullNum += side.charAt(j);
					j++;

				}
				if (fullNum != "") {
					value = value - Integer.parseInt(fullNum);
				}
			}
			fullNum = "";
			if (side.charAt(j) == '+') {
				j++;
				while(j<side.length() &&
						Character.isDigit(side.charAt(j))){
					fullNum += side.charAt(j);
					j++;
				}
				if (fullNum != "") {
					value = value + Integer.parseInt(fullNum);
				}
			}
		}
		return value;
	}

	public static void findVariable (String statement) {
		for (int i=0; i<statement.length();i++) {
			if (Character.isLetter(statement.charAt(i))) {
				variable = Character.toString(statement.charAt(i));
			}
		}
	}


	public static boolean isNum(char num) {
		if (num == '1' || num == '2' || num == '3' || num == '4'
				|| num == '5' || num == '6' || num == '7' || num == '8'
				|| num == '9' || num == '0') {
			return true;
		}

		return false;
	}
	public static boolean isOperator(char check) {
		if (check == '-'|| check == '+' || check == '*' || check == '/') {
			return true;
		}
		return false;
	}
}
