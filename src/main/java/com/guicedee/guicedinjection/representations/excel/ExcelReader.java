package com.guicedee.guicedinjection.representations.excel;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.guicedee.guicedinjection.GuiceContext;
import com.guicedee.guicedinjection.exceptions.ExcelRenderingException;
import com.guicedee.logger.LogFactory;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import static com.guicedee.guicedinjection.interfaces.ObjectBinderKeys.*;
import static com.guicedee.guicedinjection.json.StaticStrings.*;
import static java.math.BigDecimal.*;

/**
 * This class is used when a spreadsheet needs to be generated dynamically
 *
 * @author Ernst Created:23 Oct 2013
 */
@SuppressWarnings({"WeakerAccess", "unused"})

public class ExcelReader
		implements AutoCloseable
{
	private static final Logger log = LogFactory.getLog("Excel Reader");

	private InputStream inputStream;

	private HSSFWorkbook hwb;
	private XSSFWorkbook xwb;
	private boolean isH;
	private Sheet currentSheet;

	/**
	 * Constructs with a file to read
	 */
	public ExcelReader(InputStream inputStream, String extension) throws ExcelRenderingException
	{
		this(inputStream, extension, 0);
	}

	/**
	 * Constructs with a file to create
	 */
	public ExcelReader(InputStream inputStream, String extension, int sheet) throws ExcelRenderingException
	{
		if (inputStream == null)
		{
			throw new ExcelRenderingException("Inputstream for document is null");
		}
		this.inputStream = inputStream;
		if (extension.equalsIgnoreCase("xls"))
		{
			try
			{
				hwb = new HSSFWorkbook(inputStream);
			}
			catch (Throwable e)
			{
				log.log(Level.SEVERE,"Unable to excel ",e);
				throw new ExcelRenderingException("Cannot open xls workbook",e);
			}
			this.currentSheet = hwb.getSheetAt(sheet);
			isH = true;
		}
		else
		{
			try
			{
				xwb = new XSSFWorkbook(inputStream);
			}
			catch (Throwable e)
			{
				log.log(Level.SEVERE,"Unable to excel ",e);
				throw new ExcelRenderingException("Cannot open xlsx workbook", e);
			}
			this.currentSheet = xwb.getSheetAt(sheet);
			isH = false;
		}
	}

	public Workbook getWorkbook()
	{
		if (isH)
		{
			return hwb;
		}
		else
		{
			return xwb;
		}
	}

	/**
	 * Creates the cell headers
	 */
	public void writeHeader(List<String> headers)
	{
		Row row = currentSheet.createRow(0);
		int counter = 0;
		for (String item : headers)
		{
			Cell cell = row.createCell(counter);
			cell.setCellValue(item);
			counter++;
		}
	}

	/**
	 * Fetches a sheets complete data for the given number of records
	 *
	 * @param sheetNumber
	 * 		The sheet number starts at 0
	 * @param start
	 * 		How many rows to skip
	 * @param records
	 * 		The number of rows to return
	 *
	 * @return
	 */
	public Object[][] fetchRows(int sheetNumber, int start, int records)
	{
		int totalSheetRows = getRowCount(sheetNumber) + 1;
		int totalRowColumns = getColCount(sheetNumber);
		if (records > totalSheetRows)
		{
			records = totalSheetRows;
		}
		int arraySize = records - start;
		if (arraySize == 0)
		{
			arraySize = 1;
		}
		else if (arraySize < 0)
		{
			arraySize = arraySize * -1;
		}
		Object[][] tableOut = new Object[arraySize][getColCount(sheetNumber)];
		Sheet sheet;
		if (isH)
		{
			sheet = hwb.getSheetAt(sheetNumber);
		}
		else
		{
			sheet = xwb.getSheetAt(sheetNumber);
		}
		int rowN = 0;
		int cellN = 0;
		int skip = 0;
		try
		{
			for (Row row : sheet)
			{
				if (skip < start)
				{
					skip++;
					continue;
				}
				cellN = 0;
				int tCs = getColCount(sheetNumber);
				for (int cn = 0; cn < tCs; cn++)
				{
					Cell cell = row.getCell(cn, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);


					//   }
					//  for (Cell cell : row) {
					CellType cellType = cell.getCellType();
					switch (cellType)
					{
						case BLANK:
						{
							tableOut[rowN][cellN] = STRING_EMPTY;
							break;
						}
						case NUMERIC:
						{
							tableOut[rowN][cellN] = cell.getNumericCellValue();
							if (tableOut[rowN][cellN] instanceof Double)
							{
								Double d = (Double) tableOut[rowN][cellN];
								if (new BigDecimal(d).equals(ZERO))
								{
									tableOut[rowN][cellN] = 0;
								}
							}
							break;
						}
						case STRING:
						{
							tableOut[rowN][cellN] = cell.getStringCellValue();
							break;
						}
						case FORMULA:
						{
							FormulaEvaluator evaluator;
							if (isH)
							{
								evaluator = hwb.getCreationHelper()
								               .createFormulaEvaluator();
							}
							else
							{
								evaluator = xwb.getCreationHelper()
								               .createFormulaEvaluator();
							}
							CellValue cellValue = evaluator.evaluate(cell);
							Double valueD = cellValue.getNumberValue();
							tableOut[rowN][cellN] = valueD;
							break;
						}
						case BOOLEAN:
						{
							tableOut[rowN][cellN] = cell.getBooleanCellValue();
							break;
						}
						default:
						{
							break;
						}
					}
					cellN++;
					if (totalRowColumns == cellN)
					{
						break;
					}
				}
				rowN++;
				if (records == rowN)
				{
					break;
				}
			}
		}
		catch (ArrayIndexOutOfBoundsException e)
		{
			log.log(Level.WARNING, "Reached the end of the file before hitting the max results. logic error.", e);
		}
		catch (Exception e)
		{
			log.log(Level.WARNING, "Couldn't go through the whole excel file - ", e);
		}
		return tableOut;
	}

	/**
	 * Returns the number of columns
	 *
	 * @param sheetNo
	 *
	 * @return
	 */
	public int getColCount(int sheetNo)
	{
		Sheet sheet;
		if (isH)
		{
			sheet = hwb.getSheetAt(sheetNo);
		}
		else
		{
			sheet = xwb.getSheetAt(sheetNo);
		}

		Row row = sheet.getRow(sheetNo);
		return row.getLastCellNum();
	}

	public int getRowCount(int sheetNo)
	{
		if (isH)
		{
			return hwb.getSheetAt(sheetNo)
			          .getLastRowNum();
		}
		else
		{
			return xwb.getSheetAt(sheetNo)
			          .getLastRowNum();
		}
	}

	/**
	 * Writes a row of strings to the given row number (created)
	 */
	public void writeRow(int rowNumber, List<String> headers)
	{
		Row row = currentSheet.createRow(rowNumber);
		int counter = 0;
		for (String item : headers)
		{
			Cell cell = row.createCell(counter);
			cell.setCellValue(item);
			counter++;
		}
	}

	/**
	 * Writes data to a spreadsheet row
	 */
	@SuppressWarnings("ConstantConditions")
	public void writeRow(int rowNumber, Object[] rowData)
	{
		Row row = currentSheet.createRow(rowNumber);
		int counter = 0;
		for (Object item : rowData)
		{
			Cell cell = row.createCell(counter);
			if (item == null)
			{
				item = "";
			}
			String ftype = item.getClass()
			                   .getName();
			if (ftype.equals("java.lang.String"))
			{
				cell.setCellValue((String) item);
			}
			else if (ftype.equals("java.lang.Boolean") || ftype.equals("boolean"))
			{
				cell.setCellValue((Boolean) item);
			}
			else if (ftype.equals("java.util.Date"))
			{
				cell.setCellValue((Date) item);
			}
			else if (ftype.equals("java.sql.Timestamp"))
			{
				Timestamp obj = (Timestamp) item;
				Date temp = new Date(obj.getTime());
				cell.setCellValue(temp);
			}
			else if (ftype.equals("int") || ftype.equals("java.lang.Integer"))
			{
				cell.setCellValue((Integer) item);
			}
			else if (ftype.equals("long") || ftype.equals("java.lang.Long") || ftype.equals("java.math.BigInteger"))
			{
				cell.setCellValue((Long) item);
			}
			else if (ftype.equals("java.math.BigDecimal"))
			{
				cell.setCellValue(((BigDecimal) item).doubleValue());
			}
			else
			{
				cell.setCellValue((Double) item);
			}
			counter++;
		}
	}

	public Row getRow(int rowNumber)
	{
		return currentSheet.getRow(rowNumber);
	}

	public Cell getCell(int rowNumber, int cellNumber)
	{
		return currentSheet.getRow(rowNumber)
		                   .getCell(cellNumber);
	}

	public byte[] get()
	{
		byte[] output = null;
		try (ByteArrayOutputStream baos = new ByteArrayOutputStream())
		{
			if (isH)
			{
				hwb.write(baos);
			}
			else
			{
				xwb.write(baos);
			}
			output = baos.toByteArray();
		}
		catch (Exception e)
		{
			log.log(Level.SEVERE, "Unable to get the byte array for the excel file", e);
		}
		return output;
	}

	@SuppressWarnings("unchecked")
	public <T> List<T> getRecords(String sheetName, Class<T> type)
	{
		int sheetLocation = getWorkbook().getSheetIndex(sheetName);
		Object[][] rows = this.fetchRows(sheetLocation, 0, getRowCount(sheetLocation));
		Object[] headerRow = rows[0];
		List<T> output = new ArrayList<>();
		Map<Integer, Map<String, String>> cells = new TreeMap<>();
		for (int i = 1; i < rows.length; i++)
		{
			//Cell <Header,Value>
			cells.put(i, new LinkedHashMap<>());
			JSONObject rowData = new JSONObject();
			for (int j = 0; j < getColCount(sheetLocation); j++)
			{
				if (rows[i][j] instanceof BigDecimal)
				{
					rowData.put(headerRow[j].toString(), ((BigDecimal) rows[i][j]).toPlainString());
				}
				else
				{
					rowData.put(headerRow[j].toString(), rows[i][j])
					       .toString();
				}
				cells.get(i)
				     .put(headerRow[j].toString(), rows[i][j].toString());
				//cells.put(i, rows[i][j].toString().trim());
			}
			String outcome = rowData.toString();
			try
			{
				ObjectMapper om = GuiceContext.get(DefaultObjectMapper);
				T typed = om.readValue(outcome, type);
				output.add(typed);
			}
			catch (Exception e)
			{
				log.log(Level.SEVERE, "Unable to build an object from the references - " + outcome, e);
			}
		}
		return output;
	}

	@Override
	public void close() throws Exception
	{
		try
		{
			if (isH)
			{
				hwb.close();
			}
			else
			{
				xwb.close();
			}

		}
		catch (Exception e)
		{
			log.log(Level.SEVERE, "Unable to write the excel file out", e);
		}
		inputStream.close();
	}


}
