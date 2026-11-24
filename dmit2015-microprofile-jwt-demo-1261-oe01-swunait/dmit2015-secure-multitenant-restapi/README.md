# DMIT2015 Secure Multitenant REST API Demo

## Purpose

This instructor demonstration project shows how to secure personal REST API data using **MicroProfile JWT**, role-based authorization, and **record ownership**.

The project uses `TodoItem` REST endpoints to demonstrate how regular authenticated users access only their own data, while a privileged read-only role can view records belonging to all users.

## Main Learning Objectives

After reviewing this project, you should be able to explain how to:

- Read the authenticated username from the JWT `upn` claim.
- Assign ownership when a new record is created.
- Ignore or overwrite ownership values supplied by a client.
- Filter list queries by the authenticated user.
- Check record ownership before retrieval by ID, update, and delete.
- Combine role-based authorization with record-ownership checks.
- Test both permitted and denied requests using multiple accounts.

## Demo Roles

- `Sales` — regular personal-data user.
- `Shipping` — regular personal-data user.
- `Administration` — privileged **read-only** user.

The two regular roles are intentionally equivalent for this API. Using different authenticated users makes it easier to test isolation between personal records.

## Role and Ownership Permissions

| Operation | Sales | Shipping | Administration |
|---|:---:|:---:|:---:|
| Retrieve TodoItems | Own only | Own only | All users |
| Retrieve TodoItem by ID | Own only | Own only | Any user |
| Create TodoItem | Allowed | Allowed | Denied |
| Update TodoItem | Own only | Own only | Denied |
| Delete TodoItem | Own only | Own only | Denied |

An `Administration` user can read across tenants but does not become the owner of their records and cannot change those records.

## Ownership Rules

### Creating a TodoItem

1. Read the authenticated username from the verified JWT.
2. Set the TodoItem owner **on the server**.
3. Ignore or overwrite any owner value supplied in the request body.

Do not trust a client-provided username as proof of ownership.

### Listing and Retrieving TodoItems

- `Sales` and `Shipping` users receive only records owned by their authenticated username.
- An `Administration` user may retrieve all users' records.
- Individual-record lookups must enforce the same access restrictions as list queries. A user must not be able to bypass filtering by guessing an ID.

### Updating and Deleting TodoItems

1. Confirm that the caller's role permits the requested operation.
2. Load the existing record from the database.
3. Check that the stored owner matches the authenticated username.
4. Apply the change only if both the role and ownership checks pass.

A regular user cannot gain access to another person's record by changing a username in a request body.

## Role Authorization vs. Ownership Authorization

**Role authorization** asks: *May this user perform this kind of operation?*

**Ownership authorization** asks: *May this user access this particular record?*

The checks work together, but are not identical:

```text
Authenticated request
  |
  v
Does the role permit this operation?
  |
  +-- No --> 403 Forbidden
  |
  +-- Yes
       |
       +-- Privileged read permitted? --> Read requested record(s)
       |
       +-- Personal-data operation
              |
              v
          Is the record owned by this user?
              |
              +-- No --> 403 Forbidden
              |
              +-- Yes --> Complete operation
```

For a **create** operation there is no existing record to compare; the server assigns the new owner from the authenticated identity. For a regular user's **list** operation, enforce ownership through the query filter. For retrieve-by-ID, update, and delete, check access to the existing record.

When a valid JWT lacks the required role or ownership permission, the API should return `403 Forbidden` for the denied scenarios in this demo.

## REST API Endpoint

The REST API uses:

- **Port:** `8091`
- **Application path:** `/restapi`
- **Resource path:** `/todos`

Base resource URL (when deployed at the root context):

```text
http://localhost:8091/restapi/todos
```

The application deployment context path may add another URL segment.

Run the application using:

```bash
mvn wildfly:dev
```

Press `Ctrl+C` to stop WildFly.

## What to Observe During the Demo

- How the API obtains a username from the JWT rather than trusting the client.
- How the API sets ownership when saving a new `TodoItem`.
- How an owner-scoped query prevents users from viewing each other's data.
- How individual-record access checks prevent ID-based bypasses.
- Why `Administration` can read across tenants but cannot create, update, or delete.
- Why client-side filtering does not replace backend enforcement.

## Practice

1. Sign in as two different regular users and create separate TodoItems.
2. Confirm that each user sees only their own records.
3. Attempt to retrieve, update, and delete the other user's TodoItem by ID.
4. Submit a create request with a different username in the body and verify that the server assigns the authenticated user's identity.
5. Sign in as an `Administration` user and verify cross-user read access, then confirm writes are denied.

The transferable pattern is **JWT-derived identity + backend role checks + server-enforced ownership**.
