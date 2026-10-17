package dmit2015.config;

import jakarta.annotation.sql.DataSourceDefinition;
import jakarta.annotation.sql.DataSourceDefinitions;
import jakarta.enterprise.context.ApplicationScoped;

/**
 * Define Jakarta Transaction API (JTA) data source definitions for usage in a
 * development environment that can reference the `name` attribute of the `@DataSourceDefinition`
 * in `persistence.xml` using the `<jta-data-source>` element.
 * <p>
 * In a production environment where the data source definition are defined in operating system environment variables
 * the <a href="https://github.com/wildfly-extras/wildfly-datasources-galleon-pack">WildFly Datasources Galleon Feature-Pack</a>
 * are used as an alternative to these data source definitions.
 *
 */
@DataSourceDefinitions({

	@DataSourceDefinition(
		name="java:app/datasources/OracleHrDS",
		className="oracle.jdbc.xa.client.OracleXADataSource",
		url="jdbc:oracle:thin:@localhost:1521/FREEPDB1",
		user="HR",
		password="Password2015"),

})

@ApplicationScoped
public class ApplicationConfig {

}