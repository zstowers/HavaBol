package havabol;

public class HavabolUtilities {
	
    public HavabolUtilities()
	{
		
	}
	
	public static boolean checkBlank(Token token) throws Exception
	{
		
	
		char currentChar; 
		
		
		
		//if(nextToken.primClassif == 6)	//end of file 
		if(token.primClassif == 6)	
			return false;
		
		if(Scanner.textCharM.length == 0)
			return true;
	
		else
			currentChar = Scanner.textCharM[0];
		
		
		
		//get the first character, if you reach the end of the line, the line contains 
		//only spaces or tabs and is therefore blank 
		while(currentChar == ' ' || currentChar == '\t' || currentChar == '\n')
		{
			token.iColPos++;
			
			if(token.iColPos == Scanner.textCharM.length)
				return true;
			else	
				currentChar = Scanner.textCharM[token.iColPos];
		}
		
		//Line is not blank, check for comments 
		if(currentChar == '/')
		{
			if(Scanner.textCharM[token.iColPos + 1] == '/')
				return true;
		}
		
		//Line is not blank   
		return false;
	}
	
	
	public static void moveToNextLine(Token token)
	{
		Scanner.iColPos = 0;
		token.iColPos = 0;
		Scanner.iSourceLineNr++;
		
		Scanner.printLine = true;
		
		if(Scanner.iSourceLineNr == Scanner.sourceLineM.size())
		{
			//End of file 
			token.primClassif = 6;
			return;
		}
		else
			Scanner.textCharM = Scanner.sourceLineM.get(Scanner.iSourceLineNr).toCharArray();
		
	}
	
	public static void scannerError(String fmt, Object... varArgs) throws Exception
	{
		String diagnosticTxt = String.format(fmt, varArgs);
		throw new ScannerException((Scanner.iSourceLineNr + 1), diagnosticTxt);
	}
	
	
	
	
	
	
	

}
