# Regexi
A regular expression library for Java with Named Capture support

### Why?
Regular expressions are very powerful but don't scale well.

### What does that mean?
The power of the regex language trips up developers very quickly.  For instance everyone can search for a number using regex and usually they'll be wrong.
The simple and obvious pattern, `/[0-9]\*/`, will match incorrectly `ABC0345` but miss `0.89`.  

A better solution is `/(([-+]?\\b([1-9]+\\d\*|0+(?!\\d)))((\\.){1}(\\d+))?){1}/`
but 99% developers will recognise that as complete gibberish. What is it doing and why? No one knows, 
I certainly didn't when I realised I needed to extend an already large expression.

Regexi solves this by implementing all the parts of the regular expression language as part of a chaining class. So instead we would write:

      Regex.startingAnywhere()
      .anyCharacterIn("-+").onceOrNotAtAll()
      .wordBoundary()
      .beginOrGroup()
      .anyCharacterBetween('1', '9').atLeastOnce()
      .digit().zeroOrMore()
      .or().literal('0').oneOrMore().notFollowedBy(Regex.startingAnywhere().digit())
      .endOrGroup()
      .once()
      .toRegex();
      
Or even better

      Regex.startingAnywhere().number().once().toRegex();
      
### Seems nice but I've spotted 5 errors in your regex already.
That's cool, please raise an issue :)

Nothing is ever perfect, so I've kept access to all the parts of the regex language available. 
You can use PartialRegex to create your own standard patterns and combine them as required.

### Anything else I should know about?

Regexi also provides access to the more complicated features of Java's pattern matching easily.  In particular 

- Named captures are hardly mentioned even in Java's documentation but are easily used and retreived with regexi:

      var regex = Regex.startingFromTheBeginning()
            .beginNamedCapture("interval").literalCaseInsensitive("interval").once().endNamedCapture()
            .space().once()
            .beginNamedCapture("value").numberIncludingScientificNotation().once().endNamedCapture()
            .space().once()
            .beginNamedCapture("unit").word().endNamedCapture()
            .endOfInput()
            .toRegex();
      var captured = regex.getAllMatches(input).get(0).getAllNamedCaptures();
      String value = captured.get("value");
      
- RegexValueFinder streamlines named captures even further to return one value quickly

      RegexValueFinder regex
		         = Regex.startingFromTheBeginning()
             .beginNamedCapture("interval").literalCaseInsensitive("interval").once().endNamedCapture()
             .space().once()
             .beginNamedCapture("value").numberIncludingScientificNotation().once().endNamedCapture()
             .space().once()
             .beginNamedCapture("unit").word().endNamedCapture()
             .endOfInput()
             .returnValueFor("value");
            
      String value = regex.getValueFor(input).orElse("");
    
- RegexSplitter chops your input into chunks 

      List<String> words = Regex.empty().literal(" ").toSplitter().splitToList(input);
      
- RegexReplacement turns your regular expression into a find/replace using regex: 

      String escapeBackslashes = Regex.empty().backslash().replaceWith().backslash().backslash().replaceAll(s);

- There are methods for tricky characters like backslash to help you avoid using a regex instruction when you meant a letter
- Similarly the `literal(string)` method automatically escapes regex instructions to avoid problems
