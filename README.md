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

we will get the following list for the marked source:<br />
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
	"subjects": [{
		"uri": "https://he.wikipedia.org/wiki/עקידת_יצחק",
		"jbo:mentions" : ["jbr:text-tanach-1-12-1", "jbr:text-tanach-1-12-2", ..., ]
	},
	{
		"uri": "https://he.wikipedia.org/wiki/מגדל_בבל",
		"jbo:mentions" : ["jbr:text-tanach-1-11-1", "jbr:text-tanach-1-11-2", ..., ]
	}]
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
| UriConverter | converting hebrew source into a URI |
| WikiPageParser | going over a source and finding all the sources in it |

## Compiling the project
mvn clean package

## Usage

java -cp target/jbs-classifier-1.0-SNAPSHOT-jar-with-dependencies.jar MainClass
 [--all|--file <path to file>|--help] [--dbg <debug flags>]


Without any option (or only dbg flag) interactive mode will be used 
and you will be prompt for the different options.

Scan output is written to outputs/timestamp.json

Further information is available under 'stat_timestamp'
in the following files:

all_pages - All parsed pages

pages_with_refs - Pages with potential references

pages_refs - Potential references by page

pages_with_uri - Pages with references

pages_uri - URIs by page

error_pages - Un-parsable pages

profiler - Run time information of each phase

Full usage is available by running the program with --help flag.
