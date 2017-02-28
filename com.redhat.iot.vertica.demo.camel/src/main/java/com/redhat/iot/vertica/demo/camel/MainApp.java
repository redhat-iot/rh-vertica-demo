package com.redhat.iot.vertica.demo.camel;

import org.apache.camel.CamelContext;
import org.apache.camel.main.Main;
import org.apache.camel.spring.SpringCamelContext;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

/**
 * A Camel Application
 */
public class MainApp {

	/**
	 * A main() so we can easily run these routing rules in our IDE
	 */
	public static void main(String... args) throws Exception {
		Main main = new Main();
		DriverManagerDataSource ds = new DriverManagerDataSource();

		ds.setDriverClassName("org.teiid.jdbc.TeiidDriver");
		ds.setUrl("jdbc:teiid:rh_hpe_iot@mm://172.16.121.82:31000");
		ds.setUsername("teiidUser");
		ds.setPassword("Redhat1!");

		main.bind("datasource", ds);

		main.addRouteBuilder(new TestRoute());
		main.run(args);
	}

}
