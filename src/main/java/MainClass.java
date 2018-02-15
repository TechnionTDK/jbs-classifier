import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import info.bliki.wiki.dump.*;
import info.bliki.wiki.model.WikiModel;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by eurocom on 27/06/2017.
 */
public class MainClass {

	static boolean allWiki = false;
	static Scanner in = new Scanner(System.in);
	static ArrayList<String> urls = new ArrayList<String>();
	static String[] topics = {
			"ספר שמואל",
			"הפטרה",
			"יבוסים"
	};
	public static void main(String[] args) throws Exception {
		if (!parseArguments(args))
			return;
		if (allWiki || urls.size() > 0) {
			processWikis();
			return;
		}
		getInputAndProcess();
	}

	static void WelcomeMsg() {
		System.out.print("\033[H\033[2J");
		System.out.flush();
		System.out.println("***********************************************");
		System.out.println("**         Welcome to jbs-classifier         **");
		System.out.println("***********************************************");
		System.out.println("\nPlease choose one of the following actions:\n");
		System.out.println("1. Scan Wikipedia");
		System.out.println("2. Scan specific Wikipedia pages");
		System.out.println("3. Scan specific Wikipedia pages from file");
		System.out.println("4. Manually add classifications");
		System.out.println("\n0. To exit");
	}

	static void usageMsg(boolean fullMsg) {

		System.out.println("\nOptions: [--all|--file <path to file>|--help] [--dbg <debug flags>]\n");
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

	static boolean parseArguments(String[] args) throws Exception {
		boolean procWiki = false;
		for (int i=0; i < args.length ; i++) {
			if (args[i].equals("--all") || args[i].equals("-a")) {
				if (procWiki && !allWiki){
					System.out.println("\nCannot scan all wikipedia and specific files at the same time\n");
					usageMsg(false);
					return false;
				}
				allWiki = true;
				procWiki = true;
			} else if (args[i].equals("--help") ||  args[i].equals("-h")) {
				usageMsg(true);
				return false;
			} else if (args[i].equals("--file") ||  args[i].equals("-f")) {
				if (procWiki && allWiki){
					System.out.println("\nCannot scan all wikipedia and specific files at the same time\n");
					usageMsg(false);
					return false;
				}
				if (++i >= args.length) {
					System.out.println("\nNo file mentioned \n");
					usageMsg(false);
					return false;
				}
				procWiki = false;
				for(; i< args.length; i++) {
					if (!urlsFromFile(args[i])) {
						if (!procWiki) {
							usageMsg(false);
							return false;
						}
						i--;
						break;
					}
					procWiki = true;
				}
			} else if (args[i].equals("--dbg") || args[i].equals("-d")) {
				if (++i >= args.length) {
					System.out.println("\nNo debug flags, continue with defaults \n");
					break;
				}
				int tmpflag = 0;
				for(; i< args.length; i++) {
					try {
						tmpflag |= Dbg.valueOf(args[i]).id;
						Dbg.enabledFlags = tmpflag;
					} catch (Exception e) {
						System.out.println("failed to set" + args[i]);
						i--;
						break;
					}
				}
			} else {
				System.out.println("\n Unknown argument: " + args[i] + "\n");
				usageMsg(false);
				return false;
			}
		}
		return true;
	}

	static void getInputAndProcess(){
		int optNum = 4;
		while (true){
			WelcomeMsg();
			int i;
			while (true) {
				System.out.print("\nPlease enter your choice [0-" + optNum + "]: ");
				try {
					i = Integer.parseInt(in.nextLine());
					if (i >= 0 && i <= optNum)
						break;
					else
						System.out.print(i + "is not a valid option");
				} catch (NumberFormatException e) {
					System.out.println("Not a number");
				}
			}
			switch (i) {
				case 0:
					System.out.println("\nYou chose to exit (0) good bye\n");
					return;
				case 1:
					System.out.println("\nYou chose to scan Wikipedia (1), scanning will start shortly\n\n\n");
					allWiki = true;
					processWikis();
					break;
				case 2:
					System.out.println("\nYou chose to scan specific Wikipedia pages (2). " +
							"Please enter URL. to finish enter 0:");
					allWiki = false;
					while (true) {
						try {
							String newURL = in.nextLine();
							if (newURL.equals("0")) {
								processWikis();
								break;
							}
							urls.add(newURL);
							System.out.println("\nURL was added. Please enter next URL or 0 to finish:");
						} catch (Exception e) {
							System.out.println("\nNot a valid string");
						}
					}
					break;
				case 3:
					System.out.println("\nYou chose to scanning Wikipedia pages from file (3).");
					while (true) {
						System.out.println("\nPlease provide input file");
						try {
							String fileName = in.nextLine();
							if (!urlsFromFile(fileName))
								continue;
							processWikis();
							break;
						} catch (Exception e) {
							System.out.println("\nNot a char");
						}
					}
					break;
			}
			System.out.println("\nPlease press any key to continue\n");
			in.nextLine();
			urls = new ArrayList<String>();
		}
	}

	static boolean urlsFromFile(String fileName) throws Exception{
		File file = new File(fileName);
		if (!file.exists()) {
			System.out.println("\nFile " + fileName + " does not exist");
			return false;
		}
		Scanner inFile = new Scanner(file);
		while (inFile.hasNext()) {
			String newURL = inFile.nextLine();
			urls.add(newURL);
		}
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
				if (topics==null || topics.length == 0 || Arrays.asList(topics).contains(page.getTitle()) )
					new Runner(page).run();
			} catch (Exception e) {
				System.out.println("WE SHOULD NOT GET HERE!!!! \n");
				e.printStackTrace();
			}
			return;
		}
	}
}



