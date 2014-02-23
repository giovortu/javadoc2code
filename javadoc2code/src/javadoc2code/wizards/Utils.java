/**
 * 
 */
package javadoc2code.wizards;

import java.util.ArrayList;

/**
 * @author Mark
 *
 */
public class Utils {
	public static String[] breakupString(String userString, int lineLength)
	{
		ArrayList<String> returnStrings = new ArrayList<String>(); 
		String[] userStringSeparated = userString.split("\\s+");
		String currentLine = "";
		for(String currentString : userStringSeparated)
		{
			if ((currentLine.length() + currentString.length()) > lineLength){
				returnStrings.add(currentLine.trim());
				currentLine = currentString;
			} else {
				currentLine += " " + currentString;
			}
		}
		if (!currentLine.isEmpty())
		{
			returnStrings.add(currentLine.trim());
		}
				
		String[] returnArray = new String[returnStrings.size()];
		returnStrings.toArray(returnArray);
		return returnArray;
	}
}
