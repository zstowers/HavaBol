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
			// possibly remove the below statements 
			doubleValue = (double) integerValue;
		}
		
		else if(isFloat(strValue)){
			type = 3;
			doubleValue = getDoubleValue(strValue);
			//possibly remove the below stmt 
			integerValue = (int) doubleValue;
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
	 * Takes a String and verifies it as a Float or a Int.
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
