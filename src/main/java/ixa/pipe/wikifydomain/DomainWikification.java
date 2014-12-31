package ixa.pipe.wikifydomain;

import ixa.kaflib.KAFDocument;
import ixa.kaflib.Mark;
import ixa.kaflib.ExternalRef;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;
import java.util.List;


public class DomainWikification {

    private Map<String,String> domainCategories = new HashMap<String,String>();
    private String language;
    private DictManager linkMappingIndex;
    private DictManager categoriesIndex;
    private DictManager sfCountsIndex;


    public DomainWikification(String domainCategoriesFile, String language, String linkMappingIndexFile, String linkMappingHashName, String categoriesIndexFile, String categoriesHashName, String sfCountsIndexFile, String sfCountsHashName) throws FileNotFoundException, IOException {

	this.language = language;

	BufferedReader br = new BufferedReader(new FileReader(domainCategoriesFile));
	String line;
	while ((line = br.readLine()) != null) {
	    domainCategories.put(line,"");
	}
	br.close();

	if(language.equals("es")){
	    linkMappingIndex = new DictManager(linkMappingIndexFile, linkMappingHashName);
	}
	categoriesIndex = new DictManager(categoriesIndexFile, categoriesHashName);
	sfCountsIndex = new DictManager(sfCountsIndexFile, sfCountsHashName);
    }
    
    
    public void process (KAFDocument kaf, Double threshold, String language){
	String sourceMarkableIn = "in-domain-wikiterms";
	String sourceMarkableOut = "out-of-domain-wikiterms";

	Boolean indomain = false;
	List<Mark> markables = kaf.getMarks("DBpedia");
	for(Mark mark : markables){
	    indomain = false;
	    List<ExternalRef> extRefs = mark.getExternalRefs();
	    ExternalRef extRef = extRefs.get(0);
	    String ref = extRef.getReference();
	    String[] completeRef = ref.split("/");
	    String entry = completeRef[completeRef.length-1];
	    String enEntry;
	    if(language.equals("es")){
		enEntry = linkMappingIndex.getValue(entry);
	    }
	    else{
		enEntry = entry;
	    }
	    //check if in-domain
	    if(enEntry != null){
		 String enCategories = categoriesIndex.getValue(enEntry); //enCategories = cat1!!cat2!!..!!catN
		 if(enCategories != null){
		     String[] cats = enCategories.split("!!");
		     for(String cat : cats){
			 if(!indomain){
			     String exists = domainCategories.get(cat);
			     if(exists != null){ //in-domain
				 Mark markable = kaf.newMark(sourceMarkableIn, mark.getSpan());
				 markable.setLemma(mark.getLemma());
				 markable.addExternalRef(extRef);
				 indomain = true;
			     }
			 }
		     }
		 }
	    }
	    if(!indomain){//out-of-domain
		String surfaceForm = mark.getStr();
		String ratio = sfCountsIndex.getValue(surfaceForm);
		if(ratio != null){
		    if(Double.parseDouble(ratio) >= threshold){
			Mark markable = kaf.newMark(sourceMarkableOut, mark.getSpan());
			markable.setLemma(mark.getLemma());
			markable.addExternalRef(extRef);
		    }
		}
		else{
		    if(threshold == 0){
			Mark markable = kaf.newMark(sourceMarkableOut, mark.getSpan());
			markable.setLemma(mark.getLemma());
			markable.addExternalRef(extRef);
		    }
		}
	    }
	}
    }
    
}