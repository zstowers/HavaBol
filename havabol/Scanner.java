package havabol;

import java.util.ArrayList;
import java.io.*;

public class Scanner {

		
	public String sourceFileNm;				//source code file name 
	public SymbolTable symbolTable;			//object responsible for providing symbol definitions
	public static ArrayList<String> sourceLineM;	//array list of source text lines 
	public static char[] textCharM;				//char for the current text line 
	public static int iSourceLineNr;				//Line number in sourceLineM for current text line 
	public static int iColPos;						//Column position within the current text line 
	public Token currentToken;				//Token established with the most recent call to getNext()
	public Token nextToken;					//The token following the currentToken 
	private static final String delimiters = "\t;:,()\'\"=!<>+-*/[]#^\n";	//string to check for delimiters
	private static final String operators = "+-*/+=<>!^";	//string to check for operators 
	private static final String separators = "(),:;[]";		//string to check for separators 
	private static final String charOperators = "<>=!^";
	public static boolean printLine = false;		//Prints the entire line when true
	public int numBlanks = 0;				//tells the scanner if there were any number of blank lines 
	 										//that need to be printed before printing the current line 

		
		
	public Scanner(String sourceFileNm, SymbolTable symbolTable ) throws Exception
	{
		this.sourceFileNm = sourceFileNm;
		this.symbolTable = symbolTable;
		sourceLineM = new ArrayList<String>();
			
		//populate sourceLineM
		String line;
		BufferedReader reader = new BufferedReader(new FileReader(sourceFileNm));
			
		try 
		{
			while ((line = reader.readLine()) != null) 
				sourceLineM.add(line);
		} 
		catch (IOException e) 
		{
				// TODO Auto-generated catch block
				e.printStackTrace();
		}
			
		// Initialize textCharM, iSourceLineNr, and iColPos
		iColPos = 0;
		iSourceLineNr = 0;
		textCharM = sourceLineM.get(iSourceLineNr).toCharArray();
			
		// Initialize currentToken and nextToken to new objects
		// So that the parser doesn't have to check for null
		currentToken = new Token();
		nextToken = new Token();
			
		//Get the first token into nextToken
		//this.getFirst();
		getTokenNoReturn();
		printLine = true;
		
	}
	

	/**
	 * 	Gets the next token.  Automatically advances to the next source line when
	 *  necessary. 
	 *  <p>
	 *  
	 * @return			tokenStr for the current token.  If there are no more 
	 * 					tokens then it returns "". 
	 * @throws 			Exceptions from getTokenNoReurn(), assignNumber(), and 
	 * 					assignNextTokenString()
	 */
	public String getNext() throws Exception
	{
		char currentChar;
		int iStartingIndex;
		int blankLineNr;
	
		//Assign the current Token 
		currentToken = nextToken;
		
		//System.out.println("In scanner, current Token is " + currentToken.tokenStr);
		
		// If there were any blank lines or lines with comments, print them out.  This helps keep 
		// everything in order and helps with debugging 
//		if(numBlanks > 0)
//		{
//			blankLineNr = (iSourceLineNr + 1) - numBlanks;
//			for(int i = 0; i < numBlanks; i++)
//			{
//				System.out.println("  " + blankLineNr + sourceLineM.get((blankLineNr - 1)));
//				blankLineNr++;
//			}
//			numBlanks = 0;
//		}
		
		//return if there are no more tokens 
		if(currentToken.primClassif == 6)
			return "";
		
		
		nextToken = new Token();
		
		
		//The last token on the previous line has already been collected and printed so print 
		//print the line 
//		if(printLine == true)
//		{
//			System.out.printf("  %d  %s\n", (iSourceLineNr + 1), sourceLineM.get(iSourceLineNr));
//			printLine = false;
//		}
		
		//end of the line, move to the next one 
		if(iColPos == textCharM.length)
			HavabolUtilities.moveToNextLine(nextToken);
		
		//check for a blank line or comment 
		if(iColPos == 0)
		{
			//while(checkBlank() == true)
			while(HavabolUtilities.checkBlank(nextToken))
			{
				numBlanks++;
				HavabolUtilities.moveToNextLine(nextToken);
			}
		}
		
		//check to see if nextToken has hit the end of the file 
		if(nextToken.primClassif == 6)
		{
			assignToken(currentToken);
			return currentToken.tokenStr;
		}
		
		// Now ready to get the next token 
		// Get the first character of the token 
		currentChar = textCharM[iColPos];
		
		
		
		//If the first character is a space or a tab then keep going until you hit a character
		//If you don't hit a character then move to the next line 
		while(currentChar == ' ' || currentChar == '\t')
		{
			iColPos++;
			
			if(iColPos == textCharM.length)
			{
				HavabolUtilities.moveToNextLine(nextToken);
				
				while(HavabolUtilities.checkBlank(nextToken) == true)
					HavabolUtilities.moveToNextLine(nextToken);
				
				currentChar = textCharM[iColPos];
				continue;
			}
			else
				currentChar = textCharM[iColPos];
		}
		
		
		//Beginning position of the token in the string 
		iStartingIndex = iColPos;
		nextToken.iColPos = iStartingIndex;
		
		
		
		//The first character in the string is a delimiter 
		if(delimiters.indexOf(currentChar) >= 0)
		{
			//System.out.println("Current Char = " + currentChar);
			handleDelimiters(currentChar, iStartingIndex);
		}
	
		//First char  is not a delimiter, keep going through the characters until you hit a space, tab, delimiter,
		//or end of the line 
		else
		{
			//keep going through the characters until you hit a space 
			while(currentChar != ' ' && currentChar != '\t')
			{
				
				//don't increment the column position if you hit a delimiter so that it will be picked 
				//up on the next iteration 
				if(delimiters.indexOf(currentChar) >= 0)
				{
					//System.out.println("The delimiter that will cause this break is " + currentChar);
					break;
				}
				
				else
				{
					iColPos++;
					if(iColPos == textCharM.length)
						break;
					else
						currentChar = textCharM[iColPos];
				}
				 
			}
			
			assignNextTokenString(iStartingIndex);
		}
		
		nextToken.iSourceLineNr = iSourceLineNr + 1;
		
		//System.out.println("In scanner, next token is " + nextToken.tokenStr);
		
		assignToken(currentToken);
		
		return currentToken.tokenStr;
	}

	
	
	
	/**
	 * Assigns the delimiter based on its value,   
	 *  <p>
	 *  
	 * @param currentChar		The value of the delimiter 
	 * @param iStartingIndex	The position of the delimiter in the line 	
	 * @throws ScannerException 
	 */
	
	public void handleDelimiters(char currentChar, int iStartingIndex) throws Exception
	{
		// \t;:,()\'\"=!<>+-*/[]#^\n
		
		//check character operators 
		if(charOperators.indexOf(currentChar) >= 0)
		{
			//check the next character for an '='
			if(textCharM[iColPos + 1] == '=')
			{
				iColPos+= 2;
				assignNextTokenString(iStartingIndex);
			}
			else
			{
				iColPos++;
				assignNextTokenString(iStartingIndex);
			}
			
			return;
		}
		
		
		//not a character operator 
		
		switch(currentChar)
		{
			case '\'' :
				assignStringLiteral(currentChar);
				break;
			
			case '\"' :
				assignStringLiteral(currentChar);
				break;
				
			case '/' :
				if(textCharM[iColPos + 1] == '/')
				{
					//rest of the line is a comment 
					HavabolUtilities.moveToNextLine(nextToken);
					getTokenNoReturn();
				}
				
				else if(textCharM[iColPos + 1] == '=')
				{
					iColPos+= 2;
					assignNextTokenString(iStartingIndex);
				}
				
				//Not a comment, it is the division operator, assign it 
				else
				{
					iColPos++;
					assignNextTokenString(iStartingIndex);
				}
				break;
			
			default:
				iColPos++;
				assignNextTokenString(iStartingIndex);
				
		}
	}
	
	
/**
 * Assign the primary and sub classes of the current token, uses the global 
 * symbol table to check for global variables 	
 * <p>
 * @param token			current Token
 * @throws Exception 	ScannerException 
 */
	
	
	public void assignToken(Token token) throws Exception
	{
		
		char firstChar = token.tokenStr.charAt(0);
		
		STEntry entry = symbolTable.getSymbol(token.tokenStr);
		
		
		
		

		if(entry != null)
		{
			token.primClassif = entry.primClassif;
			
			switch(token.primClassif)
			{
				case 5:
					switch(token.tokenStr)
					{
					case "if":
						token.subClassif = 10;
						break;
					case "else" :
						token.subClassif = 10;
						break;
					case "while" :
						token.subClassif = 10;
						break;
					case "for" :
						token.subClassif = 10;
						break;
					case "Int" :
						token.subClassif = 12;
						break;
					case "Float" :
						token.subClassif = 12;
						break;
					case "String" :
						token.subClassif = 12;
						break;
					case "Bool" :
						token.subClassif = 12;
						break;
					default :
						token.subClassif = 11;
						break;
					}
					break;
					
				case 4:
					token.subClassif = 13;
					break;
				
				case 1:
					token.subClassif = 1;
					break;
			}
		}
		
		//Is the token a number?
		else if(Character.isDigit(firstChar))
		{
			assignNumber(token);
			return;
		}
		
		
		//Check the token to see if it has already been assigned as a string literal  
		else if(token.subClassif == 5)
			return;
		
		
		
		else if(delimiters.indexOf(firstChar) >= 0)
		{
			//is it a separator
			if(separators.indexOf(firstChar) >= 0)
			{
				token.primClassif = 3;
				return;
			}
			
			//is it an operator 
			else if(operators.indexOf(firstChar) >= 0)
			{
				token.primClassif = 2;
				return;
			}
		}
		
		else
		{
			token.primClassif = 1;
			
			if(token.tokenStr.equals("T") || token.tokenStr.equals("F"))
				token.subClassif = 4;
			else
				token.subClassif = 1;
			return;
		}
		
	
		
	}
	
	
/**
 * Assigns the string literal and handles the error if it doesn't find a matching quote
 * <p> 		
 * @param quoteChar		Either a single or double quote 
 * @throws Exception	ScannerException 
 */
		
		
	public void assignStringLiteral(char quoteChar) throws Exception 
	{
			
		int i;
		char[] newChArray = new char[textCharM.length];
		int j = 0;
		int totalChars = 0;
			
		//move the column position to the first character in the string
		iColPos++;
			
		for(i = iColPos; i < textCharM.length; i++)
		{
			//deal with unprintable characters 
			if(textCharM[i] == '\\')
			{
				i++;
					
				switch(textCharM[i])
				{
					case '\"' :
						newChArray[j] = '\"';
						j++;
						break;
						
					case '\'' :
						newChArray[j] = '\'';
						j++;
						break;
						
					case '\\' :
						newChArray[j] = '\\';
						j++;
						break;
					
					case 'n' :
						newChArray[j] = 0x0A;
						j++;
						break;
					
					case 't' :
						newChArray[j] = 0x09;
						j++;
						break;
					
					case 'a' :
						newChArray[j] = 0x07;
						j++;
						break;
				}
					
				
				totalChars++;
			}
				
			else if(textCharM[i] == quoteChar)
			{
					
				nextToken.tokenStr = String.valueOf(newChArray, 0, totalChars);
				nextToken.iColPos = i;
				nextToken.iSourceLineNr = iSourceLineNr + 1;
				
				//Assign the classes 
				nextToken.primClassif = 1;
				nextToken.subClassif = 5;
				iColPos = i + 1;
				return;
			}
				
				
			else
			{
				newChArray[j] = textCharM[i];
				j++;
				totalChars++;
			}
		}
				
				
		//if you make it here then there was no matching quoteChar 
		HavabolUtilities.scannerError("Reached the end of the line with no matching %c", quoteChar);
		
	}
	
	
	/**
	 * Assigns classes based on an integer or float value and checks for invalid characters  
	 * <p>
	 * @param token			The current token 
	 * @throws Exception	ScannerException 
	 */
		
	public void assignNumber(Token token) throws Exception
	{
		String restOfString = "";
		char[] number = token.tokenStr.toCharArray();
		char currentChar;
		int index = -1;
		int i;
			
		token.primClassif = 1;
			
		//make sure the number contains only numbers or a decimal
		for(i = 0; i < number.length; i++)
		{
			currentChar = number[i];
			if(!Character.isDigit(currentChar) && currentChar != '.' && currentChar != '_')
				HavabolUtilities.scannerError("'%c' is not a valid character for this type", currentChar);
		}
			
		index = token.tokenStr.indexOf('.');
			
		if(index >= 0)
		{
			//make sure there is not an underscore next to the decimal point 
			if(number[index-1] == '_' || number[index +1] == '_')
				HavabolUtilities.scannerError("Cannot have a %c next to a .", '_');
				
			//make sure there isn't another '.' in the number 
			restOfString = token.tokenStr.substring((index + 1), token.tokenStr.length());
				
			if(restOfString.indexOf('.') >= 0) 
				HavabolUtilities.scannerError("More than one '%c' is not allowed", '.');
			else 
				token.subClassif = 3;
		}
		
		else
			token.subClassif = 2;
			
	}
		
		
	/**
	* Puts the token into nextToken but does not assign it to the current token. 
	* Used in the constructor as well as after blank lines and comments 
	* <p>
	*  
	* @throws Exception		ScannerException 
	*/
		
		public void getTokenNoReturn() throws Exception
		{
			char currentChar;
			int startingPosition;
			
			//check for blank lines. Move to the next line if it   
			while(HavabolUtilities.checkBlank(nextToken)== true)
			{
				numBlanks++;
				HavabolUtilities.moveToNextLine(nextToken);
			}
			
			startingPosition = iColPos;
			
			//Move the the first character 
			while(textCharM[startingPosition] == ' ')
			{
				iColPos++;
				startingPosition++;
			}
			
			currentChar = textCharM[iColPos];
			
			
			//Get the token 
			while(currentChar != ' ')
			{
				if(delimiters.indexOf(currentChar) >= 0)
				{
				
					//is it a string?
					if(currentChar == '\"' || currentChar == '\'')
					{
						assignStringLiteral(currentChar);
						return;
					}
					else
						break;
				}
				
				iColPos++;
				
				if(iColPos == textCharM.length)
				{
					nextToken.iColPos = iColPos - 1;
					nextToken.iSourceLineNr = iSourceLineNr + 1;
					assignNextTokenString(startingPosition);
					return;
				}
				else
					currentChar = textCharM[iColPos];
			}
			
			
			nextToken.iColPos = iColPos;
			assignNextTokenString(startingPosition);
			//iColPos++;
		}	
		
		
	
	/**
	 * Assigns the string value to nextToken.tokenstr 
	 * <p>
	 * 
	 * @param startingPosition		position of the token's first char  
	 */
	private void assignNextTokenString(int startingPosition)
	{
		nextToken.tokenStr = String.valueOf(textCharM, startingPosition, (iColPos - startingPosition));
		return;
	}
		

	/**
	 * Sets the error message as a string and throws a ScannerException 
	 * <p>
	 * 
	 * @param fmt			provides the string to pass to the exception
	 * @param varArgs		provides the arguments in the string 
	 * @throws Exception	An exception encountered while scanning the line
	 * 
	 */
		public void error(String fmt, Object... varArgs) throws Exception
		{
			String diagnosticTxt = String.format(fmt, varArgs);
			throw new ScannerException((iSourceLineNr + 1), diagnosticTxt);
		}
		
	
}

	
	
	
	
	
	
	
	
	
	
	
