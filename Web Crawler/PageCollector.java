package WebCrawler;

import java.io.File;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.concurrent.TimeUnit;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

public class PageCollector 
{
	private static File contentPath = new File("E:\\webPages\\pagesDownloaded_task1.html"); //path to store downloaded webpages
	private static File frontierPath = new File("E:\\webPages\\webpages_task1.txt"); //path to store website links
	private static final int FRONTIER_LIMIT = 1000; //Max Limit for frontier
	private static final int MAX_DEPTH = 5; //Max Depth Limit
	private static final String SEED = "https://en.wikipedia.org/wiki/Sustainable_energy"; // SEED URL
	static PrintWriter frontierWriter = null;
	static PrintWriter webPagesWriter = null;
	public static void main(String args[])
	{
		try
		{
			ArrayList<String> pagesToVisit = new ArrayList<String>(); //Keeps track of visited pages
			LinkedHashSet<String> frontier = new LinkedHashSet<String>(); //To store webpages
			frontier.add(SEED); //seed added in the frontier
			pagesToVisit.add(SEED); // keeps track of pages visited
			int depth=1; //initialized depth to 1
			int count=1; //stores the number of webpages in each depth
			boolean isTrue= false;
			boolean isLimitReached= false;
		    while (pagesToVisit.size()!=0)
			{   
				String s= (pagesToVisit.get(0)); //copied the first page to crawl
		    	pagesToVisit.remove(0); //removes crawled pages
				Document doc = Jsoup.connect(s).get(); //connects to webpage using Jsoup
				org.jsoup.select.Elements links = doc.select("a");
				for(Element e:links) //loops over the links in the page
				{   
					if((frontier.size() < FRONTIER_LIMIT) && (depth <= MAX_DEPTH)) //checks for frontier count/ depth limits
					{	
						boolean validLink = e.attr("abs:href").startsWith("https://en.wikipedia.org/wiki") && e.attr("abs:href").split(":").length == 2;// checks for valid link
						if (validLink != false) 
						{
							if(e.attr("abs:href").indexOf("#") == -1)
							{
								isTrue = false;
								isTrue =frontier.add(e.attr("abs:href")); //adds webpages to frontier
								if (isTrue!= false)
								{
									pagesToVisit.add(e.attr("abs:href")); //adds unique webpages to pagesToVist list
								}
							}
							else
							{
								isTrue= false;
								isTrue =frontier.add(e.attr("abs:href").split("#")[0]); //adds webpages to frontier
								if(isTrue!=false)
								{
									pagesToVisit.add(e.attr("abs:href").split("#")[0]); //adds unique webpages to pagesToVist list
								}
							}
						}
					}
					if(frontier.size() == FRONTIER_LIMIT)
					{  
						isLimitReached = true;
						break;// break from looping links on reaching frontier limit
					}
				}
				if(isLimitReached == true)
				{  
					break; // break from looping webpages on reaching frontier limit 
				}
				
				count= count-1; //decrementing pages to visit count
				if(count == 0)
				{
					count = pagesToVisit.size();// gets the number of pages of the new depth
					depth++; //increment depth
				}
				TimeUnit.SECONDS.sleep(1);
			}
			frontierWriter = new PrintWriter(frontierPath);
			webPagesWriter = new PrintWriter(contentPath);
			for(String front: frontier)
			{
				frontierWriter.write(front + "\n");
				Document doc = Jsoup.connect(front).get();
				webPagesWriter.write(doc + "\n"+ "****************************************" + "\n");
				TimeUnit.SECONDS.sleep(1);
			}
			System.out.println("Crawling of "+ FRONTIER_LIMIT + " webpages or webpages until depth 5 is completed");
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		} 
		finally
		{
			frontierWriter.close();
			webPagesWriter.close();
		}
	} 			
}