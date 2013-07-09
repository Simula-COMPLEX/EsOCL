package simula.standalone.analysis;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class ModelInstanceUtility {

	Document document;
	
	

	public Document getDocument() {
		return document;
	}

	public void createModelInstanceDoc() {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = null;
		try {
			builder = factory.newDocumentBuilder();
			this.document = builder.newDocument();
			Node root = document.createElement("UMLModelInstance");
			document.appendChild(root);
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void createClassInstance(String className) {
		Node classNode = this.document.createElement(className);
		this.document.getFirstChild().appendChild(classNode);
	}
	
	public void createPropertyInstance(String className, String propertyName, String value) {
		NodeList classNodes = this.document.getElementsByTagName(className);
		Node propertyNode = this.document.createElement(propertyName);
		propertyNode.setTextContent(value);
		
		Node propertyNode_1 = this.document.createElement("cards");
		Node propertyNode_1_1= this.document.createElement("printedName");
		propertyNode_1_1.setTextContent("AA");
		propertyNode_1.appendChild(propertyNode_1_1);
		Node propertyNode_2 = this.document.createElement("cards");
		Node propertyNode_2_1= this.document.createElement("printedName");
		propertyNode_2_1.setTextContent("BB");
		propertyNode_2.appendChild(propertyNode_2_1);
		
		classNodes.item(0).appendChild(propertyNode);
		classNodes.item(0).appendChild(propertyNode_1);
		classNodes.item(0).appendChild(propertyNode_2);

		
	}

	public static void saveXml(String fileName, Document doc) {// 将Document输出到文件
		TransformerFactory transFactory = TransformerFactory.newInstance();
		try {
			Transformer transformer = transFactory.newTransformer();
			transformer.setOutputProperty("indent", "yes");

			DOMSource source = new DOMSource();
			source.setNode(doc);
			StreamResult result = new StreamResult();
			result.setOutputStream(new FileOutputStream(fileName));

			transformer.transform(source, result);
		} catch (TransformerConfigurationException e) {
			e.printStackTrace();
		} catch (TransformerException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		ModelInstanceUtility rmi = new ModelInstanceUtility();
		rmi.createModelInstanceDoc();
		rmi.createClassInstance("classA");
		rmi.createPropertyInstance("classA","propertyB","valueC");
		rmi.saveXml("test.xml", rmi.getDocument());
	}

}
