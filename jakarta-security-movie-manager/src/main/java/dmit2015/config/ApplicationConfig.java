package dmit2015.config;

import jakarta.annotation.sql.DataSourceDefinition;
import jakarta.annotation.sql.DataSourceDefinitions;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.faces.annotation.FacesConfig;

@DataSourceDefinitions({

	@DataSourceDefinition(
			name = "java:app/datasources/H2DatabaseDS",
			className = "org.h2.jdbcx.JdbcDataSource",
			url = "jdbc:h2:file:./data/movie-manager-db;AUTO_SERVER=TRUE;",
			user = "user2015",
			password = "Password2015"),

})

@ApplicationScoped
public class ApplicationConfig {

}