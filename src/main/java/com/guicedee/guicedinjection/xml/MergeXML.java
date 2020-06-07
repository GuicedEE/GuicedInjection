package com.guicedee.guicedinjection.xml;

import org.w3c.dom.Document;
import org.w3c.dom.Node;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Result;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;
import java.io.IOException;
import java.io.OutputStream;

public class MergeXML
{
	public void merge(String rootExpression, OutputStream outputStream, String... xmlFiles) throws Exception
	{
		Document doc = merge(rootExpression, xmlFiles);
		print(doc, outputStream);
	}

	private Document merge(String expression,
	                       String... xmls) throws Exception
	{
		XPathFactory xPathFactory = XPathFactory.newInstance();
		XPath xpath = xPathFactory.newXPath();
		XPathExpression compiledExpression = xpath.compile(expression);
		return merge(compiledExpression, xmls);
	}

	private void print(Document doc, OutputStream outputStream) throws Exception
	{
		TransformerFactory transformerFactory = TransformerFactory
				                                        .newInstance();
		Transformer transformer = transformerFactory
				                          .newTransformer();
		DOMSource source = new DOMSource(doc);
		Result result = new StreamResult(outputStream);
		transformer.transform(source, result);
	}

	private Document merge(XPathExpression expression,
	                       String... xmls) throws Exception
	{
		DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory
				                                           .newInstance();
		docBuilderFactory
				.setIgnoringElementContentWhitespace(true);
		DocumentBuilder docBuilder = docBuilderFactory
				                             .newDocumentBuilder();

		Document base = docBuilder.parse(xmls[0]);

		Node results = (Node) expression.evaluate(base,
		                                          XPathConstants.NODE);
		if (results == null)
		{
			throw new IOException(xmls[0]
			                      + ": expression does not evaluate to node");
		}

		for (int i = 1; i < xmls.length; i++)
		{
			Document merge = docBuilder.parse(xmls[i]);
			Node nextResults = (Node) expression.evaluate(merge,
			                                              XPathConstants.NODE);
			while (nextResults.hasChildNodes())
			{
				Node kid = nextResults.getFirstChild();
				nextResults.removeChild(kid);
				kid = base.importNode(kid, true);
				results.appendChild(kid);
			}
		}
		return base;
	}

}
