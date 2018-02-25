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
	static String[] topics = {
			"ספר שמואל",
			"הפטרה",
			"יבוסים"
	};
	public static void main(String[] args) throws Exception {
		if (!parseArguments(args))
			return;
		if (allWiki || titles.size() > 0) {
			processWikis();
			return;
		}
		getInputAndProcess();
	}


	static boolean parseArguments(String[] args) throws Exception {
		boolean procWiki = false;
		for (int i=0; i < args.length ; i++) {
			if (args[i].equals("--all") || args[i].equals("-a")) {
				if (procWiki && !allWiki){
					System.out.println("\nCannot scan all wikipedia and specific files at the same time\n");
					return false;
				}
				allWiki = true;
				procWiki = true;
			} else if (args[i].equals("--help") ||  args[i].equals("-h")) {
				return false;
			} else if (args[i].equals("--file") ||  args[i].equals("-f")) {
				if (procWiki && allWiki){
					System.out.println("\nCannot scan all wikipedia and specific files at the same time\n");
					return false;
				}
				if (++i >= args.length) {
					System.out.println("\nNo file mentioned \n");
					return false;
				}
				procWiki = false;
				for(; i< args.length; i++) {
					if (!readTitlesFromFile(args[i])) {
						if (!procWiki) {
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
				return false;
			}
		}
		return true;
	}

	static void getInputAndProcess(){
		int optNum = 4;
		while (true){
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
				case 3:
					System.out.println("\nYou chose to scanning Wikipedia pages from file (3).");
					while (true) {
						System.out.println("\nPlease provide input file");
						try {
							String fileName = in.nextLine();
							if (!readTitlesFromFile(fileName))
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
			titles = new ArrayList<String>();
		}
	}

	static boolean readTitlesFromFile(String fileName) throws Exception{
		File file = new File(fileName);
		if (!file.exists()) {
			System.out.println("\nFile " + fileName + " does not exist");
			return false;
		}
		Scanner inFile = new Scanner(file);
		while (inFile.hasNext()) {
			String newTitle = inFile.nextLine();
			titles.add(newTitle);
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



