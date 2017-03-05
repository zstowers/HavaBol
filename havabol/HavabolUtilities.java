package havabol;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

/**
* 
* @author Jason Luna
* Math utilities that take the input and crunches the numbers returning a value
* Commonly used scanner utilities 
* <p>
*/

public class HavabolUtilities {
	
    public HavabolUtilities(){
	}
    
    /**
     * Adds two integers together
     * <p> 
     * @param a		first integer 
     * @param b 	second integer 
     * @return		sum of a and b
     */
	public static int addInt(int a, int b){
		return a+b;
	}
	
	/**
	 * Subtracts two integers
	 * <p> 
	 * @param a		first integer 
	 * @param b		second integer 
	 * @return		difference of a and b 
	 */
	public static int subInt(int a, int b){
		return a-b;
	}
	
	/**
	 * Subtracts two double values 
	 * <p>
	 * @param a		first double 
	 * @param b		second double 
	 * @return		difference of a and b 
	 */
	public static double sub(double a, double b){
		return a-b;
	}
	
	
	/**
	 * Adds two double values 
	 * <p>
	 * @param a		first double 
	 * @param b		second double 
	 * @return		sum of a and b 
	 */
	public static double add(double a, double b){
		return a+b;
	}
	
	
	/**
	 * Computes the value of a base and an exponent
	 * <p> 
	 * @param a 	base 
	 * @param b		exponent  
	 * @return		a to the power of b 
	 */
	public static double exp(double a, double b){
		if(b == 0){
			return 1;
		}
		if(b <0){
			return -1;
		}
		double result = 1;
		for(int i = 0; i < b; i++){
			result *= a; 
		}	
		return result;
	}
	
	
	/**
	 * Computes the product of two number
	 * <p> 
	 * @param a		first double 
	 * @param b		second double 
	 * @return		product of a and b 
	 */
	public static double mul(double a, double b){
		return a*b;
	}
	
	/**
	 * Computes the quotient of two numbers
	 * <p>
	 * @param a		first double value 
	 * @param b		second double value 
	 * @return		a divided by b 
	 */
	public static double div(double a, double b){
		return a/b;
	}

    /**
     *  Checks to see if the line is blank or if the line contains only a comment 
     *  <p>
     * @param token			nextToken, it is set to the beginning of the line 
     * @return				true if the line is a blank or comment and false if not 
     * @throws Exception	
     */
    
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
	
	
	/**
	 * Moves to the next line of the input file and assigns it to textCharM.  If there are 
	 * no more lines it sets the token's primary class to EOF.  Also increments iSourceLineNr
	 * <p>
	 * @param token		nextToken, column position will be set to 0  
	 */
	
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
	
	

	
	/**
	 * Formats the error message and throws a scanner exception
	 * <p> 
	 * @param fmt			The error message to be printed 
	 * @param varArgs		The value of the arguments to include in the error message 
	 * @throws Exception	Throws a scanner exception 
	 */
	
	public static void scannerError(String fmt, Object... varArgs) throws Exception
	{
		String diagnosticTxt = String.format(fmt, varArgs);
		throw new ScannerException((Scanner.iSourceLineNr + 1), diagnosticTxt);
	}
	
	
	public static void printMap(HashMap<String, STEntry> table)
	{
		Set entrySet = table.entrySet();
		Iterator it = entrySet.iterator();
		STEntry st;
		String primary;
		String secondary;
		
		while(it.hasNext())
		{
			Map.Entry entry = (Map.Entry)it.next();
			st = (STEntry) entry.getValue();
			System.out.println(st.toString());
		}
	}
	
	public static void printStorage( HashMap<String, StorageEntry> map)
	{
		
		for(Map.Entry<String, StorageEntry> entry : map.entrySet())
		{
			System.out.println(entry.getKey() + " " + entry.getValue());
		}
		
	
	}
	
	
}
