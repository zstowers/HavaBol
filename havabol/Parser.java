package havabol;


public class Parser {
	
	public Scanner scan;
	public Token workingToken;
	public SymbolTable globalSymbolTable;
	public StorageManager storageManager;
	public STEntry stEntry;
	public String dataType;
	//public SymbolTable nonGlobalTable;
	
	
	
	public Parser(Scanner scan, SymbolTable globalSymbolTable, StorageManager storageManager)
	{
		this.scan = scan;
		this.globalSymbolTable = globalSymbolTable;
		this.storageManager = storageManager;
		
	}
	
	
	
	public void parseTokens() throws Exception
	{
		
		ResultValue resultValue = new ResultValue();
		
		
		if(scan.getNext() != "")
		{
			
			switch(scan.currentToken.primClassif)
			{
				case Token.CONTROL :
					if(scan.currentToken.subClassif == Token.DECLARE)
						declareStmt();
					break;
					
				case Token.OPERAND :
					assignmentStmt();
					break;
					
				case Token.FUNCTION :
					function();
					break;
					
					
				default :
					System.out.println("This needs to be handled");
				
			
			}
			
			//if(scan.currentToken.subClassif == Token.DECLARE)
			//{
				//call declare statement subroutine
			//	declareStmt();
			//}
			
			
		}
		
		
	}
	
	
	public void function() throws Exception
	{
		//name of the function 
		String functionName = scan.currentToken.tokenStr;
		String line;
		
		//make sure it is in the symbol table 
		if(globalSymbolTable.getSymbol(functionName) == null)
			System.out.println("Error, the function hasn't been declared");
		
		if(functionName.equals("print"))
		{
			String printString = "";
			
			
			
			//parse the line and check for errors 
			//next token needs to be a (
			scan.getNext();
			
			if(!scan.currentToken.tokenStr.equals("("))
				System.out.println("Error, missing (");
			
			//next token is the string  
			scan.getNext();
			
			printString = scan.currentToken.tokenStr;
			
			// end of the line, make sure to handle variables later in the program 
			// need a right paren and then a semicolon 
			scan.getNext();
			
			if(!scan.currentToken.tokenStr.equals(")"))
				System.out.println("Error, missing right parenthesis");
			
			scan.getNext();
			
			if(!scan.currentToken.tokenStr.equals(";"))
				System.out.println("Error, missing semicolon");
			
			System.out.println(printString);
			
			
			
		}
		
		
		
	}
	
	public void assign(String variableName, ResultValue value)
	{
		StorageEntry entry;
		//make sure the variableName is in the storage manager 
		entry = storageManager.getStorageEntry(variableName);
		
		if(entry == null)
			System.out.println("Error, " + variableName + " has not been declared");
		
		switch(value.type)
		{
			case "STRING" :
				StringEntry stringEntry = (StringEntry) entry;
				stringEntry.replaceValue(stringEntry, value.value);
				break;
				
			case "FLOAT" :
				FloatEntry floatEntry = (FloatEntry) entry;
				Numeric floatNum = new Numeric(this, value, "=", "second operand");
				floatEntry.replaceValue(floatEntry, floatNum.doubleValue);
				break;
				
			case "INTEGER" :
				System.out.println("Im in integer");
				System.out.println("value is " + value.value);
				IntegerEntry integerEntry = (IntegerEntry) entry;
				Numeric integerNum = new Numeric(this, value, "=", "second operand");
				System.out.println("integerNum = " + integerNum.integerValue);
				integerEntry.replaceValue(integerEntry, integerNum.integerValue);
				break;
		}
		
	}
	
	
	
	public ResultValue assignmentStmt() throws Exception
	{
		// String type - data type of the result 
		// String value = value of the result 
		// String structure - primitive, fixed array, unbounded array 
		// String terminatingStr - used for end of lists of things 
		//                         (e.g., a list of statements might be terminated by "endwhile")
		
		
		ResultValue result = new ResultValue();      // overall result  
		ResultValue resOperand1; // result value of the first operand 
		ResultValue resOperand2; // result value of the second operand 
		Numeric numOperand1;     // numeric value of the first operand 
		Numeric numOperand2;     // numeric value of the second operand 
		String variableString;   // name of the identifier
		String operatorString;   // operator type
		STEntry entry;			 // checks the entry in the symbol table 
		
		//System.out.println(scan.currentToken.tokenStr);
		//System.out.println(scan.currentToken.subClassif);
		
		if(scan.currentToken.subClassif != Token.IDENTIFIER)
			System.out.println("Subclass is not equal to identifier, handle this error");
		
		variableString = scan.currentToken.tokenStr;
		
		//Make sure variableString was already declared and is in the symbolTable
		if(globalSymbolTable.getSymbol(variableString) == null)
			System.out.println("Error, invalid identifier, hasn't been declared");
		
		//result.type = Token.strPrimClassifM[scan.currentToken.subClassif];
		
		//get the assignment operator and check it 
		scan.getNext();
		
		if(scan.currentToken.primClassif != Token.OPERATOR)
			System.out.println("Expected assignment operator, handle this error ");
		
		operatorString = scan.currentToken.tokenStr;
		
		switch(operatorString)
		{
			case("=") :
				resOperand2 = expr();
				assign(variableString, resOperand2);
				//storageManager.changeValue(variableString, resOperand2.value);
				break;
		}
		
		
		
		return result;
		
	}
	
	
	public ResultValue expr() throws Exception
	{
		ResultValue result = new ResultValue();
		//String operator;
		
		
		
		switch(scan.currentToken.primClassif)
		{
			case Token.OPERATOR :
				
				String operator = scan.currentToken.tokenStr;
				
				scan.getNext();
				
				if(scan.currentToken.primClassif != 1)
					System.out.println("Expression error, need an operand for now, need to check function ");
				
				//data type is the type of the first operand 
				result.type = Token.strSubClassifM[scan.currentToken.subClassif];
			
				// get the rest of the expression 
				while(!scan.currentToken.tokenStr.equals(";"))
				{	
					
					//switch the dataType (subclass of currentToken)
					switch(scan.currentToken.subClassif)
					{
						
						case 2 :	//integer 
							result.value = scan.currentToken.tokenStr;
							break;
					
						case 3 :	//float
							result.value = scan.currentToken.tokenStr;
							break;
							
						case 5: 	//string 
							result.value = scan.currentToken.tokenStr;
							break;
					}
					
					
					
					
					
					scan.getNext();
				}
				
				//reached the end of the expression, return
				result.terminatingStr = scan.currentToken.tokenStr;
		}
		
		
		
		
		
		return result;
	}
	
	public void declareStmt() throws Exception
	{
		
		String dataType = "";
		String name = "";
		String value = "";
		String structure = "primitive";  
		String parm = "none";
		int dclType = 1;
		int nonLocal = 99;
		int primClassif = 1;
		STEntry newEntry = null;
		StorageEntry storeEntry = null;
		
		dataType = scan.currentToken.tokenStr;
		
		
		
		switch(dataType)
		{
			case "Int" :
				dclType = 2;
				break;
			
			case "Float" :
				dclType = 3;
				break;
			
			case "Bool" :
				dclType = 4;
				break;
			
			case "String" :
				dclType = 5;
				break;
			
			default :
				System.out.println("Error.  Invalid data type");
				break;
				
		}
		
		//next token must be an operand 
		scan.getNext();
		
		if(scan.currentToken.primClassif != 1)
			System.out.println("Error, must be an operator");
		
		else
		{
			name = scan.currentToken.tokenStr;
			primClassif = scan.currentToken.primClassif;
		}
		
		// make sure that name isn't already in the symbolTable 
		if(globalSymbolTable.getSymbol(name) != null)
			System.out.println(name + " has already been identified, handle this error");
		
		// else get the next token 
		scan.getNext();
		
		
		
		// is the next token a ";", if so, put into symbol table and 
		// storage manager will get a value of 0 or "" or 'F' for boolean
		
		// if it is an "=", then it is already initialized, put it into the 
		// symbol table and storage manager with the value and make sure the 
		// value matches the type 
		
		switch(scan.currentToken.tokenStr)
		{
			case "=" :
				System.out.println("Need to initialize");
				break;
			
			case ";" :
				newEntry = new STIdentifier(name, primClassif, structure, parm, dclType, nonLocal);
				globalSymbolTable.putSymbol(name, newEntry);
				//storeEntry = new StorageEntry(name, value, dclType);
				
				//create the storage entry based on the dataType
				switch(dclType)
				{
					case 2 :  	//integer 
						storeEntry = new IntegerEntry(name, dclType, 0);
						break;
				
					case 3 :	//float
						storeEntry = new FloatEntry(name, dclType, 0);
						break;
						
					case 5 :    //string
						storeEntry = new StringEntry(name, dclType, "");
						break;
						
				}
				
				//storeEntry = new FloatEntry(name, dclType, 0);
				
				//add to the storage manager 
				storageManager.addVariable(name, storeEntry);
				
				//System.out.println("added " + name + " to symbol table and storage manager");
				
				break;
			
			default:
				System.out.println("This is an error for now");
				break;
			
		}
		
		return;
		
		
	}
	
	public void printResultValue(ResultValue value)
	{
		System.out.println("Type is : " + value.type);
		System.out.println("Value is : " + value.value);
		System.out.println("Structure is : " + value.structure);
		System.out.println("Terminating string is " + value.terminatingStr);
	}
	
	
	

}
