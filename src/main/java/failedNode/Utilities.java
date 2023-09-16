package failedNode;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utilities {
	
	public String getFileName(String elementName, List<String[]> failElementsFile)
    {
    	String fileName = null;
    	for(int i=0; i<failElementsFile.size();i++)
    	{
    		if(failElementsFile.get(i)[1].toString().equalsIgnoreCase(elementName))
    		{
    			fileName = failElementsFile.get(i)[0].toString();
    			//System.out.println(fileName);
    			break;
    		}
    			
    	}
    	
    	return fileName;
    }
	

	    public String[] ReadTextFileIntoVariable(String filePath) {
	        StringBuilder content = new StringBuilder();
            String line1 = null;
            String[] fileContent =null;
	        File file = new File(filePath);

	        try {
	        	String line;
	            BufferedReader reader = new BufferedReader(new FileReader(file));
	            while ((line = reader.readLine()) != null) {
	            	if(line1==null)
	            		line1 = line;
	            	else
	            		line1= line1+"; "+line;
	            	//System.out.println(line);
	            }
	            reader.close();
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
	        if(line1!=null)
	        {
	        fileContent = line1.toString().split("; ");
	        //System.out.println(fileContent);
	        }
	        return fileContent;
	    }

}