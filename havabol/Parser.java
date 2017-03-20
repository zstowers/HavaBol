package havabol;


public class Parser {
	
	public Scanner scan;
	public SymbolTable symbolTable;
	public StorageManager storageManager;
	public boolean isNegative = false;
	
	public static final int INTEGER 	= 2; 	// integer constant 
	public static final int FLOAT		= 3;	// float constant 
	public static final int BOOLEAN		= 4;	// boolean constant 
	public static final int STRING 		= 5;	// string constant
	public static final int DATE		= 6;	// date constant 
	private static final String mathOperators = "+-*/^";	//string to check for operators 
	
	
	public Parser(Scanner scan, SymbolTable symbolTable, StorageManager storageManager) throws Exception
	{
		this.scan = scan;
		this.symbolTable = symbolTable;
		this.storageManager = storageManager;
	}
	
	/**
	 * Parses the file based on the first token of the line until there are no more tokens 
	 * left in the file 
	 * <p>
	 * 
	 * @param  bExec		Tells the loops if we should execute their control statement  
	 * @throws Exception 	
	 * 
	 */
	
	public void statement(boolean bExec) throws Exception
	{
		

		while (! scan.getNext().isEmpty())
		{

		STEntry beginningToken = symbolTable.getSymbol(scan.currentToken.tokenStr);
		
		//if the entry is in there, find out what type it is 
		if(beginningToken != null)
		{
			
			//is it a declaration, if statement, while statement, endif, else ?
			if(beginningToken instanceof STControl)
			{
				//Token.DECLARE, Token.FLOW, Token.END
				switch(((STControl)beginningToken).subClassif)
				{
					case Token.DECLARE :
						declareVariable();
						break;
						
					case Token.FLOW :
						switch (scan.currentToken.tokenStr)
						{
							case ("if") :
								ifStmt(bExec);
								break;
								
							case ("while") :
								//System.out.println("WhileStmt will be called");
								whileStmt(bExec);
								break;
								
							case ("else") :
								return;
								
						}
						break;
					
					case Token.END :
						return;
						
					
					
				}
				
			}
			
			//is it a variable that has already been declared?  
			else if(beginningToken instanceof STIdentifier)
			{
				switch(scan.nextToken.tokenStr)
				{
					case "=" :
						assignmentStmt();
						break;
						
					case "+=" :
						System.out.println("Future use");
						break;
						
					case "-=" :
						System.out.println("Future use");
						break;
						
					case "++" :
						System.out.println("Future use");
						break;
						
					case "--" :
						System.out.println("Future use");
						
					default :
						System.out.println("Error, cannot use " + scan.nextToken.tokenStr + " after " + scan.currentToken.tokenStr);
						break;
					
				}
			}
			
			else if(beginningToken instanceof STFunction)
			{
				// print is the only valid function for now 
				function();
			}
			
			else
				System.out.println("Error in statement(), statement cannot begin with " + scan.currentToken.tokenStr);
			
			
		}
		}
	} //end statement 
	
	
	/**
	 * Compares two operands based on a logical operator 
	 * <p>
	 * 
	 * @param result 		first operand that will be used in the comparison 
	 * @return result		The value of the comparison.  Will either be "T" or "F"
	 * 						Also sets result.type based on the left operand 
	 * @throws Exception 
	 *  
	 * 
	 */
	public ResultValue compareValues(ResultValue result) throws Exception 
	{
		ResultValue compareTo = new ResultValue();
		
		String comparisonOperator = scan.currentToken.tokenStr;
		
		scan.getNext();
		
		//get the value of the comparison 
		compareTo = expression();
		
		
		
		 
		switch(comparisonOperator)
		{
		case "==" :
			if(result.value.equals(compareTo.value))
			{
				result.value = "T";
				result.type = 4;
			}
			break;
			
		case ">=" :
			
			switch(result.type)
			{
				case 2:	//integer 
					int iNum1 = Numeric.getIntegerValue(result.value);
					int iNum2 = Numeric.getIntegerValue(compareTo.value);
					if(iNum1 >= iNum2)
						result.value = "T";
					else
						result.value = "F";
					break;
				
				case 3 :
					double dNum1 = Numeric.getDoubleValue(result.value);
					double dNum2 = Numeric.getDoubleValue(compareTo.value);
					if(dNum1 >= dNum2)
						result.value = "T";
					else
						result.value = "F";
				break;
			}
			break;
			
		case ">" :
			result.value = resultOfComparison(comparisonOperator, result.type, result.value, compareTo.value);
			break;
			
		case "<" :
			result.value = resultOfComparison(comparisonOperator, result.type, result.value, compareTo.value);
			break;
			
		case "<=" :
			result.value = resultOfComparison(comparisonOperator, result.type, result.value, compareTo.value);
			break;
		
		case "!=" :
			result.value = resultOfComparison(comparisonOperator, result.type, result.value, compareTo.value);
			break;
			
		}
		
		
		
		return result;
	}
	
	
	/**
	 * Compares two values.  Converts the second operand to the same data type of the first operand 
	 * <p>
	 * 
	 * @param operator	The logical operator that is used for the comparison 
	 * @param dataType	Integer, float, or string 
	 * @param val1		The value of the first operand as a string 
	 * @param val2		The value of the second operand as a string 
	 * @return 			T or F, depending on the result of the comparison 
	 */
	public String resultOfComparison(String operator, int dataType, String val1, String val2)
	{
		
		switch(dataType)
		{
			case 2: 	//integer
				int iNum1 = Numeric.getIntegerValue(val1);
				int iNum2 = Numeric.getIntegerValue(val2);
				
				switch(operator)
				{
					
					case "<" :
						if(iNum1 < iNum2)
							return "T";
						else
							return "F";
						
					case "<=" :
						if(iNum1 <= iNum2)
							return "T";
						else
							return "F";
					
					case ">" :
						if(iNum1 > iNum2)
							return "T";
						else
							return "F";
						
					case ">=" :
						if(iNum1 >= iNum2)
							return "T";
						else
							return "F";
					
					case "==": 
						if(iNum1 == iNum2)
							return "T";
						else
							return "F";
				}
				
				break;
				
			case 3: 	//float
				double dNum1 = Numeric.getDoubleValue(val1);
				double dNum2 = Numeric.getDoubleValue(val2);
				
				switch(operator)
				{
					
					case "<" :
						if(dNum1 < dNum2)
							return "T";
						else
							return "F";
						
					case "<=" :
						if(dNum1 <= dNum2)
							return "T";
						else
							return "F";
					
					case ">" :
						if(dNum1 > dNum2)
							return "T";
						else
							return "F";
						
					case ">=" :
						if(dNum1 >= dNum2)
							return "T";
						else
							return "F";
					
					case "==": 
						if(dNum1 == dNum2)
							return "T";
						else
							return "F";
				}
				
				break;
				
			case 5: 	//string 
				switch(operator)
				{
					case "!=" :
						if(val1.equals(val2))
							return "F";
						else
							return "T";
						
						
					case "==" :
						if(val1.equals(val2))
							return "T";
						else
							return "F";
					
					case ">" :
						if(val1.compareTo(val2) > 0)
							return "T";
						else
							return "F";
						
					case ">=" :
						if(val1.compareTo(val2) >= 0)
							return "T";
						else
							return "F";
					
					case "<" :
						if(val1.compareTo(val2) < 0)
							return "T";
						else
							return "F";
						
					case "<=" :
						if(val1.compareTo(val2) <= 0)
							return "T";
						else
							return "F";
						
						
						
					default :
						System.out.println("Error for now");
						break;
						
				}
				
			
			
				
		}
		
		return "Error";
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

		int startingLineNr = scan.currentToken.iSourceLineNr;
		ResultValue compResult = expression();
		
		if(bExec == true)
		{
			//The while condition returned true 
			while(compResult.value.equals("T"))
			{
				while(!scan.currentToken.tokenStr.equals("endwhile"))
				{
					//execute all the statements until you encounter the correct "endwhile" 
					statement(true);
				}
				
				// current token is endWhile, check the next token for a ; 
				if(!scan.nextToken.tokenStr.equals(";"))
					System.out.println("Error in while statement, missing a ;");
			
				//move back to the line that began with while and check the condition again
				scan.setPosition(startingLineNr, 0);
				scan.getNext();				//Call getNext() to put the nextToken into currentToken since the return from 
											//setPosition leaves the current Token empty.
				
				//evaluate the expression again
				compResult = expression();
				
			}
		
			//The while condition was not true 
			if(compResult.value.equals("F"))
			{
			
				scan.setPosition(startingLineNr, 0);
				scan.getNext();
				
				//ignore all statements until you hit an endwhile 
				while(!scan.currentToken.tokenStr.equals("endwhile"))
				{
					scan.getNext();
					
					//handles the nested while statements so that you will keep the endwhile statements 
					//correct 
					if(scan.currentToken.tokenStr.equals("while"))
					{
						while(!scan.currentToken.tokenStr.equals("endwhile"))
							scan.getNext();
						
						//current token is endwhile, get the next token so you can make sure you are 
						//dealing with the correct "endwhile"
						scan.getNext();
					}
				}
					
				//This is the "endwhile" that matches with the while statement that we are skipping
				scan.getNext();
				
				if(!scan.currentToken.tokenStr.equals(";"))
					System.out.println("Error in whileStmt(), missing a ; ");
				
			}
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
		
		//Evaluate the condition?
		if(bExec == true)
		{
			ResultValue resCond = expression();
			
			//which part do we need to execute?
			if(resCond.value.equals("T"))
			{
				// statements(true, "if");
				statement(true);
				
				if(scan.currentToken.tokenStr.equals("endif"))
				{
					//make sure the next token is a ; 
					scan.getNext();
					
					if(!scan.currentToken.tokenStr.equals(";"))
						System.out.println("Error, missing a ; in ifStmt(). Current token is " + scan.currentToken.tokenStr);
					
				}
				
				//Condition was true, don't need to execute the else part  
				else if(scan.currentToken.tokenStr.equals("else"))
				{
					scan.getNext();
					
					while(!scan.currentToken.tokenStr.equals("endif"))
					{
						scan.getNext();
						
						
						//This handles nested if statements 
						if(scan.nextToken.tokenStr.equals("if"))
						{
							while(!scan.currentToken.tokenStr.equals("endif"))
								scan.getNext();
							
						}
						
						scan.getNext();
					}
					
				}
			
			}
			
			//if statement was false, check for an "else" 
			if(resCond.value.equals("F"))
			{

				// continue grabbing tokens until you hit an else 
				while(!scan.currentToken.tokenStr.equals("else"))
				{
					scan.getNext();
					//if you hit an if statement, don't execure
					if(scan.nextToken.tokenStr.equals("if"))
						statement(false);
					
					if(scan.currentToken.tokenStr.equals("endif"))
						System.out.println("THERE WAS NO ELSE STATEMENT, return");
					
				}
				
				// Current token is "else" 
				scan.getNext();
				
				if(!scan.currentToken.tokenStr.equals(":"))
					System.out.println("Error in ifStmt, missing a : after the else statement ");
				
				//execute until you hit the correct "endif" 	
				while(!scan.currentToken.tokenStr.equals("endif"))
				{
					statement(true);
				}
				
				//Current token is endif, make sure there is a ; 
				scan.getNext();
				
				if(!scan.currentToken.tokenStr.equals(";"))
					System.out.println("Error in ifStmt, missing a ;");
				
			}
			
		}
		
		//bExec is false 
		else if(bExec == false)
		{
			//skip through all tokens until the correct endif is encountered 
			while(!scan.currentToken.tokenStr.equals("endif"))
			{
				if(scan.nextToken.tokenStr.equals("if"))
					statement(false);
				
				scan.getNext();
			}
			
			//make sure next token is ; 
			if(!scan.nextToken.tokenStr.equals(";"))
				System.out.println("Error, in ifStmt when bExec is false, missing a ; ");
		
		}
		
	}
	
	

	/**
	 * Checks to make sure that the function has been defined and is in the symbol table and executes 
	 * the function if it is a valid function with correct parameters.
	 * <p>
	 * 
	 * @throws Exception
	 */
	public void function() throws Exception
	{
		
		String functionName = scan.currentToken.tokenStr;
		ResultValue leftOperand = new ResultValue();
		ResultValue result = new ResultValue();
		String operatorString = "";
		StorageEntry value = null;
		
		
		//make sure it is in the symbol table 
		if(symbolTable.getSymbol(functionName) == null)
			System.out.println("Error, the function hasn't been declared");
		
		//Print is the only function for now, this will need to be changed later  
		if(functionName.equals("print"))
		{
			//parse the line and check for errors 
			//next token needs to be a (
			scan.getNext();
			
			if(!scan.currentToken.tokenStr.equals("("))
				System.out.println("Error, missing (");
			
			//next token is the string  
			scan.getNext();
			
			
			
			
			while(!scan.currentToken.tokenStr.equals(")"))
			{
				
				if(scan.currentToken.tokenStr.equals("-"))
				{
					// the number is negative 
					System.out.print(scan.currentToken.tokenStr);
					scan.getNext();
				}
				
				switch(scan.currentToken.subClassif)
				{
					case Token.INTEGER :	
						//check for a math operator 
						if(mathOperators.indexOf(scan.nextToken.tokenStr) >= 0)
						{
							
							leftOperand.type = INTEGER;
							leftOperand.value = scan.currentToken.tokenStr;
							scan.getNext();
							operatorString = scan.currentToken.tokenStr;
							
							//move to the right operand and perform the math function 
							scan.getNext();
							result = operateOnExpression(leftOperand, operatorString);
							//print the value as a string based on the data type of the left operand 
							System.out.print(result.value);
						}
						
						else
							System.out.print(scan.currentToken.tokenStr);
						
						break;
				
					case Token.FLOAT :
						//check for a math operator 
						if(mathOperators.indexOf(scan.nextToken.tokenStr) >= 0)
						{
							
							leftOperand.type = FLOAT;
							leftOperand.value = scan.currentToken.tokenStr;
							scan.getNext();
							operatorString = scan.currentToken.tokenStr;
							
							//move to the right operand and perform the math function 
							scan.getNext();
							result = operateOnExpression(leftOperand, operatorString);
							//print the value as a string based on the data type of the left operand 
							System.out.print(result.value);
						}
						
						else
							System.out.print(scan.currentToken.tokenStr);
						
						break;
						
					case Token.BOOLEAN :
						System.out.println(scan.currentToken.tokenStr);
						break;
						
					case Token.DATE :
						System.out.println(scan.currentToken.tokenStr);
						break;
				
					case Token.STRING: //It is a string literal 
						System.out.print(scan.currentToken.tokenStr);
						break;
					
					case Token.IDENTIFIER: //It is an identifier, make sure it is valid 
						
						STEntry entry = symbolTable.getSymbol(scan.currentToken.tokenStr);
						
						if(entry == null)
							System.out.println("Error, variable has not been declared");
						
						value = storageManager.getStorageEntry(scan.currentToken.tokenStr);
						
						//check for a math operator 
						if(mathOperators.indexOf(scan.nextToken.tokenStr) >= 0)
						{
							
							leftOperand.type = value.dataType;
							leftOperand.value = value.returnValueAsString();
							scan.getNext();
							operatorString = scan.currentToken.tokenStr;
							
							//move to the right operand and perform the math function 
							scan.getNext();
							result = operateOnExpression(leftOperand, operatorString);
							//print the value as a string based on the data type of the left operand 
							System.out.print(result.value);
							break;
						}
						
						
						//No operations necessary, just print the value of the identifier 
						if(value instanceof IntegerEntry)
						{
							value = (IntegerEntry) value;
							System.out.print(((IntegerEntry) value).variableValue);
						}
						
						if(value instanceof FloatEntry)
						{
							value = (FloatEntry) value;
							System.out.print(((FloatEntry) value).variableValue);
						}
						
						if(value instanceof StringEntry)
						{
							System.out.print(" " + ((StringEntry) value).variableValue);
						}
						
						break;
						
					
					default :
						//must be a - for a negative number 
						//System.out.println(scan.currentToken.tokenStr);
						//if(!scan.currentToken.tokenStr.equals("-"))
						System.out.println("Error in print, invalid token");
						
						//scan.getNext();
						//value = storageManager.getStorageEntry(scan.currentToken.tokenStr);
						//System.out.print("-" + value.returnValueAsString());
						break;
						
				}
				
				scan.getNext();
				
				if(scan.currentToken.tokenStr.equals(","))
				{
					System.out.print(" ");
					scan.getNext();
				}
				
				if(scan.currentToken.tokenStr.equals(";"))
					System.out.println("Error, reached the end of the line without ) ");
				
			}
			
			
			// end of the line, need a semicolon 
			scan.getNext();
			
			
			if(!scan.currentToken.tokenStr.equals(";"))
				System.out.println("Error, missing semicolon");
			
			System.out.printf("\n");
			
		}
		
	}
	
	
	/**
	 * Parser encountered a variable, parses the rest of the statement and assigns it a value 
	 * <p>
	 * Assigns the value and updates its value in the storage manager 
	 * 
	 * @throws Exception 
	 * 
	 */
	public void assignmentStmt() throws Exception
	{
		String variableString = scan.currentToken.tokenStr;
		ResultValue res02;
		ResultValue res01;
		
		//make sure it is an identifier 
		if(scan.currentToken.subClassif != Token.IDENTIFIER)
			System.out.println("Error in assigment, " + variableString + " is not an identifier");
		
		 //get the operator
		scan.getNext();
		
		if(scan.currentToken.primClassif != Token.OPERATOR)
			System.out.println("Error in assignmentStmt, " + scan.currentToken.tokenStr + " is not an operator");
		
		String operator = scan.currentToken.tokenStr;
		
		//get the next token, it should be an operand  
		scan.getNext();
		
		
		switch(operator)
		{
			case "=" :
				//parse the rest of the line to get a value and data type 
				res02 = expression();
				assign(variableString, res02);	//assigns the value of res02 to the variable that is identified 
												//by variableString 
			
		}
		
		//next token should be a ; 
		scan.getNext();
		
		if(!scan.currentToken.tokenStr.equals(";"))
			System.out.println("Error in assignmentStmt, need a ;, current token is " + scan.currentToken.tokenStr
					+ " next token is " + scan.nextToken.tokenStr);
		
	} //end of assignmentStmt
	
	
	/**
	 * 	Updates the value of the variable in the storage manager 
	 * <p>
	 */
	public void assign(String targetVariable, ResultValue value)
	{
	
		
		StorageEntry entry = storageManager.getStorageEntry(targetVariable);
		entry.replaceValue(entry, value.value, isNegative);
		
	}
	
	
	/**
	 *  parses the expression and returns a value
	 *  <p>
	 *  @return	result value with the value and datatype of the calculated expression  
	 */
	public ResultValue expression() throws Exception
	{
		StorageEntry varEntry = null;
		ResultValue result = new ResultValue();
		
		
		//token is while, if, or for.  Get the next token for the expression that you need to parse
		if(scan.currentToken.primClassif == Token.CONTROL)
			scan.getNext();
		
		
		//next token must be an operand unless it is a - for a negative number 
		if(scan.currentToken.tokenStr.equals("-"))
		{	
			isNegative = true;
			scan.getNext();
		}
		else
			isNegative = false;
		
		//current token should be an operand
		if(scan.currentToken.primClassif != Token.OPERAND)
			System.out.println("Error, " + scan.currentToken.tokenStr + " is not an operand");
		
		String firstOperand = scan.currentToken.tokenStr;
	
		//Check for an identifier 
		if(scan.currentToken.subClassif == Token.IDENTIFIER)
		{
			//make sure it has been declared 
			if(symbolTable.getSymbol(firstOperand) == null)
				System.out.println("Error, " + firstOperand + " has not been declared");
			
			else
				varEntry = storageManager.getStorageEntry(firstOperand);
			
			//need to get the data type and the value of the variable 
			result.type = varEntry.dataType;
			result.value = varEntry.returnValueAsString();
		}
		
		//it is not an identifier, it is a literal 
		else
		{
			result.type = scan.currentToken.subClassif;
			result.value = scan.currentToken.tokenStr;
		}
		
		//check the next token for the end of the line 
		if(scan.nextToken.tokenStr.equals(";"))
			return result;
		
		//end of a control statement 
		else if(scan.nextToken.tokenStr.equals(":"))
		{
			//get the :  
			scan.getNext();
			return result;
		}
		
		
		else
		{
			//This is not the end of the expression, need to keep going 
			scan.getNext();
			
			//current token must be an operator 
			if(scan.currentToken.primClassif != Token.OPERATOR)
				System.out.println("Error, " + scan.currentToken.tokenStr + " is not an operator");
			
			String operatorString = scan.currentToken.tokenStr;
			
			if(operatorString.equals("==") || operatorString.equals(">=") || operatorString.equals("<") || operatorString.equals("!=")
					|| operatorString.equals("<=") || operatorString.equals(">"))
			{
				result = compareValues(result);
			}
			
			
			else
			{
				//get the next operand 
				scan.getNext();
			
				if(scan.currentToken.primClassif != Token.OPERAND)
					System.out.println("Error, " + scan.currentToken.tokenStr + " is not an operand");
			
				result = operateOnExpression(result, operatorString);
			}
			
		}
		
		return result;
		
	} //end expression 
	
	
	
	/**
	 * 	Does arithmetic on the expression and returns the result
	 * <p>
	 * 
	 * @return	The value and datatype of the expression 
	 */
	
	public ResultValue operateOnExpression(ResultValue firstOperand, String operatorString)
	{
		ResultValue returnedValue = new ResultValue();
		String secondOperandString = scan.currentToken.tokenStr;
		int firstDataType = firstOperand.type;	//get the data type of the left operand
		StorageEntry secondVariableEntry = null;
		ResultValue secondOperand = new ResultValue();
		
		
		
		//operand is the current token 
		if(scan.currentToken.subClassif == Token.IDENTIFIER)
		{
			//need to get the value of the variable
			if(symbolTable.getSymbol(secondOperandString) == null)
				System.out.println("Error, " + secondOperandString + " has not been declared");
			
			else
				secondVariableEntry = storageManager.getStorageEntry(secondOperandString);
			
			//need to get the data type
			secondOperand.type = secondVariableEntry.dataType;
			
			//get the actual value 
			secondOperand.value = secondVariableEntry.returnValueAsString();
		}
		
		//It must be a literal 
		else
			secondOperand.value = scan.currentToken.tokenStr;
		
		
		//perform the operation 
		switch(firstOperand.type)
		{
			
			case INTEGER :
				int iOp1 = Numeric.getIntegerValue(firstOperand.value);
				int iOp2 = Numeric.getIntegerValue(secondOperand.value);
				int iResult = Numeric.intArithmetic(iOp1, iOp2, operatorString);
				returnedValue.value = String.valueOf(iResult);
				return returnedValue;
		
			case FLOAT :
				double dOp1 = Numeric.getDoubleValue(firstOperand.value);
				double dOp2 = Numeric.getDoubleValue(secondOperand.value);
				double dResult = Numeric.doubleArithmetic(dOp1, dOp2, operatorString);
				returnedValue.value = String.valueOf(dResult);
				return returnedValue;
				
		}
			
		return returnedValue;
	}
	
	
	
	
	
	
	/**
	 * 	Parser has encountered "Int", "Float", "String", "Bool", or "Date". Places the variable name in the symbol 
	 *  table and creates and initializes the variable in the storage manager. 
	 *  <p>
	 *  @throws Exception 
	 */
	public void declareVariable() throws Exception
	{
		int dataType;
		String typeString = scan.currentToken.tokenStr;
		String variableName;
		STIdentifier newEntry = null;
		StorageEntry storageEntry = null;
		
		//get the name of the variable
		scan.getNext();
		variableName = scan.currentToken.tokenStr;
		
		
		if(symbolTable.getSymbol(variableName) != null)
			System.out.println("Error, " + variableName + " has already been declared");
		
		//switch based on tokenstring to find out the datatype of the variable 
		switch(typeString)
		{
			case "Int" :
				dataType = INTEGER;
				newEntry = new STIdentifier(variableName, Token.OPERAND, "primitive", "not a parm", Token.INTEGER, 99);
				storageEntry = new IntegerEntry(variableName, INTEGER, 0);
				break;
				
			case "Float" :
				dataType = FLOAT;
				newEntry = new STIdentifier(variableName, Token.OPERAND, "primitive", "not a parm", Token.FLOAT, 99);
				storageEntry = new FloatEntry(variableName, FLOAT, 0.0);
				break;
				
			case "String" :
				dataType = STRING;
				newEntry = new STIdentifier(variableName, Token.OPERAND, "primitive", "not a parm", Token.STRING, 99);
				storageEntry = new StringEntry(variableName, STRING, "");
				break;
			
			case "Bool" :
				dataType = BOOLEAN;
				newEntry = new STIdentifier(variableName, Token.OPERAND, "primitive", "not a parm", Token.BOOLEAN, 99);
				storageEntry = new BooleanEntry(variableName, BOOLEAN, "F");
				break;
			
			case "Date" :
				dataType = DATE;
				newEntry = new STIdentifier(variableName, Token.OPERAND, "primitive", "not a parm", Token.DATE, 99);
				//need to create date entry 
				break;
			
			default :
				System.out.println("Error, " + typeString + " is not a valid identifier");
				
		}
		
		symbolTable.putSymbol(variableName, newEntry);
		storageManager.addVariable(variableName, storageEntry);
		
		//if scan.nextToken.tokenStr == " = ", need to assign it a value 
		//else, need to make sure it is a ; 
		scan.getNext();
		
		if(!scan.currentToken.tokenStr.equals(";"))
			System.out.println("Expecting a ; instead it returned " + scan.currentToken.tokenStr);
		
		return;
	
	}


}

