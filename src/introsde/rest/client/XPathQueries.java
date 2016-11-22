package introsde.rest.client;

import java.io.IOException;
import java.io.StringReader;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class XPathQueries {

   	private XPath xpath;
   	private InputSource inputSource;

    public void loadXML(String xmlString) throws ParserConfigurationException, SAXException, IOException, XPathExpressionException {

        getXPathObj();
        inputSource = new InputSource(new StringReader(xmlString));
        
    }

    public XPath getXPathObj() {
        XPathFactory factory = XPathFactory.newInstance();
        xpath = factory.newXPath();
        return xpath;
    }
    
    public NodeList getPersonIDs(String xmlString)throws XPathExpressionException, ParserConfigurationException, SAXException, IOException{
    	loadXML(xmlString);
    	inputSource = new InputSource(new StringReader(xmlString));
    	NodeList nodes = (NodeList)xpath.evaluate("/people/person/idPerson",
        		inputSource, XPathConstants.NODESET);
    	 return nodes;
    }
    
    public Node getPersonById(int id, String xmlString)throws XPathExpressionException, ParserConfigurationException, SAXException, IOException{
    	Node node = (Node)xpath.evaluate("person[idPerson=' " + id + "']",
        		inputSource, XPathConstants.NODE);
    	 return node;
    }

    
    public NodeList getNodeListResult(String condition , String xmlString) throws XPathExpressionException, ParserConfigurationException, SAXException, IOException {
    	loadXML(xmlString);
    	inputSource = new InputSource(new StringReader(xmlString));
    	XPathExpression expr = xpath.compile(condition);
        NodeList nodes = (NodeList) expr.evaluate(inputSource, XPathConstants.NODESET);
        
        return nodes;
        
    }
    
    public Node getNodeResult(String expression, String xmlString) throws XPathExpressionException, ParserConfigurationException, SAXException, IOException{
    	loadXML(xmlString);
    	inputSource = new InputSource(new StringReader(xmlString));
      	XPathExpression expr = xpath.compile(expression);
        Node node = (Node) expr.evaluate(inputSource, XPathConstants.NODE);
        return node;
    }
    
    public Node getPersonFirstName(int id, String xmlString) throws XPathExpressionException, ParserConfigurationException, SAXException, IOException{
    	loadXML(xmlString);
    	inputSource = new InputSource(new StringReader(xmlString));
    	System.err.println(id);
    	Node name = (Node) xpath.evaluate("person[idPerson=' " + id + "']/name",
        		inputSource, XPathConstants.NODE);
    	if(name == null) System.err.println("This is not gonna work");
    	return name;
    	
    }
}
