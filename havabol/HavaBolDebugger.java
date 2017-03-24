package havabol;

import java.io.*;
import java.util.*;

public class HavaBolDebugger {
	private Token token;
	private String expr;
	private String assign;
	private String statement;
	
	private static final String debugStr = "/t/t...";
	private static final String[] settings = { "bShowtoken", "bShowExpr", "bShowAssign", "on", "off" };
	private static boolean bShow, bExp, bAssign, debugOn;

	public HavaBolDebugger(String[] args) {
		bShow = false;
		bExp = false;
		bAssign = false;
		debugOn = false;

		if (args.length > 4) {
			// error
		} else {
			/* creates an arraylist  of the args and compares it to the settings */
			ArrayList<String> v = new ArrayList<String>(Arrays.asList(args));
			int i = 0;
			while (i < args.length) {
				if (v.contains(settings[i])) {
					if (i == 0) {
						bShow = true;
					} else if (i == 1) {
						bExp = true;
					} else if (i == 2) {
						bAssign = true;
					} else if (i == 3) {
						debugOn = true;
					} else if (i == 4) {
						debugOn = false;
					}
				}

				i++;
			}
		}
	}
	/**
	 * Takes in a token and sets the toString
	 * @param token
	 */
	public void debug(Scanner s) {
		while (debugOn) {

			if (bShow) {
				System.out.println(debugStr + s.currentToken);
			}
			if (bExp) {
				
			}
			if (bAssign) {
				
			}
		}
	}

	public static void main(String[] args) {
		HavaBolDebugger h1 = new HavaBolDebugger(args);
	}

	@Override
	public String toString() {
		return " ";
	}
}
