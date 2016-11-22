package introsde.rest.ehealth.resources;

import introsde.rest.ehealth.model.HealthMeasureHistory;
import introsde.rest.ehealth.model.Person;

import java.util.List;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;


@Stateless
@LocalBean
public class PersonResource {
	@Context
	UriInfo uriInfo;
	@Context
	Request request;
	
	EntityManager entityManager;
	
	int id;

	public PersonResource(UriInfo uriInfo, Request request,int id, EntityManager em) {
		this.uriInfo = uriInfo;
		this.request = request;
		this.id = id;
		this.entityManager = em;
	}
	
	public PersonResource(UriInfo uriInfo, Request request,int id) {
		this.uriInfo = uriInfo;
		this.request = request;
		this.id = id;
	}
	
	// Application integration
	@GET
	@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON , MediaType.TEXT_XML})
	public Response getPerson() {
		 Person person = Person.getPersonById(id);
		 if (person == null)
			 return Response.status(404).build();
		 return Response.ok().entity(person).build();
	}

	@PUT
	@Consumes({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	public Response putPerson(Person person) {
		System.out.println("--> Updating Person... " +this.id);
		System.out.println("--> "+person.toString());
		//Person.updatePerson(person);
		
		Response res;
		
		Person existing = Person.getPersonById(this.id);
		
		if (existing == null) {
			res = Response.noContent().build();
		} else {
			res = Response.created(uriInfo.getAbsolutePath()).build();
			person.setIdPerson(this.id);
			
			//Checks ensure that fields are not lost during update
			if(person.getFirstname() == null)
				person.setFirstname(existing.getFirstname());
			if(person.getLastname() == null)
				person.setLastname(existing.getLastname());
			if(person.getBirthdate() == null)
				person.setBirthdate(existing.getBirthdate());
			if(person.getEmail() == null)
				person.setEmail(existing.getEmail());
			if(person.getUsername() == null)
				person.setUsername(existing.getUsername());
			
			person.setMeasure(existing.getMeasure());
			
			Person.updatePerson(person);
		}
		return res;
	}

	@DELETE
	public void deletePerson() {
		Person c = Person.getPersonById(id);
		if (c == null)
			throw new RuntimeException("Delete: Person with " + id + " not found");
		
		//Delete the history of the person 
		List<HealthMeasureHistory> history = HealthMeasureHistory.getHealthMeasureHistoryByPersonId(c.getIdPerson());
		for(HealthMeasureHistory m : history)
			HealthMeasureHistory.removeHealthMeasureHistory(m);
		
		Person.removePerson(c);
	}

	//forward the request to further path 
	@Path("{measureType}")
	public MeasureResource getHistory(@PathParam("measureType") String name) {
		return new MeasureResource(uriInfo, request, id, name);
	}
}
