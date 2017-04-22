package havabol;

import java.util.ArrayList;

public class Parser {

	public Scanner scan;
	public SymbolTable symbolTable;
	public StorageManager storageManager;
	public boolean isNegative = false;
	public Print printer;
	public String term = "";
	public ResultValue listEntry;
	public ArrayList<ResultValue> valueList;  //= new ArrayList<ResultValue>();

	public static final int INTEGER = 2; // integer constant
	public static final int FLOAT = 3; // float constant
	public static final int BOOLEAN = 4; // boolean constant
	public static final int STRING = 5; // string constant
	public static final int DATE = 6; // date constant

	private static final String[] comparisonOperators = { "==", ">", ">=", "<", "<=", "!=" }; // logical
																								// operators
	private static final String separators = "(),:;["; // string to check for
														// separators
	
	public static boolean bShowToken = false;
	public static boolean bShowAssign = false;
	public static boolean bShowExpr = false;
	

	public Parser(Scanner scan, SymbolTable symbolTable, StorageManager storageManager) throws Exception {
		this.scan = scan;
		this.symbolTable = symbolTable;
		this.storageManager = storageManager;
	}

	/**
	 * Parses the file based on the first token of the line until there are no
	 * more tokens left in the file
	 * <p>
	 * 
	 * @throws Exception
	 */

	public void statements() throws Exception {

		ResultValue result = new ResultValue();

		while (!scan.getNext().isEmpty()) {
			
			//check for debugging aids 
			if(scan.currentToken.tokenStr.equals("debug"))
			{
				scan.getNext();
				String debugType = scan.currentToken.tokenStr;
				scan.getNext();
				String debugOnOff = scan.currentToken.tokenStr;
				
				HavabolUtilities.debugInput(this, debugType, debugOnOff);
				
				scan.getNext();
				checkCurrentToken(";");
				scan.getNext();
				
			}
			
			// switch based on the first token in the statement

			switch (scan.currentToken.primClassif) {
			case Token.OPERAND:

				// switch based on the subclass
				switch (scan.currentToken.subClassif) {
				case Token.IDENTIFIER:
					result = assignmentStmt();
					break;

				default:
					error("Error in statements(), '%s'can't begin a line", scan.currentToken.tokenStr);
					
				}
				break; // from Token.OPERAND

			case Token.CONTROL:
				// switch based on the subclass
				switch (scan.currentToken.subClassif) {
				case Token.DECLARE:
					declareVariable();
					break;

				case Token.FLOW:
					switch (scan.currentToken.tokenStr) {
					case "if":
						ifStmt(true);
						break;

					case "while":
						whileStmt(true);
						break;

					case "else":
						// return back to the ifStmt method, it will determine
						// how to handle else statments
						return;
						
					case "for" :
						result = forStmt(true);
						continue;

					default:
						System.out.println("Error in statementTest(), " + scan.currentToken.tokenStr
								+ " is not a valid flow statment");
						break;
					}

				case Token.END:
					// return to the calling function, it will determine how to
					// handle
					
				
					
					
					
					return;

				}
				break; // from control

			case Token.FUNCTION:
				// is it user defined or built in
				switch (scan.currentToken.subClassif) {
				case Token.BUILTIN:
					result = builtInFunctions();
					break;

				// TODO will handle user defined in future
				}
				break; // from function

			}
		}

	}
	
	
	/**
	 * Parser has grabbed a "for" token
	 * Will iterate through the for statement and execute all expressions inside of the loop.  Also contains
	 * an optional "by" token which lets you set the increment value.  If "by" is not included then the default 
	 * increment value is 1.  Will also let you print all of the values in an array. 
	 * <p>
	 * 
	 * @param bExec		Boolean value to let us know if we should execute the loop 
	 * @return result	ResultValue back to the calling function to let it know that it is complete 
	 * @throws Exception
	 */
	
	public ResultValue forStmt(boolean bExec) throws Exception
	{
		ResultValue result = new ResultValue();
		Numeric numControl = null;	// numeric value of the control variable 
		Numeric numLimit = null; 	// numeric value of the limit variable 
		Numeric numIncr = null;		// numeric value of the increment variable 
		STEntry symbolTableEntry = null;
		StorageEntry storageEntry = null;
		
		if(bExec == true)
		{
			//current token is "for", get the next token 
			scan.getNext();
			
			if(scan.currentToken.subClassif == Token.DECLARE)
			{
				//TODO
				System.out.println("Handle this, token is being declared in the for statement");
				
			}
			
			//current token is an identifier
			String controlVar = scan.currentToken.tokenStr;	//the controlVariable
			
			
			//create a control variable if it hasn't already been declared 
			if (SymbolTable.getSymbol(controlVar) == null)
			{
				//may need to remove these when done  
				symbolTableEntry = new STIdentifier(controlVar, Token.OPERAND, "primitive", "not a parm", Token.INTEGER, 0);
				symbolTable.putSymbol(controlVar, symbolTableEntry);
				
				storageEntry = new StorageEntry(controlVar, Token.INTEGER, "0", null);
				storageManager.addVariable(controlVar, storageEntry);
				
			}
	
			
			else 
			{
				//control variable has already been declared 
				storageEntry = StorageManager.getStorageEntry(controlVar);
			}
			
			
			scan.getNext(); //should be an = or "in" 
			
			
			if(scan.currentToken.tokenStr.equals("in"))
			{
				
				ResultValue arrayControl = new ResultValue();
				
				//TODO next token should be an array or a string, add strings  
				scan.getNext();
				
				// if it is an array 
				StorageEntry arrayEntry = StorageManager.getStorageEntry(scan.currentToken.tokenStr);
				
				
				//TODO Must be an array or a string, add strings 
				if(StorageManager.isArray(scan.currentToken.tokenStr) == false)
					error("In for statement, '%s' is not an array");
				
				int numElements = arrayEntry.valueList.size();
				
				scan.getNext();
				checkCurrentToken(":");
				
				
				scan.getNext();
				
				//Save the position of the beginning of the for loop 
				Token beginningToken = scan.currentToken;
				Token endingToken = null;	// for the end of the for loop 
				
				for(int i = 0; i < numElements; i++)
				{
					
					while(!scan.currentToken.tokenStr.equals("endfor"))
					{
						//assign the value of the array element to the control variable 
						storageEntry.variableValue= arrayEntry.valueList.get(i).value;
						ResultValue expr = expression();
						scan.getNext();
					}
					
					// This is "endfor"
					endingToken = scan.currentToken;
					
					//return to the beginning of the for loop 
					scan.setPosition(beginningToken.iSourceLineNr, beginningToken.iColPos);
					scan.getNext();
				}
				
				// set to the end of the for loop and move to the next line 
				scan.setPosition(endingToken.iSourceLineNr, endingToken.iColPos);
				scan.getNext();
				scan.getNext();
				
				
			}
			
			else
			{
				
				ResultValue resValControl = expression();
			
				//assign value to the control variable 
				assign(controlVar, resValControl);
			
				//need a numeric value for the control variable
				numControl = new Numeric(this, resValControl, "=", "for loop control variable");
			
				
				//current token is where you want the limit for the control variable, need the numeric value
				ResultValue resValLimit = expression();
				numLimit = new Numeric(this, resValLimit, "=", "for loop limit value");
				
				
				if(scan.nextToken.tokenStr.equals(":"))
					scan.getNext();
				
				
				if(scan.currentToken.tokenStr.equals(":"))
				{
					
					ResultValue resIncr = new ResultValue();
					resIncr.type = Token.INTEGER;
					resIncr.value = "1";
					numIncr = new Numeric(this, resIncr, "=", "for loop increment value");
				}
				
				else
				{
					
					scan.getNext();
					//current token is the increment value, need the numeric value
					ResultValue resIncr = expression();
					numIncr = new Numeric(this, resIncr, "=", "for loop increment value");
					scan.getNext();
				}
				
				
		 
				if(scan.currentToken.tokenStr.equals("by"))
				{
					scan.getNext();
					ResultValue resIncr = returnValue();
					numIncr = new Numeric(this, resIncr, "=", "for loop increment value");
					scan.getNext();
				}
				
				//current token should now be a :
				checkCurrentToken(":");
			
			
				//save the starting point of the next token 
				Token startingPoint = scan.nextToken;
	
			
				while(numControl.integerValue < numLimit.integerValue)
				{
					//set the position to the first statement in the for block 
					scan.setPosition(startingPoint.iSourceLineNr, startingPoint.iColPos);
				
					//handle all the statements in the for block 
					statements();
				
					//statements returned "endfor" 
					//get the current value of the control variable in case any of the statements changed its value 
				
					resValControl = getIdentifier(controlVar);
					numControl.integerValue = Numeric.getIntegerValue(resValControl.value);
				
					//increment the control variable 
					numControl.integerValue += numIncr.integerValue;
				
					//assign the value of the control variable 
					resValControl.value = String.valueOf(numControl.integerValue);
				
					assign(controlVar, resValControl);
				
				}
			
				scan.getNext();
			}
		}
		
		return result;
	}
	

	
	
	/**
	 * The parser has hit the token "while"
	 * <p>
	 * This method will iterate through the while statement and reset the line
	 * to the start of the while statement when it encounters an "endwhile". If
	 * the comparison is false, it will ignore all lines until it encounters the
	 * correct "endwhile".
	 * 
	 * 
	 * @param bExec
	 *            Tells us if we should execute the while statement or skip it.
	 *            If skipped then we need to move to the correct "endwhile" and
	 *            ignore all statements in between.
	 * @throws Exception
	 */

	public void whileStmt(boolean bExec) throws Exception {
		if (bExec == true) {
			scan.getNext();
			// save the position of the while statement so we can check the
			// condition again
			Token startingPoint = scan.currentToken;

			// Evaluate the while statement condition
			ResultValue resCond = evalCondition();

			scan.getNext();

			if (!scan.currentToken.tokenStr.equals(":"))
				System.out.println("Error in whileStmt, missing a colon, current token is " + scan.currentToken);

			if (resCond.value.equals("T")) {

				while (resCond.value.equals("T")) {
					statements();
					// returned endwhile, check the condition again
					scan.setPosition(startingPoint.iSourceLineNr, startingPoint.iColPos);
					scan.getNext();

					resCond = evalCondition();
				}

				// resCond returned false, break out of the while loop
				whileStmt(false);

				scan.getNext();
				if (!scan.currentToken.tokenStr.equals(";"))
					System.out.println("Error in whileStmt, expected a ; ");

			}

			else if (resCond.value.equals("F")) {
				// initial evaluation of the while statement was false, move
				// past the correct while statement
				while (!scan.currentToken.tokenStr.equals("endwhile"))
					scan.getNext();

				// currentToken is endwhile
				scan.getNext();
				if (!scan.currentToken.tokenStr.equals(";"))
					System.out.println("Error in while statement when rescond is false, no semicolon");
			}

			statements();

		} // end of bExec = true

		// ignore execution of the while statement and move to the statement
		// after the correct endwhile
		if (bExec == false) {
			while (!scan.currentToken.tokenStr.equals("endwhile")) {
				if (scan.currentToken.tokenStr.equals("while")) {
					while (!scan.currentToken.tokenStr.equals("endwhile"))
						scan.getNext();
				}
				scan.getNext();
			}

			// token is endwhile
			return;
		}

	}

	/**
	 * Parser has encountered an "if" when parsing the line
	 * <p>
	 * Handles flow of if and if-else statements as well as nested if and
	 * if-else statements
	 * 
	 * @param bExec
	 *            Tells the function if it should execute the if condition
	 * @throws Exception
	 */

	public void ifStmt(boolean bExec) throws Exception {

		if (bExec == true) {
			
			scan.getNext();
			// evaluate the condition
			ResultValue resCond = evalCondition();

			// make sure the next token is a :
			if (!scan.currentToken.tokenStr.equals(":"))
				scan.getNext();

			if (!scan.currentToken.tokenStr.equals(":"))
				System.out.println("Error in ifStmt, " + scan.currentToken.tokenStr + " does not equal :");

			// conditional statement was true
			if (resCond.value.equals("T")) {
				while (!scan.currentToken.tokenStr.equals("endif")) {
					
					statements();
					

					// if "else" was returned, need to skip
					if (scan.currentToken.tokenStr.equals("else"))
						ifStmt(false);
				}
				
				// currentToken is "endif", make sure the next token is ;
				scan.getNext();

				if (!scan.currentToken.tokenStr.equals(";"))
					System.out.println("Error in ifStmt, current token should be ;, not " + scan.currentToken.tokenStr);

			}

			// conditional statement was false
			else if (resCond.value.equals("F")) {

				while (!scan.currentToken.tokenStr.equals("else")) {
					scan.getNext();
					// if you hit another if statement, skip it
					if (scan.currentToken.tokenStr.equals("if")) {
						while (!scan.currentToken.tokenStr.equals("endif"))
							scan.getNext();
						// get the token after the endif
						scan.getNext();
					}

					// skip to the else statement, if there is no else just skip
					// the if statement
					// there was no else
					if (scan.currentToken.tokenStr.equals("endif")) {
						scan.getNext();
						if (!scan.currentToken.tokenStr.equals(";"))
							System.out.println("Error in ifStmt, missing a semicolon");
						return;
					}

				}

				// current token is else
				scan.getNext();

				if (!scan.currentToken.tokenStr.equals(":"))
					System.out.println("Error in ifStmt, missing a ; in the false part, current token is "
							+ scan.currentToken.tokenStr);

				// execute the else portion of the if statmement
				while (!scan.currentToken.tokenStr.equals("endif")) {
					statements();
				}

				// endif, check for a ;
				scan.getNext();

				if (!scan.currentToken.tokenStr.equals(";"))
					System.out.println("In if statement, missing a ;, current token is " + scan.currentToken.tokenStr);
				
				
			}  //end of the elseif 
			
			
			

			// finished with the execution of the if statement, get the next
			// statement
			
			scan.getNext();
			
			return;
			
		} // end of bExec is true

		// don't execute any statement until you get to the correct endif
		// statement
		else if (bExec == false) {
			// keep grabbing tokens until you find the correct endif
			while (!scan.nextToken.tokenStr.equals("endif")) {
				// need to get the matching endif if you hit another if
				// statement
				if (scan.currentToken.tokenStr.equals("if")) {
					while (!scan.currentToken.tokenStr.equals("endif"))
						scan.getNext();
				}

				scan.getNext();
			}

			// next Token is endif
			scan.getNext();

			return;

		} // end of false bExec

	}

	/**
	 * Evaluates the result of an if or while statement and returns the result
	 * of the operation
	 * <p>
	 * 
	 * @return a ResultValue object that contains either "T" or "F" as the value
	 * @throws Exception
	 */

	public ResultValue evalCondition() throws Exception {
		ResultValue result = new ResultValue();
		ResultValue resOp1;
		ResultValue resOp2;
		String compOperator;


		if(scan.currentToken.tokenStr.equals("not"))
		{
			scan.getNext();
			StorageEntry boolVal = StorageManager.getStorageEntry(scan.currentToken.tokenStr);
			
			if(boolVal.variableValue.equals("T"))
				result.value = "F";
			else
				result.value = "T";
			
			
			
			return result;
			
		}
		
		
		// get the first expression
		resOp1 = expression();

		if (searchForValue(comparisonOperators, scan.currentToken.tokenStr) == false)
			System.out.println(
					"Error in evalCondition, " + scan.currentToken.tokenStr + " is not a valid comparison operator");

		compOperator = scan.currentToken.tokenStr;

		// get the expression that will be compared with resOp1
		scan.getNext();

		if (scan.currentToken.primClassif != Token.OPERAND)
			System.out.println("Error in evalCondition, expecting an operand, not " + scan.currentToken.tokenStr);

		resOp2 = expression();

		// If the left operand is a number, compare it with the right operand
		if (resOp1.type == INTEGER || resOp1.type == FLOAT) {
			// compare the numeric values

			if (HavabolUtilities.compareNumerics(this, resOp1, resOp2, compOperator))
				result.value = "T";
			else
				result.value = "F";
		}

		else {
			// left operand is a string
			// compare strings
			switch (compOperator) {
			case ("=="):
				if (resOp1.value.equals(resOp2.value))
					result.value = "T";
				else
					result.value = "F";
				break;

			case (">"):
				if (resOp1.value.compareTo(resOp2.value) > 0)
					result.value = "T";
				else
					result.value = "F";
				break;

			case (">="):
				if (resOp1.value.compareTo(resOp2.value) >= 0)
					result.value = "T";
				else
					result.value = "F";
				break;

			case ("<"):
				if (resOp1.value.compareTo(resOp2.value) < 0)
					result.value = "T";
				else
					result.value = "F";
				break;

			case ("<="):
				if (resOp1.value.compareTo(resOp2.value) >= 0)
					result.value = "T";
				else
					result.value = "F";
				break;

			case ("!="):
				if (resOp1.value.equals(resOp2.value))
					result.value = "F";
				else
					result.value = "T";
				break;

			default:
				System.out.println("Error in evalCondition, " + compOperator + "is not a valid comparison operator");
				break;
			}
		}

		return result;
	}
	
	
	
	
	
	
	/**
	 * Parser has encountered a function 
	 * <p>
	 * @return ResultValue to let the calling function know it is done 
	 * @throws Exception
	 */
	public ResultValue functions() throws Exception 
	{
		ResultValue result = new ResultValue();
		
		if(scan.currentToken.subClassif == Token.BUILTIN)
			result = builtInFunctions();
		
		//else
			//TODO user-defined functions 
			
		return result;
	}
	
	
	/**
	 *  Statement is a built in function.  Call the specified built-in function 
	 *  <p>
	 *  
	 * @throws Exception	If the function is not one of the built in functions 
	 */
	public ResultValue builtInFunctions() throws Exception
	{
		ResultValue result = new ResultValue();
		ResultValue returnedResult = new ResultValue();
		
		//switch based on current token 
		switch(scan.currentToken.tokenStr)
		{
			case "print" :
				printFunction();
				break;
				
			case "LENGTH" : 
				returnedResult = lengthFunction();
				break;
				
			case "SPACES" : 
				returnedResult = spacesFunction();
				break;
				
			case "ELEM" :
				returnedResult = elemFunction();
				break;
				
			case "MAXELEM" :
				returnedResult = maxElemFunction();
				break;
				
			default :
				error("Error in builtInFunctions, '%s' is not a built-in function", scan.currentToken.tokenStr);
				
				
		}
		
		return returnedResult;
		
	}
	
	
	
	/**
	 * 	Built-in function "MAXELEM"
	 * <p>
	 * @return				The number of declared elements  	
	 * @throws Exception
	 */
	public ResultValue maxElemFunction() throws Exception 
	{
		
		ResultValue returnedValue = new ResultValue();
		
		scan.getNext();
		checkCurrentToken("(");
		scan.getNext();
		
		StorageEntry arrayName = StorageManager.getStorageEntry(scan.currentToken.tokenStr);
		
		if(StorageManager.isArray(scan.currentToken.tokenStr) == false)
			error("In elemFunction, variable '%s' has not been declared ", scan.currentToken.tokenStr);
		
		
		returnedValue.type = Token.INTEGER;
		returnedValue.value = String.valueOf(arrayName.valueList.size());
		
		
		scan.getNext(); // should be ")"
		scan.getNext();
		
		return returnedValue;
	}
	
	
	
	
	/**
	 * Built-in function ELEM
	 * <p>
	 * @return	Highest populated index in an array   
	 * @throws Exception
	 */
	public ResultValue elemFunction() throws Exception
	{
		ResultValue returnedValue = new ResultValue();
		
		scan.getNext();
		checkCurrentToken("(");
		scan.getNext();
		
		StorageEntry arrayName = StorageManager.getStorageEntry(scan.currentToken.tokenStr);
		
		if(arrayName == null)
			error("In elemFunction, variable '%s' has not been declared ", scan.currentToken.tokenStr);
		
		if(StorageManager.isArray(scan.currentToken.tokenStr) == false)
			error("In elemFunction, '%s' is not an array", scan.currentToken.tokenStr);
		
		int size = arrayName.valueList.size();
		
		
	
		int largestIndex = 0;
		
		for(int i = 0; i < size; i++)
		{
			if(arrayName.valueList.get(i).value != null && !arrayName.valueList.get(i).value.trim().isEmpty())
			{
				largestIndex = i;
			}
			
		}
		
		if(largestIndex == 0 && arrayName.valueList.get(0).value.trim().isEmpty())
			largestIndex = 0;
		
		else
			largestIndex++;
		
		returnedValue.value = String.valueOf(largestIndex);
		returnedValue.type = Token.INTEGER;
		
		scan.getNext(); // Should be ")"
		scan.getNext();
		
		
		return returnedValue;
		
		
	}
	
	
	
	/**
	 *  Built-in function SPACES
	 *  <p>
	 * @return	T if the string contains only spaces or tabs or is null 
	 * 	        F if the string contains any other value  
	 * @throws Exception
	 */
	public ResultValue spacesFunction() throws Exception
	{
		ResultValue returnedValue = new ResultValue();
		ResultValue result;
		
		
		scan.getNext();
		checkCurrentToken("(");
		
		//get the variable 
		scan.getNext();
		
		result = returnValue();
		
		if(result.value.trim().length() > 0)
			returnedValue.value = "F";
		else
			returnedValue.value = "T";
		
		scan.getNext();
		
		
		
		return returnedValue;
	}
	
	
	/**
	 * Built-in function LENGTH 
	 * <p>
	 * @return The lenth of the specified string 
	 * @throws Exception
	 */
	public ResultValue lengthFunction() throws Exception
	{
		int length = 0;
		ResultValue returnedLength = new ResultValue();
		ResultValue result = new ResultValue(); 
		 
		scan.getNext();
		checkCurrentToken("(");
		scan.getNext();
		
		
		if(scan.nextToken.tokenStr.equals(")"))
		{
			//it is a variable or a constant 
			result = returnValue();
			scan.getNext();
		}
		
		else if(scan.nextToken.tokenStr.equals("#"))
		{
			result = concatString();
			scan.getNext();
		}
		
		else if(scan.currentToken.primClassif == Token.FUNCTION)
		{
			result = functions();
			scan.getNext();
		}
		
		//get the length of the variable and return it 
		length = result.value.length();
		returnedLength.value = String.valueOf(length);
		
		return returnedLength;
	}
	
	

	

	/**
	 * Handles the built-in function, print. Called from the builtInFunctions
	 * method. Prints a space if a ',' is encountered. Also, looks up variables
	 * and prints their value
	 * <p>
	 * 
	 * @throws Exception
	 *             if not surrounded by parenthesis
	 */
	public void printFunction() throws Exception {
		
		ResultValue result = new ResultValue();
		
		
		scan.getNext();

		if (!scan.currentToken.tokenStr.equals("("))
			System.out.println("Error in the print function, " + scan.currentToken.tokenStr + "is not equal to (");
		
		

		scan.getNext();
		
		

		while (!scan.currentToken.tokenStr.equals(")")) {
			
			// if you hit a ; before the right paren it is an error
			if (scan.currentToken.tokenStr.equals(";")) {
				break;
			}

			// if you hit a , need to add a space
			if (scan.currentToken.tokenStr.equals(",")) {
				result.value += " ";
			}

			else if (scan.currentToken.primClassif == Token.OPERAND) {
				
				ResultValue opValue = expression();
				result.value += opValue.value;

				if(scan.currentToken.tokenStr.equals(","))
					result.value += " ";

			}
			
			else if (scan.currentToken.primClassif == Token.FUNCTION)
			{
				ResultValue expRes = expression();
				result.value += expRes.value;
	
			}

			// Not an operand, it is a string literal
			else {

				result.value += scan.currentToken.tokenStr;
			}

			scan.getNext();

		}
		
		
		
		

		printer = new Print(result);
		
		

		// currentToken is a )
		if (!scan.currentToken.tokenStr.equals(";")) {
			scan.getNext();
		}

		if (!scan.currentToken.tokenStr.equals(";")) {
			System.out.println("Error in printFunction, missing a;, current token is " + scan.currentToken.tokenStr);
		}

		
		
		
		return;
	}

	
	
	/**
	 * Parser encountered an identifier, need to assign it based on the operator
	 * <p>
	 * >
	 * 
	 * @return The value of the variable
	 * @throws Exception
	 */
	public ResultValue assignmentStmt() throws Exception {
		ResultValue res = null;
		ResultValue resO1 = null;
		ResultValue resO2;
		ResultValue arrIndex = null;
		String variableStr;
		String operatorStr;
		int index = 0;
		
		
		
		// getIdentifier method will handle the error if it is not an identifier
		// make sure the variable has been declared
		if (StorageManager.isDeclared(scan.currentToken.tokenStr) == false)
			System.out.println("Error in assignmentStmt, current token has not been declared");

		variableStr = scan.currentToken.tokenStr;

		// get the assignment operator
		scan.getNext();
		
		StorageEntry varEntry = StorageManager.getStorageEntry(variableStr);
		
		
		
		
		
		
		//check for array 
		if(varEntry.valueList != null)
		{
			
			
			
			if(scan.currentToken.tokenStr.equals("="))
			{
				//fill every index with the value 
				scan.getNext();
				
				ResultValue arrValue = new ResultValue();
				
				if(scan.currentToken.subClassif == Token.IDENTIFIER)
				{
					StorageEntry assignedVal = StorageManager.getStorageEntry(scan.currentToken.tokenStr);
					
					if(StorageManager.isArray(scan.currentToken.tokenStr))
					{
						for(int i = 0; i < varEntry.valueList.size(); i++)
						{
							if(i >= assignedVal.valueList.size())
								break;
							
							varEntry.valueList.set(i, assignedVal.valueList.get(i));
						}
						
						scan.getNext();
						return arrValue;
						
					}
					
					
					
					
				}
				
				else
				{
					arrValue.value = scan.currentToken.tokenStr;
					arrValue.type = varEntry.dataType;
				
					for(int i = 0; i < varEntry.valueList.size(); i++)
						varEntry.valueList.set(i, arrValue);
				
					scan.getNext();
					return arrValue;
				}
			
			}
			
			else if(scan.currentToken.tokenStr.equals("["))
			{
				scan.getNext();
				index = getArrayIndex();
				scan.getNext();
			}
		}
		
		//is it a string, check if you are trying to access a char or chars in the string 
		else if(varEntry.dataType == Token.STRING)
		{
			// is the current token an index in the string?
			if(scan.currentToken.tokenStr.equals("["))
			{
				scan.getNext();
				
				ResultValue strIndex = expression();
				strIndex = HavabolUtilities.calculateExp(this, strIndex.value, Token.INTEGER);
				int charIndex = Numeric.getIntegerValue(strIndex.value);
				
				if(!scan.currentToken.tokenStr.equals("]"))
					scan.getNext(); //current token is "]"
				
				scan.getNext();	// current token is the =
				
				String operatorString = scan.currentToken.tokenStr;
				
				scan.getNext();  //replacement character 
				
				//get the character to replace 
				String replaceWith = scan.currentToken.tokenStr;
				
				// are you replacing more than one character?
				if(replaceWith.length() > 1)
				{
					String beginningString = varEntry.variableValue.substring(0, charIndex);
					String restOfString = varEntry.variableValue.substring(charIndex + 1, varEntry.variableValue.length());
					String newString = beginningString + replaceWith + restOfString;
					varEntry.variableValue = newString;
				}
				
				else
				{
					char replacementChar = replaceWith.charAt(0);
					char[] charString = varEntry.variableValue.toCharArray();
					charString[charIndex] = replacementChar;
					varEntry.variableValue = String.valueOf(charString);
				}
				
				
				scan.getNext();
				
				checkCurrentToken(";");
				
				return strIndex;
				
				
				
				
			}
		}
		
		
		
		
		
		operatorStr = scan.currentToken.tokenStr;

		// only dealing with = for now
		switch (operatorStr) {
		case "=":
			resO2 = expression();
			if(StorageManager.getStorageEntry(variableStr).valueList != null)
			{
				// need to assign the array value
				assignArray(variableStr, index, resO2);
				break;
			}
			assign(variableStr, resO2);
			break;

		default:
			System.out.println("Error, " + operatorStr + " is not a valid assignment operator ");
		}

		return res;
	}
	
	
	
	
	
	
	
	
	

	/**
	 * Returns the value of an operand. If the operand is an identifier, it
	 * makes sure it is valid.
	 * <p>
	 * 
	 * @return The value of the identifer as a string, as well as its data type
	 * @throws Exception 
	 */
	public ResultValue returnValue() throws Exception {
		ResultValue result = new ResultValue();

		if (scan.currentToken.primClassif != Token.OPERAND)
			System.out.println("Error in returnValue(), " + scan.currentToken.tokenStr + " is not an operand");

		if (scan.currentToken.subClassif == Token.IDENTIFIER)
		{
			result = getIdentifier(scan.currentToken.tokenStr);
		}
		
		else {
			result.value = scan.currentToken.tokenStr;
			result.type = scan.currentToken.subClassif;
		}

		return result;

	}

	
	/**
	 * Accesses the symbol table and storage manager to return the value of the
	 * specified variable
	 * <p>
	 * 
	 * @param variableName
	 *            The name of the variable that contains the value that we are
	 *            attempting to access
	 * @return value and data type of the identified variable
	 * @throws Exception 
	 */
	public ResultValue getIdentifier(String variableName) throws Exception {
		
		ResultValue result = new ResultValue();
		STEntry varEntry = SymbolTable.getSymbol(variableName);
		StorageEntry storageEntry = null;

		if (varEntry == null)
			error("Error, variable '%s' has not been declared", variableName);

		else {
			storageEntry = StorageManager.getStorageEntry(variableName);
			
			if(StorageManager.isArray(variableName))
			{
				//TODO get array value 
				scan.getNext();
				scan.getNext();
				//System.out.println("Calling getArrayIndex with token " + scan.currentToken.tokenStr);
				int index = getArrayIndex();
				result = storageEntry.valueList.get(index);
				//System.out.println("In identifier, result is " + result);
				//scan.getNext();
				
			}
				
			else
			{
				result.value = storageEntry.variableValue;
				result.type = storageEntry.dataType;
			}
		}

		return result;
	}


	
	/**
	 * parses the expression and returns a value
	 * <p>
	 * 
	 * @return result value with the value and datatype of the calculated
	 *         expression
	 */

	public ResultValue expression() throws Exception {
		ResultValue evaluatedExpression = new ResultValue();
		// System.out.println("In expression, Next Token " + scan.currentToken.tokenStr);

		switch (scan.currentToken.primClassif) {

		case Token.OPERATOR:
			
			// get the right side of the expression
			scan.getNext();

			// Check for a negative number
			if (scan.currentToken.tokenStr.equals("-")) {
				isNegative = true;
				scan.getNext();
			}

			// must be an operand
			if (scan.currentToken.primClassif != Token.OPERAND)
				System.out.println("Error, " + scan.currentToken.tokenStr + " is not an operand");

			// evaluate the expression
			evaluatedExpression = operations();
			
			return evaluatedExpression;

		case Token.OPERAND:

			// is it an identifier?
			switch (scan.currentToken.subClassif) {
		
			case Token.STRING:
				
				

				evaluatedExpression.value = scan.currentToken.tokenStr;
				evaluatedExpression.type = STRING;

				if (scan.nextToken.tokenStr.equals("#")) {
					evaluatedExpression = evalConcat(); //added string concat
					scan.getNext();
				}

				return evaluatedExpression;

			case Token.IDENTIFIER: // make sure there is an entry for the
									// variable
				
				evaluatedExpression = operations();
				break;

			case Token.INTEGER:
				evaluatedExpression = operations();
				break;

			case Token.FLOAT:
				evaluatedExpression = operations();
				break;

			default:
				// TODO Test other subclasses in future programs
				break;

			}
			break;
			
		case Token.FUNCTION :
			// make sure it is in the symbol table
			
			STEntry funcEntry = SymbolTable.getSymbol(scan.currentToken.tokenStr);
			ResultValue valueOfFunc = new ResultValue();
			
			
			
			
			
			if(funcEntry == null)
				error("Function '%s' has not been defined", scan.currentToken.tokenStr);
			
			if(scan.currentToken.subClassif == Token.BUILTIN)
			{
				valueOfFunc = builtInFunctions();
				
				//did built in functions return an operator?
				if(scan.currentToken.primClassif == Token.OPERATOR)
				{
					String expressionString = valueOfFunc.value;
					//TODO may need to add a while loop here 
					
					while(scan.currentToken.primClassif == Token.OPERATOR)
					{
						
						
						expressionString += scan.currentToken.tokenStr;	// returned value of function plus the operator 
					
						scan.getNext();
						
						
						
						ResultValue restOfExpression = expression();
					
						
					
						expressionString += restOfExpression.value;
					
						
					}
						//TODO Evaluate as an integer for now, change later 
						
						//no more operators, evaluate the expression and return 
						evaluatedExpression = HavabolUtilities.calculateExp(this, expressionString, Token.INTEGER);
						return evaluatedExpression;
				}
		
				
				
				return valueOfFunc;
			}
			
			break;

		default:
			error("In expression, '%s' is not an operator, operand, or a function", scan.currentToken.tokenStr);
			break;

		}

		return evaluatedExpression;
	}


	
	/**
	 * Handles any type of operation that needs to be done on an expression.
	 * <p>
	 * 
	 * @return the result and data type of the operation based on the left
	 *         operand
	 * @throws Exception
	 */
	public ResultValue operations() throws Exception {
		ResultValue result = new ResultValue();
		StorageEntry entry;
		String negativeString = "-";

		
		//System.out.println("In operations, " + scan.currentToken.tokenStr);
		if (scan.currentToken.subClassif == Token.IDENTIFIER) {
			
			
			
			
			
				result.type = scan.currentToken.subClassif;
				result = returnValue();
				
				if(result.type == Token.STRING)
				{
					if(scan.nextToken.tokenStr.equals("["))
					{
						scan.getNext();
						scan.getNext();
						int index = getArrayIndex();
						
						char[] resChar = result.value.toCharArray();
						result.value = String.valueOf(resChar[index]);
						return result;
						
						
					}
				}
				
				
			
				
				term += result.value;
				scan.getNext();
				

			
		}
		
		// not an identifier, assign based on the current token's string and
		else{
			
			result.value = scan.currentToken.tokenStr;
			result.type = scan.currentToken.subClassif;
				
			if(scan.nextToken.tokenStr.equals("]"))
				return result;
			
		}
		
		if (isNegative) {
			negativeString += result.value;
			result.value = negativeString;
			isNegative = false;
		}
		
		//check if you are in a for loop 
		if(scan.nextToken.tokenStr.equals("to"))
		{
			//get the next token and return
			scan.getNext();
			scan.getNext();
			term = "";
			return result;
		}
				
		// in a for loop
		else if(scan.nextToken.tokenStr.equals("by"))
		{
			//get the next token and return
			scan.getNext();
			scan.getNext();
			term = "";
			return result;
		}
		
		// return if you hit a separator or a logical operator
		if (separators.indexOf(scan.nextToken.tokenStr) >= 0) {
			
			if(scan.nextToken.tokenStr.equals("("))
			{
				term += scan.currentToken.tokenStr;
				scan.getNext();
				term += scan.currentToken.tokenStr;
				scan.getNext();
				
			}
			else  
			{
			
				term = "";
				return result;
			}
		
		} if (searchForValue(comparisonOperators, scan.nextToken.tokenStr) == true) {

			term = "";
			return result;
		} else {
			
			
			//keep evaluating until the execute ';'
			while (!scan.currentToken.tokenStr.equals(";")) {
			
				if (scan.currentToken.tokenStr.equals(")")) {
					if (scan.nextToken.tokenStr.equals(";")) {
						break;
					}
				}
				
				if(scan.currentToken.tokenStr.equals("]"))
				{
					if(scan.nextToken.tokenStr.equals(",") || scan.nextToken.tokenStr.equals(";") || scan.nextToken.tokenStr.equals("="))
					{
						
						break;
					}
					else
					{
					
						scan.getNext();
						continue;
					}
				
				}
				
				if (searchForValue(comparisonOperators, scan.currentToken.tokenStr) == true) {
					
					break;
				}
				if (scan.currentToken.tokenStr.equals(":")) {
					break;
				}
				if (scan.currentToken.tokenStr.equals(",")) {
					break;
				}
				if (scan.currentToken.tokenStr.equals("#")) {
					scan.getNext();
				}
				if (scan.currentToken.primClassif == Token.OPERAND) {
					
				
					
					//If you hit a "by" then you are assigning the increment variable, need to return
					//so that the for function can evaluate 
					if(scan.currentToken.tokenStr.equals("by"))
					{
						
						result.value = term;
						term = "";
						return result;
						
					}
					
					

						
					ResultValue opResult = returnValue();
					term += opResult.value;
					result.type = opResult.type;
	
				}
				
				else if (scan.currentToken.primClassif == Token.FUNCTION)
				{
					term += functions().value;
				}
				else {
					
					term += scan.currentToken.tokenStr;
				}
				
				

				scan.getNext();
			}

		}
		
		
	
		
		
			result = HavabolUtilities.calculateExp(this, term, result.type);
		
		term = "";

		return result;
	}
	
	
	/**
	 * parses the expression inside of [ ] 
	 * @return
	 * @throws Exception
	 */
	public int getArrayIndex() throws Exception 
	{
		
		ResultValue result = new ResultValue();
		String resultString = "";
		int dataType = 2;	// index must be an integer 
		int index = 0;
		boolean nextOperand = false;
		boolean nextOperator = false;
		
		//Current token is the first token inside the "["
		
		while(scan.currentToken.tokenStr.equals("("))
		{
			resultString += scan.currentToken.tokenStr;
			scan.getNext();
		}
		
		
		if(scan.currentToken.primClassif != Token.OPERAND && scan.currentToken.primClassif != Token.FUNCTION)
			error("First element inside [ ] is '%s' but must be an operand", scan.currentToken.tokenStr);
		

		while(!scan.currentToken.tokenStr.equals("]"))
		{
			
			if(scan.currentToken.tokenStr.equals(";"))
				error("In getArrayIndex(), reached the end of the statement before a matching ']'");
			
			
			
			
			
			if(scan.currentToken.subClassif == Token.IDENTIFIER)
			{
				ResultValue entry = returnValue();
				resultString += entry.value;
			}
						
			else
				resultString += scan.currentToken.tokenStr;
						

			scan.getNext();
			
			
		}
		
	
		
		//current token is "]", the expression is complete, get the index 
		
		result = HavabolUtilities.calculateExp(this, resultString, dataType);
		index = Numeric.getIntegerValue(result.value);

		return index;
	}
	
	/**
	 * Concatenates strings together to form one string 
	 * <p>
	 * @return The concatenated string 
	 * @throws Exception
	 */
	public ResultValue concatString() throws Exception
	{
		ResultValue result = new ResultValue();
		String resString = "";
		
		while(scan.currentToken.primClassif == Token.OPERAND)
		{
			result = returnValue();
			resString += result.value;
			
			if(scan.nextToken.tokenStr.equals("#"))
				scan.getNext();
			else
				break;
			
			scan.getNext();
		}
		
		result.value = resString;
		result.type = Token.STRING;
		return result;
	}
	

	/**
	 * Determines string concatenation of two strings 
	 * 
	 * @return result of two concatenated strings 
	 */

	public ResultValue evalConcat() throws Exception {
		
		ResultValue result = new ResultValue();
		String aToken = scan.currentToken.tokenStr;
		int type = scan.currentToken.subClassif;
		scan.getNext();
		scan.getNext();
		String bToken = scan.currentToken.tokenStr;
		
		result = HavabolUtilities.stringConcat(aToken, bToken, type);

		return result;
	}
	

	/**
	 * Searches for a certain value in a string array
	 * <p>
	 * 
	 * @param arr
	 *            The declared string array that contains the value that we are
	 *            looking for
	 * @param targetValue
	 *            The value that we are trying to find in the string array
	 * 
	 * @return true if arr contains targetValue, false if not
	 */
	public static boolean searchForValue(String[] arr, String targetValue) {
		for (String s : arr) {
			if (s.equals(targetValue))
				return true;
		}
		return false;
	}

	/**
	 * Assigns or replaces the value of a variable in the storage manager
	 * <p>
	 * 
	 * @param targetVariable
	 *            The name of the variable that we want to replace the value of
	 * @param value
	 *            The value that we are assigning to the variable in the storage
	 *            manager
	 */
	public void assign(String targetVariable, ResultValue value) {

		int dataType = StorageManager.getDataType(targetVariable);
		
		// Make sure you are storing the correct datatype, if values are numbers
		if (value.type != dataType) {
			if (dataType == Token.INTEGER) {
				int intValue = Numeric.getIntegerValue(value.value);
				value.value = String.valueOf(intValue);
			}

			else if (dataType == Token.FLOAT) {
				double floatVal = Numeric.getDoubleValue(value.value);
				value.value = String.valueOf(floatVal);
			}
		}

		storageManager.changeValue(targetVariable, value.value);
	}
	
	
	
	/**
	 * 	Assigns and changes the value of an element in an array 
	 * <p>
	 * 
	 */
	public void assignArray(String arrayVariable, int index, ResultValue value)
	{
		
		
		StorageEntry entry = StorageManager.getStorageEntry(arrayVariable);
		
		
		
		if(entry.valueList.size() < index)
		{
			//make sure it can hold an infinite num of variables 
			for( int i = entry.valueList.size(); i < index; i++ )
			{
				//add values up until you get to the specific index
				entry.valueList.add(null);
			}
			
			entry.valueList.add(value);
				
		}
		
		
	
		ResultValue result = entry.valueList.get(index);
		
		int dataType = StorageManager.getDataType(arrayVariable);
		
		
		
		if(value.type != dataType)
		{
			if(dataType == Token.INTEGER)
			{
				int intValue = Numeric.getIntegerValue(value.value);
				value.value = String.valueOf(intValue);
				
			}
			
			else if (dataType == Token.FLOAT)
			{
				double floatVal = Numeric.getDoubleValue(value.value);
				value.value = String.valueOf(floatVal);
			}
			
		}
		
		//change the value 
		entry.valueList.get(index).value = value.value;
		
		return;
	}
	
	

	/**
	 * Parser has encountered "Int", "Float", "String", "Bool", or "Date".
	 * Places the variable name in the symbol table and creates and initializes
	 * the variable in the storage manager.
	 * <p>
	 * 
	 * @throws Exception
	 */
	public void declareVariable() throws Exception {

		String typeString = scan.currentToken.tokenStr; // declaration type :
														// int, float, bool,
														// date, string
		String variableName;
		STIdentifier newEntry = null;
		StorageEntry storageEntry = null;
		int dataType = -1;

		// get the datatype
		switch (typeString) {
		case "Int":
			dataType = INTEGER;
			break;
		case "Float":
			dataType = FLOAT;
			break;
		case "String":
			dataType = STRING;
			break;
		case "Bool":
			dataType = BOOLEAN;
			break;
		case "Date":
			dataType = DATE;
			break;
		default:
			System.out.println("Error in declare variable, " + typeString + " is not a valid datatype");
			break;
		}

		// get the name of the variable
		scan.getNext();
		variableName = scan.currentToken.tokenStr;
		
		//Array declaration statement, need to call declareVariableArray 
		if ( scan.nextToken.tokenStr.equals("["))
			declareVariableArray(typeString, dataType, variableName);

		else
		{
			if (SymbolTable.getSymbol(typeString) == null)
				System.out.println("Error, " + typeString + " is not a valid declaration type");

			if (SymbolTable.getSymbol(variableName) != null)
				System.out.println("Error, " + variableName + " has already been declared");

		
		
			// the declaration is assigning the variable to a primative eg. (Int x =
			// 10;) or (x = temp[0])
			if (scan.nextToken.tokenStr.equals("=")) {
				newEntry = new STIdentifier(variableName, Token.OPERAND, "primitive", "not a parm", dataType, 99);
				symbolTable.putSymbol(variableName, newEntry);

				storageEntry = new StorageEntry(variableName, dataType, null, null);
				storageManager.addVariable(variableName, storageEntry);
				assignmentStmt();

				// TODO: still need to add code to determine if it's a function or array

				return;
			}

			// put the entry into the symbol table
			newEntry = new STIdentifier(variableName, Token.OPERAND, "primitive", "not a parm", dataType, 99);
			symbolTable.putSymbol(variableName, newEntry);

			// put the entry into the storage manager
			storageEntry = new StorageEntry(variableName, dataType, null, null);
			storageManager.addVariable(variableName, storageEntry);

			scan.getNext();
		}

		// make sure the current Token is a ;
		if (!scan.currentToken.tokenStr.equals(";"))
			System.out.println("Error in declareVariable, missing a semicolon");

		return;

	}
	
	
	
	/**
	 * declareVariableArray.
	 *  Declares and initializes the array with its corresponding valueList in the 
	 *  symbolTable and in the storageManager.
	 *  <p>
	 *  
	 *  Assumptions:
	 *  	- the currentToken is on the arrayname 
	 *  	- the next token is on the [
	 *  
	 *  @param	typeString is the string value of the variable's datatype 
	 *  @param 	dataType is the integer value of the variable's datatype 
	 *  @param 	variableName is the actual name of the variable 
	 *  
	 *  @throws Exception 
	 */
	 public void declareVariableArray(String typeString, int dataType, String arrayName ) throws Exception {
         
		 ResultValue maxElem = null;
         STIdentifier newEntry = null;
         StorageEntry storageEntry = null;
         int numValueList = 0;
         valueList = new ArrayList<ResultValue>();
         String expression = "";
         
         
         //currentToken after below call: [  
         //nextToken after below call: ] or an operand
         
         scan.getNext();
         
         
         //maxElem is specified, change currentToken to the first operand
         if( !scan.nextToken.tokenStr.equals("]") )
         {     
             scan.getNext();
             
             if (scan.currentToken.tokenStr.equals("unbound"))
                 System.out.println("Unbound array");    //TODO
             else
                 maxElem = expression();
            	
         }
         
         //maxElem is not specified
         else
         {                                           
             scan.getNext();	// current token is ']'    

             if( !scan.nextToken.tokenStr.equals("=") ){
                 System.out.println("Error in declareVariableArray(), cannot declare array without a maxElem and without a valueList");
                 System.exit(-1);
             }                    
         }
         
         //If maxElem isnt specified vs it is specified we store the array into an object
         if( maxElem == null )
         {
        	 
        	 //declare the array in the symbol table 
        	 newEntry = new STIdentifier(arrayName, Token.OPERAND, "fixed array", "not a parm", dataType, 99);
        	 symbolTable.putSymbol(arrayName, newEntry);
             
             //declare the array in the storage manager
             storageEntry = new StorageEntry(arrayName, dataType, null, valueList);
             storageManager.addVariable(arrayName, storageEntry);
             
        	 //get the next token, it should be "="
        	 scan.getNext();
        	 
        	 // now get the operand 
        	 scan.getNext();
        	 
        	
        	while ( !scan.currentToken.tokenStr.equals(";") )
        	 {
                 
                if(scan.currentToken.tokenStr.equals(","))
        	 		scan.getNext();
                 
                //Store the value in the valueList 
        	 	listEntry = expression();
                listEntry.structure = "fixed array";
                listEntry.type = dataType;
        		 
                assign(arrayName, listEntry);
                valueList.add(listEntry);
                
                
                
                
                scan.getNext();

        	 }
             
        }
         
        else{
        	
        	newEntry = new STIdentifier(arrayName, Token.OPERAND, "fixed array", "not a parm", dataType, 99);
            symbolTable.putSymbol(arrayName, newEntry);
             

            int iMax = Numeric.getIntegerValue(maxElem.value);
            
            scan.getNext();
            
            
            for(int i = 0; i < iMax; i++)
    		{
    			valueList.add(new ResultValue());
    		}
    		
    		storageEntry = new StorageEntry(arrayName, dataType, null, valueList);
    		storageManager.addVariable(arrayName, storageEntry);
    		
    		if(scan.nextToken.tokenStr.equals(";"))
    		{
    			scan.getNext();
    			return;
    		}
            
    		else if(scan.nextToken.tokenStr.equals("="))
            
            {
            
            	 
            	 //get the = 
            	 scan.getNext();
            	 //get the first operand 
            	 scan.getNext();
            	 
            	 if(scan.currentToken.primClassif != Token.OPERAND)
            		 error("In declare array, expected an operand, current token is '%s'", scan.currentToken.tokenStr);
            	 
            	 
            	 int index = 0;
            	 
            	 while(!scan.currentToken.tokenStr.equals(";"))
            	 {
            		 if(scan.currentToken.tokenStr.equals(","))
            			 scan.getNext();
            		 
            		 
            		 listEntry = expression();
            		 listEntry.structure = "fixed array";
            		 listEntry.type = dataType;
            		 
            		 storageEntry.valueList.set(index, listEntry);
            		 index++;
            		 
            		 scan.getNext();
            	 }
             }
         }
         
         return;
	 
	 }
	
	
	/**
	 * Checks the current token for a value that the calling function was expecting  
	 * @param token 	The token that we are comparing the current token to 
	 * @throws Exception If the tokens are not equal to each other 
	 */
	public void checkCurrentToken(String token) throws Exception
	{
		if(!scan.currentToken.tokenStr.equals(token))
			error("Current token is '%s', it should be %s", scan.currentToken.tokenStr, token);
	}
	
	
	/**
	 * Throws an exception that will print the line number as well as a diagnostic message 
	 * @param fmt	the diagnostic string 
	 * @param varArgs	
	 * @throws Exception
	 */
	public void error(String fmt, Object... varArgs) throws Exception
	{
		String diagnosticTxt = String.format(fmt, varArgs);
		throw new ParserException(Scanner.iSourceLineNr, diagnosticTxt, scan.sourceFileNm);
	
	
	}
	

} // end of parser class