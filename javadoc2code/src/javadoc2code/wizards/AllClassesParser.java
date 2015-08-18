package javadoc2code.wizards;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class AllClassesParser {

	public static List<String> convertJavadoc(String mainUrl) throws IOException {
		
		List<String> retVal = new ArrayList<String>();
		
		Document doc = Jsoup.connect(mainUrl).get();

		Elements lines = doc.select("li a");
		
		String[] ret = new String[lines.size()];
		
		for (Element line : lines) {
			String relHref = line.attr("href");
			retVal.add( relHref );
		}
		
		
		
		return retVal;
				
	}
	
}
