package introsde.rest.client;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.core.Response;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import javax.xml.xpath.XPathExpressionException;

import com.jayway.jsonpath.JsonPath;

import org.json.JSONArray;
import org.json.JSONObject;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

public class ClientMain {
	
	private PrintWriter writerXML;
	private PrintWriter writerJSON;
	
	private XPathQueries queryXML;
    private ClientRequests client;
    
    private int first_person_id;
    private int last_person_id;

	public ClientMain(){		
		client = new ClientRequests();   
		// Print URL
		System.out.println("URL of the server: " + ClientRequests.getBaseURI());
		
		try {		
			//----------------------- Task 2 ---------------------------
			this.xmlRun();
			this.jsonRun();
		} catch (XPathExpressionException | IOException | ParserConfigurationException | SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

    public static void main(String[] args) throws JsonParseException, JsonMappingException, IOException {
    	new ClientMain();
    }
	
	public void xmlRun() throws JsonParseException, JsonMappingException, IOException, XPathExpressionException, ParserConfigurationException, SAXException{
		
		final String format = "xml";
		//utility class, provides xPath queries
		queryXML = new XPathQueries();
		writerXML = new PrintWriter("client-server-xml.log", "UTF-8");
		
		//-------------------- Task 1 ---------------------
		
		writerXML.write("URL of the server: " + ClientRequests.getBaseURI());

		
		//-------------------- Task 3.1 --------------------
		
		
		System.out.println("\nXML:-------------------- Task 3.1 --------------------\n");
		writerXML.write("\n-------------------- Task 3.1 --------------------\n");
		
		String request = "#1 GET /person Accept: APPLICATION/XML Content-Type: APPLICATION/XML";
		Response response = client.doGET("person", format);
		String body = response.readEntity(String.class);
		
		//Get first and last ids from XPath nodes
		NodeList peopleIDs = queryXML.getPersonIDs(body);
		first_person_id = Integer.parseInt(peopleIDs.item(0).getTextContent());
		last_person_id = Integer.parseInt(peopleIDs.item(peopleIDs.getLength() - 1).getTextContent());

		String reqResult = ((peopleIDs.getLength() < 3) ? "ERROR" : "OK");

		System.out.println(printResult(request, response.getStatus(), reqResult, body, format));
		writerXML.write(printResult(request, response.getStatus(), reqResult, body, format));
		
		
		// -------------------- Task 3.2 --------------------
		
		
		System.out.println("\nXML:-------------------- Task 3.2 --------------------\n");
		writerXML.write("\n-------------------- Task 3.2 --------------------\n");
		
		request = "#2 GET /person/" + first_person_id + " Accept: APPLICATION/XML Content-Type: APPLICATION/XML";
		response = client.doGET("person/" + first_person_id, format);
		body = response.readEntity(String.class);

		reqResult = ((response.getStatus() == 200 || (response.getStatus() == 201) ? "OK" : "ERROR"));

		System.out.println(printResult(request, response.getStatus(), reqResult, body, format));
		writerXML.write(printResult(request, response.getStatus(), reqResult, body, format));
		
		
		// -------------------- Task 3.3 --------------------
		
		
		System.out.println("\nXML:-------------------- Task 3.3 --------------------\n");
		writerXML.write("\n-------------------- Task 3.3 --------------------\n");
		
		//Reprinting previous GET, no need to redo it 
		System.out.println(printResult(request, response.getStatus(), reqResult, body, format));
		writerXML.write(printResult(request, response.getStatus(), reqResult, body, format));

		//Save the first name of the person returned by 3.2
		String oldFirstName = queryXML.getNodeResult("person/firstname", body).getTextContent();

		request = "#3 PUT /person/" + first_person_id + " Accept: APPLICATION/XML Content-Type: APPLICATION/XML";

		// BODY of the PUT request
		String requestBody = "<person>"
								+ "<firstname>a" + oldFirstName + "a</firstname>"
								+ "<healthProfile></healthProfile>"
							+ "</person>";

		//PUT request to update the person with first_person_id's firstname
		response = client.doPUT("person/" + first_person_id, requestBody, format);

		//REDO GET to see if updated
		Response responseGET = client.doGET("person/" + first_person_id, format);
		String getBody = responseGET.readEntity(String.class);
		//Save the first name of the updated person
		String newFirstName = queryXML.getNodeResult("person/firstname", getBody).getTextContent();

		//if PUT request was successful and the name is actually changed then OK
		reqResult = ((response.getStatus() == 201 && !oldFirstName.equals(newFirstName) ? "OK" : "ERROR"));
		
		//Printing body empty because PUT response does not return a body
		System.out.println(printResult(request, response.getStatus(), reqResult, "", format));
		writerXML.write(printResult(request, response.getStatus(), reqResult, "", format));
		
		//reset the GET request header
		request = "#2 GET /person/" + first_person_id + " Accept: APPLICATION/JSON Content-Type: APPLICATION/JSON";
		
		//Printing the second GET
		System.out.println(printResult(request, responseGET.getStatus(), "OK", getBody, format));
		writerXML.write(printResult(request, responseGET.getStatus(), "OK", getBody, format));
		
		// -------------------- Task 3.4 --------------------
		
		
		System.out.println("\nXML:-------------------- Task 3.4 --------------------\n");
		writerXML.write("\n-------------------- Task 3.4 --------------------\n");
		
		request = "#4 POST /person Accept: APPLICATION/XML Content-Type: APPLICATION/XML";

		requestBody = "<person>" 
							+ "<firstname>New</firstname>"
							+ "<lastname>Person</lastname>"
							+ "<birthdate>1978-09-01T23:00:00+01:00</birthdate>"
							+ "<email>new.person@gmail.com</email>"
							+ "<username>new.person</username>" 
							+ "<healthProfile>"
								+ "<measure>" 
									+ "<value>72.3</value>" 
									+ "<measureDefinition>"
										+ "<type>weight</type>"
									+ "</measureDefinition>" 
								+ "</measure>"
								+ "<measure>" 
									+ "<value>1.86</value>" 
									+ "<measureDefinition>"
										+ "<type>height</type>"
									+ "</measureDefinition>" 
								+ "</measure>"
							+ "</healthProfile>"
						+ "</person>";

		//Send POST request with above body
		response = client.doPOST("person", requestBody, format);
		body = response.readEntity(String.class);	
		int resultPost = response.getStatus();
		
		//Store the idPerson of the generated person
		String newIdPerson = queryXML.getNodeResult("person/idPerson", body).getTextContent();
		
		reqResult = ((resultPost == 200 || resultPost == 201 || resultPost == 202) 
				&& !newIdPerson.isEmpty() ? "OK" : "ERROR");

		System.out.println(printResult(request, resultPost, reqResult, body, format));
		writerXML.write(printResult(request, resultPost, reqResult, body, format));
		
		
		// -------------------- Task 3.5 --------------------
		
		
		System.out.println("\nXML:-------------------- Task 3.5 --------------------\n");
		writerXML.write("\n-------------------- Task 3.5 --------------------\n");
		
		request = "#5 DELETE /person/" + newIdPerson + " Accept: APPLICATION/XML Content-Type: APPLICATION/XML";
		response = client.doDELETE("person/"+ newIdPerson, format);

		String request2 = "#2 GET /person/" + newIdPerson	+ " Accept: APPLICATION/XML Content-Type: APPLICATION/XML";
		Response response2 = client.doGET("person/"+ newIdPerson, format);
		
		reqResult = (response.getStatus() == 204 && response2.getStatus() == 404 ? "OK" : "ERROR");
		
		System.out.println(printResult(request, response.getStatus(), reqResult, "", format));
		writerXML.write(printResult(request, response.getStatus(), reqResult, "", format));

		System.out.println(printResult(request2, response2.getStatus(), reqResult, "", format));
		writerXML.write(printResult(request2, response2.getStatus(), reqResult, "", format));
	
		
		
		// -------------------- Task 3.6 --------------------
		
		
		System.out.println("\nXML:-------------------- Task 3.6 --------------------\n");
		writerXML.write("\n-------------------- Task 3.6 --------------------\n");
		
		request = "#9 GET /measureTypes Accept: APPLICATION/XML Content-Type: APPLICATION/XML";

		response = client.doGET("measureTypes", format);
		body = response.readEntity(String.class);

		//Retrieve all measureType elements from the response body
		NodeList types = queryXML.getNodeListResult("measureTypes/measureType", body);
		List<String> measure_types = new ArrayList<>();
		//save them in a list
		for (int i = 0; i < types.getLength(); i++) {
			measure_types.add(types.item(i).getTextContent());
		}
		
		reqResult = ((measure_types.size() < 3) ? "ERROR" : "OK");

		System.out.println(printResult(request, response.getStatus(), reqResult, body, format));
		writerXML.write(printResult(request, response.getStatus(), reqResult, body, format));
		
		
		// -------------------- Task 3.7 --------------------
		
		
		System.out.println("\nXML:-------------------- Task 3.7 --------------------\n");
		writerXML.write("\n-------------------- Task 3.7 --------------------\n");
		
		//to be used in Task 3.8
		int measurePersonId = 0;
		String measure_id ="";
		String measure_type ="";
		
		//keep track of number measures found
		int measureCount = 0;
		
		//Structure used to Nest the two id loops
		List<Integer> ids = new ArrayList<>();
		ids.add(first_person_id);
		ids.add(last_person_id);
		
		//Iterate over people and measuretypes in search for at least one measure
		for(Integer i : ids){
			for (String measureType : measure_types) {
				//do the get requests
				response = client.doGET("person/"+ i.intValue() + "/"+ measureType, format);
				body = response.readEntity(String.class);
	
				if (response.getStatus() == 200) {
					NodeList nodes = queryXML.getNodeListResult("healthMeasureHistories/measure/mid" , body);
					
					//This will ensure that we keep at least one measure for Task 3.8
					if (nodes.getLength() > 0) {
						measurePersonId = i.intValue();
						measure_id = nodes.item(0).getTextContent();
						measure_type = measureType;
						measureCount+=nodes.getLength();
					}
				}
			}
		}
		
		//if at least one measure was found among the two people then OK 
		if (measureCount == 0) {
			reqResult = "ERROR";
			request = "#6 GET /person/{id}/{measureType} Accept: APPLICATION/XML Content-Type: APPLICATION/XML";
			System.out.println(printResult(request, 204 , reqResult, "", format));
			writerXML.write(printResult(request, 204 , reqResult, "", format));
		} 
		else {
			reqResult = "OK";
			//REDO the GET requests in order to log them
			for(Integer i : ids){
				for (String measureType : measure_types) {
					request = "#6 GET /person/"+ i.intValue() +"/" + measureType +" Accept: APPLICATION/XML Content-Type: APPLICATION/XML";
					response = client.doGET("person/"+ i.intValue() + "/"+ measureType, format);
					body = response.readEntity(String.class);
					
					//if something was found then print the body as well, otherwise content not found
					if (response.getStatus() == 200) {
						System.out.println(printResult(request, response.getStatus(), reqResult, body, format));
						writerXML.write(printResult(request, response.getStatus(), reqResult, body, format));
					} else {
						System.out.println(printResult(request, 204 , reqResult, "", format));
						writerXML.write(printResult(request, 204 , reqResult, "", format));
					}
				}
			}
		}	
		//System.out.println("Found measures: "+measureCount);
		
		
		// -------------------- Task 3.8 --------------------
		
		
		System.out.println("\nXML:-------------------- Task 3.8 --------------------\n");
		writerXML.write("\n-------------------- Task 3.8 --------------------\n");
		
		
		request = "#7 GET /person/" + measurePersonId 
				+ "/" + measure_type
				+ "/" + measure_id
				+ " Accept: APPLICATION/XML Content-Type: APPLICATION/XML";

		response = client.doGET("person/" + measurePersonId + "/" + measure_type + "/" + measure_id , format);
		body = response.readEntity(String.class);

		reqResult = (response.getStatus() == 200 ? "OK" : "ERROR");

		System.out.println(printResult(request, response.getStatus(), reqResult, body, format));
		writerXML.write(printResult(request, response.getStatus(), reqResult, body, format));
		
		
		// -------------------- Task 3.9 --------------------
		
		
		System.out.println("\nXML:-------------------- Task 3.9 --------------------\n");
		writerXML.write("\n-------------------- Task 3.9 --------------------\n");
		
		request = "#6 GET /person/" + measurePersonId + "/" + measure_type
				+ " Accept: APPLICATION/XML Content-Type: APPLICATION/XML";

		//GET all measures having type
		response = client.doGET("person/"+ measurePersonId + "/"+ measure_type, format);
		body = response.readEntity(String.class);
		
		//store how many measures there were
		measureCount = 0;
		if (response.getStatus() == 200) {
			NodeList nodes = queryXML.getNodeListResult("healthMeasureHistories/measure/mid" , body);
			measureCount = nodes.getLength();
			
			String requestPost = "#8 POST /person/" + measurePersonId + "/" + measure_type
								+ " Accept: APPLICATION/XML Content-Type: APPLICATION/XML";

			requestBody = "<measure>" 
							+ "<value>102</value>"
							+ "<created>2011-12-09</created>" 
						+ "</measure>";
			
			//POST request adding a measure
			Response responsePost = client.doPOST("person/" + measurePersonId + "/" + measure_type, requestBody, format);	
			String postBody = responsePost.readEntity(String.class);

			//Redo the GET
			responseGET = client.doGET("person/"+ measurePersonId + "/"+ measure_type, format);
			getBody = responseGET.readEntity(String.class);
			//Find new number of measures
			NodeList nodes2 = queryXML.getNodeListResult("healthMeasureHistories/measure/mid", getBody);
			int newMeasureCount = nodes2.getLength();

			//System.out.println(measureCount +" <? " + newMeasureCount);
			reqResult = (measureCount+1 == newMeasureCount ? "OK" : "ERROR");

			System.out.println(printResult(request, response.getStatus(), "OK", body, format));
			writerXML.write(printResult(request, response.getStatus(), "OK", body, format));
			
			System.out.println(printResult(requestPost, responsePost.getStatus(), reqResult, postBody, format));
			writerXML.write(printResult(requestPost, responsePost.getStatus(), reqResult, postBody, format));
			
			System.out.println(printResult(request, responseGET.getStatus(), "OK", getBody, format));
			writerXML.write(printResult(request, responseGET.getStatus(), "OK", getBody, format));
			
			
			// -------------------- Task 3.10 --------------------
			
			
			System.out.println("\nXML:-------------------- Task 3.10 --------------------\n");
			writerXML.write("\n-------------------- Task 3.10 --------------------\n");

			//Using a R#6 to retrieve the measure with specific mid = measure_id
			String requestGET = "#6 GET /person/" + measurePersonId + "/"
					+ measure_type+ "/" 
					+ measure_id
					+ " Accept: APPLICATION/XML Content-Type: APPLICATION/XML";
			
			responseGET = client.doGET("person/" + measurePersonId + "/" + measure_type + "/" + measure_id, format);
			body = responseGET.readEntity(String.class);	
			String oldValue = queryXML.getNodeResult("measure/value",body).getTextContent();
			
			request = "#10 PUT /person/" + measurePersonId + "/" + measure_type
					+ "/" + measure_id
					+ " Accept: APPLICATION/XML Content-Type: APPLICATION/XML";

			requestBody = "<measure>" 
							+ "<value>"+(oldValue+1)+"</value>"
							+ "<created>2011-12-09</created>" 
						+ "</measure>";

			response = client.doPUT("person/" + measurePersonId + "/" + measure_type + "/" + measure_id, requestBody, format);
			int statusPUT = response.getStatus();
			
			//REDO the GET
			responseGET = client.doGET("person/" + measurePersonId + "/" + measure_type + "/" + measure_id, format);
			getBody = responseGET.readEntity(String.class);	
			String newValue = queryXML.getNodeResult("measure/value",getBody).getTextContent();

			reqResult = (!oldValue.equals(newValue) && statusPUT == 201 ? "OK" : "ERROR");

			//responseGET and body belong to the first GET request
			System.out.println(printResult(requestGET, responseGET.getStatus(), "OK", body, format));
			writerXML.write(printResult(requestGET, responseGET.getStatus(), "OK", body, format));

			System.out.println(printResult(request, statusPUT, reqResult, "", format));
			writerXML.write(printResult(request, statusPUT, reqResult, "", format));
			
			//responseGET and getBody belong to the second GET request
			System.out.println(printResult(requestGET, responseGET.getStatus(), "OK", getBody, format));
			writerXML.write(printResult(requestGET, responseGET.getStatus(), "OK", getBody, format));
			
			// -------------------- Task 3.11 & 3.12 --------------------
			// Not implemented
			
		}
		writerXML.close();
	}
	
	public void jsonRun() throws JsonParseException, JsonMappingException, IOException{
		
		final String format = "json";

		writerJSON = new PrintWriter("client-server-json.log", "UTF-8");
		
		// -----------------------Task1---------------------------------
		writerJSON.write("URL of the server: " + ClientRequests.getBaseURI());


		
		//-------------------- Task 3.1 --------------------		
		
		System.out.println("\nJSON:-------------------- Task 3.1 --------------------\n");
		writerJSON.write("\n-------------------- Task 3.1 --------------------\n");
		
		String request = "#1 GET /person Accept: APPLICATION/JSON Content-Type: APPLICATION/JSON";
		Response response = client.doGET("person", format);
		String body = response.readEntity(String.class);
		
		//Get first and last ids from JSON body
		first_person_id = JsonPath.read(body, "$.[0].idPerson");
		last_person_id = JsonPath.read(body, "$.[(@.length-1)].idPerson");

		JSONArray people = new JSONArray(body);		
		String reqResult = ((people.length() < 3) ? "ERROR" : "OK");

		System.out.println(printResult(request, response.getStatus(), reqResult, body, format));
		writerJSON.write(printResult(request, response.getStatus(), reqResult, body, format));
		
		// -------------------- Task 3.2 --------------------
		
		
		System.out.println("\nJSON:-------------------- Task 3.2 --------------------\n");
		writerJSON.write("\n-------------------- Task 3.2 --------------------\n");
		
		request = "#2 GET /person/" + first_person_id + " Accept: APPLICATION/JSON Content-Type: APPLICATION/JSON";
		response = client.doGET("person/" + first_person_id, format);
		body = response.readEntity(String.class);

		reqResult = ((response.getStatus() == 200 || (response.getStatus() == 201) ? "OK" : "ERROR"));

		System.out.println(printResult(request, response.getStatus(), reqResult, body, format));
		writerJSON.write(printResult(request, response.getStatus(), reqResult, body, format));
	
		
		// -------------------- Task 3.3 --------------------
		
		
		System.out.println("\nJSON:-------------------- Task 3.3 --------------------\n");
		writerJSON.write("\n-------------------- Task 3.3 --------------------\n");
		
		//Reprinting previous GET, no need to redo it 
		System.out.println(printResult(request, response.getStatus(), reqResult, body, format));
		writerJSON.write(printResult(request, response.getStatus(), reqResult, body, format));

		//Save the firstname of the person returned by 3.2
		String oldFirstName =  JsonPath.read(body, "$.firstname");
		
		request = "#3 PUT /person/" + first_person_id + " Accept: APPLICATION/JSON Content-Type: APPLICATION/JSON";
		// BODY of the PUT request
		String requestBody ="{" 
								+ "\"firstname\" : \"a"+ oldFirstName +"a\","
								+ "\"measure\": []"
							+"}";

		//PUT request to update the person with first_person_id's firstname
		response = client.doPUT("person/" + first_person_id, requestBody, format);

		//REDO GET to see if updated
		Response responseGET = client.doGET("person/" + first_person_id, format);
		body = responseGET.readEntity(String.class);
		//Save the first name of the updated person
		String newFirstName = JsonPath.read(body, "$.firstname");

		//if PUT request was successful and the name is actually changed then OK
		reqResult = ((response.getStatus() == 201 && !oldFirstName.equals(newFirstName) ? "OK" : "ERROR"));
		
		//Printing body empty because PUT response does not return a body
		System.out.println(printResult(request, response.getStatus(), reqResult, "",format));
		writerJSON.write(printResult(request, response.getStatus(), reqResult, "",format));
		
		//reset the GET request header
		request = "#2 GET /person/" + first_person_id + " Accept: APPLICATION/JSON Content-Type: APPLICATION/JSON";
		
		//Printing the second GET
		System.out.println(printResult(request, responseGET.getStatus(), "OK", body,format));
		writerJSON.write(printResult(request, responseGET.getStatus(), "OK", body,format));
		
		// -------------------- Task 3.4 --------------------
		
		
		System.out.println("\nJSON:-------------------- Task 3.4 --------------------\n");
		writerJSON.write("\n-------------------- Task 3.4 --------------------\n");
		
		request = "#4 POST /person Accept: APPLICATION/JSON Content-Type: APPLICATION/JSON";
		
		requestBody ="{" 
						+ "\"firstname\" : \"New\","	
						+ "\"lastname\" : \"Person\","
						+ "\"birthdate\" : \"1978-09-01\","
						+ "\"email\" : \"new.person@gmail.com\","
						+ "\"measure\": ["
						+ "{"
							+ "\"value\" : \"72.3\","
							+ "\"measureDefinition\" : {"
								+ "\"type\": \"weight\""
							+"}"
						+ "},"
						+ "{"
							+ "\"value\" : \"1.86\","
							+ "\"measureDefinition\" : {"
								+ "\"type\": \"height\""
							+"}"
						+ "}"
					+ "]}";
		
		//Send POST request with above body
		response = client.doPOST("person", requestBody, format);
		body = response.readEntity(String.class);	
		
		int resultPost = response.getStatus();
		
		//Store the idPerson of the generated person
		String newIdPerson = JsonPath.read(body, "$.idPerson").toString();
		
		System.out.println(newIdPerson);
		
		reqResult = ((resultPost == 200 || resultPost == 201 || resultPost == 202) 
				&& !newIdPerson.isEmpty() ? "OK" : "ERROR");

		System.out.println(printResult(request, resultPost, reqResult, body, format));
		writerJSON.write(printResult(request, resultPost, reqResult, body, format));
		
		// -------------------- Task 3.5 --------------------
		
		
		System.out.println("\nJSON:-------------------- Task 3.5 --------------------\n");
		writerJSON.write("\n-------------------- Task 3.5 --------------------\n");
		
		request = "#5 DELETE /person/" + newIdPerson + " Accept: APPLICATION/JSON Content-Type: APPLICATION/JSON";
		response = client.doDELETE("person/"+ newIdPerson, format);

		String request2 = "#2 GET /person/" + newIdPerson	+ " Accept: APPLICATION/JSON Content-Type: APPLICATION/JSON";
		Response response2 = client.doGET("person/"+ newIdPerson, format);
		
		reqResult = (response.getStatus() == 204 && response2.getStatus() == 404 ? "OK" : "ERROR");
		
		System.out.println(printResult(request, response.getStatus(), reqResult, "", format));
		writerJSON.write(printResult(request, response.getStatus(), reqResult, "", format));

		System.out.println(printResult(request2, response2.getStatus(), reqResult, "", format));
		writerJSON.write(printResult(request2, response2.getStatus(), reqResult, "", format));
	
		
		
		// -------------------- Task 3.6 --------------------
		
		
		System.out.println("\nJSON:-------------------- Task 3.6 --------------------\n");
		writerJSON.write("\n-------------------- Task 3.6 --------------------\n");
		
		request = "#9 GET /measureTypes Accept: APPLICATION/JSON Content-Type: APPLICATION/JSON";

		response = client.doGET("measureTypes", format);
		body = response.readEntity(String.class);

		//Retrieve all measureType elements from the response body
		JSONArray types = new JSONObject(body).getJSONArray("measureType");
		List<String> measure_types = new ArrayList<>();
		for(int i =0; i< types.length();i++){
			measure_types.add(types.getString(i));
		}

		reqResult = ((measure_types.size() < 3) ? "ERROR" : "OK");

		System.out.println(printResult(request, response.getStatus(), reqResult, body, format));
		writerJSON.write(printResult(request, response.getStatus(), reqResult, body, format));
		
		
		// -------------------- Task 3.7 --------------------
		
		
		System.out.println("\nJSON:-------------------- Task 3.7 --------------------\n");
		writerJSON.write("\n-------------------- Task 3.7 --------------------\n");
		
		//to be used in Task 3.8
		int measurePersonId = 0;
		String measure_id ="";
		String measure_type ="";
		
		//keep track of number measures found
		int measureCount = 0;
		
		//Structure used to Nest the two id loops
		List<Integer> ids = new ArrayList<>();
		ids.add(first_person_id);
		ids.add(last_person_id);
		
		//Iterate over people and measuretypes in search for at least one measure
		for(Integer i : ids){
			for (String measureType : measure_types) {
				//do the get requests
				response = client.doGET("person/"+ i.intValue() + "/"+ measureType, format);
				body = response.readEntity(String.class);
	
				if (response.getStatus() == 200) {
					JSONArray measures = new JSONArray(body);

					//This will ensure that we keep at least one measure for Task 3.8
					if(measures.length()>0){
						measurePersonId = i.intValue();
						measure_id = ((JSONObject)measures.get(0)).getInt("mid")+"";
						measure_type = measureType;
						measureCount += measures.length();
					//	System.out.println(i.toString() +" : "+ measure_id + " " + measure_type +" "+measureCount);
					}
				}
			}
		}
		
		//if at least one measure was found among the two people then OK 
		if (measureCount == 0) {
			reqResult = "ERROR";
			request = "#6 GET /person/{id}/{measureType} Accept: APPLICATION/JSON Content-Type: APPLICATION/JSON";
			System.out.println(printResult(request, 204 , reqResult, "", format));
			writerJSON.write(printResult(request, 204 , reqResult, "", format));
		} 
		else {
			reqResult = "OK";
			//REDO the GET requests in order to log them
			for(Integer i : ids){
				for (String measureType : measure_types) {
					request = "#6 GET /person/"+ i.intValue() +"/" + measureType +" Accept: APPLICATION/JSON Content-Type: APPLICATION/JSON";
					response = client.doGET("person/"+ i.intValue() + "/"+ measureType, format);
					body = response.readEntity(String.class);
					
					//if something was found then print the body as well, otherwise content not found
					if (response.getStatus() == 200) {
						System.out.println(printResult(request, response.getStatus(), reqResult, body, format));
						writerJSON.write(printResult(request, response.getStatus(), reqResult, body, format));
					} else {
						System.out.println(printResult(request, 204 , reqResult, "", format));
						writerJSON.write(printResult(request, 204 , reqResult, "", format));
					}
				}
			}
		}	
		
		// -------------------- Task 3.8 --------------------
		
		
		System.out.println("\nJSON:-------------------- Task 3.8 --------------------\n");
		writerJSON.write("\n-------------------- Task 3.8 --------------------\n");
		
		
		request = "#7 GET /person/" + measurePersonId 
				+ "/" + measure_type
				+ "/" + measure_id
				+ " Accept: APPLICATION/JSON Content-Type: APPLICATION/JSON";

		response = client.doGET("person/" + measurePersonId + "/" + measure_type + "/" + measure_id , format);
		body = response.readEntity(String.class);

		reqResult = (response.getStatus() == 200 ? "OK" : "ERROR");

		System.out.println(printResult(request, response.getStatus(), reqResult, body, format));
		writerJSON.write(printResult(request, response.getStatus(), reqResult, body, format));
		
		
		// -------------------- Task 3.9 --------------------
		
		
		System.out.println("\nJSON:-------------------- Task 3.9 --------------------\n");
		writerJSON.write("\n-------------------- Task 3.9 --------------------\n");
		
		request = "#6 GET /person/" + measurePersonId + "/" + measure_type
				+ " Accept: APPLICATION/JSON Content-Type: APPLICATION/JSON";

		//GET all measures having type
		response = client.doGET("person/"+ measurePersonId + "/"+ measure_type, format);
		body = response.readEntity(String.class);
		
		//store how many measures there were
		measureCount = 0;
		if (response.getStatus() == 200) {
			JSONArray measuresBefore = new JSONArray(body);
			measureCount = measuresBefore.length();
			
			String requestPost = "#8 POST /person/" + measurePersonId + "/" + measure_type
								+ " Accept: APPLICATION/JSON Content-Type: APPLICATION/JSON";

			requestBody = "{ \"value\":\"102\","
							+ "\"created\":\"1950-10-10\"}";
			
			//POST request adding a measure
			Response responsePost = client.doPOST("person/" + measurePersonId + "/" + measure_type, requestBody, format);	
			String postBody = responsePost.readEntity(String.class);

			//Redo the GET
			responseGET = client.doGET("person/"+ measurePersonId + "/"+ measure_type, format);
			String getBody = responseGET.readEntity(String.class);
			//Find new number of measures
			JSONArray measuresAfter = new JSONArray(getBody);
			int newMeasureCount = measuresAfter.length();

			//System.out.println(measureCount +" <? " + newMeasureCount);
			reqResult = (measureCount+1 == newMeasureCount ? "OK" : "ERROR");

			System.out.println(printResult(request, response.getStatus(), "OK", body, format));
			writerJSON.write(printResult(request, response.getStatus(), "OK", body, format));
			
			System.out.println(printResult(requestPost, responsePost.getStatus(), reqResult, postBody, format));
			writerJSON.write(printResult(requestPost, responsePost.getStatus(), reqResult, postBody, format));
			
			System.out.println(printResult(request, responseGET.getStatus(), "OK", getBody, format));
			writerJSON.write(printResult(request, responseGET.getStatus(), "OK", getBody, format));	
			
			
			// -------------------- Task 3.10 --------------------
			
			
			System.out.println("\nJSON:-------------------- Task 3.10 --------------------\n");
			writerXML.write("\n-------------------- Task 3.10 --------------------\n");
			
			//Using a R#6 to retrieve the measure with specific mid = measure_id
			String requestGET = "#6 GET /person/" + measurePersonId + "/"
					+ measure_type + "/" 
					+ measure_id
					+ " Accept: APPLICATION/JSON Content-Type: APPLICATION/JSON";
			
			responseGET = client.doGET("person/" + measurePersonId + "/" + measure_type + "/" + measure_id, format);
			body = responseGET.readEntity(String.class);	
			String oldValue = JsonPath.read(body, "$.value");
			
			request = "#10 PUT /person/" + measurePersonId + "/" + measure_type
					+ "/" + measure_id
					+ " Accept: APPLICATION/JSON Content-Type: APPLICATION/JSON";

			requestBody =  "{"+
					  			"\"value\": \""+(oldValue+1)+"\","
					  			+"\"created\": \"2011-12-08\""
				  			+ "}";

			response = client.doPUT("person/" + measurePersonId + "/" + measure_type + "/" + measure_id, requestBody, format);
			int statusPUT = response.getStatus();
			
			responseGET = client.doGET("person/" + measurePersonId + "/" + measure_type + "/" + measure_id, format);
			getBody = responseGET.readEntity(String.class);	
			String newValue = JsonPath.read(getBody, "$.value");

			reqResult = (!oldValue.equals(newValue) && statusPUT == 201 ? "OK" : "ERROR");

			//responseGET and body belong to the first GET request
			System.out.println(printResult(requestGET, responseGET.getStatus(), "OK", body, format));
			writerXML.write(printResult(requestGET, responseGET.getStatus(), "OK", body, format));

			System.out.println(printResult(request, statusPUT, reqResult, "", format));
			writerXML.write(printResult(request, statusPUT, reqResult, "", format));
			
			//responseGET and getBody belong to the second GET request
			System.out.println(printResult(requestGET, responseGET.getStatus(), "OK", getBody, format));
			writerXML.write(printResult(requestGET, responseGET.getStatus(), "OK", getBody, format));
			
			// -------------------- Task 3.11 & 3.12 --------------------
			// Not implemented
		
		}
		writerJSON.close();
	}
    
	public String printResult(String request, int status, String result, String body, String format) throws JsonParseException, JsonMappingException, IOException {
		String craftedResult = "\nRequest: " 
				+ request + "\n" 
				+ "=>Result: " + result + "\n"
				+ "=>HTTP Status: " + status + "\n" ;
		
		craftedResult += (format.equals("xml")) ? prettyXML(body) : prettyJSON(body);
		
		return craftedResult;
	}
	
	public static String prettyJSON(String input) throws JsonParseException,
			JsonMappingException, IOException {

    	if(input != null && !input.isEmpty()){
			ObjectMapper mapper = new ObjectMapper();
			mapper.enable(SerializationFeature.INDENT_OUTPUT);
	
			Object json = mapper.readValue(input, Object.class);
			String indented = mapper.writerWithDefaultPrettyPrinter()
					.writeValueAsString(json);
			return indented;
    	}
    	return " ";
	}
	
	public static String prettyXML(String input) {
		int indent = 2;
	    try {
	    	if(input != null && !input.isEmpty()){
		        Source xmlInput = new StreamSource(new StringReader(input));
		        StringWriter stringWriter = new StringWriter();
		        StreamResult xmlOutput = new StreamResult(stringWriter);
		        TransformerFactory transformerFactory = TransformerFactory.newInstance();
		        transformerFactory.setAttribute("indent-number", indent);
		        Transformer transformer = transformerFactory.newTransformer(); 
		        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
		        transformer.transform(xmlInput, xmlOutput);
		        return xmlOutput.getWriter().toString();
	        }
	    	return " ";
	    } catch (Exception e) {
	        throw new RuntimeException(e); // simple exception handling, please review it
	    }
	}
}
