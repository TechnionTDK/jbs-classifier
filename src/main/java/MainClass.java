import info.bliki.wiki.dump.*;
import utils.Dbg;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Scanner;

import static java.lang.System.exit;

/**
 * Created by eurocom on 27/06/2017.
 */
public class MainClass {

	static boolean allWiki = false;
	static WikiArticle testwiki=null;
	static ArrayList<String> titles = new ArrayList<String>();
	static String inTS="";

	public static void main(String[] args) throws Exception {
		if (!readArguments(args))
			return;
		if (!inTS.equals(""))
			Runner.setTS(inTS);
		if (allWiki || !titles.isEmpty())
			processWikis();
		else if (testwiki!=null){
			System.out.println(testwiki.getText());
			new Runner(testwiki).run();
		}

	}

	static void usageMsg(boolean fullMsg, String errorMsg) {
		if (errorMsg!=null) System.out.println(errorMsg);
		System.out.println("\nOptions: --all|--file <path to file>|--help [--dbg <debug flags>]\n");
		if(!fullMsg){
			System.out.println("To see full usage please run with --help/-h\n\n");
			return;
		}
		System.out.println("Without any option (or only dbg flag) interactive mode will be used.\n");

		System.out.println("-a, --all                       Start Wikipedia full scan (scan all pages).\n");
		System.out.println("-f, --file <file>               Scan specific Wikipedia pages.\n" +
						   "                                Pages topic should be provided in the added file\n" +
						   "                                Using \'topics.in\' as default file\n");
		System.out.println("-t, --test <file>               Scan a file emulating a Wikipedia page.\n" +
						   "                                Using \'test.in\' as default file\n");
		System.out.println("-T, --time <timestamp>          Provide timestamp used for the output folder.\n" );
		System.out.println("-h, --help                      Print this message");
		System.out.println("-d, --dbg <debug flags>         Specify which debug flags to enable.");
		System.out.println("                                PAGE and ERROR are enabled by default.");
		System.out.println("                                Following debug flags are available:");
		System.out.println("                                NONE  - Disable debug messages.");
		System.out.println("                                ERROR - Show failure messages.");
		System.out.println("                                INFO  - General information on the program status.");
		System.out.println("                                PAGE  - General information on a parsed wiki page.");
		System.out.println("                                CAT   - Information on page categories.");
		System.out.println("                                FOUND - Potential references as found in parsed wiki page.");
		System.out.println("                                FINAL - References found in parsed wiki page in final format.");
		System.out.println("                                URI   - References URI found in parsed wiki page.");
		System.out.println("                                ANY   - Enable all debug messages.\n");

		System.out.println("Scan output is written to outputs/<timestamp>.json\n");

		System.out.println("Further information is available under 'stat_<timestamp>' \nin the following files:");
		System.out.println("all_pages                       All parsed pages");
		System.out.println("pages_with_refs                 Pages with potential references");
		System.out.println("pages_refs                      Potential references by page");
		System.out.println("pages_with_uri                  Pages with references");
		System.out.println("pages_uri                       URIs by page");
		System.out.println("error_pages                     Un-parsable pages");
		System.out.println("profiler                      	Run time information of each phase\n\n");
	}

	static boolean readArguments(String[] args) throws Exception {
		int currArg = 1;
		if (args==null || args.length==0){
			usageMsg(false, "please add require arguments");
			return false;
		}

		if (args[0].equals("--all") || args[0].equals("-a")) {
			allWiki = true;
		}
		else if (args[0].equals("--help") || args[0].equals("-h")) {
			usageMsg(true, null);
			return false;
		}
		else if (args[0].equals("--file") || args[0].equals("-f")) {
			String path="";
			if (args.length <= currArg || args[currArg].equals("--dbg") || args[currArg].equals("-T") || args[currArg].equals("--time")) {
				System.out.println("\nNo file mentioned, using default - topics.in \n");
				path="topics.in";
			} else {
				path = args[currArg];
				currArg++;
			}
			if (!readTitlesFromFile(path)) {
				usageMsg(false, null);
				return false;
			}
		}
		else if (args[0].equals("--test") || args[0].equals("-t")) {
			String path="";
			if (args.length <= currArg || args[currArg].equals("--dbg") || args[currArg].equals("-T") || args[currArg].equals("--time")) {
				System.out.println("\nNo file mentioned, using default - test.in \n");
				path="test.in";
			} else {
				path = args[currArg];
				currArg++;
			}
			String fileContent = new String ( Files.readAllBytes( Paths.get(path) ) );
			testwiki = new WikiArticle();
			testwiki.setText(fileContent);
		}
		else {
			usageMsg(false, "\nUnknown argument: " + args[0] + "\n");
			return false;
		}
		if (args.length <= currArg)
			return true;

		if (args[currArg].equals("--time") || args[currArg].equals("-T")) {
			currArg++;
			if (args.length <= currArg || args[currArg].equals("--dbg") ) {
				System.out.println("\nNo timestamp provided, timestamp will be calculated automaticly\n");
			} else {
				inTS = args[currArg];
				currArg++;
			}
		}
		if (args.length <= currArg)
			return true;

		if (!args[currArg].equals("--dbg") && !args[currArg].equals("-d")) {
			usageMsg(false, "\nBad argument \'" + args[currArg] + "\' after \'" + args[0] + "\' expected no argument or --dbg\n");
			return false;
		}
		if (++currArg >= args.length) {
			System.out.println("\nNo debug flags, continue with defaults \n");
			return true;
		}
		Dbg.enabledFlags = 0;
		for(; currArg< args.length; currArg++) {
			try {
				Dbg.enabledFlags |= Dbg.valueOf(args[currArg]).id;
			} catch (Exception e) {
				usageMsg(false, "failed to set debug flag: " + args[currArg]);
				return false;
			}
		}
		return true;
	}

	static boolean readTitlesFromFile(String fileName) throws Exception{
		File file = new File(fileName);
		if (!file.exists()) {
			System.out.println("\nFile \'" + fileName + "\' does not exist");
			return false;
		}
		Scanner inFile = new Scanner(file);
		System.out.println("\nParsing following wiki pages from file \'" + fileName + "\':");
		while (inFile.hasNext()) {
			String newTitle = inFile.nextLine();
			newTitle = newTitle.trim();
			if (!newTitle.matches("^[א-ת0-9a-zA-Z]" + ".*")) {
				continue;
			}
			titles.add(newTitle);
			System.out.println("*" + newTitle + "*");
		}
		System.out.println("");
		inFile.close();
		return true;
	}

	static void processWikis(){
	   String Filename = "hewiki-20160203-pages-articles.xml";
	   try {
		   IArticleFilter handler = new DumpArticleFilter();
		   File f = new File(Filename);
		   info.bliki.wiki.dump.WikiXMLParser wxp = new info.bliki.wiki.dump.WikiXMLParser(f,handler);
		   wxp.parse();
	   } catch (Exception e) {
		   e.printStackTrace();
	   }
	   System.out.println("\n\nPage Scan finished. \nOutputs added to files with Timestamp " + Runner.ts + "\n\n");
   }

	static class DumpArticleFilter implements IArticleFilter {
		public void process(WikiArticle page, Siteinfo info) throws IOException {
			try {
				if ( allWiki || titles.contains(page.getTitle()) ) {
					titles.remove(page.getTitle());
					new Runner(page).run();
				}
			} catch (Exception e) {
				System.out.println("WE SHOULD NOT GET HERE!!!! \n");
				e.printStackTrace();
			}
			if (!allWiki && titles.isEmpty()) {
				System.out.println("\n\nFinished scanning all topics. \nOutputs added to files with Timestamp " + Runner.ts + "\n\n");
				exit(0);
			}
		}
	}
}



