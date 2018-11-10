
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

public class Tokenizer {
	//data structures used to store document dictionary and token dictionary
	private static HashMap<Integer, String> DocumentDict = new HashMap<>();
	private static HashMap<Integer, String> TokenDict = new HashMap<>();
	static FileWriter output=null;
	
	// array lists data structures  for text data, tokens and stop words
	static ArrayList<String> TextContentList = new ArrayList<String>();
	static ArrayList<String> TokensList = new ArrayList<String>();
	static ArrayList<String> stopWordsList = new ArrayList<>();
	
	//method to get file data and write to text files
	void getFileData() throws IOException {
		//To write document and text values to a text file
		BufferedWriter docText = new BufferedWriter(new FileWriter("./docsText.txt"));
		BufferedWriter textValue = new BufferedWriter(new FileWriter("./TextValue.txt"));
		//PrintWriter writer = new PrintWriter("docDict.txt");
		String curr[];
		String currtext[];
		int doccount = 0;	
		try {
			File file = new File("./src/ft911/");
			System.out.println("Loading the directory to read all files.....");
			System.out.println("Reading each file.....");
			for (File f : file.listFiles()) {
				try {	
					String fileText = FileUtils.readFileToString(f,"UTF-8"); // from commons nio
					curr = StringUtils.substringsBetween(fileText, "<DOCNO>", "</DOCNO>");
					//to loop through multiple <docno> tags in the file
					for(String cur:curr) {
						DocumentDict.put(++doccount/*key*/,cur);
					}
					
					currtext = StringUtils.substringsBetween(fileText, "<TEXT>", "</TEXT>");
					for(String cur:currtext){
						TextContentList.addAll(Arrays.asList(cur.split(" ")));
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			//removing empty values
			/*Iterator<String> i = TextContent.iterator();
			while (i.hasNext())
			{
			    String s = i.next();
			    if (s == null || s.isEmpty())
			    {
			        i.remove();
			    }
			}*/
		    docText.write(DocumentDict.toString());
		    System.out.println("Document info found in docText.txt");
		    textValue.write(TextContentList.toString());
		    System.out.println("Text info found in textValue.txt");
		    docText.close();
		    textValue.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			//System.out.println("For document dictonary printing exception....");
			e.printStackTrace();
		}
		finally {
			System.out.println("File Data extracted......");
		}
	}
	
	void loadStopwords() { //method to read and load list of stop words
		try {
			// reading the file stopwordlist.txt
			System.out.println("Reading stop words from the given file..." );
			Scanner s = new Scanner(new File("./src/stopwordlist.txt"));
			while (s.hasNext()){
			    stopWordsList.add(s.next().trim().toLowerCase());
			}
			System.out.println(stopWordsList);
			s.close();
			
		} 
		catch ( IOException e ) // to catch filenotfound and other io exceptions
		{
			System.out.println(e);
			e.printStackTrace();
		} 
	}
	
	void TokenizeRules() {//method to apply tokenization rules
		for (String text : TextContentList) {
			//String regexRule=text.replaceAll("[0-9]|^[a]$", "");//removes numbers
			String regexRule=text.replaceAll("\\w*\\d+\\w*", "").trim().toLowerCase(); 
			TokensList.addAll(Arrays.asList(regexRule.split("[\\p{Punct}\\s]+")));//splitting on punctuations
		}
			TokensList.removeAll(stopWordsList);
			TokensList.removeAll(Arrays.asList("", null));// to remove empty values 
			//System.out.println(TokensList);
	}
	
	void stemmingWords() {// method to perform stemming using porter algorithm
		Porter porter = new Porter();
		for(int i=0;i<TokensList.size();i++) {
			String curr=porter.stripAffixes(TokensList.get(i));		
			TokensList.set(i,curr);			
		}
	}
	
	void setTokenMap() {
		int tokenCounter = 0;

		// removing duplicates
		Set<String> temp = new HashSet<>(); //using a set so that it will not allow duplicates
		temp.addAll(TokensList);
		TokensList.clear();
		TokensList.addAll(temp);
		Collections.sort(TokensList);
		TokensList.removeAll(Arrays.asList("", null));		
		//Assigning unique value for each token 
		for(String token:TokensList) {
			TokenDict.put(++tokenCounter,token);
		}
	}
	
	void setOutputFile(FileOutputStream fout) throws IOException {
		//Writing document dictionary and token dictionary to output file
				BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(fout, "utf-8")); 
				writer.write("---------------TOKEN DICTIONARY---------------------");
				writer.newLine();
				for(Object token:TokenDict.keySet()) {
					 writer.write(TokenDict.get(token)+" - "+token);
					 writer.newLine();
				}
				writer.write("---------------DOCUMENT DICTIONARY-----------------------");
				writer.newLine();
				for(Object document:DocumentDict.keySet()) {
					 writer.write(DocumentDict.get(document)+" - "+document);
					 writer.newLine();
				}
				writer.close();
	}
	
	public static void main(String[] args) throws IOException {
		FileOutputStream fout=new FileOutputStream("parsed_output.txt");		
		Tokenizer tokenizer = new Tokenizer();
		
		//reading all the file data from the given folder ft911
		try {
			tokenizer.getFileData();
		} catch (FileNotFoundException e) {
			System.out.println(e);
			e.printStackTrace();
		}

		//reading and loading stop list words
		tokenizer.loadStopwords();
		
		//Applying tokenization rules 
		tokenizer.TokenizeRules();
		
		//stemming using porter 
		tokenizer.stemmingWords();
		
		//creating the token list with uniques id 
		tokenizer.setTokenMap();
		
		//Printing the output file
		tokenizer.setOutputFile(fout);	
		
	    System.out.println("Tokenization DONE! Token dictionary and Document dictionary info found in parsed_output.txt");
		//System.out.println(TokenDict);
			
	}

}
