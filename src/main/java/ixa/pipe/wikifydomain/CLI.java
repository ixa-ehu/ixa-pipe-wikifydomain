package ixa.pipe.wikifydomain;

import ixa.kaflib.KAFDocument;
import ixa.kaflib.Mark;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.file.*;
import java.util.List;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import net.sourceforge.argparse4j.ArgumentParsers;
import net.sourceforge.argparse4j.inf.ArgumentParser;
import net.sourceforge.argparse4j.inf.ArgumentParserException;
import net.sourceforge.argparse4j.inf.Namespace;


public class CLI {

    /**
     * Get dynamically the version of ixa-pipe-wikifidomain by looking at the MANIFEST
     * file.
     */
    private final String version = CLI.class.getPackage().getImplementationVersion();
    private final String commit = CLI.class.getPackage().getSpecificationVersion();

    private static final Pattern WHITESPACE_PATTERN = Pattern.compile("\\s+");
    private static final Pattern JARPATH_PATTERN_BEGIN = Pattern.compile("file:");
    private static final Pattern JARPATH_PATTERN_END = Pattern.compile("[^/]+jar!.+");
   
    private String linkMappingIndexFile;
    private String categoriesIndexFile;
    private String sfCountsIndexFileES;
    private String sfCountsIndexFileEN;
    private final String linkMappingHashName = "esEn";
    private final String categoriesHashName = "articleCategories";
    private final String sfCountsHashName = "surfaceformsRatios";

    public CLI(){
    }

    public static void main(String[] args) throws Exception {
	CLI cmdLine = new CLI();
	cmdLine.parseCLI(args);
    }

    public final void parseCLI(final String[] args) throws Exception {
    	
    	Namespace parsedArguments = null;

        // create Argument Parser
        ArgumentParser parser = ArgumentParsers.newArgumentParser(
            "ixa-pipe-wikifydomain-1.0.0.jar").description(
            "ixa-pipe-wikifydomain-1.0.0 is a module to adapt the Wikification output to a domain.\n");

        // specify domain categories
        parser
            .addArgument("-c", "--categories")
            .required(true)
            .help(
		  "It is REQUIRED to specify the file with domain categories from YAGO2.");

	parser
	    .addArgument("-t", "--threshold")
	    .setDefault("1.0")
	    .required(false)
	    .help("Specify the threshold to filter the out-of-domain output of the wikification (default is 1.0).");

        /*
         * Parse the command line arguments
         */

        // catch errors and print help
        try {
	    parsedArguments = parser.parseArgs(args);
        } catch (ArgumentParserException e) {
	    parser.handleError(e);
	    System.out
		.println("Run java -jar target/ixa-pipe-wikifydomain.jar -help for details");
	    System.exit(1);
        }

        // Load domain categories file
        String categoriesFile = parsedArguments.getString("categories");
	// Get threshold
	Double thresholdValue = Double.parseDouble(parsedArguments.getString("threshold"));
	
	String jarpath = this.getClass().getResource("").getPath();
	Matcher matcher = JARPATH_PATTERN_BEGIN.matcher(jarpath);
	jarpath = matcher.replaceAll("");
	matcher = JARPATH_PATTERN_END.matcher(jarpath);
	jarpath = matcher.replaceAll("");
	linkMappingIndexFile = jarpath + "/resources/wikipedia-db";
	categoriesIndexFile = jarpath + "/resources/yago2-categories-db/yago2-categories-db";
	sfCountsIndexFileES = jarpath + "/resources/dbpedia-sfCounts-db/dbpedia-sfCounts-db-es";
	sfCountsIndexFileEN = jarpath + "/resources/dbpedia-sfCounts-db/dbpedia-sfCounts-db-en";

	if (!Files.isRegularFile(Paths.get(linkMappingIndexFile))) {
	    System.err.println("wikipedia-db file not found. wikipedia-db* files must exist under 'resources/' folder.");
	    throw new Exception();
	}
	if (!Files.isRegularFile(Paths.get(categoriesIndexFile))) {
	    System.err.println("yago2-categories-db file not found. yago2-categories-db* files must exist under 'resources/yago2-categories-db/' folder.");
	    throw new Exception();
	}
	if (!Files.isRegularFile(Paths.get(sfCountsIndexFileES))) {
	    System.err.println("dbpedia-sfCounts-db-es file not found. dbpedia-sfCounts-db-es* files must exist under 'resources/dbpedia-sfCounts-db/' folder.");
	    throw new Exception();
	}
	if (!Files.isRegularFile(Paths.get(sfCountsIndexFileEN))) {
	    System.err.println("dbpedia-sfCounts-db-en file not found. dbpedia-sfCounts-db-en* files must exist under 'resources/dbpedia-sfCounts-db/' folder.");
	    throw new Exception();
	}

	// Input
	BufferedReader stdInReader = null;
	// Output
	BufferedWriter w = null;

	stdInReader = new BufferedReader(new InputStreamReader(System.in,"UTF-8"));
	w = new BufferedWriter(new OutputStreamWriter(System.out,"UTF-8"));
	KAFDocument kaf = KAFDocument.createFromStream(stdInReader);
	
	
	KAFDocument.LinguisticProcessor lp = kaf.addLinguisticProcessor("markables", "ixa-pipe-wikifydomain", version + "-" + commit);
	lp.setBeginTimestamp();

	String lang = kaf.getLang();

	try{
	    List<Mark> markables = kaf.getMarks("DBpedia");
	    if (!markables.isEmpty()){
		DomainWikification domain = null;
		if(lang.equals("en")){
		    domain = new DomainWikification(categoriesFile, lang, linkMappingIndexFile, linkMappingHashName, categoriesIndexFile, categoriesHashName, sfCountsIndexFileEN, sfCountsHashName);
		}
		else if(lang.equals("es")){
		    domain = new DomainWikification(categoriesFile, lang, linkMappingIndexFile, linkMappingHashName, categoriesIndexFile, categoriesHashName, sfCountsIndexFileES, sfCountsHashName);
		}
		domain.process(kaf,thresholdValue,lang);
	    }
	    else{
		System.err.println("No DBpedia markables found for the document.");
	    }
	}
	catch (Exception e){
	    System.err.println("ixa-pipe-wikifydomain failed: ");
	    e.printStackTrace();
	}
	finally {
	    lp.setEndTimestamp();
	    w.write(kaf.toString());
	    w.close();
	}
    } 

}
