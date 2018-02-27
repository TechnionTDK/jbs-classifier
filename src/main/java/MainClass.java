import info.bliki.wiki.dump.*;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

/**
 * Created by eurocom on 27/06/2017.
 */
public class MainClass {

	static boolean allWiki = false;
	static Scanner in = new Scanner(System.in);
	static ArrayList<String> titles = new ArrayList<String>();

	public static void main(String[] args) throws Exception {
		if (!readArguments(args))
			return;
		if (allWiki || !titles.isEmpty())
			processWikis();
	}

	static void usageMsg(boolean fullMsg, String errorMsg) {
		if (errorMsg!=null) System.out.println(errorMsg);
		System.out.println("\nOptions: --all|--file <path to file>|--help [--dbg <debug flags>]\n");
		if(!fullMsg){
			System.out.println("To see full usage please run with --help/-h\n\n");
			return;
		}
		System.out.println("Without any option (or only dbg flag) interactive mode will be used.\n");

		System.out.println("-a, --all                       Start Wikipedia full scan (scan all pages).\n" +
				  		   "                                This option cannot be combined with -f/--file.");
		System.out.println("-f, --file <files>              Scan specific Wikipedia pages.\n" +
						   "                                Pages URLs should be provided in the added files\n" +
						   "                                This option cannot be combined with -a/--all");
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
			allWiki = false;
			if (args.length <= currArg) {
				usageMsg(false, "\nNo file mentioned \n");
				return false;
			}
			if (!readTitlesFromFile(args[currArg])) {
				usageMsg(false, null);
				return false;
			}
			currArg++;
		}
		else {
			usageMsg(false, "\nUnknown argument: " + args[0] + "\n");
			return false;
		}

		if (args.length <= currArg)
			return true;

		if (!args[currArg].equals("--dbg") && !args[currArg].equals("-d")) {
			usageMsg(false, "\nBad argument \'" + args[currArg] + "\' after \'" + args[0] + "\' expected no argument or --dbg\n");
			return false;
		} else {
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
			titles.add(newTitle);
			System.out.println(newTitle);
		}
		System.out.println("");
		inFile.close();
		allWiki = false;
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
				if ( titles.isEmpty() || titles.contains(page.getTitle()) )
					new Runner(page).run();
			} catch (Exception e) {
				System.out.println("WE SHOULD NOT GET HERE!!!! \n");
				e.printStackTrace();
			}
			return;
		}
	}
}



