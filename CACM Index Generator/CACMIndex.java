import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedList;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class CACMIndex 
{
	static LinkedHashMap<String, ArrayList<String>> index = new LinkedHashMap<String, ArrayList<String>>();
	static LinkedHashMap<String, LinkedHashMap<String, Integer>> invertedIndex = new LinkedHashMap<String, LinkedHashMap<String, Integer>>();
	static LinkedHashMap<String, LinkedHashMap<String, Integer>> invertedIndexSorted = new LinkedHashMap<String, LinkedHashMap<String, Integer>>();
	private static File stopWordListPath = new File("E:\\IR\\Assignment3\\LuceneStopWordsFilter.txt");
	private static File invertedIndexPath = new File("E:\\IR\\Assignment3\\indexTC.out.txt");	
	private static File corpusDirectory = new File("E:\\IR\\Assignment3\\cacm.tar\\cacm");
	static PrintWriter indexWriter = null;
	
	public static void main(String[] args) 
	{
		File[] corpusList = corpusDirectory.listFiles();
		BufferedReader br = null;
		BufferedReader br_stopList = null;
		String[] tempList = null;
		int lineLength=0;
		int tokenLength = 0;
		//ArrayList<String> stopWordList = new ArrayList<String>();
		LinkedList<String> stopWordList = new LinkedList<String>();
		try 
		{   
			br_stopList = new BufferedReader(new FileReader(stopWordListPath));
			String sCurrentLine;
			while ((sCurrentLine = br_stopList.readLine()) != null)
			{
				stopWordList.add(sCurrentLine);
			}
			
			ArrayList<String> postings = null;
			for (File currentFile : corpusList)
		    {
				br = new BufferedReader(new FileReader(currentFile));
				Document html = Jsoup.parse(currentFile, null);
				sCurrentLine = html.body().getElementsByTag("pre").text();
				tempList= sCurrentLine.split("[^\\w.]");
				postings = new ArrayList<String>();
				for(String s: tempList)
				{
					s = s.toLowerCase();
					if(s.endsWith("."))
					{
						tokenLength = s.length() - 1;
						s= s.substring(0, tokenLength);
					}
					if(s.matches("\\w+(\\.?\\w+)*") && !(stopWordList.contains(s)))
					postings.add(s);
				}
				index.put(currentFile.getName().replaceFirst("[.][^.]+$", ""), postings);
				br.close();
	        }
			for(String key : index.keySet())
			{    
			     postings = index.get(key);
			     for(String temp : postings)
			     {
			    	 if(invertedIndex.get(temp)!= null)
			    	 {
			    		 LinkedHashMap<String, Integer> invertedIndexPostings = new LinkedHashMap<String, Integer>();
			    		 invertedIndexPostings = invertedIndex.get(temp);
			    		 invertedIndexPostings.put(key, Collections.frequency(postings, temp));
			    		 invertedIndex.put(temp, invertedIndexPostings);
			    	 }
			    	 else
			    	 {	 
			    		 LinkedHashMap<String, Integer> invertedIndexPostings = new LinkedHashMap<String, Integer>();			    	 
			    		 invertedIndexPostings.put(key, Collections.frequency(postings, temp));
			    		 invertedIndex.put(temp, invertedIndexPostings);
			    	 }
			     }
			}
			ArrayList<String> sortedKeys=new ArrayList<String>(invertedIndex.keySet());
			Collections.sort(sortedKeys);
			for(String s : sortedKeys)
			{
				invertedIndexSorted.put(s, invertedIndex.get(s));
			}			
			indexWriter = new PrintWriter(invertedIndexPath);
			for(String key : invertedIndexSorted.keySet())
			{   
				StringBuffer indexToWrite = new StringBuffer();
				indexToWrite.append(key + " -> ");
				LinkedHashMap<String, Integer> invertedIndexPostings = new LinkedHashMap<String, Integer>();
				invertedIndexPostings = invertedIndexSorted.get(key);
				for(String keyA : invertedIndexPostings.keySet())
				{
					indexToWrite.append("("+keyA + "," +invertedIndexPostings.get(keyA)+"), ");
				}
				lineLength = indexToWrite.length();
				lineLength = lineLength-2;
				indexWriter.write(indexToWrite.substring(0, lineLength)+"\n");
			}
			System.out.println("Index file is successfully created at "+ invertedIndexPath);
		}
		catch (Exception e) 
		{
			e.printStackTrace();
		} 
		finally 
		{
			try 
			{
				if (br != null)br.close();	
				indexWriter.close();
			} 
			catch (Exception ex) 
			{
				ex.printStackTrace();
			}
		}
	}
}