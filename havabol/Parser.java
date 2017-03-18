package havabol;


public class Parser {
	
	public Scanner scan;
	public SymbolTable symbolTable;
	public StorageManager storageManager;
	//public boolean bExec = true;
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
		
		//get the first symbol into the current Token
		//scan.getNext();
		
	}
	
	/**
	 * @throws Exception 
	 * 
	 */
	public void statement() throws Exception
	{
		
		boolean bExec = true;
		
		//need a loop right here
		//while(scan.currentToken.tokenStr != "")
		//{
		while (! scan.getNext().isEmpty())
		{
		//scan.getNext();
		
		//symbol table entry 
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
						//System.out.println("Token.End " + scan.currentToken.tokenStr);
						return;
						//break;
					
					
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
					
					default :
						System.out.println("This needs to be handled");
						break;
					
				}
			}
			
			else if(beginningToken instanceof STFunction)
			{
				function();
			}
			
			
		}
		}
	} //end statement 
	
	
	/**
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
		
		
		
		//will need to change this later 
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
	 * 
	 * @param operator
	 * @param dataType
	 * @param val1
	 * @param val2
	 * @return
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
				}
				
			
			
				
		}
		
		
		return "Error";
	}
	
	
	/**
	 * 
	 * @param bExec
	 * @throws Exception
	 */
	public void whileStmt(boolean bExec) throws Exception
	{
//		System.out.println("In the while method");
//		scan.getNext();
		Token startingPoint = scan.currentToken;
		Token endPoint = null;
//		System.out.println("In while starting point is " + startingPoint.tokenStr);
//		System.out.println("column position is " + startingPoint.iColPos);
		ResultValue compResult = expression();
		
//		System.out.println(compResult.value);
		
//		System.out.println(compResult.value);
		
//		System.out.println("Current token is " + scan.currentToken.tokenStr);
		
		while(compResult.value.equals("T"))
		{
			statement();
				
			if(scan.currentToken.tokenStr.equals("endwhile"))
			{
				endPoint = scan.currentToken;
				scan.setPosition(startingPoint.iSourceLineNr, startingPoint.iColPos);
				scan.getNext();
				compResult = expression();
				//System.out.println(compResult.value);
			}
		
		}
		
		scan.setPosition(endPoint.iSourceLineNr, endPoint.iColPos);
		scan.getNext();
		
		//current Token is endwhile, make sure next is a ; 
		scan.getNext();
		if(!scan.currentToken.tokenStr.equals(";"))
			System.out.println("Error in whileStmt, missing a ; ");
		
		
		
		
		
//		else
//			System.out.println("While statement was false");
		
//		if(scan.currentToken.tokenStr.equals("endwhile"))
//		{
//			System.out.println(scan.currentToken.tokenStr);
//			scan.setPosition(startingPoint.iSourceLineNr, startingPoint.iColPos);
//			scan.getNext();
			
//		}
		
		//System.out.println(scan.currentToken.tokenStr);
//		statement();
		
		
		
//		scan.getNext();
		
//		
		
//		scan.getNext();
//		System.out.println("After settting the starting point " + scan.currentToken.tokenStr);
//		System.out.println("column position is " + scan.currentToken.iColPos);
		
		
		
		
		
		//check for : 
//		if(!scan.currentToken.tokenStr.equals(":"))
//			System.out.println("In whileStmt, error, no : " );
		
	
		
		//System.out.println("In while statement " + compResult.value);
		
//		if(compResult.value.equals("T"))
//		{
//			statement();
			
//			System.out.println("This should equal endwhile or else ......" + scan.currentToken.tokenStr);
			
//			if(scan.currentToken.tokenStr.equals("endwhile"))
//			{
				//go back and test the condition again 
//				scan.setPosition(startingPoint.iSourceLineNr, startingPoint.iColPos);
//				whileStmt(true);
//				System.out.println("Returned to whileStmt() here ");
//			}
//		}
		
//		if(compResult.value.equals("F"))
//			System.out.println("This is where we need to exit");
		
		//compResult.terminatingStr = "endwhile";
		
		//if(!scan.currentToken.tokenStr.equals(":"))
		//	System.out.println("Error in whileStmt, should be a : ");
		
		 
		//scan.getNext();
		
		
		
		//if(compResult.value.equals("T"))
		//{
		//	while(!scan.currentToken.tokenStr.equals(compResult.terminatingStr))
		//		statement();
		//}
		
		//scan.setPosition(startingPoint.iSourceLineNr, startingPoint.iColPos);
		//scan.getNext();
		
		//whileStmt(bExec);
	}

	
	/**
	 * @throws Exception 
	 * 
	 */
	
	public void statements(boolean condResult, String flowType) throws Exception
	{
		boolean bExec = true;
		
		
		
		switch(flowType)
		{
			case "if" :
				if(condResult == true)
				{
					//execute statements until you hit an endif or an else 
					while(!scan.currentToken.tokenStr.equals("endif"))
					{
						
						//if you hit another if then you can execute the test condition 
						//if you hit an else, skip it 
						if(bExec == true)
						{
							statement();
							//System.out.println("IF STMT IS TRUE, statement() returned " + scan.currentToken.tokenStr);
							//if you hit and else statement, keep going 
							if(scan.currentToken.tokenStr.equals("else"))
							{
								//bExec = false;
								//keep going until you hit the correct endif
								//scan.getNext();
								
								while(!scan.currentToken.tokenStr.equals("endif"))
									scan.getNext();
								
								scan.getNext();
							}
							
//							System.out.println("This is the token I am trying to find " + scan.currentToken.tokenStr);
						}
						
						
						
						else
							scan.getNext();
						
						//did you hit an endif
						if(scan.currentToken.tokenStr.equals("endif"))
						{
//							System.out.println("*******************************");
//							System.out.println("bExec is " + bExec);
							if(bExec == false)
							{
								scan.getNext();
								while(!scan.currentToken.tokenStr.equals("endif"))
									scan.getNext();
							}
						}
						
						
						//what if you hit an else? 
						if(scan.currentToken.tokenStr.equals("else"))
						{
//							System.out.println("****Need to skip this else statement **** ");
							bExec = false;
							//keep going until you hit the endif statement
						}
						
					}	
						
					//current token is endif, turn the execution flag back on 
					//bExec = true;
					scan.getNext();
					
//					System.out.println("At bottom of function, " + scan.currentToken.tokenStr);
//					System.out.println(scan.nextToken.tokenStr);
					
					if(!scan.currentToken.tokenStr.equals(";"))
						System.out.println("Error in statements, need a ; ");
				}
				
				//condition is false 
				else
				{
//					System.out.println("*****THE COND IS FALSE *****");
//					System.out.println("bExec is " + bExec);
//					System.out.println("Current Token is " + scan.currentToken.tokenStr);
					
					while(!scan.currentToken.tokenStr.equals("else"))
					{
						scan.getNext();
//						System.out.println("In the loop, current Token is " + scan.currentToken.tokenStr);
						
						//if you hit another if statement, set the bExec flag to false so that it 
						//will ignore the else and endif statements
						if(scan.currentToken.tokenStr.equals("if"))
							bExec = false;
						
						if(scan.currentToken.tokenStr.equals("else"))
						{
							if(bExec == true)
							{
								break;
								//need to keep going 
								//System.out.println("***********SKIP THIS**********");
								//go until you hit "endif"
								//while(!scan.currentToken.tokenStr.equals("endif"))
								//	scan.getNext();
								
								//current token in "endif", get the next token 
								//scan.getNext();
						//		continue;
								//continue to next iteration 
								//continue;
							}
							
							//equals endif, go ahead and get the next token 
							//scan.getNext();
							//System.out.println("current token is else and bExec is false");
						}
						
						if(scan.currentToken.tokenStr.equals("endif"))
						{
							if(bExec == false)
								bExec = true;
							else
								return;		//there was no else statement 
						}
					} // end while
					
//					System.out.println(" In statements, this should be equal else .... " + scan.currentToken.tokenStr);
					
					//current token is "else" 
					while(!scan.currentToken.tokenStr.equals("endif"))
					{
						statement();
//						System.out.println("Statement() returned " + scan.currentToken.tokenStr);
					}
					
					
					
					
				}
				break;
				
//			case "while" :
//				if(condResult == true)
//				{
//					while(!scan.currentToken.tokenStr.equals("endwhile"))
//					{
//						System.out.println("In while switch statement " + scan.currentToken.tokenStr);
//						System.out.println("In while switch statement " + scan.nextToken.tokenStr);
//						statement();
//					}
					
					//token should equal endwhile 
//					System.out.println("This should be endwhile " + scan.currentToken.tokenStr);
//					System.out.println("This is what i am looking for ");
//				}
				
		} //end switch 
		
		
		
		
		
	}
	
	
	/**
	 * 
	 * @param bExec
	 * @throws Exception 
	 */
	public void ifStmt(boolean bExec) throws Exception
	{
		
		//Evaluate the condition?
		if(bExec == true)
		{
			ResultValue resCond = expression();
			//resCond.terminatingStr = "endif";
			
			
			//which part do we need to execute?
			if(resCond.value.equals("T"))
				statements(true, "if");
			
			else 
				statements(false, "if");
			
		}
		
		
//		System.out.println("I returned here");
		
		
	//	scan.getNext();
	//	System.out.println(scan.currentToken.tokenStr);
	//	statement();
		
		
		
		
		
		// the condition is true 
		//if(bExec == true)
		//{
			
		//	scan.getNext();
			
		//	ResultValue resCond = expression();
			
		//	scan.getNext();
			
			//handle based on resCond 
			//System.out.println(resCond.value);
		//	if(resCond.value.equals("T"))
		//		controlStatements(true);
			
		//	else if(resCond.value.equals("F"))
		//		controlStatements(false);
			
		//	else
		//		System.out.println("Error, " + resCond.value + " is not a valid boolean value");
			
		//}
		
		//end of the line, current should be a ; 
		//get the next token 
		//scan.getNext();
	}
	
	
	/**
	 * 
	 * @param boolVal
	 * @throws Exception
	 */
	
	public void controlStatements(boolean boolVal) throws Exception
	{
		
		boolean skipThisControlStmt = false;
		
		//System.out.println("Made it into control statements");
		//System.out.println(boolVal);
		
		
		if(boolVal == true)
		{
			//go until you hit endif
			while(!scan.currentToken.tokenStr.equals("endif"))
			{
				if(scan.currentToken.tokenStr.equals("else"))
				{
					//go until you hit the endif
					while(!scan.currentToken.tokenStr.equals("endif"))
					{
						//if you hit another if stmt, skip it 
						if(scan.currentToken.tokenStr.equals("if"))
							skipThisControlStmt = true;
						scan.getNext();
						
						//if you hit an endif statement and the skip flag is true, turn the flag off and keep going
						if(scan.currentToken.tokenStr.equals("endif") && skipThisControlStmt == true)
						{
							scan.getNext();
							skipThisControlStmt = false;
						}
					}
				}
				
				else
					statement();	//keep going through the if statement 
			}
			
		}
		
		
		else 
		{
			//execute the false portion\
			
			
			
			scan.getNext();
			
			
			
			//if you hit another if stmt, set the skip flag
			//skip down to else 
			while(!scan.currentToken.tokenStr.equals("else"))
			{
				scan.getNext();
				//System.out.println(scan.currentToken.tokenStr);
				
				if(scan.currentToken.tokenStr.equals("if"))
				{
					skipThisControlStmt = true;
					//scan.getNext();
				}
				
				if(scan.currentToken.tokenStr.equals("endif") && skipThisControlStmt == true)
				{
					skipThisControlStmt = false;
					//scan.getNext();
				}
				
				if(scan.currentToken.tokenStr.equals("else") && skipThisControlStmt == true)
				{
					skipThisControlStmt = false;
					//scan.getNext();
				}
				
				//if you hit "endif" then there was no else, just return 
				if(scan.currentToken.tokenStr.equals("endif") && skipThisControlStmt == false)
				{
					//System.out.println("This is where the screw up is");
					//make sure there is a ;
					scan.getNext();
					if(!scan.currentToken.tokenStr.equals(";"))
						System.out.println("Error, missing a ;");
					return;
				}
			}
			
			//token equals else, make sure there is a : 
			scan.getNext();
			
			
			if(!scan.currentToken.tokenStr.equals(":"))
				System.out.println("Error, missing a : ");
			
			scan.getNext();
			
			//continue until you hit the end of the if statement 
			while(!scan.currentToken.tokenStr.equals("endif"))
			{
				//get the next token and execute 
				//scan.getNext();
				statement();
				scan.getNext();
				//System.out.println(scan.currentToken.tokenStr);
			}
			
			
			
		}
	
		
		//get the next token, should be a ; 
		scan.getNext();
		if(!scan.currentToken.tokenStr.equals(";"))
			System.out.println("Error, missing a ';");
		
	}
	
	/**
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
				
				
				switch(scan.currentToken.subClassif)
				{
					case Token.STRING: //It is a string literal 
						System.out.print(scan.currentToken.tokenStr);
						break;
					
					case Token.IDENTIFIER: //It is an identifier, make sure it is valid 
						
						STEntry entry = symbolTable.getSymbol(scan.currentToken.tokenStr);
						
						if(entry == null)
							System.out.println("Error, variable has not been declared");
						
						value = storageManager.getStorageEntry(scan.currentToken.tokenStr);
						
						
						//System.out.println("Current token class is " + scan.currentToken.primClassif);
						//System.out.println("Next token is " + scan.nextToken.tokenStr);
						//System.out.println("Next token class is " + scan.nextToken.primClassif);
						
						
						//check for a math operator 
						if(mathOperators.indexOf(scan.nextToken.tokenStr) >= 0)
						{
							
							leftOperand.type = value.dataType;
							leftOperand.value = value.returnValueAsString();
							scan.getNext();
							operatorString = scan.currentToken.tokenStr;
							//move the the right operand and perform the math function 
							scan.getNext();
							result = operateOnExpression(leftOperand, operatorString);
							System.out.print(result.value);
							break;
						}
						
						
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
						if(!scan.currentToken.tokenStr.equals("-"))
							System.out.println("Operator error, expected a - ");
						
						scan.getNext();
						value = storageManager.getStorageEntry(scan.currentToken.tokenStr);
						System.out.print("-" + value.returnValueAsString());
						break;
						
						
						
					
			
				}
				
				scan.getNext();
				
				if(scan.currentToken.tokenStr.equals(","))
					scan.getNext();
				
				if(scan.currentToken.tokenStr.equals(";"))
					System.out.println("Error, reached the end of the line without ) ");
				
			}
			
			
			// end of the line, need a semicolon 
			scan.getNext();
			
			
			if(!scan.currentToken.tokenStr.equals(";"))
				System.out.println("Error, missing semicolon");
			
			//System.out.println(printString);
			
			System.out.printf("\n");
			
			//get the next token 
			//scan.getNext();
			
			
		}
		
	}
	
	
	/**
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
		
		//*****get the next token 
		scan.getNext();
		
		
		switch(operator)
		{
			case "=" :
				res02 = expression();
				assign(variableString, res02);
			
		}
		
		//next token should be a ; 
		scan.getNext();
		
		if(!scan.currentToken.tokenStr.equals(";"))
			System.out.println("Error in assignmentStmt, need a ;, current token is " + scan.currentToken.tokenStr
					+ " next token is " + scan.nextToken.tokenStr);
		
		//get the next token 
		//scan.getNext();
		
		
		
	} //end of assignmentStmt
	
	
	/**
	 * 
	 */
	public void assign(String targetVariable, ResultValue value)
	{
	//	System.out.println(targetVariable);
	//	System.out.println(value.value);
		
		StorageEntry entry = storageManager.getStorageEntry(targetVariable);
		//get the datatype of the target variable 
		//int dataType = entry.dataType;
		
		
		entry.replaceValue(entry, value.value, isNegative);
		
		
//		switch(dataType)
//		{
//			case STRING :
//				entry.replaceValue(entry, value.value);
//				break;
				
//			case INTEGER :
				//int intValue = Numeric.getIntegerValue(targetVariable);
//				entry.replaceValue(entry, value.value);
//				break;
				
//			case FLOAT :
//				entry.replaceValue(entry, value.value);
//				break;
				
//		}
		
		
	}
	
	
	/** 
	 * 
	 */
	public ResultValue getIdentifierValue()
	{
		ResultValue result = new ResultValue();
		
		if(scan.currentToken.subClassif != Token.IDENTIFIER)
			System.out.println("Error, the token should be an identifier");
		
		return result;
	}
	
	
	/**
	 *  parses the expression and returns a value 
	 */
	public ResultValue expression() throws Exception
	{
		StorageEntry varEntry = null;
		ResultValue result = new ResultValue();
		
		
		
		
		if(scan.currentToken.primClassif == Token.CONTROL)
			scan.getNext();
		
		
		//next token must be an operand unless it is a - for a negative number 
		
		
//		System.out.println("In expression " + scan.currentToken.tokenStr);
		
		if(scan.currentToken.tokenStr.equals("-"))
		{	
			isNegative = true;
			scan.getNext();
		}
		else
			isNegative = false;
		
		
//		if(scan.currentToken.primClassif != Token.OPERAND)
//			scan.getNext();
		
		
		
		
		//current token should be an operand
		if(scan.currentToken.primClassif != Token.OPERAND)
			System.out.println("Error, " + scan.currentToken.tokenStr + " is not an operand");
		
		String firstOperand = scan.currentToken.tokenStr;
	
//		System.out.println("In expression, first operand is " + firstOperand);
		
		
		
		//Check for an identifier 
		if(scan.currentToken.subClassif == Token.IDENTIFIER)
		{
			//make sure it has been declared 
			if(symbolTable.getSymbol(firstOperand) == null)
				System.out.println("Error, " + firstOperand + " has not been declared");
			
			else
				varEntry = storageManager.getStorageEntry(firstOperand);
			
			//need to get the data type
			result.type = varEntry.dataType;
			
			//get the actual value 
			result.value = varEntry.returnValueAsString();
		}
		
		
		
		
		//it is not an identifier, it is a literal 
		else
		{
			result.type = scan.currentToken.subClassif;
			result.value = scan.currentToken.tokenStr;
		}
		
		
//		System.out.println("In expression, result.value is " + result.value);
		
		
		
		
		//check the next token for the end of the line 
		if(scan.nextToken.tokenStr.equals(";"))
			return result;
		
		
		else if(scan.nextToken.tokenStr.equals(":"))
		{
			//get the :  
			scan.getNext();
			//scan.getNext();
			return result;
		}
		
		
		else
		{
			//This is not the end of the expression, need to keep going
			//get the next token 
			scan.getNext();
			
			
			
			//current token must be an operator 
			if(scan.currentToken.primClassif != Token.OPERATOR)
				System.out.println("Error, " + scan.currentToken.tokenStr + " is not an operator");
			
			String operatorString = scan.currentToken.tokenStr;
			
			//System.out.println("In expression, the operator is " + operatorString);
			
			
			if(operatorString.equals("==") || operatorString.equals(">=") || operatorString.equals("<") || operatorString.equals("!=")
					|| operatorString.equals("<=") || operatorString.equals(">"))
			{
				result = compareValues(result);
				
				
//				System.out.println("In expression " + result.value);
				//scan.getNext();
				//System.out.println("In expression, current token is " + scan.currentToken.tokenStr);
				//should be a : for now 
			}
			
			
			else
			{
				//get the next operand 
				scan.getNext();
			
				if(scan.currentToken.primClassif != Token.OPERAND)
					System.out.println("Error, " + scan.currentToken.tokenStr + " is not an operand");
			
				result = operateOnExpression(result, operatorString);
			
				//This should be a ; for now 
				//scan.getNext();
				
			}
			
		}
		
		return result;
		
	} //end expression 
	
	
	
	/**
	 * 
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
	 * 
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
		
		//get the next Token after the ; 
		//scan.getNext();
	
		return;
	
	}


}

