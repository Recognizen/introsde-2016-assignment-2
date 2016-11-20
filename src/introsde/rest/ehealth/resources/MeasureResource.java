package introsde.rest.ehealth.resources;

import introsde.rest.ehealth.model.HealthMeasureHistory;
import introsde.rest.ehealth.model.Measure;
import introsde.rest.ehealth.model.MeasureDefinition;
import introsde.rest.ehealth.model.Person;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
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
public class MeasureResource {
	@Context
	UriInfo uriInfo;
	@Context
	Request request;
	
	EntityManager entityManager;
	
	int idPerson;
	String measureType;

	public MeasureResource(UriInfo uriInfo, Request request,int idPerson, String measureType, EntityManager em) {
		this.uriInfo = uriInfo;
		this.request = request;
		this.idPerson = idPerson;
		this.measureType = measureType;
		this.entityManager = em;
	}
	
	public MeasureResource(UriInfo uriInfo, Request request,int idPerson, String measureType) {
		this.uriInfo = uriInfo;
		this.request = request;
		this.idPerson = idPerson;
		this.measureType = measureType;
	}

	@GET
	@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	public List<HealthMeasureHistory> getMeasureHistory() {
		List<HealthMeasureHistory> all = HealthMeasureHistory.getAll();
		List<HealthMeasureHistory> history = new ArrayList<>();
		for(HealthMeasureHistory m : all){
			if(m.getPerson().getIdPerson() == idPerson 
					&& m.getMeasureDefinition().getType().equals(measureType)){
				history.add(m);
			}
		}
		return history;
	}
	
	@GET
	@Path("{mid}")
	@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	public HealthMeasureHistory getMeasurebyId(@PathParam("mid") int mid) {
		HealthMeasureHistory m = null;
		for(HealthMeasureHistory tmp : this.getMeasureHistory()){
			if(tmp.getMid() == mid)
				m = tmp;
		}
		return m;
	}
	
	@POST
	@Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON} )
	@Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	public Person newMeasure(Measure measure) throws IOException {
		System.out.println("Creating new measure..." + idPerson + " "+measureType );

		measure.setMeasureDefinition(MeasureDefinition.getByName(measureType));
		
		//type check if(good type) do else error response
		if(measure.getMeasureDefinition() != null){
			
			Measure existing = null;
			
			Person p = Person.getPersonById(idPerson);
			measure.setPerson(p);
		
			List<Measure> oldMeasures = p.getMeasure();
			for(int i = 0; i < oldMeasures.size(); i++){
				Measure tmp = oldMeasures.get(i);
				if(tmp.getMeasureDefinition().getType().equals(measureType)){
					existing = tmp;
					
					HealthMeasureHistory newEntry = new HealthMeasureHistory();
					newEntry.setPerson(p);
					newEntry.setValue(existing.getValue());
					newEntry.setCreated(new Date());
					newEntry.setMeasureDefinition(existing.getMeasureDefinition());
					
					HealthMeasureHistory.saveHealthMeasureHistory(newEntry);
					Measure.removeLifeStatus(existing);
					
					oldMeasures.remove(existing);
					oldMeasures.add(measure);
				}
			}
			
			if(existing == null)
			{
				oldMeasures.add(measure);
			}
			
			Measure.saveLifeStatus(measure);
			
			return Person.updatePerson(p);
		}
		return null;
	}
}
