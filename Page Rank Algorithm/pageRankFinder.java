package GenerateWebGraph;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;


public class pageRankFinder
{
	static LinkedHashMap<String, Double> L = new LinkedHashMap<String, Double>();
	static LinkedHashMap<String, Double> newPR = new LinkedHashMap<String, Double>();
	static LinkedHashMap<String, Double> PR = new LinkedHashMap<String, Double>();
	static LinkedHashMap<String, Double> inLink = new LinkedHashMap<String, Double>();
	private static File pageRankPath = new File("E:\\webPages\\pageRank.txt");
	static PrintWriter pageRankWriter = null;
	private static File inLinkPath = new File("E:\\webPages\\inLink.txt");
	static PrintWriter inLinkWriter = null;
	static double oldPerplexity = 0;
	static int counter=0;
	
	public static void main(String[] args) 
	{
		BufferedReader br = null;
		LinkedHashSet<String> valueList = new LinkedHashSet<String>();
		LinkedHashSet<String> S = new LinkedHashSet<String>();
		LinkedHashMap<String, LinkedHashSet<String>> graph = new LinkedHashMap<String, LinkedHashSet<String>>();
		
		String[] tempList = null;		
		double N;
		double d = 0.85;		
		
		String webGraphFilePath = "E:\\IR\\Assignment2\\wt2g_inlinks.txt";
		try 
		{
			String sCurrentLine;
			br = new BufferedReader(new FileReader(webGraphFilePath));
			pageRankWriter = new PrintWriter(pageRankPath);
			inLinkWriter = new PrintWriter(inLinkPath);
						
            // Finds N
			while ((sCurrentLine = br.readLine()) != null) 
			{
				tempList= sCurrentLine.split(" ");
				for(String s: tempList)
				{
					if (s == tempList[0]) continue;
					valueList.add(s);
				};
				graph.put(tempList[0], valueList);
				valueList = new LinkedHashSet<String>();
			}
			for(String key: graph.keySet())
			{
				inLink.put(key, (double)(graph.get(key).size()) );			
			}
			N= graph.size();
			
			for (String key : graph.keySet())
			{
				L.put(key, 0.0);
			}

			for (LinkedHashSet<String> values : graph.values())
			{
				for(String element: values)
				{
					L.put(element, (L.get(element) + 1));
				}
			}
			for (String key : L.keySet())
			{
				if(L.get(key) == 0){S.add(key);}
			}
						
			for(String key: graph.keySet())
			{
				PR.put(key, 1/N);
			}
			
			while(isConverged(PR))
			{
				double sinkPR = 0;
				for(String s: S)
				{
					sinkPR += PR.get(s);
				}
				for(String key: graph.keySet())
				{
					newPR.put(key, ((1-d)/N));
					newPR.put(key, (newPR.get(key)+ d*sinkPR/N));
					valueList= graph.get(key);
					for(String q: valueList)
					{
						newPR.put(key, (newPR.get(key)+ d*PR.get(q)/L.get(q)));
					}
				}
				for(String key: graph.keySet())
				{
					PR.put(key, newPR.get(key));
				}				
			}
			
			Comparator<Map.Entry<String, Double>> byMapValues = new Comparator<Map.Entry<String, Double>>() 
			{
			    public int compare(Map.Entry<String, Double> left, Map.Entry<String, Double> right) 
				{
			        return left.getValue().compareTo(right.getValue());
			    }
			};
			   
			List<Map.Entry<String, Double>> temp = new ArrayList<Map.Entry<String, Double>>();
			List<Map.Entry<String, Double>> temp1 = new ArrayList<Map.Entry<String, Double>>();
			
			temp.addAll(PR.entrySet());
			temp1.addAll(inLink.entrySet());
			   
			Collections.sort(temp,byMapValues);
			Collections.sort(temp1,byMapValues);
			   
			for(int i = temp.size()-1; i > (temp.size() - 51); i--)
			{
			   	pageRankWriter.write("Page DocID: " + temp.get(i).getKey() + " PageRank: " + temp.get(i).getValue()+ "\n");
		    }
			   
			for(int i = temp1.size()-1; i > (temp1.size() - 51); i--)
			{
				inLinkWriter.write("Page DocID: " + temp1.get(i).getKey() + " Inlinks Count: " + temp1.get(i).getValue()+ "\n");
			}
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
				pageRankWriter.close();
				inLinkWriter.close();
			} 
			catch (Exception ex) 
			{
				ex.printStackTrace();
			}
		}
	}
	public static Boolean isConverged(LinkedHashMap<String, Double> PR)
	{
		double hPR = 0;
		double temphPR = 0;
		Boolean toReturn = true;
		double tempValue =0;
		double tempValue1=0;
		double tempValue2=0;
		double tempValue3=0;
		
		for(String key: PR.keySet())
		{
			tempValue= PR.get(key);
			tempValue1 = ((Math.log(tempValue)) / (Math.log(2)));
			tempValue2= (tempValue1*tempValue);
			tempValue3 = (tempValue2 + tempValue3);
		}
			temphPR = -tempValue3;
		
		hPR = (double)(Math.pow(2, temphPR));
		if((Math.abs((Math.abs(hPR)) - (Math.abs(oldPerplexity))))<1)
		{
			counter++;
			if(counter>=4)
			toReturn = false;			
		}
		else
		{
			counter=0;
			toReturn = true;
		}
		oldPerplexity = hPR;
		return toReturn;
	}
	
}