package WebCrawler;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedHashSet;
import java.util.concurrent.TimeUnit;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

public class FocusedDFS 
{
	private static File frontierPath = new File("E:\\webPages\\webpages_task2b.txt"); //path to store website links
	private static LinkedHashSet<String> frontier =new LinkedHashSet<String>();//Linkedhashset to store frontier
	private static final int FRONTIER_LIMIT = 1000; //Max Limit for frontier
	private static final int MAX_DEPTH = 5; //Max Depth Limit
	private static final String SEED = "https://en.wikipedia.org/wiki/Sustainable_energy"; // SEED URL
	private static final String FOCUS_KEYWORD = "solar"; // Focus Keyword
	static FileWriter frontierWriter;
		
	
	public static void main(String args[])
	{
		crawlWebPageDFS(SEED,1); 
	}

	private static void crawlWebPageDFS(String url, int depth) 
	{
		if (frontier.size()>=FRONTIER_LIMIT) 
		{
			return; //when frontier counter is exceeded
		}
		if(depth>MAX_DEPTH)
		{	
			return;
		}
		try 
		{
			Document doc = Jsoup.connect(url).get();//connects to webpage using Jsoup
			//TimeUnit.SECONDS.sleep(1);// wait for 1 sec 
			if(!(url.contains(FOCUS_KEYWORD) || doc.toString().contains(FOCUS_KEYWORD)))
				return;
			
			org.jsoup.select.Elements links = doc.select("a");
			Boolean isAdded=frontier.add(url); // adds URL to the frontier
			if(!isAdded)
			{
				return; // calls next URL if a duplicate URL is encountered
			}
			frontierWriter = new FileWriter(frontierPath, true);
			frontierWriter.write(url + "\n");
			frontierWriter.close();
			
			for(Element e:links) //loops over the links in the page
			{   
				String cleanedURL=null;
				boolean validLink = e.attr("abs:href").startsWith("https://en.wikipedia.org/wiki") && e.attr("abs:href").split(":").length == 2;// checks for valid link
				if (validLink != false)
				{
					if((e.attr("abs:href").indexOf("#")!=-1))
					{
						cleanedURL=e.attr("abs:href").substring(0,e.attr("abs:href").indexOf("#"));						
					}
					else 
					{
						cleanedURL=e.attr("abs:href");
					}
				}
				if(cleanedURL!=null)
				{
					crawlWebPageDFS(cleanedURL,depth+1);
				}				
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
				frontierWriter.close();
			} 
			catch (IOException e) 
			{
				e.printStackTrace();
			}
		}		
	}
}