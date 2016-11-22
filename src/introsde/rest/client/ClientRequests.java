package introsde.rest.client;


import java.io.IOException;
import java.net.URI;    
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;

import org.glassfish.jersey.client.ClientConfig;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

public class ClientRequests {
	
	private ClientConfig clientConfig;
	private Client client;
	private WebTarget service;
	
	
	public ClientRequests() {
		clientConfig = new ClientConfig();
        client = ClientBuilder.newClient(clientConfig);
        service = client.target(getBaseURI());
	}

    protected static URI getBaseURI() {
      //  return UriBuilder.fromUri("http://localhost:5900/sdelab").build();
        return UriBuilder.fromUri("https://warm-dawn-26932.herokuapp.com/sdelab").build();
    }
    
	public Response doGET(String path, String format) throws JsonParseException, JsonMappingException, IOException{
    	
		Response response = null;
		
		if(format.equals("xml")){
			response = service.path(path)
					.request()
					.accept(MediaType.APPLICATION_XML).get();

		}
		else if(format.equals("json")){
			response = service.path(path)
					.request()
					.accept(MediaType.APPLICATION_JSON).get();

		}
		
    	return response;
    	
    }	
	
	public Response doPUT(String path, String request, String format) throws JsonParseException, JsonMappingException, IOException{
    		
		Response response = null;

		if(format.equals("xml")){
			response = service.path(path).
					request(MediaType.APPLICATION_XML_TYPE).put(Entity.xml(request));

		}
		else if(format.equals("json")){
			response = service.path(path).
					request(MediaType.APPLICATION_JSON_TYPE).put(Entity.json(request));
		}
		
    	return response;
    	
    }
	
	public Response doPOST(String path, String request, String format) throws JsonParseException, JsonMappingException, IOException{
    	
		Response response = null;

		if(format.equals("xml")){
			response = service.path(path).
					request(MediaType.APPLICATION_XML_TYPE).post(Entity.xml(request));

		}
		else if(format.equals("json")){
			response = service.path(path).
					request(MediaType.APPLICATION_JSON_TYPE).post(Entity.json(request));
		}
		
    	return response;
    }
	
	//maybe separation not needed
	public Response doDELETE(String path, String format) throws JsonParseException, JsonMappingException, IOException{
    	
		Response response = null;

		if(format.equals("xml")){
			response = service.path(path)
					.request()
					.accept(MediaType.APPLICATION_XML).delete();
		}
		else if(format.equals("json")){
			response = service.path(path)
					.request()
					.accept(MediaType.APPLICATION_JSON).delete();
		}
		
    	return response;
    	
    }
}