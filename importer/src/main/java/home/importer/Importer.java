package home.importer;

import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.opencsv.CSVWriter;

import home.importer.model.DataType;
import home.importer.model.Element;
import home.importer.model.Enumeration;
import home.importer.model.Property;
import home.importer.service.ImporterService;

/**
 * 
 * Main for command line.
 * 
 */
public class Importer {

	public Importer(String rootElement, String... models) throws Exception {
		String metaModelURL = "metamodel2.xml"; // meta model definition to use
		String xmiTransURL  = "xmiTrans2_0.xml"; // XMI transformations to use

		ImporterService cx = new ImporterService(rootElement, metaModelURL, xmiTransURL, models);
		List<Element> parsedElement = cx.getParsedElement();

		List<Enumeration> enumerations = new ArrayList<Enumeration>();

		for (Element element : parsedElement) {
			DataType root = (DataType) element;
			String fileName = StringUtils.remove(root.getName(), ' ') + "-" + System.currentTimeMillis() + ".csv";
			StringBuilder sbHeader = new StringBuilder().append("name").append(";").append("identifier").append(";");
			sbHeader.append("type").append(";").append("constraint").append(";").append("mandatory").append(";").append("type definition");
			sbHeader.append(";").append("attribute definition");
			
			CSVWriter writer = new CSVWriter(new FileWriter(fileName), ';');
			writer.writeNext(sbHeader.toString().split(";"));

			for (Property property : root.getProperties()) {
				String name = property.getName();
				String identifier = property.getIdentifier();
				String primitive = (property.getDataType().getIsEnumeration())? property.getDataType().getEnumeration().getName() : property.getDataType().getPrimitive().getName();
				String constraint = property.getDataType().getConstraint();
				String mandatory = property.isMandatory();
				String dataTypeDefinition = property.getDataType().getDataTypeDefinition();
				String propertyDefinition = property.getDescription();

				StringBuilder sb = new StringBuilder();
				sb.append(name).append(";");
				sb.append(identifier).append(";");
				sb.append(primitive).append(";");
				sb.append(constraint).append(";");
				sb.append(mandatory).append(";");
				sb.append(dataTypeDefinition).append(";");
				sb.append(propertyDefinition).append(";");

				// Create record
				String[] record = sb.toString().split(";");
				writer.writeNext(record);

				if(property.getDataType().getIsEnumeration()) {
					enumerations.add(property.getDataType().getEnumeration());
				}
			}
			writer.close();
		}
		
		if(enumerations.size()>0) {
			String fileName = "Enumerations-" + System.currentTimeMillis() + ".csv";
			CSVWriter writer = new CSVWriter(new FileWriter(fileName), ';');

			StringBuilder sbHeader = new StringBuilder().append("enumeration name").append(";").append("property name").append(";");
			sbHeader.append("identifier").append(";").append("property definition").append(";");

			writer.writeNext(sbHeader.toString().split(";"));

			for(Enumeration enumeration:enumerations) {
				for(Property property:enumeration.getProperties()) {
					StringBuffer sb = new StringBuffer();
					
					String name = enumeration.getName();
					String propName = property.getName();
					String identifier = property.getIdentifier();
					String propertyDefinition = property.getDescription();

					sb.append(name).append(";");
					sb.append(propName).append(";");
					sb.append(identifier).append(";");
					sb.append(propertyDefinition).append(";");

					// Create record
					String[] record = sb.toString().split(";");
					writer.writeNext(record);
				}
			}
			writer.close();
		}
	}

	public static void main(String[] args) throws Exception {
		String rootElement = args[0];
		String[] models = new String[args.length-1];

		for(int i=1; i<args.length; i++) {
			models[i-1] = args[i];
		}
		
		new Importer(rootElement, models);
	}
}
