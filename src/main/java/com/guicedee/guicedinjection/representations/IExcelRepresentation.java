package com.guicedee.guicedinjection.representations;

import com.guicedee.guicedinjection.representations.excel.ExcelReader;
import com.guicedee.guicedinjection.exceptions.ExcelRenderingException;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public interface IExcelRepresentation
{
	
	/**
	 * Reads an excel file into it's objects representation
	 * <p>
	 *
	 * @param stream
	 * @param objectType
	 * @param sheetName
	 * @param <T>
	 * @return
	 * @throws ExcelRenderingException
	 */
	default <T> List<T> fromExcel(InputStream stream, Class<T> objectType, String sheetName) throws ExcelRenderingException
	{
		try (ExcelReader excelReader = new ExcelReader(stream, "xlsx"))
		{
			return excelReader.getRecords(sheetName, objectType);
		}
		catch (IOException e)
		{
			throw new ExcelRenderingException("Cannot read the excel file");
		}
		catch (Exception e)
		{
			throw new ExcelRenderingException("General error with the excel file", e);
		}
	}
	
	default String toExcel()
	{
		return null;
	}
	
	
}
