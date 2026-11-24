# DMIT2015 Secure REST Client Demo

## Purpose

This instructor demonstration project shows how a **Jakarta Faces REST client** can sign in through an OpenID Connect identity provider and call REST APIs protected by **MicroProfile JWT**.

It complements the secured `Student` and `TodoItem` REST APIs in this repository. The primary focus is the **client-side authentication workflow, JWT propagation, and error handling**; authorization decisions remain the responsibility of the APIs.

## Main Learning Objectives

After reviewing this project, you should be able to explain how to:

- Authenticate a user and obtain a JWT access token from an identity provider.
- Maintain authentication state for the current user's server-side session.
- Send `Authorization: Bearer <access_token>` on requests to **both** secured APIs.
- Distinguish an unusable/expired token (`401`) from insufficient permission (`403`).
- Preserve a valid login session when an operation is forbidden.
- Clear local authentication state when logging out.
- Keep authentication and API authorization responsibilities separate.

## Connected Services

| Service | Default port | Example resource |
|---|---:|---|
| OpenID Connect provider | Configured separately | Login and token issuance |
| RBAC REST API | `8090` | `Student` |
| Multitenant REST API | `8091` | `TodoItem` |
| Jakarta Faces REST client | Check client configuration | Faces views |

| Service                 | Port                  | REST API resource path    |
| ----------------------- | --------------------- | ------------------------- |
| OpenID Connect provider | Configured separately | Login and token issuance |
| RBAC REST API           | 8090                  | `/restapi/students`       |
| Multitenant REST API    | 8091                  | `/restapi/todos`          |
| REST Client             | Configured separately | Jakarta Faces application |

## Authentication and Request Flow

```text
User opens a secured Faces page
  |
  v
No usable login session? -- Yes --> Sign in through identity provider
  |                                      |
  No                                     v
  |                                 Access token
  +-------------------+------------------+
                      |
                      v
             Jakarta Faces REST client
                      |
             Authorization: Bearer JWT
                 /                \
                v                  v
          RBAC REST API      Multitenant REST API
          Student             TodoItem
```

The API validates the JWT and decides whether each requested operation is permitted.

## JWT and Token Propagation

An access token used by the APIs should include the claims required by their configuration, such as:

- `upn` — authenticated username.
- `groups` — application roles.
- `exp` — token expiration time.

For secured REST calls, attach the access token as an HTTP header:

```http
Authorization: Bearer <access_token>
```

The placeholder above is not a literal token. Pass the **current signed-in user's** access token to both APIs; do not use one hard-coded token for every user.

Keep tokens associated with the correct user's session. Do not display reusable tokens in a public page or application log.

## Handling Authentication and Authorization Errors

| Result | Meaning | REST client behavior |
|---|---|---|
| `401 Unauthorized` | The supplied token cannot be authenticated (for example, expired or invalid) | Clear or refresh unusable authentication state, then return the user to login |
| `403 Forbidden` | The token is valid, but the user lacks the required role or record ownership | **Keep** the valid login session and show an access-denied message |
| Successful response | JWT is accepted and the requested operation is permitted | Display or process the returned data |

A request with **no JWT** is denied, but its exact HTTP status can depend on the WildFly security configuration. In the configuration used for these examples, anonymous access to an `@RolesAllowed` endpoint may return `403` rather than `401`.

If the project uses a custom REST Client response mapper, check that a mapper intended for `403` handles only `403`, not every error status.

## Logout

Logging out of the client must clear its local authenticated session and stored access token. If the application integrates identity-provider logout, follow that configured flow as well. Clearing the client session and ending a single-sign-on session are related but distinct operations.

## Running the Project

1. Start the configured identity provider.
2. Start the RBAC REST API (`8090`) and multitenant REST API (`8091`).
3. Verify the REST client's configured service base URLs and identity-provider settings.
4. From this project folder, start the application with the configured WildFly Maven plugin goal:

```bash
mvn wildfly:dev
```

5. Open the URL configured for this Faces application and sign in with a test account.

Press `Ctrl+C` to stop WildFly. The REST client's port and deployment context path must be confirmed from its project configuration.

## What to Observe During the Demo

- What happens when an unauthenticated user navigates to a secured page.
- How the app identifies the current user after login.
- Where a REST request obtains the current user's token.
- Whether calls to **both** APIs include the bearer token.
- How the UI responds to a denied operation without signing out a valid user.
- How an invalid or expired token returns the user to an authentication workflow.
- What changes after logout and a subsequent attempt to access a secured page.

## Practice

1. Sign in with a `Sales` user and attempt both permitted and denied operations.
2. Repeat with `Shipping` and `Administration` to see how the two APIs use roles differently.
3. Confirm that a denied operation returning `403` does **not** remove a valid login session.
4. Test an expired or invalid token and observe the `401` recovery path where supported by the server configuration.
5. Sign out and verify that secured pages and APIs cannot continue using the previous local session.

The central lesson is that the **client authenticates the user and forwards the JWT**, while **each API enforces its own role and ownership rules**.
