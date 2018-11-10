

	package com.java2novice.files;
	import java.io.BufferedReader;
	import java.io.BufferedWriter;
	import java.io.File;
	import java.io.FileNotFoundException;
	import java.io.FileReader;
	import java.io.FileWriter;
	import java.io.IOException;
	import java.sql.Timestamp;
	import java.util.ArrayList;
	import java.util.HashMap;
	import java.util.List;
	import java.util.Map;
	import java.util.Scanner;
	public class exmple {
		private Map<String,Integer> Docdict = new HashMap<>();
		private Map<Integer,Map<Integer , Integer>> invertedict = new HashMap<>();
		private Map<Integer,Map<Integer , Integer>> forwardindexdict = new HashMap<>();
		private Map<String,Integer> Tokendict = new HashMap<>();
		private  List<String> stopword = new ArrayList<>();
		Porter porter ;
		List<String> Tokenlist;
		List<String> Text ;
		Map<Integer,Integer> forwardToken ;
		int tokennum = 0;
		int docname = 0;
		int token;
		//int tokenCounter;
		BufferedReader br;
		//load stopwords
		private   List<String> stopwords(){
			try {
				 br = new BufferedReader(new FileReader("./src/stopwordlist.txt"));
				String l = null;
				while((l = br.readLine()) != null){
		 			l= l.trim();
		 			if(l.length() > 0){
		 				l = l.toLowerCase();
		 				stopword.add(l);	
		 			}
		 		}
				br.close();
			} 
			catch (FileNotFoundException e1)
			{
				e1.printStackTrace();
			} 
			catch (IOException e)
			{
				e.printStackTrace();
			}
			return stopword;
			}
		//load the files from the path given
		public void loadData(String path) {
			Docdict = new HashMap<>();
			invertedict = new HashMap<>();
			Tokendict = new HashMap<>();
			//System.out.println("entered the file");
			File file = new File(path);
			File[] files = file.listFiles();
			for (File f : files) {
				processFileContent(f);
			}
		}

		// process the text as required
		private void processFileContent(File fileToRead) { 
			System.out.println("Started " + fileToRead +"at :"+ new Timestamp(System.currentTimeMillis()));
			//System.out.println("formating the file"+fileToRead );
			String line = "";
			String docid = null;
			porter = new Porter();
			try {
				BufferedReader bufferedReader = new BufferedReader(new FileReader(fileToRead));
				while ((line = bufferedReader.readLine()) != null) {
					Tokenlist = new ArrayList<String>();
					//Text = new ArrayList<String>();
					forwardToken = new HashMap<>();
					String Text = null;
					
					//read the document id
					if (line.contains("<DOCNO>")) {
						docid = line.substring(line.indexOf("<DOCNO>") + "<DOCNO>".length(), line.indexOf("</DOCNO>"));
						//System.out.println("docid" + docid);
			}
					//read the test data
					else if (line.contains("<TEXT>")) {
						while (!(Text = bufferedReader.readLine()).contains("</TEXT>")) {
							for (String token : Text.toLowerCase().replaceAll("\\w*\\d\\w*", "").trim()
									.split("\\s*[^a-z]\\s*")) {
								if (!token.isEmpty() && !stopword.contains(token)) {
									Tokenlist.add(porter.stripAffixes(token));
								}
									
								}
							}
						//add the tokens with unique count to the Tokendict hashmap 
						for (String sortwrds : Tokenlist) {
							if(!sortwrds.isEmpty() && !Tokendict.containsKey(sortwrds)){
								tokennum = tokennum+1;
								Tokendict.put(sortwrds,tokennum);
							
							}
							
						}
						
						//add document id to the dictionary
						Docdict.put(docid, ++docname);
						int tokenCounter;
						// add the word ids with its frequency to the forwardtoken hashmap
						for (String sortwrds : Tokenlist) {
							if (!sortwrds.isEmpty()) {
								//token = Tokendict.get(sortwrds);
								if(forwardToken.containsKey(Tokendict.get(sortwrds))){
									tokenCounter =forwardToken.get(Tokendict.get(sortwrds));
								}
								else tokenCounter = 0;
								forwardToken.put(Tokendict.get(sortwrds),++tokenCounter);
							}
						}
						//add the document id and its corresponding forwardtoken hashmap to forwardindexdict
						forwardindexdict.put(Docdict.get(docid),forwardToken);
						int tokencount; 
						//add tokens to the invertedict with its documentid and frequency
						for (String str :Tokenlist)
						{
							tokencount = 0;
							Map<Integer,Integer> invertedTokendic1 = new HashMap<>();
							if(!str.isEmpty()){
								//check if the word already exists in the dictionary
							if (invertedict.containsKey(Tokendict.get(str)))
							{
								
								invertedTokendic1.putAll(invertedict.get(Tokendict.get(str)));
								//check if the word exists in the corresponding document
								if (invertedTokendic1.containsKey(Docdict.get(docid)))
								{
									tokencount = invertedTokendic1.get(Docdict.get(docid)) ;
								}
								invertedTokendic1.put(Docdict.get(docid), ++tokencount);
								invertedict.put(Tokendict.get(str), invertedTokendic1);
							}
							else{
								invertedTokendic1.put(Docdict.get(docid), ++tokencount);
								invertedict.put(Tokendict.get(str), invertedTokendic1);
							}
						}
						
						}
						
					}
				}
				bufferedReader.close();
				
			}
			catch (Exception e) {
				e.printStackTrace();
			}
			
		}
		//write the values to a hashmap
		private void writeContent(Map<String, Integer> mapToPrint, BufferedWriter writer) {

			try {
				for (Map.Entry<String, Integer> entry : mapToPrint.entrySet()) {
					writer.write(entry.getValue() + " " + entry.getKey());
					writer.newLine();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		// get the data from nested hashmap
		private void writenestedhashmap(Map<Integer, Map<Integer,Integer>> mapToPrint, BufferedWriter writer) {

			try {
				for (Map.Entry<Integer, Map<Integer,Integer>> entry : mapToPrint.entrySet()) {
					writer.write(entry.getKey() + " " + entry.getValue());
					writer.newLine();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		// write the values to corresponding files 
		private void writefiles(invertedindex invertedindex){
			BufferedWriter writer = null;
			try {
				writer = new BufferedWriter(new FileWriter("./porter_output.txt"));
				//System.out.println("writing to the file " +" parser_output");

				invertedindex.writeContent(invertedindex.Tokendict, writer);
				invertedindex.writeContent(invertedindex.Docdict, writer);

				writer.flush();
				writer.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
			private void nestedhashmap(invertedindex invertedindex){
				BufferedWriter writer = null;
				BufferedWriter wr = null;
				try {
					writer = new BufferedWriter(new FileWriter("./forwardindex.txt"));
					wr = new BufferedWriter(new FileWriter("./invertedindex.txt"));
					writenestedhashmap(invertedindex.forwardindexdict, writer);
					System.out.println("writen to the file " +" forwardindex");

					writenestedhashmap(invertedindex.invertedict, wr);
					System.out.println("writen to the file " +" invertedindex");

					writer.flush();
					writer.close();
					wr.flush();
					wr.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			
			
		public static void main(String a[]) {
			System.out.println("Started at " + new Timestamp(System.currentTimeMillis()));
			invertedindex invertedindex = new invertedindex();
			@SuppressWarnings("resource")
			Scanner scanner = new Scanner(System.in);
			if (invertedindex.stopword.isEmpty()) {
				invertedindex.stopword = invertedindex.stopwords();
			}
			invertedindex.loadData("./src/ft911");
			invertedindex.writefiles(invertedindex);
			invertedindex.nestedhashmap(invertedindex);
			System.out.println("Completed at " + new Timestamp(System.currentTimeMillis()));
			// enter the path name
			System.out.println("enter the file path name");
			String p = scanner.next();
			invertedindex.loadData(p);
			
			//enter the word to be indexed
			System.out.println("enter the word");
			String word = scanner.next();
			if(word.matches("\\w*\\d+\\w*|^[a]$"))
			{
				System.out.println("word cannot be alpha numeric");
			}
			else{
			word = word.trim().toLowerCase();
			for(String str : invertedindex.stopword)
			{
				if(str.contains("word"))
				{
					System.out.println("it is a stopword");
					
				}
				
			}
			Porter port;
			port = new Porter();
			word = port.stripAffixes(word);
			Map<Integer,Integer> temp = new HashMap<>();
	if(invertedindex.Tokendict.containsKey(word)){
				
				//System.out.println(invertedindex.invertedict.get(invertedindex.Tokendict.get(word)));
				temp = invertedindex.invertedict.get(invertedindex.Tokendict.get(word));
				List<Integer> list = new ArrayList<Integer>(temp.keySet());
				for (Integer l :list){
					for (Map.Entry<String, Integer> entry : invertedindex.Docdict.entrySet()) {
			            if (entry.getValue().equals(l)) {
			                System.out.println(entry.getKey()+"="+invertedindex.invertedict.get(invertedindex.Tokendict.get(word)).get(l));
			                //System.out.println(invertedindex.invertedict.get(invertedindex.Tokendict.get(word)).get(l));
			            }
			        }
				}
			}
			else{
				System.out.println("the word does not exists in the dictionary");
			}
			}
		}
			
			
	}
			

			
}
