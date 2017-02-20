package havabol;

import java.io.*;

public class ScannerException extends RuntimeException  {
	
	
	public int iLineNr;
	public String diagnoticTxt;
	
	
	
	public ScannerException(int iLineNr, String diagnosticTxt)
	{
		this.iLineNr = iLineNr;
		this.diagnoticTxt = diagnosticTxt;
		printErrorMessage(iLineNr, diagnosticTxt);
	}
	
	/**
	 * Throws a runtime exception and prints a diagnostic error message 
	 * <p>
	 * 	 
	 * @param iLineNr			The line number of the error 
	 * @param diagnosticTxt		The error message to be printed 
	 */
	
	public void printErrorMessage(int iLineNr, String diagnosticTxt)
	{
		System.out.println("Error on line " + iLineNr + ": " + diagnosticTxt);
	}

}
