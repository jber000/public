package home.importer.service;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang3.StringUtils;

import com.sdmetrics.model.MetaModel;
import com.sdmetrics.model.Model;
import com.sdmetrics.model.ModelElement;
import com.sdmetrics.model.XMIReader;
import com.sdmetrics.model.XMITransformations;
import com.sdmetrics.util.XMLParser;

import home.importer.model.DataType;
import home.importer.model.Element;
import home.importer.model.Property;

public class ImporterService {

	private Map<String, ModelElement> elements		= new HashMap<String, ModelElement>();
	private Map<String, ModelElement> ea_elements	= new HashMap<String, ModelElement>();
	private Map<String, ModelElement> ea_attributes	= new HashMap<String, ModelElement>();
	private MetaModel metaModel						= new MetaModel();
	private List<Element> parsedElements  			= new ArrayList();

	public ImporterService(String rootElement, InputStream metaModelIS, InputStream xmiTransformationIS, InputStream... modelsIS) throws Exception{

		XMLParser parser = new XMLParser();
		parser.parse(metaModelIS, metaModel.getSAXParserHandler());

		XMITransformations trans = new XMITransformations(metaModel);
		parser.parse(xmiTransformationIS, trans.getSAXParserHandler());

		for(InputStream modelIS:modelsIS) {
			Model model = new Model(metaModel);
			XMIReader xmiReader = new XMIReader(trans, model);
			parser.parse(modelIS, xmiReader);			

			this.parseDataType(metaModel, model);
			this.parseProperty(metaModel, model);

			this.parseEADataType(metaModel, model);
			this.parseEAAttribute(metaModel, model);
		}

		List<ModelElement> results = find(rootElement);

		for(ModelElement element:results) {
			Element el = recurse(element, getMetaModel());
			parsedElements.add(el);
		}

	}

	public ImporterService(String rootElement, String metaModelURL, String xmiTransformationURL, String... modelURLs) throws Exception{

		XMLParser parser = new XMLParser();
		parser.parse(metaModelURL, metaModel.getSAXParserHandler());

		XMITransformations trans = new XMITransformations(metaModel);
		parser.parse(xmiTransformationURL, trans.getSAXParserHandler());

		for(String modelURL:modelURLs) {
			Model model = new Model(metaModel);
			XMIReader xmiReader = new XMIReader(trans, model);
			parser.parse(modelURL, xmiReader);			

			this.parseDataType(metaModel, model);
			this.parseProperty(metaModel, model);

			this.parseEADataType(metaModel, model);
			this.parseEAAttribute(metaModel, model);
		}

		List<ModelElement> results = find(rootElement);

		for(ModelElement element:results) {
			Element el = recurse(element, getMetaModel());
			parsedElements.add(el);
		}

	}

	public List<Element> getParsedElement() {
		return parsedElements;
	}

	public MetaModel getMetaModel() {
		return this.metaModel;
	}

	public ModelElement getElement(String id) {
		return elements.get(id);
	}

	public ModelElement getEAElement(String id) {
		return ea_elements.get(id);
	}

	public ModelElement getEAAttribute(String id) {
		return ea_attributes.get(id);
	}

	public List<ModelElement> find(String name) throws RuntimeException{
		List<ModelElement> results = new ArrayList();

		Iterator<Entry<String, ModelElement>> it = elements.entrySet().iterator();
		while(it.hasNext()) {
			ModelElement el = it.next().getValue();
			String n1 = el.getName();
			if(StringUtils.equalsIgnoreCase(n1, name)) {
				results.add(el);
			}
		}

		return results;
	}

	private void parseProperty(MetaModel metaModel, Model model) {
		Iterator<ModelElement> it = model.getElements(metaModel.getType("property")).iterator();
		ModelElement e = null;
		while(it.hasNext()) {
			e = it.next();
			elements.put(e.getXMIID(), e);
		}
	}

	private void parseDataType(MetaModel metaModel, Model model) {
		Iterator<ModelElement> it = model.getElements(metaModel.getType("datatype")).iterator();
		ModelElement e = null;
		while(it.hasNext()) {
			e = it.next();
			elements.put(e.getXMIID(), e);
		}
	}

	private void parseEADataType(MetaModel metaModel, Model model) {
		Iterator<ModelElement> it = model.getElements(metaModel.getType("ea_datatype")).iterator();
		ModelElement e = null;
		while(it.hasNext()) {
			e = it.next();
			String idref = e.getPlainAttribute("idref");
			ea_elements.put(idref, e);
		}
	}

	private void parseEAAttribute(MetaModel metaModel, Model model) {
		Iterator<ModelElement> it = model.getElements(metaModel.getType("ea_attribute")).iterator();
		ModelElement e = null;
		while(it.hasNext()) {
			e = it.next();
			String idref = e.getPlainAttribute("idref");
			ea_attributes.put(idref, e);
		}
	}

	/**
	 * here is the magic...
	 * @param node
	 * @param model
	 * @return
	 */
	private Element recurse(ModelElement node, MetaModel model) {
		if(node == null) return null;

		String name			= node.getName();
		String id			= node.getXMIID();
		String elementType	= node.getType().getName();

		Element element = null;

		if(StringUtils.equalsIgnoreCase(node.getType().getName(), "datatype")) {
			Iterator<ModelElement> ownedElement = node.getOwnedElements().iterator();
			element = new DataType();
			ModelElement eaElement = getEAElement(id);
			ModelElement umlElement = getElement(id);
			((DataType)element).init(umlElement, eaElement, this);

			while(ownedElement.hasNext()) {
				ModelElement me = ownedElement.next();
				Property prop = (Property)recurse(me, model);
				((DataType)element).getProperties().add(prop);
			}
		} else if (StringUtils.equalsIgnoreCase(node.getType().getName(), "property")) {
			String refid = node.getPlainAttribute("propertytypeid");

			ModelElement eaElement  = getEAAttribute(id);

			element  = new Property(eaElement);
			
			ModelElement me = getElement(refid);
			DataType dt = (DataType)recurse(me, model);
			((Property)element).setDataType(dt);
			return element;
		}
		
		return element;
	}
}
