package home.importer.model;

import org.apache.commons.lang3.StringUtils;

import com.sdmetrics.model.ModelElement;

public class Property extends Element {

	private ModelElement eaElement;
	private DataType dataType;
	private String description;
	private String lowerBound;
	private String upperBound;
	private String identifier;

	public Property(ModelElement eaElement) {
		this.eaElement  = eaElement;
		this.name       = eaElement.getPlainAttribute("name");
		this.upperBound = eaElement.getPlainAttribute("upperBound");
		this.lowerBound = eaElement.getPlainAttribute("lowerBound");
		this.identifier = eaElement.getPlainAttribute("identifier");
		this.description = eaElement.getPlainAttribute("description");
	}

	public DataType getDataType() {
		return dataType;
	}

	public void setDataType(DataType dataType) {
		this.dataType = dataType;
	}

	public ModelElement getEaElement() {
		return eaElement;
	}

	public void setEaElement(ModelElement eaElement) {
		this.eaElement = eaElement;
	}

	public String getLowerBound() {
		return lowerBound;
	}

	public void setLowerBound(String lowerBound) {
		this.lowerBound = lowerBound;
	}

	public String getUpperBound() {
		return upperBound;
	}

	public void setUpperBound(String upperBound) {
		this.upperBound = upperBound;
	}

	public String getIdentifier() {
		return identifier;
	}

	public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}

	public Boolean isGrandChildPrimitive() {
		return this.dataType.getPrimitive() != null;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * Yes --> mandatory
	 * No --> not mandatory
	 * ? --> couldn't be analysed
	 * @return
	 */
	public String isMandatory() {

		if(StringUtils.isNumeric(lowerBound) && StringUtils.isNumeric(upperBound) ) {
			int low = Integer.parseInt(lowerBound);
			int up  = Integer.parseInt(upperBound);

			return (low == 1 && up == 1)?"yes":"no";
		}
		
		return "?";
	}
}
