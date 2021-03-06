# jbs-classifier

## What is it?
Our tool is able to get a text, go over it and find mentions of mekorot (from the Tanach or the Talmud).
Then, we convert all the hebrew references into URIs, and produce a list of all the URIs for the specific text.

## What we achieved?
We went over all the Hebrew Wikipedia pages and let our tool go ove reach page.
For every page that contains Tanach or Talmud references we have it's list of references in URIs form.
Now we can mark each URI as related to the topic it was mentioned in.

## Example
![](https://user-images.githubusercontent.com/23153327/32541137-6868d552-c477-11e7-9b0e-0ffc1077a6dd.png)

we will get the following list for the marked reference:<br />
jbr:text-tanach-1-22-1<br />
jbr:text-tanach-1-22-2<br />
jbr:text-tanach-1-22-3<br />
⋅<br />
⋅<br />
⋅<br />
jbr:text-tanach-1-22-19

## Output format:
The ouput is in the following json format:
```
{
	"subjects": [
	{
	  "uri": "https://he.wikipedia.org/wiki/עקידת_יצחק",
	  "jbo:mentions": [
	    {
	      "pasuk": "jbr:tanach-1-22-1",
	      "context": "סיפור עֲקֵדַת יִצְחָק (עקדה = קשירה) מופיע במקרא בספר בראשית (כ"ב, א-יט), ולפיו עקד אברהם על פי מצוותו של האלוהים, את יצחק בנו האהוב על מזבח ועצים במטרה לשוחטו ולשורפו כקורבן, וברגע האחרון בטרם פעולת השחיטה נקרא על ידי מלאך האלוהים לא לעשות לבנו מאומה, ושיבח אותו כי הוא "ירא אלוהים", אברהם נשא את עיניו וראה איל אחוז בקרניו בסבך העצים, אותו הקריב על המזבח תחת בנו. מעשה זה נחשב במסורת היהודית ובמסורות נוספות למופת של מסירות נפש ושל אמונה באלוהים, ולניסיון הקשה ביותר מבין עשרה ניסיונות שנתנסה אברהם על ידי האל, ועמד בו.",
	      "label": "בראשית כב א"
	    },
	    {
	      "pasuk": "jbr:tanach-1-22-2",
	      "context": "סיפור עֲקֵדַת יִצְחָק (עקדה = קשירה) מופיע במקרא בספר בראשית (כ"ב, א-יט), ולפיו עקד אברהם על פי מצוותו של האלוהים, את יצחק בנו האהוב על מזבח ועצים במטרה לשוחטו ולשורפו כקורבן, וברגע האחרון בטרם פעולת השחיטה נקרא על ידי מלאך האלוהים לא לעשות לבנו מאומה, ושיבח אותו כי הוא "ירא אלוהים", אברהם נשא את עיניו וראה איל אחוז בקרניו בסבך העצים, אותו הקריב על המזבח תחת בנו. מעשה זה נחשב במסורת היהודית ובמסורות נוספות למופת של מסירות נפש ושל אמונה באלוהים, ולניסיון הקשה ביותר מבין עשרה ניסיונות שנתנסה אברהם על ידי האל, ועמד בו.",
	      "label": "בראשית כב ב"
	    }
	  ]
	},
	....
	....
	....
    ]
}

```
## Cloning the project
Using git: git clone https://github.com/TechnionTDK/jbs-classifier.git

## Main classes
| class | purpose |
| ------ | ------ |
| MainClass | assembling all the project components  |
| Queries | all the SPARQL queries are executed here |
| RefRegex | generic class in order to be able to create regular expressions |
| Runner | writing the results to the output file |
| UriConverter | converting hebrew reference into a URI |
| WikiPageParser | going over a reference and finding all the sources in it |

## Compiling the project
mvn clean package

## Usage

Since we want to improve our preformance, we are using the Hebrew Wikipeia dump.
In order to run the program, you need to downloaded the dumpfile from [here](https://archive.org/details/hewiki-20160203).
Click on Download options -> GZIP -> show all -> hewiki-20160203-pages-articles.xml.bz2.
Then, extract the file and put it in the root folder of the project - jbs-classifier.

There are 2 running modes: working with all wkikpedia pages or aorking with specific pages given in as input in a file.
In input file mode, you need to supply a file with the names of your desired articles' titles, each one in a seperate line.

Running:
java -cp target/jbs-classifier-1.0-SNAPSHOT-jar-with-dependencies.jar MainClass
 [--all|--file <path to file>|--help] [--dbg <debug flags>]


The results and other statistics are written to results/timestam directory. 

The information is organized in the following files:

results.json - the output 

all_pages - All parsed pages' titles. 

pages_with_refs - The titles of pages with potential references found.

pages_refs - Potential references, in the form of page title + the page references.

pages_with_uri - Pages with references after validation (converting to uri)

pages_uri - URIs found, in the same form as pages refs.

profiler - Run time information of each phase (in ms)

## Expanding the project to recognize more sources

In order to find referneces from mroe books, you need to add compatible parsers.

more information can be found [here](https://github.com/TechnionTDK/jbs-classifier/wiki/adding-parsers-for-new-books)

To see the whole process of adding a new book to the project, you can check out this [example](https://github.com/TechnionTDK/jbs-classifier/wiki/Example-of-adding-a-book-to-the-project)

### Current supported books:

 * Tanach
 * Talmud Bavli
 * Mishneh Torah
 * Shulchan Aruch
