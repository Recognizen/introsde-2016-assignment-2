package introsde.rest.ehealth.resources;

import introsde.rest.ehealth.model.Measure;
import introsde.rest.ehealth.model.MeasureDefinition;
import introsde.rest.ehealth.model.Person;

import java.io.IOException;
import java.util.Date;
import java.util.List;

import javax.ejb.*;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceContextType;
import javax.persistence.PersistenceUnit;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.UriInfo;

@Stateless
@LocalBean
@Path("/person")
public class PersonCollectionResource {

	// Allows to insert contextual objects into the class,
	// e.g. ServletContext, Request, Response, UriInfo
	@Context
	UriInfo uriInfo;
	@Context
	Request request;

	// THIS IS NOT WORKING
	@PersistenceUnit(unitName="introsde-jpa")
	EntityManager entityManager;
	
	// THIS IS NOT WORKING
    @PersistenceContext(unitName = "introsde-jpa",type=PersistenceContextType.TRANSACTION)
    private EntityManagerFactory entityManagerFactory;

	// Return the list of people to the user in the browser
	@GET
	@Produces({MediaType.TEXT_XML,  MediaType.APPLICATION_JSON ,  MediaType.APPLICATION_XML })
	public List<Person> getPersonsBrowser() {
		System.out.println("Getting list of people...");
	    List<Person> people = Person.getAll();
		return people;
	}

	@GET
	@Path("count")
	@Produces(MediaType.TEXT_PLAIN)
	public String getCount() {
		System.out.println("Getting count...");
	    List<Person> people = Person.getAll();
		int count = people.size();
		return String.valueOf(count);
	}

	@POST
	@Produces({MediaType.APPLICATION_XML,MediaType.APPLICATION_JSON})
	@Consumes({MediaType.APPLICATION_XML,MediaType.APPLICATION_JSON})
	public Person newPerson(Person person) throws IOException {
		System.out.println("Creating new person...");
		List<Measure> measures = person.getMeasure();
		
		if(!measures.isEmpty()){
			for(Measure m : measures)
			{
				//set the idPerson in the measure table
				m.setPerson(person);
				if(m.getCreated() == null)
					m.setCreated(new Date());
				
				//find a pre-defined MeasureDefinition type
				MeasureDefinition mDef = MeasureDefinition.getByName(m.getMeasureDefinition().getType());
				
				//if the measure does not have a correct type defined then remove it
				if(mDef != null){
					m.setMeasureDefinition(mDef);
				}else{
					//this will stop the request from being processed and the person is not saved in the database
					measures.remove(m);
					
					//change and return bad request
				}
			}
		}		
		return Person.savePerson(person);
	}
	
	//Forward the request to PersonResource
	@Path("{personId}")
	public PersonResource getPerson(@PathParam("personId") int id) {
		return new PersonResource(uriInfo, request, id);
	}
}
