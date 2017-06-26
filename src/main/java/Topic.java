import org.apache.jena.atlas.lib.ListUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.URLDecoder;
import java.util.LinkedList;
import java.util.List;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by eurocom on 12/05/2017.
 */
public class Topic {
    String url;
    List<String> categoriesList = new LinkedList<String>();
    String mainBook;
    String topicName;
    Document jsoupDoc;
    WikiTanachRefs tanachRefs;
    WikiGmaraRefs gmaraRefs;
    static String allBooks = RefRegex.booksInit(WikiTanachRefs.tanachBooksList) + "|" + RefRegex.booksInit(WikiGmaraRefs.gmaraBooksList);


    static String bookPath = "div#catlinks.catlinks > div#mw-normal-catlinks.mw-normal-catlinks > ul > li ";
    static String titlePath = "h1#firstHeading.firstHeading";



    Topic(String topicURL) throws IOException {
        url=topicURL;
        org.jsoup.Connection conn = Jsoup.connect(URLDecoder.decode(url));
        jsoupDoc = conn.get();
    }



    /**************************************  topic + main book  ********************************************/

    void getTopicName(){
        Elements topicNameElements = jsoupDoc.select(titlePath);

        if (topicNameElements.size() < 0){
            //do somthing!!
        }
        topicName = topicNameElements.first().text();
        System.out.println(topicNameElements.first().text());
    }

    void findMainBook(){
        String costumeBookRegex = allBooks.replaceAll(" [א-ב]" + "\\)" , "( [א-ב])?" + "\\)");
        String costumeBookRegexStrict = "(" + "סיפורי ספר " + costumeBookRegex + ")";

        Elements bookElements = jsoupDoc.select(bookPath + "[title~=" + "קטגוריה:" + costumeBookRegexStrict + "]");
        if (bookElements.size() <= 0)
            bookElements = jsoupDoc.select(bookPath + "[title~=" + "קטגוריה:" + costumeBookRegex + "]");
        if (bookElements.size() <= 0){
            mainBook = null;
            return;
        }

        //System.out.println(bookElements.first().text());
        Matcher m = StringUtils.findRegInString(bookElements.first().text(),costumeBookRegex);
        if (m.find())
            mainBook = m.group(0);
        System.out.println(mainBook);
    }

    void findCategories(){

        for (Element categoryElement : jsoupDoc.select(bookPath + "[title~=" + "קטגוריה:" + "]")){
            System.out.println(categoryElement.text());
            categoriesList.add(categoryElement.text());
        }
    }

    void getTopicAll(){
        tanachRefs = new WikiTanachRefs(mainBook);
        gmaraRefs = new WikiGmaraRefs(mainBook);
        tanachRefs.getAllBookRefs(jsoupDoc);
        gmaraRefs.getAllBookRefs(jsoupDoc);
    }

    void expandSource(){}

    public static void main(String[] args) {
        String npagesStr;
        int n_pages;
        String[] topicss = {"https://he.wikipedia.org/wiki/%D7%99%D7%A6%D7%99%D7%90%D7%AA_%D7%9E%D7%A6%D7%A8%D7%99%D7%9D",
                "https://he.wikipedia.org/wiki/%D7%A0%D7%97%D7%A9_%D7%94%D7%A7%D7%93%D7%9E%D7%95%D7%A0%D7%99",
                "https://he.wikipedia.org/wiki/%D7%91%D7%9F_%D7%90%D7%99%D7%A9%D7%94_%D7%99%D7%A9%D7%A8%D7%90%D7%9C%D7%99%D7%AA",
                "https://he.wikipedia.org/wiki/%D7%A0%D7%93%D7%91_%D7%95%D7%90%D7%91%D7%99%D7%94%D7%95%D7%90",
                "https://he.wikipedia.org/wiki/%D7%94%D7%A1%D7%A0%D7%94_%D7%94%D7%91%D7%95%D7%A2%D7%A8",
                "https://he.wikipedia.org/wiki/%D7%97%D7%9C%D7%95%D7%9D_%D7%99%D7%A2%D7%A7%D7%91",
                "https://he.wikipedia.org/wiki/%D7%99%D7%A6%D7%99%D7%90%D7%AA_%D7%9E%D7%A6%D7%A8%D7%99%D7%9D",
                "https://he.wikipedia.org/wiki/%D7%93%D7%95%D7%93_%D7%95%D7%99%D7%94%D7%95%D7%A0%D7%AA%D7%9F"};

        String[] topics = {"https://he.wikipedia.org/wiki/%D7%93%D7%95%D7%93_%D7%95%D7%91%D7%AA_%D7%A9%D7%91%D7%A2",
                            "https://he.wikipedia.org/wiki/%D7%AA%D7%9C%D7%9E%D7%95%D7%93_%D7%91%D7%91%D7%9C%D7%99",
                            "https://he.wikipedia.org/wiki/%D7%9E%D7%A2%D7%A9%D7%94_%D7%91%D7%93%27_%D7%9E%D7%90%D7%95%D7%AA_%D7%99%D7%9C%D7%93%D7%99%D7%9D_%D7%95%D7%99%D7%9C%D7%93%D7%95%D7%AA_%D7%A9%D7%A0%D7%A9%D7%91%D7%95_%D7%9C%D7%A7%D7%9C%D7%95%D7%9F"
        };


        //npagesStr = JOptionPane.showInputDialog("enter num of classifiers");
        //n_pages = Integer.parseInt((npagesStr));
        //for (int i = 0; i< topics.length; i++) {
        for (String url : topics) {
            //url = JOptionPane.showInputDialog("please enter classifiers source" + i);
            try {
                Topic newTopic = new Topic(url);
                newTopic.getTopicName();
                newTopic.findMainBook();
                newTopic.findCategories();
                newTopic.getTopicAll();
            } catch (IOException e) {
                e.printStackTrace();
            }
/*
            new Thread(new Runnable() {
                public void run() {
                    try {
                        Topic newTopic = new Topic(url);
                        newTopic.getTopicName();
                        newTopic.findMainBook();
                        newTopic.findCategories();
                        newTopic.getTopicAll();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
*/
        }
    }
}



