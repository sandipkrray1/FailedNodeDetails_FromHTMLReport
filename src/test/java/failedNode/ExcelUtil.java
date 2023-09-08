package failedNode;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class ExcelUtil
{
    public XSSFWorkbook xssfWorkbook;
    public File file;

    public ExcelUtil(String filePath, boolean fNew)
    {
        try
        {
            File fd = new File(filePath.substring(0, filePath.lastIndexOf(File.separator)));
            if (!fd.exists())
            {
                fd.mkdirs();
            }
            file = new File(filePath);
            if (file.exists())
            {
                if (fNew)
                {
                    file.delete();
                    xssfWorkbook = new XSSFWorkbook();
                }
                else
                {
                    xssfWorkbook = new XSSFWorkbook(file);
                }
            }
            else
            {
                xssfWorkbook = new XSSFWorkbook();
            }
        }
        catch (InvalidFormatException | IOException e)
        {
            e.printStackTrace();
        }
    }

    public boolean fSheetExist(String sheetName)
    {
        int num = xssfWorkbook.getNumberOfSheets();
        for (int i = 0; i < num; i++)
        {
            if(xssfWorkbook.getSheetAt(i).getSheetName().equalsIgnoreCase(sheetName))
            {
                return true;
            }
        }
        return false;
    }

    public ExcelUtil(String filePath)
    {
        this(filePath, false);
    }

    /*public ExcelUtil(File file)
    {
        this(file, false);
    }

    public ExcelUtil(File file, boolean fNew)
    {
        try
        {
            File fd = new File(file.getAbsolutePath().substring(0, file.getAbsolutePath().lastIndexOf(File.separator)));
            if (!fd.exists())
            {
                fd.mkdirs();
            }
            this.file = file;
            if (file.exists())
            {
                if (fNew)
                {
                    file.delete();
                    xssfWorkbook = new XSSFWorkbook();
                }
                else
                {
                    xssfWorkbook = new XSSFWorkbook(file);
                }
            }
            else
            {
                xssfWorkbook = new XSSFWorkbook();
            }
        }
        catch (InvalidFormatException | IOException e)
        {
            e.printStackTrace();
        }
    }*/

    public XSSFWorkbook getXSSFWorkbook()
    {
        return xssfWorkbook;
    }

    public XSSFSheet getSheet(String sheetName)
    {
        return xssfWorkbook.getSheet(sheetName);
    }

    public List<XSSFSheet> getAllSheet()
    {
        int num = xssfWorkbook.getNumberOfSheets();
        List<XSSFSheet> sheetList = new ArrayList<XSSFSheet>();
        for (int i = 0; i < num; i++)
        {
            sheetList.add(xssfWorkbook.getSheetAt(i));
        }
        return sheetList;
    }

    public String[][] getAllValuesOnSheet(XSSFSheet xssfSheet)
    {
        int rowstart = xssfSheet.getFirstRowNum();
        int rowEnd = xssfSheet.getLastRowNum();
        String[][] values = new String[rowEnd + 1][xssfSheet.getRow(0).getLastCellNum() + 1];
        for (int i = rowstart; i <= rowEnd; i++)
        {
            XSSFRow row = xssfSheet.getRow(i);
            if (null == row)
                continue;
            int cellStart = row.getFirstCellNum();
            int cellEnd = row.getLastCellNum();
            if (cellStart < 0 || cellEnd < 0)
            {
                continue;
            }

            for (int k = cellStart; k <= cellEnd; k++)
            {
                XSSFCell cell = row.getCell(k);
                if (null == cell)
                    continue;
                String value = "";
                switch (cell.getColumnIndex())
                {
                case 0:
                    value = cell.getNumericCellValue() + "";
                    break;
                case 1:
                    value = cell.getStringCellValue();
                    break;
                case 4:
                    value = cell.getBooleanCellValue() + "";
                    break;
                case 2:
//                    
//                    XSSFFormulaEvaluator evaluator = new XSSFFormulaEvaluator(xssfWorkbook);
//                    value = evaluator.evaluate(cell) + "";
//                    
//                    value = cell.getCellFormula() + "";
                    value = cell.getStringCellValue();
                    break;
                default:
                    value = "";
                    break;
                }
                if (value != "" && k < xssfSheet.getRow(0).getLastCellNum() + 1)
                {
                    values[i][k] = value;
                }
            }
        }
        return values;
    }


    public void WriteXlsxrep(List<String[]> data)
    {
        try
        {
            XSSFSheet sheet = null;
            XSSFRow row = null;
            sheet = xssfWorkbook.createSheet("Final Report");
            
            for (int i=0; i<data.size(); i++)
            {
            	XSSFCell cell;
                row = sheet.createRow(i);
                for (int j = 0; j < data.get(i).length; j++)
                {
                        cell = row.createCell(j);
                        if(j==0 && i!=0)
                        {
                            cell.setCellValue(i);
                        }
                        else
                        	cell.setCellValue(data.get(i)[j]);
                }

            }
            FileOutputStream outputStream = new FileOutputStream(file.getAbsolutePath());
            outputStream.flush();
            xssfWorkbook.write(outputStream);
            outputStream.close();
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    public void writeXlsx(String[] data)
    {
        try
        {
            XSSFSheet sheet = null;
            XSSFRow row = null;
            if (xssfWorkbook.getNumberOfSheets() > 0)
            {
                sheet = xssfWorkbook.getSheetAt(0);
                row = sheet.createRow(sheet.getLastRowNum() + 1);
            }
            else
            {
                sheet = xssfWorkbook.createSheet();
                row = sheet.createRow(0);
            }

            for (int i = 0; i < data.length; i++)
            {
                XSSFCell cell = row.createCell(i);
                cell.setCellValue(data[i]);
            }
            FileOutputStream outputStream = new FileOutputStream(file.getAbsolutePath());
            outputStream.flush();
            xssfWorkbook.write(outputStream);
            outputStream.close();
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
    
    public void close()
    {
        try
        {
            xssfWorkbook.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

}
