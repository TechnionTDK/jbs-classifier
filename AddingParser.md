# Adding Parser
.
###  Reference general structure: 
References are supported in one of two types:

Type  | Format
------------- | -------------
1 | *<book*> [non allowed words] [prefix 1] *<location1*> [prefix 2] *<location 2*>
2 | *<book*> [sub book prefix] *<sub book*> [non allowed words] [prefix 1] *<location1*> [prefix 2] *<location 2*>  

- If not defined otherwise location is 1-3 alphabet characters. 
- Ranges are added automatically. 

-----
.
###  Creating new parser class:
- Since parser data is same for all instances, keep all variable static. 
- A parser class consist of the following:
- Extending RefExtractor\RefExtractorSubBook, according to type 1\2 references.
- Initializing static final variable of type ParserData.
  ParserData constructor expect the following arguments in the following order:
   
		String parser name - name to identify the parser (mainly for debugs).  
		List<String> books - List of all possible books.
		List<List<String>> sub books - Only in case of type 2 references. 
					       Holds list of sub books for each book in the books list. 
		ImmutableMap.<String, Object> optional parameters - Map (key,value) for all optional parameters:
							"subBookPref", List<String> - Sub-book prefix in-case of type 2 references, default - empty.
							"banned", List<String> - non allowed words, default - empty.
							"pref1", List<String> - location1 prefix, default - empty.
							"pref2", List<String> - location2 prefix, default - empty.
							"loc1", String - Regular expression describing location1, default - 1-3 alphabet characters.
							"loc2", String - Regular expression describing location2, default - 1-3 alphabet characters.
							"uriTag", String - Prefix for the URI query, default - empty.
	* NOTE: The matched results are normalized to a minimal format of *<book*>,*<sub book*>,location1,location2[-location2].
If "loc1" or "loc2" catches extra data, this can and should be remove by overriding parent method
`List<String> formatReference(String reference)`
The function should first call the parent class method using 'super'than manipulate the result.
 - protected ParserData getParserData() - getter for the ParserData variable.
----
.
### Registering the new parser:
In the WikiPageParser constructor add the following:
`parsers.add(new <parser class name>());`
