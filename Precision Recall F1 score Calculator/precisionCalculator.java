import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.LinkedList;

public class precisionCalculator 
{
	private static File relevanceIndexFilePath = new File("E:\\IR\\Assignment3\\misc\\relevance_judgement_index.txt");
	private static File retrievedIndexFilePath = new File("E:\\IR\\Assignment3\\misc\\index_BM25_rj.txt");
	static LinkedList<String> relevantDocs = new LinkedList<String>();
	static LinkedList<String> retrievedDocs = new LinkedList<String>();
	static LinkedHashMap<String, String> relevantDocIndex = new LinkedHashMap<String, String>();
	static LinkedHashMap<String, String> retrievedDocIndex = new LinkedHashMap<String, String>();
	static LinkedHashMap<String, String> results = new LinkedHashMap<String, String>();
	public static void main(String[] args) 
	{
		BufferedReader br = null;
		BufferedReader br2 = null;
		try
		{
			int relevanceDocCount=0;
			int retrievedDocCount=0;
			int Length =0;
			double Counter =0;
			double precision = 0;
			double recall =0;
			double F1 =0;
			String keyHolder = null;
			String sCurrentLine;
			String tempString = null;
			br = new BufferedReader(new FileReader(relevanceIndexFilePath));
			br2 = new BufferedReader(new FileReader(retrievedIndexFilePath));
			String[] tempList = null;
			while ((sCurrentLine = br.readLine()) != null)
			{
				tempList= sCurrentLine.split("\\s+");
				for(String s: tempList)
				{
					relevanceDocCount++;
					if(relevanceDocCount>2)
					{
						relevantDocs.add(s);
					}					
				}
				relevanceDocCount = relevanceDocCount-2;
				keyHolder = tempList[0];
				relevantDocIndex.put(keyHolder, relevantDocs.toString());
				relevanceDocCount = 0;
				tempList = null;
				relevantDocs.clear();
			}
			while ((sCurrentLine = br2.readLine()) != null)
			{
				tempList= sCurrentLine.split("\\s+");
				for(String s: tempList)
				{
					retrievedDocCount++;
					if(retrievedDocCount>2)
					{
						retrievedDocs.add(s);						
					}					
				}
				retrievedDocCount = retrievedDocCount-2;
				keyHolder = tempList[0];
				retrievedDocIndex.put(keyHolder, retrievedDocs.toString());
				retrievedDocCount = 0;
				tempList = null;
				retrievedDocs.clear();
			}
			tempList = null;
			for(String key : retrievedDocIndex.keySet())
			{
				tempString = retrievedDocIndex.get(key);
				tempList= tempString.split("\\s+");
				for(String s: tempList)
				{
					Length = s.length()-1;
					if(s.startsWith("["))
					{
						s= s.substring(1, Length);
					}
					else
					{
						s= s.substring(0, Length);
					}
					retrievedDocs.add(s);			
				}				
				tempString = relevantDocIndex.get(key);				
				tempList= tempString.split("\\s+");				
				for(String s: tempList)
				{   
					Length = s.length()-1;
					if(s.startsWith("["))
					{
						s= s.substring(1, Length);
					}
					else
					{
						s= s.substring(0, Length);
					}
						relevantDocs.add(s);			
				}
				for(String t: retrievedDocs){
				if (relevantDocs.contains(t))
				{
					Counter++;
				}}
				//System.out.println(Counter);
				//System.out.println(retrievedDocs.size());
				precision = Counter/retrievedDocs.size();
				recall = Counter/relevantDocs.size();
				F1 = (2*precision*recall)/(precision+recall);
				results.put(key, " | precision: "+precision+" | recall: "+recall+" | F1: "+F1);
				tempList = null;
				retrievedDocs.clear();
				relevantDocs.clear();
				Counter=0;
			}
			for(String key : results.keySet())
			{
				System.out.println("Query: "+key + results.get(key));
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			try 
			{
				br.close();
			} 
			catch (IOException e) 
			{
				e.printStackTrace();
			}
		}
	}
}
