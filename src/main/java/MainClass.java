import com.google.gson.Gson;
import com.google.gson.GsonBuilder;


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

	static boolean multiThread = false;
	static boolean allWiki = true;
	static Scanner in = new Scanner(System.in);
	static ArrayList<String> urls = new ArrayList<String>();


	public static void main(String[] args) {
		String[] topics = {
				"https://he.wikipedia.org/wiki/%D7%93%D7%95%D7%93_%D7%95%D7%91%D7%AA_%D7%A9%D7%91%D7%A2",
				"https://he.wikipedia.org/wiki/%D7%AA%D7%9C%D7%9E%D7%95%D7%93_%D7%91%D7%91%D7%9C%D7%99"
		};

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
							//System.out.println(newURL);
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
					allWiki = false;
					while (true) {
						System.out.println("\nPlease provide input file");
						try {
							String fileName = in.nextLine();
							File file = new File(fileName);
							if (!file.exists()) {
								System.out.println("\nFile" + fileName + "does not exist");
								continue;
							}
							Scanner inFile = new Scanner(file);
							while (inFile.hasNext()) {
								String newURL = inFile.nextLine();
								urls.add(newURL);
							}
							inFile.close();
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
			   System.out.println("WE SHOULD NOT GET HERE !!!! \n" + url);
			   e.printStackTrace();
		   }
	   }
	   System.out.println("\n\nPage Scan finished.\n\n");
   }
}

