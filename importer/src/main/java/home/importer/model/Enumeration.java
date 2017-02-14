package home.importer.model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.sdmetrics.model.ModelElement;

import home.importer.service.ImporterService;

public class Enumeration extends Element {

	private List<Property> properties = new ArrayList<Property>();

	public Enumeration(ModelElement umlElement, ImporterService cx) {
		Iterator<ModelElement> enumProp = (Iterator<ModelElement>)umlElement.getSetAttribute("ownedliterals").iterator();
		this.name = umlElement.getName();
		while(enumProp.hasNext()) {
			ModelElement prop = enumProp.next();
			ModelElement eaEl = cx.getEAAttribute(prop.getXMIID());
			Property property = new Property(eaEl);
			properties.add(property);
		}
	}

	public List<Property> getProperties() {
		return properties;
	}

	public void setProperties(List<Property> properties) {
		this.properties = properties;
	}

}
