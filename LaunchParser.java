/*
 ****************************************************************************
 *
 * Copyright (c)2018 The Vanguard Group of Investment Companies (VGI)
 * All rights reserved.
 *
 * This source code is CONFIDENTIAL and PROPRIETARY to VGI. Unauthorized
 * distribution, adaptation, or use may be subject to civil and criminal
 * penalties.
 *
 ****************************************************************************
 Module Description:

 $HeadURL:$
 $LastChangedRevision:$
 $Author:$
 $LastChangedDate:$
*/
package co.uk.vg;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class LaunchParser {

	public static void main(String[] args) {
		LaunchParser launchParser = new LaunchParser();
		System.out.println("*******************Comparison XSD with VXSD ****************************");	
		
		List<String> recordTagsInGenXsd = launchParser.getAllRecordTagsInGenXsd();
		
		System.out.println("complexTypes with contents to be added to vxsd");
		Map<String, List<String>> ctNames2ElementsUnderneath = recordTagsInGenXsd.stream()
				.filter(tag -> !launchParser.getAllRecordTagsInVxsd().contains(tag))			
                 .collect(Collectors.toMap(Function.identity(), 
					launchParser::getElementsOfComplexTypeInGeneratedXsd));
				
//		
//		System.out.println("complexTypes with contents to be modified in vxsd");
//		recordTagsInGenXsd.stream()
//			.filter(tag -> launchParser.getAllRecordTagsInVxsd().contains(tag))
//			.peek(System.out::println)
//			.forEach(complexTagName -> {
//				launchParser.getElementsOfComplexTypeInGeneratedXsd(complexTagName).stream()
//				.filter(element -> !launchParser.getElementsOfComplexTypeInVxsd(complexTagName).contains(element))
//				.forEach(element -> System.out.println("\t: "+ element));
//			});
//		launchParser.getElementsOfComplexTypeInVxsd("CE_record");
//		launchParser.getElementsOfComplexTypeInGeneratedXsd("CE_record").stream().forEach(System.out::println);
				
		List<String> additionalTags = launchParser.getAllElementTagsOfAssetInGenXsd().stream().filter(tag -> !launchParser.getAllElementTagsOfAssetInVxsd().contains(tag)).collect(Collectors.toList());
		launchParser.addElementstoVxsd(ctNames2ElementsUnderneath, additionalTags);		
		//add order attribute to additional tags
	}

	
	private List<String> getAllRecordTagsInGenXsd() {
		List<String> recordTagList = new ArrayList<>();
		try {
			// parse the document
			DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
			Document doc = docBuilder.parse(new File("C:/Users/u8de/Downloads/assets.xsd"));
			NodeList list = doc.getElementsByTagName("element");

			for (int i = 0; i < list.getLength(); i++) {
				Element first = (Element) list.item(i);
				if (first.hasAttributes()) {
					if (!nodeHasMixedComplexTypeChild(first)) {
						String nm = first.getAttribute("name");
						if (nm != null && nm.endsWith("record")) {
							recordTagList.add(nm);
						}
					}
				}
			}
		} catch (Exception ed) {
			ed.printStackTrace();
		}
		return recordTagList;
	}
	
	private Set<String> getAllElementTagsOfAssetInGenXsd() {
		Set<String> subElementList = new HashSet<>();
		Set<String> possibleElementsinAsset = new HashSet<>();
		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document doc = builder.parse("C:/Users/u8de/Downloads/assets.xsd");
			XPathFactory xPathfactory = XPathFactory.newInstance();
			XPath xpath = xPathfactory.newXPath();
			XPathExpression expr = xpath.compile("//element[@name='ASSET']/complexType/choice/element");			
			NodeList nl = (NodeList) expr.evaluate(doc, XPathConstants.NODESET);			
			
			for (int i = 0; i < nl.getLength(); i++) {
				Node childNode = nl.item(i);
				if(childNode.getNodeType() == Node.ELEMENT_NODE) {
					String referenceName = childNode.getAttributes().getNamedItem("ref").getTextContent().replace("t:", "");
					if(!referenceName.startsWith("FhlmcModel")) {
						subElementList.add(referenceName);
					}
				}
			}
			
			subElementList.stream().forEach(assetType -> {
				XPathExpression exprForSubElements;
				try {
					exprForSubElements = xpath.compile("//element[@name='"
							+ assetType
							+ "']/complexType/sequence/element");
					NodeList nodeList = (NodeList) exprForSubElements.evaluate(doc, XPathConstants.NODESET);					
					for (int i = 0; i < nodeList.getLength(); i++) {
						Node childNode = nodeList.item(i);
						if(childNode.getNodeType() == Node.ELEMENT_NODE) {
							possibleElementsinAsset.add(childNode.getAttributes().getNamedItem("ref").getTextContent().replace("t:", ""));
						}
					}
				} catch (Exception e) {					
					e.printStackTrace();
				}
			});
			
		} catch(Exception e) {
			e.printStackTrace();
		}
		return possibleElementsinAsset;
	}

	private List<String> getAllRecordTagsInVxsd() {
		List<String> recordTagList = new ArrayList<>();
		try {
			// parse the document
			DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
			Document doc = docBuilder.parse(new File("C:/Users/u8de/Downloads/2go/vxsds/INDEX_smf.vxsd"));
			NodeList list = doc.getElementsByTagName("*");		
			
			for (int i = 0; i < list.getLength(); i++) {
				Element first = (Element) list.item(i);
				if (first.hasAttributes()) {
					String nm = first.getAttribute("complex");
					if (nm != null && nm.equals("true")) {
						recordTagList.add(first.getTagName());
					}
				}
			}
		} catch (Exception ed) {
			ed.printStackTrace();
		}
		return recordTagList;
	}

	private static boolean nodeHasMixedComplexTypeChild(Element first) {
		NodeList nodeList = first.getElementsByTagName("complexType");
		for (int i = 0; i < nodeList.getLength(); i++) {
			String mixedAtribute = ((Element) nodeList.item(i)).getAttribute("mixed");
			if (mixedAtribute != null && mixedAtribute.equals("true")) {
				return true;
			}
		}
		return false;
	}
	
	private Set<String> getAllElementTagsOfAssetInVxsd() {
		Set<String> subElementList = new HashSet<>();

		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document doc = builder.parse("C:/Users/u8de/Downloads/2go/vxsds/INDEX_smf.vxsd");
			XPathFactory xPathfactory = XPathFactory.newInstance();
			XPath xpath = xPathfactory.newXPath();
			XPathExpression expr = xpath.compile("/ASSET");			
			NodeList nl = (NodeList) expr.evaluate(doc, XPathConstants.NODESET);			
			nl = nl.item(0).getChildNodes();
			
			for (int i = 0; i < nl.getLength(); i++) {
				Node childNode = nl.item(i);
				if(childNode.getNodeType() == Node.ELEMENT_NODE && childNode.getAttributes().getNamedItem("complex") == null) {
					subElementList.add(childNode.getNodeName());
				}
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
		return subElementList;
	}

	private List<String> getElementsOfComplexTypeInVxsd(String complexTypeTagName) {
		List<String> subElementList = new ArrayList<>();
		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document doc = builder.parse("C:/Users/u8de/Downloads/2go/vxsds/INDEX_smf.vxsd");
			XPathFactory xPathfactory = XPathFactory.newInstance();
			XPath xpath = xPathfactory.newXPath();
			XPathExpression expr = xpath.compile("/ASSET/" + complexTypeTagName);
			
			NodeList nl = (NodeList) expr.evaluate(doc, XPathConstants.NODESET);
			NodeList list = nl.item(0).getChildNodes();
			for (int i = 0; i < list.getLength(); i++) {
				Node childNode = list.item(i);
				if(childNode.getNodeType() == Node.ELEMENT_NODE) {
					subElementList.add(childNode.getNodeName());
				}
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
		return subElementList;
	}
	
	private List<String> getElementsOfComplexTypeInGeneratedXsd(String complexTypeTagName) {
		List<String> subElementList = new ArrayList<>();
		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document doc = builder.parse("C:/Users/u8de/Downloads/assets.xsd");
			XPathFactory xPathfactory = XPathFactory.newInstance();
			XPath xpath = xPathfactory.newXPath();
			XPathExpression expr = xpath.compile("//element[@name='" + complexTypeTagName + "']/complexType/sequence/element");
			
			NodeList nl = (NodeList) expr.evaluate(doc, XPathConstants.NODESET);
			
			for (int i = 0; i < nl.getLength(); i++) {
				Node childNode = nl.item(i);
				if(childNode.getNodeType() == Node.ELEMENT_NODE) {
					subElementList.add(childNode.getAttributes().getNamedItem("ref").getTextContent().replace("t:", ""));
				}
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
		return subElementList;
	}

	private void addElementstoVxsd(Map<String, List<String>> ctNames2ElementsUnderneath, List<String> additionalAssetTags) {
			try {
			// parse the document
			DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
			Document doc = docBuilder.parse(new File("C:/Users/u8de/Downloads/2go/vxsds/INDEX_smf.vxsd"));						
			ctNames2ElementsUnderneath.entrySet().stream().forEach(mapEntry -> {
				enrichDocument(doc, mapEntry);
			});					
		
			enrichDocument(doc, additionalAssetTags);
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			DOMSource source = new DOMSource(doc);
			StreamResult result = new StreamResult(new File("C:/Users/u8de/Downloads/2go/vxsds/INDEX_smf_modified.vxsd"));
			transformer.transform(source, result);

			System.out.println("Done");			
		} catch (Exception ed) {
			ed.printStackTrace();
		}	
	}

	private void enrichDocument(Document doc, Entry<String, List<String>> mapEntry) {
		Element newComplexType = doc.createElement(mapEntry.getKey());
		AtomicInteger order = new AtomicInteger();
		mapEntry.getValue().stream().forEach(element -> {
			Element newElement = doc.createElement(element);	
			newElement.setAttribute("order", ""+ order.incrementAndGet());
			newComplexType.appendChild(newElement);
		});				
		addComplexTypeAttributes(newComplexType);
		doc.getElementsByTagName("ASSET").item(0).appendChild(newComplexType);
	}
	
	private void enrichDocument(Document doc, List<String> additionalTags) {	
		additionalTags.stream().forEach(element -> {
			Element newElement = doc.createElement(element);				
			doc.getElementsByTagName("ASSET").item(0).appendChild(newElement);
		});				
	}


	private void addComplexTypeAttributes(Element newComplexType) {
		newComplexType.setAttribute("complex","true");
		newComplexType.setAttribute("delimiter","|" );
		newComplexType.setAttribute("escapeChar","&quot;" );
		newComplexType.setAttribute("initialFieldsbeforeInserts","true" );
		newComplexType.setAttribute("loadparentbeforesave","true" );
		newComplexType.setAttribute("saveType","1" );
		newComplexType.setAttribute("savename", "index_smf_"
				+ newComplexType.getTagName().toLowerCase()
				+ ".{ODATE}.txt"		);
	}

}
