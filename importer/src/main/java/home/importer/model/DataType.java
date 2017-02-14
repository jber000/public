package home.importer.model;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.sdmetrics.model.ModelElement;

import home.importer.service.ImporterService;

public class DataType extends Element {

	private List<Property> properties = new ArrayList<Property>();
	private String dataTypeDefinition;
	private String constraint;
	private Primitive primitive;
	private Enumeration enumeration;
	private Boolean isGeneralization;
	private Boolean isEnumeration;

	public DataType() {}

	public void init(ModelElement umlElement, ModelElement eaElement, ImporterService cx) {
		this.name = eaElement.getName();
		this.id = umlElement.getXMIID();
		this.dataTypeDefinition = eaElement.getPlainAttribute("documentation");
		this.constraint = eaElement.getPlainAttribute("constraint");
		this.isGeneralization = !umlElement.getSetAttribute("generalizations").isEmpty();
		this.isEnumeration = StringUtils.equalsIgnoreCase(eaElement.getPlainAttribute("stereotype"), "enumeration");

		if(this.isGeneralization) {
			String primitiveId = eaElement.getPlainAttribute("generalizationId");
			ModelElement primitive = cx.getEAElement(primitiveId);
			this.primitive = new Primitive(primitive);
		}

		if(this.isEnumeration) {
			this.enumeration = new Enumeration(umlElement, cx);
		}
	}

	public List<Property> getProperties() {
		return properties;
	}

	public void setProperties(List<Property> properties) {
		this.properties = properties;
	}

	public String getDataTypeDefinition() {
		return dataTypeDefinition;
	}

	public void setDataTypeDefinition(String dataTypeDefinition) {
		this.dataTypeDefinition = dataTypeDefinition;
	}

	public String getConstraint() {
		return constraint;
	}

	public void setConstraint(String constraint) {
		this.constraint = constraint;
	}

	public Primitive getPrimitive() {
		return primitive;
	}

	public void setPrimitive(Primitive primitive) {
		this.primitive = primitive;
	}

	public Enumeration getEnumeration() {
		return enumeration;
	}

	public void setEnumeration(Enumeration enumeration) {
		this.enumeration = enumeration;
	}

	public Boolean getIsGeneralization() {
		return isGeneralization;
	}

	public void setIsGeneralization(Boolean isGeneralization) {
		this.isGeneralization = isGeneralization;
	}

	public Boolean getIsEnumeration() {
		return isEnumeration;
	}

	public void setIsEnumeration(Boolean isEnumeration) {
		this.isEnumeration = isEnumeration;
	}

}
