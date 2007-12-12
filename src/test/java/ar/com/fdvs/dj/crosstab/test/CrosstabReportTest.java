/*
 * Dynamic Jasper: A library for creating reports dynamically by specifying
 * columns, groups, styles, etc. at runtime. It also saves a lot of development
 * time in many cases! (http://sourceforge.net/projects/dynamicjasper)
 *
 * Copyright (C) 2007  FDV Solutions (http://www.fdvsolutions.com)
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 *
 * License as published by the Free Software Foundation; either
 *
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 *
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 *
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 *
 *
 */

package ar.com.fdvs.dj.crosstab.test;


import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import junit.framework.TestCase;
import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.view.JasperDesignViewer;
import net.sf.jasperreports.view.JasperViewer;
import ar.com.fdvs.dj.core.DynamicJasperHelper;
import ar.com.fdvs.dj.core.layout.ClassicLayoutManager;
import ar.com.fdvs.dj.core.layout.Dj2JrCrosstabBuilder;
import ar.com.fdvs.dj.domain.ColumnProperty;
import ar.com.fdvs.dj.domain.ColumnsGroupVariableOperation;
import ar.com.fdvs.dj.domain.DJCrosstab;
import ar.com.fdvs.dj.domain.DJCrosstabColumn;
import ar.com.fdvs.dj.domain.DJCrosstabRow;
import ar.com.fdvs.dj.domain.DynamicReport;
import ar.com.fdvs.dj.domain.builders.FastReportBuilder;
import ar.com.fdvs.dj.domain.entities.ColumnsGroup;
import ar.com.fdvs.dj.test.ReportExporter;
import ar.com.fdvs.dj.test.TestRepositoryProducts;
import ar.com.fdvs.dj.util.SortUtils;

public class CrosstabReportTest extends TestCase {

	private Map params = new HashMap();
	private DJCrosstab djcross;

	public DynamicReport buildReport() throws Exception {


		/**
		 * Creates the DynamicReportBuilder and sets the basic options for
		 * the report
		 */
		FastReportBuilder drb = new FastReportBuilder();
		drb.addColumn("State", "state", String.class.getName(),30)
			.addColumn("Branch", "branch", String.class.getName(),30)
			.addColumn("Product Line", "productLine", String.class.getName(),50)
			.addColumn("Item", "item", String.class.getName(),50)
			.addColumn("Item Code", "id", Long.class.getName(),30,true)
			.addColumn("Quantity", "quantity", Long.class.getName(),60,true)
			.addColumn("Amount", "amount", Float.class.getName(),70,true)
			.addGroups(1)
			.setTitle("November 2006 sales report")
			.setSubtitle("This report was generated at " + new Date())
			.setUseFullPageWidth(true);
//			drb.setTemplateFile("templates/crosstab3-test.jrxml");
		
		

		djcross = new DJCrosstab();
		djcross.setHeight(200);
		djcross.setWidth(500);
		djcross.setMeasure(new ColumnProperty("amount",Float.class.getName()));
		djcross.setOperation(ColumnsGroupVariableOperation.SUM);
		
		DJCrosstabRow row = new DJCrosstabRow();
		row.setProperty(new ColumnProperty("productLine",String.class.getName()));
		row.setHeaderWidth(100);
		row.setHeight(30);		
		djcross.getRows().add(row);	
		row = new DJCrosstabRow();
		row.setProperty(new ColumnProperty("item",String.class.getName()));
		row.setHeaderWidth(100);
		row.setHeight(30);		
		djcross.getRows().add(row);	
		
		DJCrosstabColumn col = new DJCrosstabColumn();
		col.setProperty(new ColumnProperty("state",String.class.getName()));
		col.setHeaderHeight(40);
		col.setWidth(50);
		djcross.getColumns().add(col);
		
		col = new DJCrosstabColumn();
		col.setProperty(new ColumnProperty("branch",String.class.getName()));
		col.setHeaderHeight(40);
		col.setWidth(50);
		djcross.getColumns().add(col);		
		

		DynamicReport dr = drb.build();
		
		ColumnsGroup group = (ColumnsGroup) dr.getColumnsGroups().get(0);
		group.getFooterCrosstabs().add(djcross);
		
		return dr;
	}

	public void testReport() {
		try {
			DynamicReport dr = buildReport();
			Collection dummyCollection = TestRepositoryProducts.getDummyCollection();
			dummyCollection = SortUtils.sortCollection(dummyCollection,dr.getColumns());
						
			JRDataSource ds = new JRBeanCollectionDataSource(dummyCollection);		//Create a JRDataSource, the Collection used
																											//here contains dummy hardcoded objects...
			params.put("sr", SortUtils.sortCollection(TestRepositoryProducts.getDummyCollection(),djcross));
			
			JasperPrint jp = DynamicJasperHelper.generateJasperPrint(dr, new ClassicLayoutManager(), ds, params );	//Creates the JasperPrint object, we pass as a Parameter
																											//the DynamicReport, a new ClassicLayoutManager instance (this
																											//one does the magic) and the JRDataSource 
			ReportExporter.exportReport(jp, System.getProperty("user.dir")+ "/target/FastReportTest.pdf");
			JasperViewer.viewReport(jp);	//finally display the report report
			JasperReport jr = DynamicJasperHelper.generateJasperReport(dr,  new ClassicLayoutManager());
			JasperDesignViewer.viewReportDesign(jr);
		} catch (Exception e) {
//			e.getCause().printStackTrace();
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		CrosstabReportTest test = new CrosstabReportTest();
		test.testReport();
	}

}
