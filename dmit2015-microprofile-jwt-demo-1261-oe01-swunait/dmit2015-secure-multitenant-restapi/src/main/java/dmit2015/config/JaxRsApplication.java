package dmit2015.config;

import jakarta.annotation.security.DeclareRoles;
import jakarta.ws.rs.ApplicationPath;
import jakarta.ws.rs.core.Application;
import org.eclipse.microprofile.auth.LoginConfig;

@ApplicationPath("/restapi")
@LoginConfig(authMethod = "MP-JWT", realmName = "dmit2015-realm")
@DeclareRoles({"Sales","Shipping","Administration"})
public class JaxRsApplication extends Application {

}