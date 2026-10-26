## Jakarta Security Practice – Movie Manager

This application demonstrates Jakarta Security with Keycloak OpenID Connect authentication, LDAP user federation, role-based authorization, and user-specific data access using Jakarta Data.

### Application Roles

The application uses two roles:

- **Contributor:** Can create movies, view their own movies, and edit movies they own.
- **Administrator:** Can view all movies and delete movies but cannot create or edit movies.

### Role-Based Permissions

| Operation | Contributor | Administrator |
|---|---|---|
| View own movies | Yes | Yes |
| View all movies | No | Yes |
| Create movie | Yes | No |
| Edit own movies | Yes | No |
| Delete movies | No | Yes |

### Suggested LDAP Test Accounts

| Username | LDAP Department | Application Role |
|---|---|---|
| EABEL | Sales | Contributor |
| SANDE | Sales | Contributor |
| JWHALEN | Administration | Administrator |

Configure Keycloak to map the appropriate LDAP users or groups to the application roles.

### Practice Activities

1. Configure Keycloak OpenID Connect authentication.
2. Configure LDAP user federation using the provided Windows Server 2022 VM.
3. Map LDAP identities to the `Contributor` and `Administrator` roles.
4. Implement URL-based access restrictions using `web.xml`.
5. Implement service-level authorization using Jakarta Security.
6. Associate newly created movies with the authenticated username.
7. Restrict contributors to viewing and editing only their own movies.
8. Allow administrators to view all movies and delete movies.
9. Verify that unauthorized operations are rejected.

### Success Checks

- EABEL creates a movie that SANDE cannot view or edit.
- SANDE can create and edit movies belonging to SANDE.
- JWHALEN can view movies created by both contributors.
- JWHALEN can delete movies but cannot create or edit movies.
- Contributors cannot delete movies.
- Authorization is enforced by the application, not merely by hiding menu items.