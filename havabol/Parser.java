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
	
	
	public Parser(Scanner scan, SymbolTable symbolTable, StorageManager storageManager)
	{
		this.scan = scan;
		this.symbolTable = symbolTable;
		this.storageManager = storageManager;
		
	}
	
	/**
	 * @throws Exception 
	 * 
	 */
	public void statement() throws Exception
	{
		String variableName = "";
		
		//need a loop right here
		// while(scan.getNext() != ""
		scan.getNext();
		
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
								ifStmt();
								break;
						}
				}
				
			}
			
			//is it a variable that has already been declared?  
			else if(beginningToken instanceof STIdentifier)
			{
				assignmentStmt();
			}
			
			else if(beginningToken instanceof STFunction)
			{
				function();
			}
			
			
		}
	} //end statement 
	
	
	public void ifStmt()
	{
		System.out.println("This is an if statement");
	}
	
	
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
						
						//System.out.println("current token is " + scan.currentToken.tokenStr);
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
		
		
		switch(operator)
		{
			case "=" :
				res02 = expression();
				assign(variableString, res02);
			
		}
		
		
		
	} //end of assignmentStmt
	
	
	/**
	 * 
	 */
	public void assign(String targetVariable, ResultValue value)
	{
		
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
	 *  parses the expression and returns a value 
	 */
	public ResultValue expression() throws Exception
	{
		StorageEntry varEntry = null;
		ResultValue result = new ResultValue();
		
		//current token is the operator 
		
		//next token must be an operand unless it is a - for a negative number 
		scan.getNext();
		
		if(scan.currentToken.tokenStr.equals("-"))
		{	
			isNegative = true;
			scan.getNext();
		}
		else
			isNegative = false;
		
		
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
			
			//need to get the data type
			result.type = varEntry.dataType;
			
			//get the actual value 
			result.value = varEntry.returnValueAsString();
		}
		
		//Else, it is not an identifier, it is a literal 
		else
		{
			result.type = scan.currentToken.subClassif;
			result.value = scan.currentToken.tokenStr;
				
		}
		
		
		
		//check the next token for the end of the line 
		if(scan.nextToken.tokenStr.equals(";"))
			return result;
			
		
		
		else
		{
			//System.out.println("This is not the end of the expression, need to keep going");
			//get the next token 
			scan.getNext();
			
			
			//current token must be an operand 
			if(scan.currentToken.primClassif != Token.OPERATOR)
				System.out.println("Error, " + scan.currentToken.tokenStr + " is not an operator");
			
			String operatorString = scan.currentToken.tokenStr;
			
			//get the next operand 
			scan.getNext();
			
			if(scan.currentToken.primClassif != Token.OPERAND)
				System.out.println("Error, " + scan.currentToken.tokenStr + " is not an operand");
			
			result = operateOnExpression(result, operatorString);
			
			//This should be a ; for now 
			scan.getNext();
			
			
			
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
				//need to create boolean entry 
				//storageEntry = new BooleanEntry(variableName, INTEGER, 'F');
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

