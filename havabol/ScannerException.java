package havabol;

import java.io.*;

public class ScannerException extends RuntimeException  {
	
	
	private int iLineNr;
	private String diagnoticTxt;
	
	public ScannerException(int iLineNr, String diagnosticTxt)
	{
		this.iLineNr = iLineNr;
		this.diagnoticTxt = diagnosticTxt;
		printErrorMessage(iLineNr, diagnosticTxt);
	}
	
	public void printErrorMessage(int iLineNr, String diagnosticTxt)
	{
		System.out.println("Error on line " + iLineNr + ": " + diagnosticTxt);
	}

}
