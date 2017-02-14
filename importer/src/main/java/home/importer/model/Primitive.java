package home.importer.model;

import com.sdmetrics.model.ModelElement;

public class Primitive extends Element {

	private String documentation;

	public Primitive(ModelElement eaElement) {
		this.id = eaElement.getPlainAttribute("idref");
		this.name = eaElement.getPlainAttribute("name");
		this.documentation = eaElement.getPlainAttribute("documentation");
	}

	public String getDocumentation() {
		return documentation;
	}

	public void setDocumentation(String documentation) {
		this.documentation = documentation;
	}

}
