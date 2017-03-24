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
     *  Receives two numeric values and performs mathematical operations on them 
     *  <p>
     * 
     * @param parser for any exceptions
     * @param leftOp value and type of the left operand 
     * @param rightOp value and type of the right operand 
     * @return the calculated expression based on the operator 
     * @throws Exception if there is an invalid operator type 
     */
    public static ResultValue returnResult(Parser parser, Numeric leftOp, Numeric rightOp) throws Exception
    {
    	InfixEvaluation evaluateInfix = new InfixEvaluation(parser);
    	ResultValue result = new ResultValue();
    	double val1;
    	double val2;
    	double dVal;
    	int iVal;
    	
    	String operator = leftOp.expr;
    	
    	
    	if(leftOp.type == Token.INTEGER)
    	{
    		result.type = Token.INTEGER;
    		
    		//cast the integer values to doubles 
    		val1 = leftOp.doubleValue;
    		val2 = (double)rightOp.integerValue;

    		dVal = operate(parser, val1, val2, operator);
    		
    		//convert to an int and store the value as a string 
    		iVal = (int)dVal;
    		result.value = String.valueOf(iVal);
    		
    		return result;
    	}	
    	
    	
    	else if(leftOp.type == Token.FLOAT)
    	{
    		result.type = Token.FLOAT;
    		
    		val1 = leftOp.doubleValue;
    		val2 = rightOp.doubleValue;
    		
    		dVal = operate(parser, val1, val2, operator);
    		result.value = String.valueOf(dVal);
    		
    		return result;
    	}
    	
    	else
    	{
    		parser.error("In Havabol Utilities, '%s' is not a valid operator", operator);
    		return null;
    	}
    		
    }
    
    
    
    
    /**
     * Calculates a simple math expression based on a single operator
     * <p>  
     * @param val1	left operand 
     * @param val2	right operand 
     * @param operator	math operator 
     * @return	result of the simple expression 
     * @throws Exception If the operator is invalid 
     */
    public static double operate(Parser parser, double val1, double val2, String operator) throws Exception
    {
    	double result = 0.0;
    	
    	switch(operator)
    	{
    		case "+" :
    			return add(val1, val2);
    		
    		case "-" :
    			return sub(val1, val2);
    		
    		case "*" :
    			return mul(val1, val2);
    			
    		case "/" :
    			return div(val1, val2);
    		
    		case "^" :
    			return exp(val1, val2);
    		
    		default :
    			parser.error("error, '%s' is not a valid operator", operator);
    	}
    	
    	return result;
    }
    
    
    
    /**
     * Compares two numbers and returns the result based on a logical operator
     * <p>
     *   
     * @param parser	For error handling 
     * @param resOp1	first number 
     * @param resOp2 	second number 
     * @param operator 	logical operator 
     * @return			true or false, based on the operator 
     * @throws Exception if the operator is an invalid logical operator 
     */
    public static boolean compareNumerics(Parser parser, ResultValue resOp1, ResultValue resOp2, String operator) throws Exception
    {
    	double val1 = Numeric.getDoubleValue(resOp1.value);
    	double val2 = Numeric.getDoubleValue(resOp2.value);
    	
    	switch(operator)
    	{
    		case ">=" :
    			if(val1 >= val2)
    				return true;
    			else 
    				return false;
    		
    		case ">" :
    			if(val1 > val2)
    				return true;
    			else
    				return false;
    		
    		case "<=" :
    			if(val1 <= val2)
    				return true;
    			else
    				return false;
    		
    		case "<" :
    			if(val1 < val2)
    				return true;
    			else
    				return false;
    		
    		case "!=" :
    			if(val1 != val2)
    				return true;
    			else
    				return false;
    		
    		case "==" :
    			if(val1 == val2)
    				return true;
    			else
    				return false;
    		
    		default :
    			parser.error("Error in compare Numerics, '%s' is not a valid logical operator", operator);
    			return false;
    		
    	}
    }
    
    
    /**
	 * Subtracts two double values 
	 * <p>
	 * 
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
	 * 
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
	 *  
	 * @param a 	base 
	 * @param b		exponent  
	 * @return		a to the power of b 
	 */
	
	public static double exp(double a, double b){
		return Math.pow(a,b);
	}
	
		
	/**
	 * Computes the product of two number
	 * <p>
	 *  
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
	 * 
	 * @param a		first double value 
	 * @param b		second double value 
	 * @return		a divided by b 
	 */
	public static double div(double a, double b){
		return a/b;
	}
	
	/**
	 * 
	 * @param a
	 * @return -U
	 */
	public static double unaryMinus(double a){
		return -a;
	}
	
	/**
	 * 
	 * @param a
	 * @param b
	 * @return boolean
	 */
	public static boolean equals(double a, double b){
		return a == b;
	}
	
	/**
	 * 
	 * @param a
	 * @param b
	 * @return boolean
	 */
	public static boolean lessThan(double a, double b){
		return (a < b);
	}
	
	/**
	 * 
	 * @param a
	 * @param b
	 * @return boolean
	 */
	public static boolean greaterThan(double a, double b){
		return (a > b);
	}
	
	/**
	 * 
	 * @param a
	 * @param b
	 * @return boolean
	 */
	public static boolean greaterThanLess(double a, double b){
		return (a >= b);
	}
	
	/**
	 * 
	 * @param a
	 * @param b
	 * @return boolean
	 */
	public static boolean lessThanLess(double a, double b){
		return (a <= b);
	}
	
	/**
	 * 
	 * @param a
	 * @param b
	 * @return boolean
	 */
	public static boolean notEqual(double a, double b){
		return (a != b);
	}

    
	/**
     *  Checks to see if the line is blank or if the line contains only a comment 
     *  <p>
     *  
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
	
	
	/**
	 * Used only for debugging purposes, prints the symbol table entries 
	 * @param map
	 */
	public static void printSymbolTable(HashMap<String, STEntry> map)
	{
		System.out.println("\t\t Symbol Table\n");
		
		for(Map.Entry<String,STEntry> entry : map.entrySet())
		{
			System.out.println(entry.getKey() + " " + entry.getValue());
		}
	}
	
	
	/**
	 *  Used only for debugging purposes, prints the storage manager and the variable values 
	 * @param map
	 */
	public static void printStorage( HashMap<String, StorageEntry> map)
	{
		System.out.println("\n\t\t Storage Manager\n");
		
		for(Map.Entry<String, StorageEntry> entry : map.entrySet())
		{
			System.out.println(entry.getKey() + " "  + entry.getValue());
		}
	}
	
	public static double exponent(double op1, double op2)
	{
		
		return Math.pow(op1, op2);
		
	}
	
	
}
