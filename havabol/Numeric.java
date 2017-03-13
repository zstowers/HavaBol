package havabol;

/* *
 * Numeric Class
 * receives the input of a res02
 */
public class Numeric {

	// initialize
	public int integerValue;
	public Parser parser;
	public ResultValue result;
	public double doubleValue;
	public String expr;
	public String operandStr;
	public String strValue; // display value
	public int type; // INTEGER, FLOAT

	public Numeric(Parser parser, ResultValue result, String expr, String operandStr) {
		
		this.parser = parser;
		this.result = result;
		this.expr = expr;
		this.operandStr = operandStr;
		
		strValue = result.value;
		
		
		if(isInt(strValue)){
			type = 2;
			integerValue = getIntegerValue(strValue);
		}
		
		else if(isFloat(strValue)){
			type = 3;
			doubleValue = getDoubleValue(strValue);
		}
		
		
		
		
		
		else{
			NumberFormatException e = new NumberFormatException();
			//System.err.printf("Line %d Invalid numeric constant: %s  %s  %s %d: %s", scan.iSourceLineNr, strValue, expr, operandStr, e);
			e.printStackTrace();
			System.exit(-1);
		}	
	}
	
	/**
	 * Converts Strings to Integers 
	 * @param str
	 * @return int
	 */
	public static int getIntegerValue(String str) {
		
		if(isFloat(str))
			return (int) getDoubleValue(str);
		
		else	
			return  Integer.parseInt(str);
	}
	
	/**
	 * Converts Strings to Doubles 
	 * @param str
	 * @return
	 */
	public static double getDoubleValue(String str) { 
		
		return Double.parseDouble(str);
		
	}
	
	/**
	 * 
	 * @return
	 */
	public static double doubleArithmetic(double op1, double op2, String operator)
	{
		switch(operator)
		{
			case "^" :
				return Math.pow(op1, op2);
			
			case "+" :
				return op1 + op2;
				
			case "-" :
				return op1 - op2;
			
			case "*" :
				return op1 * op2;
			
			case "/" :
				return op1 / op2;
			
			default :
				System.out.println("This is a double math error");
				return 0.0;
			
		}
	
	}
	
	/**
	 * 
	 * @param op1
	 * @param op2
	 * @param operator
	 * @return
	 */
	public static int intArithmetic(int op1, int op2, String operator)
	{
		switch(operator)
		{
			case "^" :
				return (int) Math.pow(op1, op2);
			
			case "+" :
				return op1 + op2;
				
			case "-" :
				return op1 + op2;
			
			case "*" :
				return op1 * op2;
			
			case "/" :
				return op1 / op2;
			
			default :
				System.out.println("this is an integer math error");
				return 0;
			
		}
	
	}
	
	
	/**
	 * Takes a String and verify's it as a Float or a Int.
	 * @param str
	 * @returns boolean
	 */
	public static boolean isFloat(String str) {
		return str.matches("-?\\d+(\\.\\d+)?"); // match a number with optional
												// '-' and decimal.
	}
	public static boolean isInt(String str) {
		return str.matches("\\d+(\\+)?"); // match a number with optional '-'
											// and decimal.
	}

}
