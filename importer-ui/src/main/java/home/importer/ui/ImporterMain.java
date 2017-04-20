package home.importer.ui;

import java.util.List;

import com.vaadin.addon.tableexport.ExcelExport;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Panel;
import com.vaadin.ui.Table;
import com.vaadin.ui.VerticalLayout;

import home.importer.model.DataType;
import home.importer.model.Element;
import home.importer.model.Property;
import home.importer.service.ImporterService;

public class ImporterMain extends VerticalLayout {

	private XMLUploader xmiTransFile = new XMLUploader();
	private XMLUploader xmiMetaFile = new XMLUploader();
	private XMLUploader xmiUMLFile = new XMLUploader();
	private XMLUploader xmiUMLFile2 = new XMLUploader();
	private Table table = new Table();
	private HorizontalLayout outputLayout = new HorizontalLayout();

	public ImporterMain() {
		/*
		 * Table
		 */
		Button exportExcel = new Button("Export to Excel");
		exportExcel.addClickListener(new ExportClickEvent());
		table.setWidth(100.0f, Unit.PERCENTAGE);
		outputLayout.addComponents(table, exportExcel);
		outputLayout.setVisible(false);

		/*
		 * Transformation file
		 */
		Panel xmiTrans = new Panel("XMI transformation file");
		xmiTrans.setContent(xmiTransFile);

		/*
		 * Metamodel
		 */
		Panel xmiMeta = new Panel("XMI meta model");
		xmiMeta.setContent(xmiMetaFile);

		/*
		 * UML model
		 */
		Panel xmiUML = new Panel("UML model");
		xmiUML.setContent(xmiUMLFile);

		/*
		 * UML model
		 */
		Panel xmiUML2 = new Panel("UML model 2");
		xmiUML2.setContent(xmiUMLFile2);

		/*
		 * Button layout
		 */
		HorizontalLayout hl = new HorizontalLayout();
		Button parse = new Button("Parse");

		parse.addClickListener(new ParseClickEvent());

		hl.setMargin(false);
		hl.setSpacing(true);

		hl.addComponents(parse);

		/*
		 * Main panel
		 */
		addComponent(xmiTrans);
		addComponent(xmiMeta);
		addComponent(xmiUML);
		addComponent(xmiUML2);
		addComponent(hl);
		addComponent(outputLayout);

		setMargin(true);
		setSpacing(true);
	}

	private class ParseClickEvent implements ClickListener {

		@Override
		public void buttonClick(ClickEvent event) {
			try {
				ImporterService is = new ImporterService("??????",
						xmiMetaFile.getStreamSource().getStream(), xmiTransFile.getStreamSource().getStream(),
						xmiUMLFile.getStreamSource().getStream(), xmiUMLFile2.getStreamSource().getStream());
				List<Element> els = is.getParsedElement();
				initTable(els.get(0));
				outputLayout.setVisible(true);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}

	private class ExportClickEvent implements ClickListener {

        private static final long serialVersionUID = -73954695086117200L;
        private ExcelExport excelExport;

        public void buttonClick(final ClickEvent event) {
            excelExport = new ExcelExport(table);
            excelExport.export();
        }

	}

	public void initTable(Element root) {
		table.addContainerProperty("Name", String.class,  null);
		table.addContainerProperty("Identifier", String.class,  null);
		table.addContainerProperty("Type", String.class,  null);
		table.addContainerProperty("Constraint", String.class,  null);
		table.addContainerProperty("Mandatory", String.class,  null);
		table.addContainerProperty("Type Definition", String.class,  null);
		table.addContainerProperty("Attribute Definition", String.class,  null);

		table.setColumnWidth("Type Definition", 100);
		table.setColumnWidth("Attribute Definition", 100);
		
		int i = 0;
		for (Property property : ((DataType)root).getProperties()) {
			String[] values = new String[7];
			
			values[0] = property.getName();
			values[1] = property.getIdentifier();
			values[2] = (property.getDataType().getIsEnumeration())
					? property.getDataType().getEnumeration().getName()
					: property.getDataType().getPrimitive().getName();
			values[3] = property.getDataType().getConstraint();
			values[4] = property.isMandatory();
			values[5] = property.getDataType().getDataTypeDefinition();
			values[6] = property.getDescription();

			table.addItem(values, new Integer(i++));
		}
	}
}
