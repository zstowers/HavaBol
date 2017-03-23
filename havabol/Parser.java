package havabol;


public class Parser {
	
	public Scanner scan;
	public SymbolTable symbolTable;
	public StorageManager storageManager;
	public boolean isNegative = false;
	public Print printer;
	
	public static final int INTEGER 	= 2; 	// integer constant 
	public static final int FLOAT		= 3;	// float constant 
	public static final int BOOLEAN		= 4;	// boolean constant 
	public static final int STRING 		= 5;	// string constant
	public static final int DATE		= 6;	// date constant 
	private static final String[] comparisonOperators = {"==", ">", ">=", "<", "<=", "!="}; //logical operators
	private static final String separators = "(),:;[]";		//string to check for separators 
	
	public Parser(Scanner scan, SymbolTable symbolTable, StorageManager storageManager) throws Exception
	{
		this.scan = scan;
		this.symbolTable = symbolTable;
		this.storageManager = storageManager;
	}

	
	public void statementTest() throws Exception
	{
		ResultValue result = null;
		
		while(!scan.getNext().isEmpty())
		{
			//switch based on the first token in the statement 
			switch(scan.currentToken.primClassif)
			{
				case Token.OPERAND :
					
					//switch based on the subclass
					switch(scan.currentToken.subClassif)
					{
						case Token.IDENTIFIER :
							result = assignmentStmt();
							break; 
					
						default :
							System.out.println("Error in statementTest(), " + scan.currentToken.tokenStr + "can't begin a line");
							break;
					}
					break; // from Token.OPERAND
					
				case Token.CONTROL : 
					//switch based on the subclass 
					switch(scan.currentToken.subClassif)
					{
						case Token.DECLARE :
							declareVariable();
							break;
							
						case Token.FLOW :
							switch(scan.currentToken.tokenStr)
							{
								case "if" :
									ifStmt(true);
									break;
								
								case "while" :
									whileStmt(true);
									break;
								
								case "else" :
									//return back to the ifStmt method, it will determine how to handle esle statments
									return;
								
								default :
									System.out.println("Error in statementTest(), " + scan.currentToken.tokenStr + " is not a valid flow statment");
									break;
							}
							
						case Token.END :
								//return to the calling function, it will determine how to handle 
								return;
							
					}
					break; //from control 
				
				case Token.FUNCTION :
					//is it user defined or built in 
					switch(scan.currentToken.subClassif)
					{
						case Token.BUILTIN :
							builtInFunctions();
							break;
							
						// will handle user defined in future 
					}
					break; //from function 
				  
			}
		}
		
	}
	
	
	
	
	
	/**
	 * The parser has hit the token "while"
	 * <p>
	 * This method will iterate through the while statement and reset the line to the start of the while
	 * statement when it encounters an "endwhile".  If the comparison is false, it will ignore all lines 
	 * until it encounters the correct "endwhile". 
	 * 
	 * 
	 * @param bExec			Tells us if we should execute the while statement or skip it.  If skipped 
	 * 						then we need to move to the correct "endwhile" and ignore all statements in 
	 * 						between. 
	 * @throws Exception
	 */
	
	public void whileStmt(boolean bExec) throws Exception
	{
		if(bExec == true)
		{
			scan.getNext();
			//save the position of the while statement so we can check the condition again 
			Token startingPoint = scan.currentToken; 
			
			//Evaluate the while statement condition 
			ResultValue resCond =  evalCondition();	
			
			scan.getNext();
			
			if(!scan.currentToken.tokenStr.equals(":"))
				System.out.println("Error in whileStmt, missing a colon, current token is " + scan.currentToken);
			
			
			if(resCond.value.equals("T"))
			{
				
				while(resCond.value.equals("T"))
				{
					statementTest();
					//returned endwhile, check the condition again 
					scan.setPosition(startingPoint.iSourceLineNr, startingPoint.iColPos);
					scan.getNext();
					
					resCond = evalCondition();
				}	
				
				// resCond returned false, break out of the while loop 
				whileStmt(false);
				
				scan.getNext();
				if(!scan.currentToken.tokenStr.equals(";"))
					System.out.println("Error in whileStmt, expected a ; ");
				
			}
			
			
			else if(resCond.value.equals("F"))
			{
				//initial evaluation of the while statement was false, move past the correct while statement
				while(!scan.currentToken.tokenStr.equals("endwhile"))
					scan.getNext();
				
				//currentToken is endwhile
				scan.getNext();
				if(!scan.currentToken.tokenStr.equals(";"))
					System.out.println("Error in while statement when rescond is false, no semicolon");
			}
			
			
			statementTest();
			
		}  // end of bExec = true
		
		
		// ignore execution of the while statement and move to the statement after the correct endwhile
		if(bExec == false)
		{
			while(!scan.currentToken.tokenStr.equals("endwhile"))
			{
				if(scan.currentToken.tokenStr.equals("while"))
				{
					while(!scan.currentToken.tokenStr.equals("endwhile"))
						scan.getNext();
				}
				scan.getNext();
			}
			
			//token is endwhile
			return;
		}
		
		
	}
	
	
	
	
	/**
	 * Parser has encountered an "if" when parsing the line 
	 * <p>
	 * Handles flow of if and if-else statements as well as nested if and if-else statements 
	 * 
	 * @param bExec			Tells the function if it should execute the if condition 
	 * @throws Exception 
	 */
	
	public void ifStmt(boolean bExec) throws Exception
	{
		
		if(bExec == true)
		{
			scan.getNext();
			//evaluate the condition  
			ResultValue resCond = evalCondition();

			//make sure the next token is a : 
			scan.getNext();
			
			if(!scan.currentToken.tokenStr.equals(":"))
				System.out.println("Error in ifStmt, " + scan.currentToken.tokenStr + " does not equal :");
			
			
			//conditional statement was true 
			if(resCond.value.equals("T"))
			{
				while(!scan.currentToken.tokenStr.equals("endif"))
				{
					statementTest();
					
					//if "else" was returned, need to skip
					if(scan.currentToken.tokenStr.equals("else"))
						ifStmt(false);
				}
				
				//currentToken is "endif", make sure the next token is ; 
				scan.getNext();
				
				if(!scan.currentToken.tokenStr.equals(";"))
					System.out.println("Error in ifStmt, current token should be ;, not " + scan.currentToken.tokenStr);
				
			}
			
			//conditional statement was false 
			else if(resCond.value.equals("F"))
			{
				
				while(!scan.currentToken.tokenStr.equals("else"))
				{
					scan.getNext();
					//if you hit another if statement, skip it
					if(scan.currentToken.tokenStr.equals("if"))
					{
						while(!scan.currentToken.tokenStr.equals("endif"))
							scan.getNext();
						//get the token after the endif 
						scan.getNext();
					}
					
					//skip to the else statement, if there is no else just skip the if statement 
					//there was no else 
					if(scan.currentToken.tokenStr.equals("endif"))
					{
						scan.getNext();
						if(!scan.currentToken.tokenStr.equals(";"))
							System.out.println("Error in ifStmt, missing a semicolon");
						return;
					}
					
				}
			
				//current token is else 
				scan.getNext();
				
				if(!scan.currentToken.tokenStr.equals(":"))
					System.out.println("Error in ifStmt, missing a ; in the false part, current token is " + scan.currentToken.tokenStr);
				
				//execute the else portion of the if statmement
				while(!scan.currentToken.tokenStr.equals("endif"))
				{
					statementTest();
				}
				
				//endif, check for a ; 
				scan.getNext();
				
				if(!scan.currentToken.tokenStr.equals(";"))
					System.out.println("In if statement, missing a ;, current token is " + scan.currentToken.tokenStr);
	
			}
			
			//finished with the execution of the if statement, get the next statement 
			statementTest();
		}  // end of bExec is true
		
		
		// don't execute any statement until you get to the correct endif statement 
		else if(bExec == false)
		{
			//keep grabbing tokens until you find the correct endif
			while(!scan.nextToken.tokenStr.equals("endif"))
			{
				//need to get the matching endif if you hit another if statement 
				if(scan.currentToken.tokenStr.equals("if"))
				{
					while(!scan.currentToken.tokenStr.equals("endif"))
						scan.getNext();
				}
				
				scan.getNext();
			}
			
			//next Token is endif
			scan.getNext();
			
			return;
			
		} 	//end of false bExec 
		
	}
	
	
	
	
	
	/**
	 * Evaluates the result of an if or while statement and returns the result of the operation 
	 * <p>
	 * 
	 * @return a ResultValue object that contains either "T" or "F" as the value 
	 * @throws Exception
	 */
	
	public ResultValue evalCondition() throws Exception 
	{
		ResultValue result = new ResultValue();
		ResultValue resOp1;
		ResultValue resOp2;
		String compOperator;
		
		//get the first expression 
		resOp1 = expression();

		
		//get the next token, must be a logical operator  
		scan.getNext();
		
		if(searchForValue(comparisonOperators, scan.currentToken.tokenStr) == false)
			System.out.println("Error in evalCondition, " + scan.currentToken.tokenStr + " is not a valid comparison operator");
		
		compOperator = scan.currentToken.tokenStr;
		
		//get the expression that will be compared with resOp1 
		scan.getNext();
		
		if(scan.currentToken.primClassif != Token.OPERAND)
			System.out.println("Error in evalCondition, expecting an operand, not " + scan.currentToken.tokenStr);
		
		resOp2 = expression();

		//If the left operand is a number, compare it with the right operand 
		if(resOp1.type == INTEGER || resOp1.type == FLOAT)
		{
			//compare the numeric values 
			if(HavabolUtilities.compareNumerics(this, resOp1, resOp2, compOperator))
				result.value = "T";
			else
				result.value = "F";
		}
		

		else
		{
			// left operand is a string 
			// compare strings  
			switch(compOperator)
			{
				case ("=="):
					if(resOp1.value.equals(resOp2.value))
						result.value = "T";
					else
						result.value = "F";
					break;
				
				case (">") :
					if(resOp1.value.compareTo(resOp2.value) > 0)
						result.value = "T";
					else
						result.value = "F";
					break;
			
				case (">=") :
					if(resOp1.value.compareTo(resOp2.value) >= 0)
						result.value = "T";
					else
						result.value = "F";
					break;
			
				case ("<") :
					if(resOp1.value.compareTo(resOp2.value) < 0)
						result.value = "T";
					else
						result.value = "F";
					break;
			
				case ("<=") :
					if(resOp1.value.compareTo(resOp2.value) >= 0)
						result.value = "T";
					else
						result.value = "F";
					break;
			
				case ("!=") :
					if(resOp1.value.equals(resOp2.value))
						result.value = "F";
					else
						result.value = "T";
					break;
			
				default :
					System.out.println("Error in evalCondition, " + compOperator + "is not a valid comparison operator");
					break;
			}
		}
		
		return result;
	}
	
	
	
	/**
	 *  Statement is a built in function.  Call the specified built-in function 
	 *  <p>
	 *  
	 * @throws Exception	If the function is not one of the built in functions 
	 */
	public void builtInFunctions() throws Exception
	{
		
		
		//switch based on current token 
		switch(scan.currentToken.tokenStr)
		{
			case "print" :
				printFunction();
				break;
				
			//future functions
				
			default :
				System.out.println("Error in builtInFunctions, " + scan.currentToken.tokenStr + " is not a built-in function");
				break;
				
		}
		
	}
	
	
	/**
	 * Handles the built-in function, print.  Called from the builtInFunctions method.  Prints a space if 
	 * a ',' is encountered.  Also, looks up variables and prints their value 
	 * <p>
	 * 
	 * @throws Exception  if not surrounded by parenthesis
	 */
	public void printFunction() throws Exception
	{
		ResultValue result = new ResultValue();
		String resultString = "";
		
		scan.getNext();
		
		if(!scan.currentToken.tokenStr.equals("("))
			System.out.println("Error in the print function, " + scan.currentToken.tokenStr + "is not equal to (");
		
		scan.getNext();
		
		while(!scan.currentToken.tokenStr.equals(")"))
		{
			//if you hit a ; before the right paren it is an error 
			if(scan.currentToken.tokenStr.equals(";"))
				System.out.println("Error in print function");
			
			//if you hit a , need to add a space 
			if(scan.currentToken.tokenStr.equals(","))
				result.value += " ";
			
			else if(scan.currentToken.primClassif == Token.OPERAND)
			{
				// Get the value of the operand 
				ResultValue expRes = expression();
				result.value += expRes.value;
			}
			
			// Not an operand, it is a string literal 
			else
				result.value += scan.currentToken.tokenStr;
			
			scan.getNext();
			
		}
		
		printer = new Print(result);
		
		//currentToken is a  ) 
		scan.getNext();
		if(!scan.currentToken.tokenStr.equals(";"))
			System.out.println("Error in printFunction, missing a ;");
		
		return;
		
	}
	
	/**
	 * Parser encountered an identifier, need to assign it based on the operator 
	 * <p>>
	 * 
	 * @return The value of the variable 
	 * @throws Exception
	 */
	public ResultValue assignmentStmt() throws Exception 
	{
		ResultValue res = null;
		ResultValue resO1 = null;
		ResultValue resO2;
		String variableStr;
		String operatorStr;
		
		// getIdentifier method will handle the error if it is not an identifier
		// make sure the variable has been declared 
		if(StorageManager.isDeclared(scan.currentToken.tokenStr) == false)
			System.out.println("Error in assignmentStmt, current token has not been declared");
		
		variableStr = scan.currentToken.tokenStr;
		
		//get the assignment operator 
		scan.getNext();
		
		operatorStr = scan.currentToken.tokenStr;
		
		// only dealing with = for now
		switch(operatorStr)
		{
			case "=" :
				resO2 = expression();
				assign(variableStr, resO2);
				break;
				
			default :
				System.out.println("Error, " + operatorStr + " is not a valid assignment operator ");
		}
		
		return res;
	}
	
	
	/**
	 * Returns the value of an operand.  If the operand is an identifier, it makes sure it is valid.
	 * <p>
	 * 
	 * @return The value of the identifer as a string, as well as its data type 
	 */
	public ResultValue returnValue()
	{
		ResultValue result = new ResultValue();
		
		
		if(scan.currentToken.primClassif != Token.OPERAND)
			System.out.println("Error in returnValue(), " + scan.currentToken.tokenStr + " is not an operand");
		
		
		if(scan.currentToken.subClassif == Token.IDENTIFIER)
			result = getIdentifier(scan.currentToken.tokenStr);
		else
		{
			result.value = scan.currentToken.tokenStr;
			result.type = scan.currentToken.subClassif;
		}
		
		return result;
		
	}
	
	
	/**
	 * Accesses the symbol table and storage manager to return the value of the specified variable 
	 * <p>
	 * 
	 * @param variableName	The name of the variable that contains the value that we are attempting to access
	 * @return  value and data type of the identified variable 
	 */
	public ResultValue getIdentifier(String variableName)
	{
		ResultValue result = new ResultValue();
		STEntry varEntry = symbolTable.getSymbol(variableName);
		StorageEntry storageEntry = null;
		
		
		if(varEntry == null)
			System.out.println("Error, variable has not been declared");
		
		else
		{
			storageEntry = storageManager.getStorageEntry(variableName);
			result.value = storageEntry.variableValue;
			result.type = storageEntry.dataType;
		}
		
		return result;
	}
	
	/**
	 *  parses the expression and returns a value
	 *  <p>
	 *  @return	result value with the value and datatype of the calculated expression  
	 */
	
	public ResultValue expression() throws Exception
	{
		ResultValue evaluatedExpression = new ResultValue();
		ResultValue leftOperand = new ResultValue();
		ResultValue returnedIdentifier = new ResultValue();
		String operatorString;
		String variableValue = "";
		StorageEntry value = null;
		
		
		switch(scan.currentToken.primClassif)
		{
			
			case Token.OPERATOR :
				
				//get the right side of the expression 
				scan.getNext();
				
				// Check for a negative number 
				if(scan.currentToken.tokenStr.equals("-"))
				{
					isNegative = true;
					scan.getNext();
				}
				
				//must be an operand 
				if(scan.currentToken.primClassif != Token.OPERAND)
					System.out.println("Error, " + scan.currentToken.tokenStr + " is not an operand");
				
				//evaluate the expression 
				evaluatedExpression = operations();	
				return evaluatedExpression;
							
			
			case Token.OPERAND :
				
			//is it an identifier? 
			switch(scan.currentToken.subClassif)
			{
				// if it is a string, we are not doing any kind of operations on it for now, just return 
				// the string 
				case Token.STRING :	
					evaluatedExpression.value = scan.currentToken.tokenStr;
					evaluatedExpression.type = STRING;
					return evaluatedExpression;
					
				case Token.IDENTIFIER : // make sure there is an entry for the variable 
					evaluatedExpression = operations();
					break;	
					
				case Token.INTEGER:
					evaluatedExpression = operations();
					break;
						
				case Token.FLOAT:						
					evaluatedExpression = operations();
					break;
					
				default :
					//Test other subclasses in future programs 
					break;
						
					
			}
				break;
				
			default :
				System.out.println("Error " + scan.currentToken.tokenStr + "is not valid in test expression");
				break;
		
		}
	
		return evaluatedExpression;
	}
	
	
	/**
	 * Handles any type of operation that needs to be done on an expression. 
	 * <p>
	 * 
	 * @return the result and data type of the operation based on the left operand
	 * @throws Exception
	 */
	public ResultValue operations() throws Exception
	{
		ResultValue result = new ResultValue();
		StorageEntry entry;
		Numeric nOp2;
		Numeric nOp1;
		ResultValue resOp1;
		ResultValue resOp2 = null;
		String operatorStr;
		String variableStr;
		String negativeString = "-";
		
		if(scan.currentToken.subClassif == Token.IDENTIFIER)
		{
			entry = StorageManager.getStorageEntry(scan.currentToken.tokenStr);
			result.value = entry.variableValue;
			result.type = entry.dataType;
		}
		
		// not an identifier, assign based on the current token's string and type 
		else
		{
			result.value = scan.currentToken.tokenStr;
			result.type = scan.currentToken.subClassif;
		}
		
		if(isNegative)
		{
			negativeString += result.value;
			result.value = negativeString;
			isNegative = false;
		}
		
		//return if you hit a separator or a logical operator 
		if(separators.indexOf(scan.nextToken.tokenStr) >= 0)
			return result;
		
		else if(searchForValue(comparisonOperators, scan.nextToken.tokenStr) == true)
			return result;
		
		// token is not a separator or logical operator, get the next token, must be an operator   
		scan.getNext();
		
		if(scan.currentToken.primClassif != Token.OPERATOR)
				System.out.println("Error in operations(), expecting an operator, current token is " + scan.currentToken.tokenStr);
			
		
		// this is the left operand 
		resOp1 = result;
			
		operatorStr = scan.currentToken.tokenStr;
		scan.getNext();
		
		// this is the right operand 
		resOp2 = returnValue();
		
		// operands are numeric
		nOp1= new Numeric(this, resOp1, operatorStr, "1st Operand");
		nOp2 = new Numeric(this, resOp2, operatorStr, "2nd Operand");
			
		//return result in HavabolUtilities class will handle the result based on the operator string 
		result = HavabolUtilities.returnResult(nOp1, nOp2);
			
		return result;
	}
	
	/**
	 * Searches for a certain value in a string array 
	 * <p>
	 *  
	 * @param arr The declared string array that contains the value that we are looking for 
	 * @param targetValue	The value that we are trying to find in the string array 
	 * 
	 * @return true if arr contains targetValue, false if not 
	 */
	public static boolean searchForValue(String[] arr, String targetValue)
	{
		for(String s: arr)
		{
			if(s.equals(targetValue))
				return true;
		}
		return false;
	}
	
	
	
	
	

	
	
	
	
	
	
	
	
	
	
	
	

	
	
	
	/**
	 * Assigns or replaces the value of a variable in the storage manager 
	 * <p>
	 * 
	 * @param targetVariable The name of the variable that we want to replace the value of 
	 * @param value	The value that we are assigning to the variable in the storage manager 
	 */
	public void assign(String targetVariable, ResultValue value)
	{
		
		int dataType = StorageManager.getDataType(targetVariable);
		
		//Make sure you are storing the correct datatype, if values are numbers  
		if(value.type != dataType)
		{
			if(dataType == Token.INTEGER)
			{
				int intValue = Numeric.getIntegerValue(value.value);
				value.value = String.valueOf(intValue);
			}
			
			else if(dataType == Token.FLOAT)
			{
				double floatVal = Numeric.getDoubleValue(value.value);
				value.value = String.valueOf(floatVal);
			}
		}
		
		storageManager.changeValue(targetVariable, value.value);
	}
	
	
	/**
	 * 	Parser has encountered "Int", "Float", "String", "Bool", or "Date". Places the variable name in the symbol 
	 *  table and creates and initializes the variable in the storage manager. 
	 *  <p>
	 *  
	 *  @throws Exception 
	 */
	public void declareVariable() throws Exception
	{
		
		String typeString = scan.currentToken.tokenStr;		//declaration type : int, float, bool, date, string 
		String variableName;
		STIdentifier newEntry = null;
		StorageEntry storageEntry = null;
		int dataType = -1;
		
		//get the datatype
		switch(typeString)
		{
			case "Int" :
				dataType = INTEGER;
				break;
			case "Float" :
				dataType = FLOAT;
				break;
			case "String" :
				dataType = STRING;
				break;
			case "Bool" :
				dataType = BOOLEAN;
				break;
			case "Date" :
				dataType = DATE;
				break;
			default :
				System.out.println("Error in declare variable, " + typeString + " is not a valid datatype");
				break;
		}
		
		//get the name of the variable
		scan.getNext();
		variableName = scan.currentToken.tokenStr;
		 
		if(SymbolTable.getSymbol(typeString) == null)
			System.out.println("Error, " + typeString + " is not a valid declaration type");
		
		if(SymbolTable.getSymbol(variableName) != null)
			System.out.println("Error, " + variableName + " has already been declared");
		
		
		if(scan.nextToken.tokenStr.equals("="))
		{
			//need to get the next tokens and initialize the variable 
			System.out.println("This will be handled in a future program");
		}
		
		//put the entry into the symbol table 
		newEntry = new STIdentifier(variableName, Token.OPERAND, "primitive", "not a parm", dataType, 99);
		symbolTable.putSymbol(variableName, newEntry);
		
		//put the entry into the storage manager 
		storageEntry = new StorageEntry(variableName, dataType, null);
		storageManager.addVariable(variableName, storageEntry);
		
		scan.getNext();
		
		//make sure the current Token is a ; 
		if(!scan.currentToken.tokenStr.equals(";"))
			System.out.println("Error in declareVariable, missing a semicolon");
		
		return;
		
	}
		

} //end of parser class 

