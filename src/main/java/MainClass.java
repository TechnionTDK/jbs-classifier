


import java.io.File;

import java.util.ArrayList;

import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by eurocom on 27/06/2017.
 */
public class MainClass {

	static boolean multiThread = false;
	static boolean allWiki = true;
	static Scanner in = new Scanner(System.in);
	static ArrayList<String> urls = new ArrayList<String>();

	public static void main(String[] args) throws Exception {
		String[] topics = {
				"https://he.wikipedia.org/wiki/%D7%93%D7%95%D7%93_%D7%95%D7%91%D7%AA_%D7%A9%D7%91%D7%A2",
				"https://he.wikipedia.org/wiki/%D7%AA%D7%9C%D7%9E%D7%95%D7%93_%D7%91%D7%91%D7%9C%D7%99"
		};

		boolean procWiki = false;
		for (int i=0; i < args.length ; i++) {
			if (args[i].equals("--all") || args[i].equals("-a")) {
				if (procWiki && !allWiki){
					System.out.println("\nCannot scan all wikipedia and specific files at the same time\n");
					usageMsg(false);
					return;
				}
				allWiki = true;
				procWiki = true;
			} else if (args[i].equals("--help") ||  args[i].equals("-h")) {
				usageMsg(true);
				return;
			} else if (args[i].equals("--file") ||  args[i].equals("-f")) {
				if (procWiki && allWiki){
					System.out.println("\nCannot scan all wikipedia and specific files at the same time\n");
					usageMsg(false);
					return;
				}
				if (++i >= args.length) {
					System.out.println("\nNo file mentioned \n");
					usageMsg(false);
					return;
				}
				procWiki = false;
				for(; i< args.length; i++) {
					if (!urlsFromFile(args[i])) {
						if (!procWiki) {
							usageMsg(false);
							return;
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
				for(; i< args.length; i++) {
					int tmpflag = 0;
					try {
						tmpflag |= Dbg.valueOf(args[i]).id;
						Dbg.enabledFlags = tmpflag;
					} catch (Exception e) {
						i--;
						break;
					}
				}
			} else {
				System.out.println("\n Unknown argument: " + args[i] + "\n");
				usageMsg(false);
				return;
			}
		}
		if (procWiki) {
			processWikis();
			return;
		}


		int optNum = 4;
		while (true) {

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
				case 4:
					System.out.println("\nYou chose to manually classify specific topics (4).");
					while (true) {
						System.out.println("\nPlease enter new topic.\nto finish enter 0");
						String newTopic;
						try {
							newTopic = in.nextLine();
							if (newTopic.equals("0"))
								break;
						} catch (Exception e) {
							System.out.println("\nNot a valid string");
							continue;
						}

						JsonTuple jt = new JsonTuple("",newTopic);
						addRefs(jt);
						if (jt.mentions.isEmpty())
							continue;

						try {
							System.out.println("\nAdding topic to outputs:" + newTopic);
							Runner.writeJsonTuple(jt);
						} catch (Exception e) {
							System.out.println("\nWriting to file failed, please try again");
							e.printStackTrace();
						}
					}
					break;
			}
			System.out.println("\nPlease press any key to continue\n");
			in.nextLine();
			urls = new ArrayList<String>();
		}
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

   static void addRefs(JsonTuple jt) {
       String sheetRegx = "([א,ב])[\\']?";
       String validTanachReg = "^" + WikiTanachRefs.tanachBooks + "," + WikiBookRefs.location + "," + WikiBookRefs.location + RefRegex.locationRange(WikiBookRefs.location) + "$";
       String validGmaraReg = "^" + WikiGmaraRefs.gmaraBooks + "," + WikiBookRefs.location + "," + sheetRegx + RefRegex.locationRange(sheetRegx) + "$";

	   while(true) {
		   try {
			   System.out.println("Please add reference in the following formats: \n" +
					   "<book>,<perek>,<pasuk>[-<pasuk>] or <masechet>,<amud>,<daf>[-<daf>] \nto finish enter \"0\" ");
			   String ref = in.nextLine();
			   if (ref.equals("0"))
				   return;
			   Pattern tanachPattern = Pattern.compile(validTanachReg);
			   Matcher tanachMatcher = tanachPattern.matcher(ref);
			   Pattern gmaraPattern = Pattern.compile(validGmaraReg);
			   Matcher gmraMatcher = gmaraPattern.matcher(ref);
			   if (tanachMatcher.find() || gmraMatcher.find()) {
				   ArrayList<String> uris = new UriConverter(ref).getUris();
				   if (!uris.isEmpty()) {
					   jt.setMentions(uris);
					   System.out.println("Reference added");
				   }
			   } else
			   		System.out.println("Reference bad format, please try again");
		   } catch (Exception e) {
			   System.out.println("Not a valid string");
		   }
	   }
   }

   static void processWikis(){
	   if (allWiki == true) urls = new Queries().getAllWikipediaPages();
	   //: new ArrayList<String>(Arrays.asList(topics));
/*
	   while (true) {
		   System.out.print("Do you wish to run in multi-thread (no stats)? [y,n] ");
		   try {
			   char c = (char) System.in.read();
			   if (c=='y' || c=='Y' ) {
				   multiThread = true;
				   break;
			   }else if (c=='n' || c=='N'){
				   multiThread = false;
				   break;
			   }
			   System.out.print(c + "is not a valid option");
		   } catch (Exception e) {
			   System.out.println("Not a char");
		   }
	   }
*/
	   Profiler profiler = new Profiler();
	   System.out.println("\n\nNumber of wikipedia pages: " + urls.size() + "\n\n");
	   for(String url:urls) {
		   try {
			   if (multiThread)
				   new Thread(new Runner(url, profiler)).start();
			   else
				   new Runner(url, profiler).run();
		   } catch (Exception e) {
			   System.out.println("WE SHOULD NOT GET HERE!!!! \n" + url);
			   e.printStackTrace();
		   }
	   }
	   System.out.println("\n\nPage Scan finished. \nOutputs added to files with Timestamp " + Runner.ts + "\n\n");
   }
}

