package introsde.rest.ehealth.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="measureTypes")
public class MeasureTypes implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private List<String> measureTypes = new ArrayList<String>();
	
	public MeasureTypes(){
		List<MeasureDefinition> types = MeasureDefinition.getAll();
	    
	    for(MeasureDefinition mDef : types )
	    	measureTypes.add(mDef.getType());
	} 
	
	public List<String> getMeasureType() {
		return this.measureTypes;
	}

	public void setMeasureType(List<String> measureTypes) {
		this.measureTypes = measureTypes;
	}
}