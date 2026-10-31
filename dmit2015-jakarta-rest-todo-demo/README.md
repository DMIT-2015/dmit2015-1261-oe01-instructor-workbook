# DMIT2015 – Jakarta RESTful Web Services Practice

## Overview

In this practice activity, you will develop two Jakarta EE applications that communicate using REST.

- **dmit2015-todo-restapi** — Provides REST endpoints to manage Todo items stored in a database.
- **dmit2015-todo-restclient** — Provides a Jakarta Faces user interface that communicates with the REST API.

The projects are already configured so you can focus on implementing the required functionality instead of creating and configuring projects from scratch.

**Learning goals**

By completing this practice, you should be able to:

1. Translate functional requirements into Jakarta REST resource methods.
2. Select appropriate HTTP methods, paths, and response status codes.
3. Use Data Transfer Objects (DTOs) instead of exposing persistence entities.
4. Implement a MicroProfile REST Client to communicate with a separate application.
5. Connect Jakarta Faces components to REST Client operations.
6. Preserve optimistic-locking version information during updates.

Use the course *Jakarta RESTful Web Services* document for implementation examples and instructor demonstrations.

---

## Part 1 – Implement the Todo REST API

**Project:** `dmit2015-todo-restapi`

### Task 1 – Explore the starter project

Before writing code:

- [ ] Locate the `TodoItem` entity.
- [ ] Identify the entity properties, including its primary key and version.
- [ ] Locate the persistence repository.
- [ ] Identify the repository methods available for CRUD operations.
- [ ] Locate the Jakarta REST application configuration.
- [ ] Start the project and verify that WildFly is running on port `8090`.

**Think about:** Which classes should access the database, and which classes should handle HTTP requests?

### Task 2 – Implement the REST resource

Create or complete a `TodoItemResource` class that exposes the following endpoints.

| HTTP method | Resource path | Required behaviour | Success status |
|---|---|---|---|
| GET | `/restapi/todos` | Return all Todo items | 200 OK |
| GET | `/restapi/todos/{id}` | Return one Todo item | 200 OK |
| POST | `/restapi/todos` | Create a Todo item | 201 Created |
| PUT | `/restapi/todos/{id}` | Update an existing Todo item | 200 OK |
| DELETE | `/restapi/todos/{id}` | Delete a Todo item | 204 No Content |

These are the endpoint paths for this practice activity.

For each endpoint:

1. Identify the appropriate Jakarta REST annotation.
2. Determine whether information comes from the URL, request body, or database.
3. Call the appropriate repository method.
4. Return the expected HTTP response.

**Success checks**

- [ ] GET All returns a JSON array.
- [ ] GET One returns the requested record.
- [ ] POST creates a record and returns 201.
- [ ] PUT modifies an existing record.
- [ ] DELETE removes a record and returns 204.
- [ ] Requests for nonexistent records return 404 where appropriate.
- [ ] Invalid data produces an appropriate client-error response.

Use IntelliJ IDEA HTTP Client to test the endpoints before creating the frontend.

### Task 3 – Introduce Data Transfer Objects

The initial REST resource may return `TodoItem` entities directly. Refactor it to use a DTO.

Create `TodoItemDto` and the corresponding mapping logic.

Use the property names demonstrated in the reference lesson:

- `id`
- `name`
- `complete`
- `version`

The persistence entity uses `task` and `done` rather than `name` and `complete`.

**Implementation requirements**

- [ ] Convert an entity into a DTO when returning data.
- [ ] Convert incoming DTO data into appropriate entity changes when creating or updating.
- [ ] Update every required endpoint to use DTOs.
- [ ] Keep persistence-managed fields out of the public API.
- [ ] Include the version property for optimistic locking.

**Success check:** Inspect a GET response. It must contain the expected DTO properties, not the original entity property names.

**Think about:** Why should a REST API control which properties are exposed rather than serializing the entire persistence entity?

### Task 4 – Handle updates and optimistic locking

Review the entity's version property and how Jakarta Persistence maintains it.

When updating a Todo item:

1. Retrieve the existing record using the ID from the URL.
2. Read the requested changes from the DTO.
3. Preserve and check the submitted version as part of the update.
4. Apply the changes to the persistent entity.
5. Return an appropriate result.

**Success checks**

- [ ] GET One returns the current version.
- [ ] PUT accepts the version supplied by the client.
- [ ] A successful update returns the updated representation and version.
- [ ] A stale version cannot silently overwrite a newer record.
- [ ] The API returns a meaningful error when a version conflict is detected.

### Part 1 completion checkpoint

Before proceeding, confirm that you can use an HTTP client to perform all five CRUD operations using DTOs.

**Do not begin implementing the Jakarta Faces client until the REST API is functioning.**

---

## Part 2 – Implement the Todo REST Client

**Project:** `dmit2015-todo-restclient`

This application must obtain and modify Todo data through HTTP requests to the REST API.

**Important:** The REST Client application must not use Jakarta Persistence or access the database directly.

### Task 5 – Configure the MicroProfile REST Client

Create a REST Client interface that communicates with the Todo REST API.

The interface must support:

- [ ] Read All
- [ ] Read One
- [ ] Create
- [ ] Update
- [ ] Delete

Create a client-side `TodoItemDto` matching the API's JSON representation.

Use the course reference document to determine the appropriate MicroProfile REST Client annotations and configuration.

**Success check:** The REST Client can request Todo data from the API running on port `8090`.

### Task 6 – Display Todo items in Jakarta Faces

Create or complete a Jakarta Faces backing bean and page that displays Todo records.

**Required behaviour**

1. The backing bean obtains Todo records using the REST Client.
2. The page displays those records in a table.
3. Users can refresh the list to see current data.

**Success checks**

- [ ] The page displays Todo records from the API.
- [ ] The page does not access the database directly.
- [ ] Changes made through an HTTP client become visible after refreshing the list.
- [ ] API connection failures produce meaningful feedback rather than an unexplained page error.

**Think about:** Which class is responsible for making HTTP requests? Which class manages the state of the user interface?

### Task 7 – Create Todo items

Add a form allowing users to create a Todo item.

**Required behaviour**

1. The user enters the required properties.
2. The backing bean prepares a DTO.
3. The REST Client sends a POST request.
4. The page displays the result and refreshes the list.

**Success checks**

- [ ] A new Todo item appears in the list after saving.
- [ ] Invalid data is rejected appropriately.
- [ ] The user receives meaningful success or error feedback.

### Task 8 – Edit and update Todo items

Add an Edit operation for an existing Todo item.

**Required behaviour**

1. The user selects a Todo item.
2. The REST Client retrieves the selected record using its ID.
3. The backing bean stores the retrieved DTO.
4. The user modifies the editable fields.
5. The REST Client sends a PUT request using the selected ID and modified DTO.
6. The page displays the result.

**Important:** The version returned by the API must be preserved and sent back during the update.

**Success checks**

- [ ] The correct Todo item is retrieved for editing.
- [ ] Existing values appear in the edit form.
- [ ] Changes are saved through the REST API.
- [ ] The version is preserved between retrieval and update.
- [ ] Failed updates produce meaningful feedback.

### Task 9 – Delete Todo items

Add a Delete operation for an existing Todo item.

**Required behaviour**

1. The user selects an item to delete.
2. The application sends the selected ID to the REST API.
3. The page refreshes the list after successful deletion.

**Success checks**

- [ ] The correct record is deleted.
- [ ] The deleted record no longer appears in the list.
- [ ] Errors are handled without crashing the Jakarta Faces application.

---

## Part 3 – Integration Testing

Run both applications simultaneously.

| Application | Port | Responsibility |
|---|---|---|
| Todo REST API | 8090 | REST endpoints and database access |
| Todo REST Client | 8080 | Jakarta Faces frontend and HTTP communication |

Complete the following end-to-end test:

- [ ] Start both applications.
- [ ] Open the Jakarta Faces frontend.
- [ ] Display the existing Todo items.
- [ ] Create a new Todo item.
- [ ] Edit the newly created item.
- [ ] Confirm that the updated values appear in the list.
- [ ] Delete the item.
- [ ] Confirm that it no longer appears.
- [ ] Stop the REST API and observe how the frontend handles the connection failure.
- [ ] Restart the REST API and verify that the frontend works again.

### Final reflection questions

Be prepared to explain:

1. Why does the frontend use a REST Client instead of injecting the persistence repository?
2. What is the difference between a persistence entity and a DTO?
3. How does the REST resource determine which HTTP status code to return?
4. Why must the version property be preserved when editing a record?
5. What happens when the REST API is unavailable?

## Next Step – Assignment 6

Apply the same architectural concepts to Assignment 6, which uses a `Bill` entity instead of `TodoItem`.

The assignment requires a separate REST API and Jakarta Faces REST Client, including CRUD operations, DTO mapping, HTTP error handling, and optimistic-locking support.

**Use this practice to understand the implementation process. Your Assignment 6 solution must satisfy its own specifications and rubric.**

## Reference Material

- Course document: *Jakarta RESTful Web Services*
- [Jakarta EE Tutorial – Building RESTful Web Services](https://eclipse-ee4j.github.io/jakartaee-tutorial/#building-restful-web-services-with-jakarta-rest)
- [IntelliJ IDEA HTTP Client](https://www.jetbrains.com/help/idea/http-client-in-product-code-editor.html)
- [MicroProfile REST Client Specification](https://download.eclipse.org/microprofile/microprofile-rest-client-4.0/microprofile-rest-client-spec-4.0.html)
