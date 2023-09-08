package failedNode;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.xpath.XPathExpressionException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class FailedNodeDetailsExtractor {
	static Utilities utils = new Utilities();
	private static Set<String> failedElements;
	private static List<String[]> failedElementsWithDiffType;

	public static void main(String[] args) throws XPathExpressionException, IOException {
		
		 List<String> filePaths = new ArrayList<>();
	     failedElements = new HashSet<String>();
	     failedElementsWithDiffType = new ArrayList<String[]>();
		 
	     //String folderPath = "C:\\Users\\rays3\\Downloads\\validator\\test\\CAASPUBT_187783";
	     String folderPath = args[0];
	     File files = new File(folderPath);
		 long startTime = System.currentTimeMillis();

		 filePaths = FilesCollection(files);
		 int i = 0;
	     for (String filePath : filePaths) {
	            ExtractFailNodes(filePath);
	            i++;
	        	System.out.println(i+". The File: "+new File(filePath).getName()+" is processing...");
	     }
	     System.out.println("Creating excel report for Failed Elements....");
	     ExcelUtil excel2 = new ExcelUtil(files.toString() + File.separator + "FailedNodes_Details.xlsx", true);
	     excel2.WriteXlsxrep(FailedElementsDetails());
	     System.out.println("Report is created at: "+folderPath);
     	long endTime1 = System.currentTimeMillis();
     	System.out.println("Thats took " + (endTime1 - startTime)/1000 + " seconds --> ");    


	}
	
	private static void ExtractFailNodes(String filePath)
	{

        try {
            File inputFile = new File(filePath);
            Document document = Jsoup.parse(inputFile, "UTF-8");

            Elements tables = document.select("table");
            
            for (Element table : tables) {
                List<Element> trs = table.select("tr");
                for(Element tr : trs)
                {
                	if(tr.toString().contains("<th"))
                		continue;
                	
                	if(tr.toString().contains("<del ") || tr.toString().contains("<ins "))
                	{
                    List<Element> tds = tr.select("td");
                    Element td1 = tds.get(0);
                    Element td2 = tds.get(1);
                    
                    DetailsExtractor(inputFile.getName().toString(), td1, td2);
                    //System.out.println(diffDetails);
                	}
                }
                //System.out.println(failedElements.toString());
            	System.out.println("Processing of "+inputFile.getName()+" is done.");
            	System.out.println();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

	}
	
    private static void DetailsExtractor(String fileName, Element td1, Element td2)
    {
    	String text1 = null;
    	
    	if(td1.toString().contains("<del"))
    	{
    		Elements delElms = td1.getElementsByTag("del");
    		text1 = delElms.get(0).text()+"\t \t"+"DELETE";
    		
    	}
    	else if(td1.toString().contains("<ins"))
    	{
    		Elements td1Elm = td1.getElementsByTag("ins");
    		Elements td2Elm = td2.getElementsByTag("ins");
    		text1 = td1Elm.get(0).text()+"\t"+td2Elm.get(0).text()+"\tCHANGE";
    	}
    	else if(td2.toString().contains("<ins"))
    	{
    		Elements td2Elm = td2.getElementsByTag("ins");
    		text1 = " \t"+td2Elm.get(0).text()+"\tINSERT";
    	}
    	
    	//failedElements.add(text1);
    	if(RegexString(text1)!=null)
    	{
    	elementExtracter(text1);
    	}
    	else
    	{
    		failedElements.add(text1);
    	}
    	failedElementsWithDiffType.add(new String[] {fileName, text1});
    	
    	//return text1;
    }
	
	public static List<String> FilesCollection(File inputFolder)
	{
		List<String> filePaths = new ArrayList<>();
		
        for (File inputFile : inputFolder.listFiles())
        {
            if (!inputFile.getName().endsWith(".html"))
            {
                continue;
            }
            
            filePaths.add(inputFile.getAbsolutePath());
        }
        
        return filePaths;
	}
	
    public static List<String[]> FailedElementsDetails()
    {
        List<String[]> failedElmRep = new ArrayList<>();
        List<String> uniqueFailedElm = new ArrayList<>();
        uniqueFailedElm.addAll(failedElements);
 
        if(failedElmRep.isEmpty())
        	failedElmRep.add(new String[] {"S.No.", "File Name","Input(Element Name)","Output(Element Name)","Difference Remarks"});
        int i=0;
        for(int j=0; j<uniqueFailedElm.size(); j++)
        {
        	i++;
        	String failElm = uniqueFailedElm.get(j).toString();
        	String[] failElm1 = uniqueFailedElm.get(j).toString().split("\t");
        	
        	String fileName1 = utils.getFileName(failElm, failedElementsWithDiffType);
        	failedElmRep.add(new String[] {String.valueOf(i), fileName1,failElm1[0],failElm1[1],failElm1[2]});
        }
        	
        //failedElements.clear();
        //failedElementsWithDiffType.clear();

        return failedElmRep;
    }
    
	
    private static void elementExtracter(String text)
    {
    	List<String> list = new ArrayList<>();
    	list.addAll(failedElements);
		String regexText = RegexString(text);
		Pattern pattern = Pattern.compile(regexText);
        Matcher matcher = pattern.matcher(text);
        if(regexText==null)
        {
    		failedElements.add(text);
        }
        else if (matcher.find())
        {
        	boolean flag = false;
        	for(int i=0; i<list.size(); i++)
        	{
        		String text1 = list.get(i).toString();
                Matcher matcher1 = pattern.matcher(text1);
        		if(matcher1.find())
        		{
        			flag=true;
        			//System.out.println("Text matched in existing element list.");
        			break;
        		}

        	}
        	if(flag==false)
        		failedElements.add(text);
        }
        else
        {
    		failedElements.add(text);
        	//System.out.println("Pattern not matched from current text.");
        }	
        
    }
    
    private static String RegexString(String text)
    {
    	String regex = null;
    	Pattern pattern;
    	Matcher matcher;
    	String[] regList = utils.ReadTextFileIntoVariable("Properties/RegexList.txt");
    	if(regList!=null)
    	{
    	for(int i = 0; i<regList.length; i++)
    	{
    		regex = regList[i].toString();
        	pattern = Pattern.compile(regex);
            matcher = pattern.matcher(text);
            
        	if(matcher.find())
        	{
        		break;
        	}
    		
    	}
    	}
    	
    	return regex;
    }

}
