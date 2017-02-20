package havabol;
import java.util.*;

public class Token 
{
	public String tokenStr = "";
	public int primClassif = 0;
	public int subClassif = 0;
	public int iSourceLineNr = 0;
	public int iColPos = 0;
	
	//Constants for primClassif
	public static final int OPERAND = 	1;	// constants, identifier 
	public static final int OPERATOR = 	2;	// + - * / < > = ! 
	public static final int SEPARATOR = 3;	// ( ) , : ; [ ] 
	public static final int FUNCTION = 	4;	// TBD 
	public static final int CONTROL = 	5;	// TBD
	public static final int EOF = 		6;		// EOF encountered 
	public static final int RT_PAREN = 	7;	// TBD
	
	//Constants for OPERAND's subClassif
	public static final int IDENTIFIER 	= 1;
	public static final int INTEGER 	= 2; 	// integer constant 
	public static final int FLOAT		= 3;	// float constant 
	public static final int BOOLEAN		= 4;	// boolean constant 
	public static final int STRING 		= 5;	// string constant
	public static final int DATE		= 6;	// date constant 
	public static final int VOID		= 7;	// void 
	
	//Constants for CONTROL's subClassif (after Pgm 1)
	public static final int FLOW		= 10; 	// flow statement (e.g., if)
	public static final int END			= 11;	// end statement (e.g., endif)
	public static final int DECLARE		= 12;	// declare statement (e.g., Int)
	
	//Constants for FUNCTION's subClassif (definedby)
	public static final int BUILTIN		= 13;	//builtin function (e.g., print)
	public static final int USER		= 14;	//user defined 
	
	// array of primClassif string values for the constants 
	public static final String[] strPrimClassifM = 
		{ "Undefined"
				, "OPERAND"		// 1
				, "OPERATOR"	// 2
				, "SEPARATOR"	// 3
				, "FUNCTION"	// 4 
				, "CONTROL"		// 5 
				, "EOF"			// 6 
		};
	
	public static final int PRIM_CLASS_MAX = 6;
	// array of subClassif string values for the constants 
	public static final String[] strSubClassifM = 
		{"Undefined"
				, "IDENTIFIER"		// 1 
				, "INTEGER"			// 2 
				, "FLOAT"			// 3 
				, "BOOLEAN"			// 4 
				, "STRING"			// 5 
				, "DATE"			// 6 
				, "Void"			// 7 
				, "**not used**"	// 8 
				, "**not used**"	// 9 
				, "FLOW"			// 10 
				, "END"				// 11
				, "DECLARE"			// 12 
		};
	
	public static final int OPERAND_SUB_CLASS_MIN = 1;
	public static final int OPERAND_SUB_CLASS_MAX = 7;
	public static final int CONTROL_SUB_CLASS_MIN = 10;
	public static final int CONTROL_SUB_CLASS_MAX = 12;
	
	
	
	
	
	public Token(String value)
	{
		this.tokenStr = value;
		this.primClassif = 0;
		this.subClassif = 0;
		this.iSourceLineNr = 0;
		this.iColPos = 0;
	}
	
	public Token()
	{
		this(""); 	// invoke the other constructor 
	}
	
	
	/**
	 * 	Prints the string, primary value, and secondary value of the current token 
	 * <p>
	 */
	
	public void printToken()
	{
		String primClassifStr;
		String subClassifStr;
		
		//convert the primClassif to a string 
		if(primClassif >= 0 && primClassif <= PRIM_CLASS_MAX)
			primClassifStr = strPrimClassifM[primClassif];
		else
			primClassifStr = "**garbage**";
		
		//convert the subClassif to a string 
		switch(primClassif)
		{
			case Token.OPERAND:
				if (subClassif >= OPERAND_SUB_CLASS_MIN
						&& subClassif <= OPERAND_SUB_CLASS_MAX)
					subClassifStr = strSubClassifM[subClassif];
				else
					subClassifStr = "**garbage**";
				break;
			case Token.CONTROL:
				if (subClassif >= CONTROL_SUB_CLASS_MIN
						&& subClassif <= CONTROL_SUB_CLASS_MAX)
					subClassifStr = strSubClassifM[subClassif];
				else
					subClassifStr = "**garbage**";
				break;
			case Token.FUNCTION:
				if (subClassif == BUILTIN)
					subClassifStr = "BUILTIN";
				else if (subClassif == USER)
					subClassifStr = "USER";
				else
					subClassifStr = "**garbage**";
				break;
			default:
				subClassifStr = "-";
		}
		
		System.out.printf("%-11s %-12s %s\n"
			, primClassifStr
			, subClassifStr
			, tokenStr);
		
	}
	
	
	
    /**
     * Prints a string that may contain non-printable characters as two lines.  
     * <p>
     * On the first line, it prints printable characters by simply
     * printing the character.  For non-printable characters
     * in the string, it prints ". ".  
     * <p>
     * The second line prints a two character hex value for the non printable
     * characters in the string line.  For the printable characters, it prints 
     * a space.
     * <p>
     */

	public void hexPrint()
	{
		int length = tokenStr.length();
		char[] chArray = tokenStr.toCharArray();
		char ch;
		int numSpaces = 25;
		boolean containsUnprintable = false;
		
		//string contains an escaped character, need to hex print
		System.out.printf("%-11s %-12s "
				, "OPERAND"
				, "STRING");
			
		for(int i = 0; i < length; i++)
		{
			ch = chArray[i];
			if (ch > 31 && ch < 127)   // ASCII printable characters
				System.out.printf("%c", ch);
	        else
	        {
	            System.out.printf(". ");
	            containsUnprintable = true;
	        }
		}
			
		System.out.printf("\n");
		
		//return if the string doesn't contain any escaped value chars
		if(containsUnprintable == false)
			return;
			
		for(int i = 0; i < numSpaces; i++)
			System.out.printf(" ");
			
		for(int i = 0; i < length; i++)
		{
			ch = chArray[i];
			
			if(ch > 31 && ch < 127)
				System.out.printf(" ");
			else
				System.out.printf("%02X", (int) ch);
		}
		
		System.out.printf("\n");
			
			
		
	}
		
		
	
	
	
	
	
	
	
	
}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
		
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	

