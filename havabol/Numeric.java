package havabol;

/* *
 * Numeric Class
 * receives the input of a res02
 */
public class Numeric {

	// initialize
	public int integerValue;
	public Scanner scan;
	public ResultValue result;
	public double doubleValue;
	public String expr;
	public String operandStr;
	public String strValue; // display value
	public int type; // INTEGER, FLOAT

	public Numeric(Scanner scan, ResultValue result, String expr, String operandStr) {
		
		this.scan = scan;
		this.result = result;
		this.expr = expr;
		this.operandStr = operandStr;
		
		strValue = result.getValue();
		if(isFloat(strValue)){
			type = 3;
			doubleValue = getDoubleValue(strValue);
		}
		else if(isInt(strValue)){
			type = 2;
			integerValue = getIntegerValue(strValue);
		}
		else{
			NumberFormatException e = new NumberFormatException();
			System.err.printf("Line %d Invalid numeric constant: %s  %s  %s %d: %s", scan.iSourceLineNr, strValue, expr, operandStr, e);
			e.printStackTrace();
			System.exit(-1);
		}	
	}
	
	/**
	 * Converts Strings to Integers or Doubles
	 * @param str
	 * @return ints or doubles
	 */
	public int getIntegerValue(String str) {
		return  Integer.parseInt(str);
	}
	public double getDoubleValue(String str) { 
		return Double.parseDouble(str);
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
