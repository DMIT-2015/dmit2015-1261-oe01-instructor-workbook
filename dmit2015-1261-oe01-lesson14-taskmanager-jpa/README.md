# Lesson 14 TaskManager JPA Practice

This project is the practice activity for **Lesson 14: Introduction to Jakarta Persistence**.

The starter project already contains a working Jakarta Faces TaskManager application that uses an in-memory service implementation.

Your goal is to upgrade the application so it uses **Jakarta Persistence (JPA)** and a relational database instead of memory storage.

This practice prepares you for **Assignment 3**, where you will apply the same pattern to the `CampusEvent` domain model.

---

## How to Use This Repository

This repository is a public starter project for the Lesson 14 TaskManager JPA practice activity.

Do **not** clone the original repository directly unless you only want a read-only copy. If you clone the original repository, you will not be able to push your changes back to GitHub.

Use the following workflow:

1. Click **Fork** on the GitHub repository page.
2. Create the fork under your own GitHub account.
3. Clone **your fork** into IntelliJ IDEA.
4. Complete the TaskManager JPA practice activity.
5. Commit and push your changes to **your fork**.
6. Use what you learned to complete Assignment 3 in your separate Assignment 3 GitHub Classroom repository.

Important:

- This practice repository is **not** your Assignment 3 submission.
- Assignment 3 must be completed in your separate GitHub Classroom Assignment 3 repository.
- Do not submit your Lesson 14 practice fork for Assignment 3.


## Important

This is a practice project only.

Do not submit this repository for Assignment 3.

Assignment 3 must be completed in your separate GitHub Classroom Assignment 3 repository.

---

## What is already provided

The starter project includes:

- `pom.xml`
- `Task.java`
- `TaskPriority.java`
- `TaskService.java`
- `MemoryTaskService.java`
- `TaskCrudView.java`
- `manage-tasks.xhtml`
- `beans.xml`
- `web.xml`
- `layout.xhtml`
- `index.xhtml`

The application currently works with `MemoryTaskService`.

That means tasks are stored only in memory and will be lost when the application restarts.

---

## Practice Goal

By the end of this practice activity, your project should use a JPA service implementation instead of the memory service.

The final architecture should be:

```text
TaskCrudView
    -> TaskService interface
        -> TaskJpaService
            -> EntityManager
                -> H2 relational database
````

---

## Required Tasks

Complete the following steps.

### 1. Configure the Jakarta EE project for Jakarta Persistence

Create the following file using the provided IntelliJ IDEA file template:

```text
src/main/java/dmit2015/config/ApplicationConfig.java
```

Use the file template:

```text
DMIT2015 Jakarta Persistence ApplicationConfig
```

This file should define a JTA datasource for the H2 database.

For this practice project, use a file-based H2 database stored in the project `data` folder.

Example database URL:

```text
jdbc:h2:file:./data/lesson14-taskmanager-jpa-db
```

---

### 2. Create `persistence.xml`

Create the following file using the provided IntelliJ IDEA file template:

```text
src/main/resources/META-INF/persistence.xml
```

Use the file template:

```text
DMIT2015 Jakarta Persistence persistence.xml
```

The persistence unit should use the JTA datasource defined in `ApplicationConfig.java`.

During early development, it is acceptable to use:

```xml
<property name="jakarta.persistence.schema-generation.database.action" value="drop-and-create"/>
```

This setting recreates the database tables each time the application starts.

After the entity is stable, change the value to:

```xml
<property name="jakarta.persistence.schema-generation.database.action" value="none"/>
```

This prevents the file-based H2 database from being deleted and recreated on every restart.

---

### 3. Convert `Task` into a JPA entity

Update `Task.java` so it is mapped as a JPA entity.

Your entity should include:

* `@Entity`
* `@Id`
* `@GeneratedValue`
* validation annotations for required fields
* `@Version`
* `createTime`
* `updateTime`
* `@PrePersist`
* `@PreUpdate`

Recommended audit timestamp behavior:

```java
@PrePersist
void onCreate() {
    LocalDateTime now = LocalDateTime.now();
    createTime = now;
    updateTime = now;
}

@PreUpdate
void onUpdate() {
    updateTime = LocalDateTime.now();
}
```

---

### 4. Create a JPA service implementation

Create a new service implementation using the provided IntelliJ IDEA file template:

```text
src/main/java/dmit2015/service/TaskJpaService.java
```

Use the file template:

```text
DMIT2015 Model Service Interface Jakarta Persistence Implementation
```

`TaskJpaService` must implement `TaskService`.

The service should use:

```java
@PersistenceContext
private EntityManager entityManager;
```

Implement the basic CRUD methods using `EntityManager`.

---

### 5. Update `TaskCrudView` to use the JPA service

The starter project currently uses `MemoryTaskService`.

Update the JSF View/backing bean so it uses the JPA implementation.

The JSF page should not contain database logic.

Database logic belongs in:

```text
TaskJpaService.java
```

not in:

```text
TaskCrudView.java
```

---

### 6. Seed the database with sample data

Create a database initializer class using the provided IntelliJ IDEA file template:

```text
DMIT2015 Jakarta Persistence Entity Class Initializer
```

Recommended file location:

```text
src/main/java/dmit2015/config/TaskDataInitializer.java
```

The initializer should add sample tasks only when the database is empty.

Do not place seed data logic in the JSF View class.

---

### 7. Run the application

Start the application with:

```bash
mvn wildfly:dev
```

Open the application in your browser:

```text
http://localhost:8080/
```

Open the task page and verify that you can create, view, update, and delete tasks using the JPA service.

---

## Optional: H2 Console

The project includes the H2 Console for local development and troubleshooting.

Open:

```text
http://localhost:8080/h2-console
```

Use the same JDBC URL, username, and password configured in `ApplicationConfig.java`.

Example:

```text
JDBC URL: jdbc:h2:file:./data/lesson14-taskmanager-jpa-db
User Name: user2015
Password: Password2015
```

The H2 Console is for debugging only.

Do not use it as a replacement for the JSF page or service layer.

---

## Practice Checkpoint

Before asking for Assignment 3 troubleshooting help, be prepared to show your Lesson 14 practice progress.

Minimum checkpoint:

* `ApplicationConfig.java` created
* `persistence.xml` created
* `Task.java` mapped as a JPA entity
* `TaskJpaService.java` created
* `TaskCrudView.java` uses the JPA service
* `manage-tasks.xhtml` can create and display tasks from the database

---

## Common Mistakes to Avoid

* Do not keep using `MemoryTaskService` after creating `TaskJpaService`.
* Do not put `EntityManager` code inside `TaskCrudView`.
* Do not forget to add `Task` to the persistence unit if your `persistence.xml` requires explicit entity classes.
* Do not run with `drop-and-create` and expect file-based H2 data to survive a restart.
* Do not create duplicate service classes in different packages.
* Do not manually create required demo data only through the H2 Console.

---

## Reflection

Answer the following after completing the practice activity.

### What changed?

Briefly describe how the application changed when you replaced memory storage with JPA storage.

### What did `ApplicationConfig.java` configure?

Write one or two sentences.

### What did `persistence.xml` configure?

Write one or two sentences.

### What does `TaskJpaService` do?

Write one or two sentences.

### What problem did you run into?

Briefly describe one issue you encountered and how you solved it.

---

