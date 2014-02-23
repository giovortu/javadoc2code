/**
 * 
 */
package javadoc2code.wizards;

import java.io.FileWriter;
import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Entities;
import org.jsoup.select.Elements;

/**
 * @author Mark
 *
 */
public class JavadocConverter {
	final static int LINE_LENGTH = 60;
	
	public static String convertJavadoc(String url) throws IOException
	{	
		Document doc = Jsoup.connect(url).get();
		doc = Jsoup.parse(doc.html().replace("&nbsp;", " "));
		doc.outputSettings().escapeMode(Entities.EscapeMode.xhtml);
	
		//FileWriter outputFile = new FileWriter(filename);
		StringBuilder outputFile = new StringBuilder();
		
		// Write classname
		outputFile.append(getClassDeclaration(doc));
		outputFile.append(" {\n\n");
		
		// Write constructor
		outputFile.append(getMethods(doc, "constructor_detail"));
		
		// Write methods 
		outputFile.append(getMethods(doc, "method_detail"));
		
		outputFile.append("}\n");
		return outputFile.toString();
	}
	
	private static String getClassDeclaration(Document htmlDoc)
	{	
		StringBuilder declarationString = new StringBuilder();
		Element classDeclaration = htmlDoc.select("pre").get(0);
		declarationString.append("/**\n");
		String[] classDescription = Utils.breakupString(
				classDeclaration.siblingElements().select("div").text(), LINE_LENGTH);
		for (String s : classDescription)
		{
			declarationString.append(" * " + s + "\n");
		}
		declarationString.append(" *\n");
		declarationString.append(" * @author <insert name>\n");
		declarationString.append(" */\n");
		declarationString.append(classDeclaration.text().replace("\n", " "));
		return declarationString.toString();
	}
	
	private static String getMethods(Document htmlDoc, String category)
	{
		StringBuilder methodString = new StringBuilder();
		Element methodDetail = htmlDoc.select("a[name=" + category + "]").first();
		Elements methods = methodDetail.siblingElements().select("li");
		
		for (Element method : methods)
		{
			methodString.append("\t/**\n");
			String[] methodDescription = Utils.breakupString(method.select("div").text(), LINE_LENGTH);
			for (String s : methodDescription){
				methodString.append("\t * " + s  + "\n");
			}
			for (Element param : method.select("dl dd"))
			{	
				if (param.text().contains("-") && (param.childNodeSize() > 0)){
					String[] paramInfo = param.text().split("-", 2);
					methodString.append("\t * @param \t" 
							+ paramInfo[0].trim() + "\t " 
							+ paramInfo[1].trim() + "\n");
				} else {
					if(param.previousElementSibling().text().equals("Returns:")){
						methodString.append("\t * @returns \t" 
								+ param.text().trim() + "\n");
					}
				}
			}
			methodString.append("\t */\n");
			String methodDeclaration = method.select("pre").text();
			methodString.append("\t" + methodDeclaration);
			if (methodDeclaration.contains(" abstract ")){
				methodString.append(";\n\n");
			} else {
				methodString.append(" {\n");
				methodString.append("\n\t}\n\n");
			}
		}
		return methodString.toString();
	}
}
