# DMIT2015 MicroProfile JWT Demo

## Purpose

This repository contains three instructor demonstration projects for securing Jakarta REST APIs with **MicroProfile JWT** and calling secured APIs from a **Jakarta Faces REST client**.

The examples separate three related security concerns:

1. **Role-based access control (RBAC):** deciding which operations a caller's roles permit.
2. **Multitenant data security:** restricting access to records based on the authenticated user and record ownership.
3. **REST client security:** signing in, sending JWT access tokens to APIs, and handling authentication and authorization failures.

These are teaching examples. The emphasis is on understanding and testing security patterns, not on reusing the domain models or role rules unchanged in another application.

## Projects

| Project                               | Base URL                                 | Focus                                                 |
| ------------------------------------- | ---------------------------------------- | ----------------------------------------------------- |
| `dmit2015-secure-rbac-restapi`        | `http://localhost:8090/restapi/students` | JWT authentication and RBAC                           |
| `dmit2015-secure-multitenant-restapi` | `http://localhost:8091/restapi/todos`    | JWT-derived ownership and multitenancy                |
| `dmit2015-secure-restclient`          | `http://localhost:8080`                  | Authentication, token propagation, and error handling |

Each project has its own README with learning objectives, role rules, and run/test guidance.

## Main Learning Objectives

After working through the demos, you should be able to:

- Explain the difference between **authentication**, **role authorization**, and **record ownership**.
- Identify the JWT claims used by the APIs, including `upn`, `groups`, and `exp`.
- Protect REST endpoints with MicroProfile JWT and `@RolesAllowed`.
- Enforce record ownership in backend code instead of relying on client-side filtering.
- Pass a JWT to secured APIs using `Authorization: Bearer <access_token>`.
- Test allowed and denied requests with different authenticated users.
- Explain how the client should respond to `401 Unauthorized` versus `403 Forbidden`.

## Demo Roles

The examples use these fictional business roles:

- `Sales`
- `Shipping`
- `Administration`

**The same role can have different permissions in different APIs.** In the RBAC example, `Administration` has full CRUD access to `Student` records. In the multitenant example, `Administration` can read all `TodoItem` records but cannot create, update, or delete them. Always follow the rules defined for the particular resource and API.

## How the Components Fit Together

```text
                          Identity provider
                          (e.g., Keycloak)
                                 |
                        Login / JWT issuance
                                 |
                                 v
                        Jakarta Faces client
                                 |
                    Authorization: Bearer JWT
                       /                 \
                      v                   v
               RBAC REST API       Multitenant REST API
               Student records     TodoItem records
               port 8090           port 8091
```

The API must verify the JWT and enforce access rules. Disabling a button or hiding a link in the client is not a substitute for backend authorization.

## Before You Start

- Confirm that the identity provider and each application's configured ports are available.
- Configure test users and role mappings so JWT access tokens contain the claims required by the APIs.
- Check each project's README and configuration for service URLs, deployment context paths, and startup steps.
- Use **test accounts and test data**, not personal or production credentials.

## Running and Testing

1. Start the configured OpenID Connect identity provider.
2. Start the RBAC REST API on port `8090`.
3. Start the multitenant REST API on port `8091`.
4. Start the REST client using its configured port and API base URLs.
5. Sign in with different test users and compare permitted and denied operations.

For a project using the WildFly Maven plugin, start it from that project's directory:

```bash
mvn wildfly:dev
```

Press `Ctrl+C` to stop it. Run the three applications separately. If your local configuration differs from the ports shown above, use the configured values.

Use IntelliJ IDEA HTTP Client or another REST client to test API behavior directly; then use the Jakarta Faces client to observe JWT propagation and user-facing error handling.

## Security Response Reference

| Situation | Typical response or client behavior |
|---|---|
| No usable JWT | Access is denied; the status can depend on server security configuration |
| Invalid or expired JWT | `401 Unauthorized`; the client should clear or refresh unusable authentication state and return to login |
| Valid JWT, insufficient role or ownership | `403 Forbidden`; keep the valid login session and show an access-denied message |
| Valid JWT, permitted request | Execute the endpoint operation |

In the WildFly configuration used for these examples, an anonymous request to an endpoint protected by `@RolesAllowed` may return `403 Forbidden` rather than `401 Unauthorized`. To prove a **role denial**, use a *valid* JWT for a user with an insufficient role.

## Suggested Learning Sequence

1. **RBAC:** test `Student` endpoints with `Sales`, `Shipping`, and `Administration` tokens.
2. **Multitenancy:** create `TodoItem` records with two different user identities, then test cross-user access by ID.
3. **REST client:** observe login, how tokens are sent to both APIs, `401`/`403` handling, and logout.

## References

- [MicroProfile JWT Authentication Specification](https://download.eclipse.org/microprofile/microprofile-jwt-auth-2.1/microprofile-jwt-auth-spec-2.1.html)
- [MicroProfile REST Client Specification](https://download.eclipse.org/microprofile/microprofile-rest-client-4.0/microprofile-rest-client-spec-4.0.html)
- [Keycloak Documentation](https://www.keycloak.org/documentation)
- [IntelliJ IDEA HTTP Client](https://www.jetbrains.com/help/idea/exploring-http-syntax.html)
