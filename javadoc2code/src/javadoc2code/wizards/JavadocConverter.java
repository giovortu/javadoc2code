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

	public static String convertJavadoc(String url) throws IOException {
		Document doc = Jsoup.connect(url).get();
		doc = Jsoup.parse(doc.html().replace("&nbsp;", " "));
		doc.outputSettings().escapeMode(Entities.EscapeMode.xhtml);

		// FileWriter outputFile = new FileWriter(filename);
		StringBuilder outputFile = new StringBuilder();

		// Write classname
		String classDeclaration = getClassDeclaration(doc);
		outputFile.append(classDeclaration);
		outputFile.append(" {\n\n");

		boolean isInterface = classDeclaration.contains(" interface ");

		if (!isInterface) {
			// Write constructor
			outputFile
					.append(getMethods(doc, "constructor_detail", isInterface));
		}

		// Write methods
		outputFile.append(getMethods(doc, "method_detail", isInterface));

		outputFile.append("}\n");
		return outputFile.toString();
	}

	private static String getClassDeclaration(Document htmlDoc) {
		StringBuilder declarationString = new StringBuilder();
		Element classDeclaration = htmlDoc.select("pre").get(0);
		declarationString.append("/**\n");
		String[] classDescription = Utils.breakupString(classDeclaration
				.siblingElements().select("div").text(), LINE_LENGTH);
		for (String s : classDescription) {
			declarationString.append(" * " + s + "\n");
		}
		declarationString.append(" *\n");
		declarationString.append(" * @author <insert name>\n");
		declarationString.append(" */\n");
		declarationString.append(classDeclaration.text().replace("\n", " "));
		return declarationString.toString();
	}

	private static String getMethods(Document htmlDoc, String category,
			boolean isInterface) {
		StringBuilder methodString = new StringBuilder();
		Elements categoryElements = htmlDoc.select("a[name=" + category + "]");

		if (!categoryElements.isEmpty()) {
			Element methodDetail = categoryElements.first();
			Elements methods = methodDetail.siblingElements().select("li");

			for (Element method : methods) {
				methodString.append("\t/**\n");
				
				String[] methodDescription = Utils.breakupString(
						method.select("div").text(), LINE_LENGTH);
				
				// Grab the descriptions of the method
				for (String s : methodDescription) {
					methodString.append("\t * " + s + "\n");
				}
				
				boolean containsThrows = false;
				StringBuilder throwsString = new StringBuilder();
				// Get all the parameters and throws
				for (Element param : method.select("dl dd")) {
					if (param.text().matches("\\w+\\s-\\s.*")
							&& (param.childNodeSize() > 0)) {
						// Check to see if we have entered the throws section
						if(!containsThrows)
						{
							if (param.previousElementSibling().text().equals("Throws:"))
							{
								containsThrows = true;
							}
						}
						String[] paramInfo = param.text().split("-", 2);
						if (!containsThrows){
							methodString.append("\t * @param \t");
						} else {
							methodString.append("\t * @throws \t");
							// Add the throws types to their own string for adding
							// to the method declaration
							throwsString.append(paramInfo[0].trim() + " ");
						}
						methodString.append(paramInfo[0].trim() + "\t "
								+ paramInfo[1].trim() + "\n");
					} else {
						if (param.previousElementSibling().text()
								.equals("Returns:")) {
							methodString.append("\t * @return \t"
									+ param.text().trim() + "\n");
						}
					}
				}
				methodString.append("\t */\n");
				String methodDeclaration = method.select("pre").text();
				// So Strings can just be Strings
				methodDeclaration.replace("java.lang.", "");
				methodString.append("\t" + methodDeclaration);
				if (containsThrows)
				{
					methodString.append(" throws " + throwsString);
				}
				if (methodDeclaration.contains(" abstract ") || isInterface) {
					methodString.append(";\n\n");
				} else {
					methodString.append(" {\n");
					methodString.append("\n\t}\n\n");
				}
			}
		}
		return methodString.toString();
	}
}
