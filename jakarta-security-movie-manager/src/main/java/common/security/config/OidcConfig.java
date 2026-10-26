package common.security.config;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.context.Initialized;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.security.enterprise.authentication.mechanism.http.OpenIdAuthenticationMechanismDefinition;
import lombok.Getter;
import org.eclipse.microprofile.config.inject.ConfigProperty;

/**
 * This class assumes that the Microprofile Config dependency has been added to the project.
 * <pre>@code{
 *
 * <dependency>
 *     <groupId>org.eclipse.microprofile.config</groupId>
 *     <artifactId>microprofile-config-api</artifactId>
 *     <version>3.1</version>
 * </dependency>
 *
 * }</pre>
 */

@OpenIdAuthenticationMechanismDefinition(
		providerURI = "${oidcConfig.oidcProviderURI}",
		clientId = "${oidcConfig.oidcClientId}",
		clientSecret = "${oidcConfig.oidcClientSecret}",
		redirectURI = "${oidcConfig.oidcRedirectURI}",
//		redirectToOriginalResource = true,
		jwksReadTimeout = 5000,
		jwksConnectTimeout = 5000
)

@Named("oidcConfig")
@ApplicationScoped
@Getter
public class OidcConfig {

	@Inject
	@ConfigProperty(name = "keycloak.oidc.providerURI")
	private String oidcProviderURI;

	@Inject
	@ConfigProperty(name = "keycloak.oidc.clientId")
	private String oidcClientId;

	@Inject
	@ConfigProperty(name = "keycloak.oidc.clientSecret")
	private String oidcClientSecret;

	@Inject
	@ConfigProperty(name = "keycloak.oidc.redirectURI")
	private String oidcRedirectURI;

	public void initialize(@Observes @Initialized(ApplicationScoped.class) Object event) {

    }
}