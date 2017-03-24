package havabol;

import java.util.ArrayList;
import java.util.Stack;

/**
 * Takes in a infix expression. example "5+3" or "(5*(5+2)" and returns the
 * computed result
 * 
 * @author Jason Luna
 *
 */
public class InfixEvaluation {
	private Parser errParser;
	
	private ArrayList<String> tokens = new ArrayList<String>(); //creates tokes

	Stack<String> operatorStack = new Stack<String>(); //holds the operators
	Stack<Double> valueStack = new Stack<Double>(); //keeps a running total of the stack

	private String buffer = "";
	public int pointer = 0;
	double result = 0.0;

	
	public InfixEvaluation(Parser p) {

		errParser = p; //this scanner is used for reference to send errors too

	}
	
	/**
	 * creates Stack of terms and operators and returns the caculation
	 * @param term
	 * @return Double total
	 */
	public double Calculate(String term){ 
		
		term = term.replaceAll(" ", ""); //strips whitespace
		
		if(!isValid(term)){
			System.out.println("Term invalid");
			//return an error to the scanner
		}
		
		String token;
		term = "(" + term + ")"; //creates end points
		System.out.println(term);
		getInfixTokens(term);
		//DEBUG
		
		
		while (hasMoreTokens()){
			
			token = nextToken();
			
			//DEBUG:
			//System.out.print(" " + token);
			
			Double total = 0.0;
			//System.out.print(total + "(");
			if (token.equals("(")){
				operatorStack.push(token);
			}
			
			else if (!isOperator(token) && !token.equals(")")) {
			
				total = Double.parseDouble(token);
				//DEBUG
				//System.out.print(total + "( ");
				valueStack.push(total);
				
			} else if (token.equals(")")){
			
				while (operatorStack.peek().equals("(") == false){ //checks to see if your at the end
					String operator = (String) operatorStack.pop(); 
					Double val1 = (double) valueStack.pop();
					Double val2 = (double) valueStack.pop();
					total = evaluate(operator, val1, val2);
					//System.out.print("debug: " + val1 + " " + operator + " "  + val2 + " = " + total + " \n");
					valueStack.push(total);	
				}
				operatorStack.pop();
				
			} else if (isOperator(token)) {
			
				while (operatorStack.isEmpty() == false && (getPriority(operatorStack.peek()) >= getPriority(token))){
					String operator = operatorStack.pop();
					double val1 = valueStack.pop();
					double val2 =  valueStack.pop();
					total = evaluate(operator, val1, val2);
					//System.out.print("debug: " + val1 + " " + operator + " "  + val2 + " = " + total + " \n");
					valueStack.push(total);	
					
				}
				operatorStack.push(token);
				//System.out.print(token + " )");
			}
		}
		
		result = (double) valueStack.pop();
		
		return result;
	}
	
	/**
	 * takes in a string of numbers and operators and parses it to tokens
	 */
	public void getInfixTokens(String term) {
		
		// we need to check if there is a ^ character
		// if there is the next character must be a '(' followed by a number,
		// followed by another ')'	
		for (int i = 0; i < term.length(); i++) {
			String character = term.substring(i, i + 1);
			if (character.equals("^")) {
				term = term.substring(0, i + 1) + "(" + term.substring(i + 1) + ")";
				i += 2;
			}
		}

		// we need to split term on "()*/+-^"
		char[] chars = term.toCharArray();

		for (int i = 0; i < chars.length; i++) {

			switch (chars[i]) {
			case '(':
				tokenFind();
				tokens.add("(");
				break;
			case ')':
				tokenFind();
				tokens.add(")");
				break;
			case '*':
				tokenFind();
				tokens.add("*");
				break;
			case '/':
				tokenFind();
				tokens.add("/");
				break;
			case '+':
				tokenFind();
				tokens.add("+");
				break;
			case '-':
				if ((i == 0 && isExpOperator(chars[i + 1])) || chars[i + 1] == '(') {
					tokens.add("-1");
					tokens.add("*");
				} else if (isExpOperator(chars[i - 1])) {
					buffer += chars[i];
				} else {
					tokenFind();
					tokens.add("-");
				}
				break;
			case '^':
				tokenFind();
				tokens.add("^");
				break;
			default:
				buffer += chars[i];
				break;
			}

		}

	}

	// looks for numbers
	private void tokenFind() {

		if (buffer.length() > 0) {
			tokens.add(buffer);
		}
		buffer = "";
	}
	
	/*determins if the character is an operator*/
	private boolean isExpOperator(char c) {
		if (c == '+' || c == '-' || c == '*' || c == '/' || c == '(' || c == '^')
			return true;
		return false;
	}
	
	/*simple tokenizer method*/
	public boolean hasMoreElements() {
		if (pointer < tokens.size()) return true;
		return false;
	}
	
	/*kept for compatibility to StringTokenizer*/
	public boolean hasMoreTokens() {
		return hasMoreElements();
	}

	/*returns the next element in the string*/
	public String nextElement() {
		String element = tokens.get(pointer);
		pointer++;
		return element;
	}
	
	/*kept for compatibility to StringTokenizer*/
	public String nextToken() {	
		return nextElement();
	}
	
	/*does math arithmatic using Utility*/
	public double evaluate(String operator, double val1, double val2){
		HavabolUtilities u1 = new HavabolUtilities();
		double result = 0.0;

		if (operator.equals("^")){
			result = u1.exp(val2, val1);
		}
		if (operator.equals("+")){
			result = u1.add(val1, val2);
		}
		if (operator.equals("-")){
			result = u1.sub(val2, val1);
		}
		if (operator.equals("*")){
			result = u1.mul(val1, val2);
		}
		if (operator.equals("/")){
			result = u1.div(val2, val1);
		}
		
		return result;
	}
	
	/*takes in a string and determins if it's an operator*/
	public boolean isOperator(String ch){
		if(ch.equals("+") || ch.equals("-") || ch.equals("*") || ch.equals("/") || ch.equals("^")){
			return true;
		}
		return false;
	}
	
	/*creates a priority list number*/
	public int getPriority(String ch){
		int priority = 0;
		
		if (ch.equals("^")){
			priority = 3;
		}
		else if (ch.equals("*") || ch.equals("/")){
			priority = 2; 
		}
		else if (ch.equals("+") || ch.equals("-")){
			priority = 1;
		}
		else if (ch.equals("(") || ch.equals(")")){
			priority = 0;
		}
		
		return priority;
	}
	
	/*helper method makes sure if the term is even valid, making sure the '(' are equal*/
	public boolean isValid(String term){
		boolean valid = true;

		int tmp = 0;
		for (Character c : term.toCharArray()){
			if (c.equals('(')){
				tmp++;
			}
			if (c.equals(')')){
				tmp--;
			}
			
		}
		if (tmp != 0){
			return false;
		}
	
		return valid;
	}
	
	//Debug Main Method to test infix
	
	/*public static void main(String[] args) {
		double testResult;
		Scanner s1 = null;
		//test cases
		
		//String doubleTest = "(5.3)/2.2*2.6/(1.22+4.4)*(66.33/11.11)^(2.7)";
		String test = "(5)/2*2/(1+4)*(66/11)^(2)";
		//test = "((3 + 4) * (5 + 2))";
		
		InfixEvaluation t1 = new InfixEvaluation(s1);
		
		//testResult = t1.Calculate(doubleTest);
		testResult = t1.Calculate(test);
		
		System.out.println(testResult);
	}*/

}