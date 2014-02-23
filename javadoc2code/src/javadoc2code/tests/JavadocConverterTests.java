/**
 * 
 */
package javadoc2code.tests;

import static org.junit.Assert.*;

import java.io.IOException;

import javadoc2code.wizards.JavadocConverter;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author Mark
 *
 */
public class JavadocConverterTests {

	/**
	 * Test method for {@link javadoc2code.wizards.JavadocConverter#convertJavadoc(java.lang.String, java.lang.String)}.
	 */
	@Test
	public void testConvertJavadoc() {
		final String URL = "https://www.cs.rit.edu/~csci142/Labs/04/Doc/Game.html";
		final String URL2 = "https://www.cs.rit.edu/~csci142/Labs/04/Doc/GermanBoardGame.html";
		final String URL3 = "http://docs.oracle.com/javase/7/docs/api/java/lang/String.html";
		String result;
		try {
			result = JavadocConverter.convertJavadoc(URL);
			System.out.println(result);
			assertTrue(result.contains("public abstract class Game"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
