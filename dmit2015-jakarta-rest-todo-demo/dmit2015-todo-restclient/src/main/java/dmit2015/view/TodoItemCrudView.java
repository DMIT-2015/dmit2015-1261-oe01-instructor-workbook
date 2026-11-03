package dmit2015.view;

import dmit2015.model.TodoItem;
import dmit2015.service.TodoItemService;
import jakarta.annotation.PostConstruct;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import lombok.Getter;
import lombok.Setter;
import net.datafaker.Faker;
import org.omnifaces.util.Messages;
import org.primefaces.PrimeFaces;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * This Jakarta Faces backing bean class contains the data and event handlers
 * to perform CRUD operations using a PrimeFaces DataTable configured to perform CRUD.
 */
@Named("currentTodoItemCrudView")
@ViewScoped // create this object for one HTTP request and keep in memory if the next is for the same page
public class TodoItemCrudView implements Serializable {

    @Inject
    @Named("currentMpRestClientTodoItemService")
    private TodoItemService todoItemService;

    /**
     * The selected TodoItem instance to create, edit, update or delete.
     */
    @Getter
    @Setter
    private TodoItem selectedTodoItem;

    /**
     * The unique name of the selected TodoItem instance.
     */
    @Getter
    @Setter
    private Long selectedId;

    /**
     * The list of TodoItem objects fetched from the data source
     */
    @Getter
    private List<TodoItem> todoItems;

    /**
     * Fetch all TodoItem from the data source.
     * <p>
     * If FacesContext message sent from init() method annotated with @PostConstruct in the Faces backing bean class are not shown on page:
     * 1) Remove the @PostConstruct annotation from the Faces backing bean class
     * 2) Add metadata tag shown below to the page to execute the init() method
     * <f:metadata>
     * <f:viewParam name="dummy" />
     * <f:event type="postInvokeAction" listener="#{currentBeanView.init}" />
     * </f:metadata>
     */
    @PostConstruct
    public void init() {
        try {
            todoItems = todoItemService.getAllTodoItems();
        } catch (Exception e) {
            Messages.addGlobalError("Error getting todoItems %s", e.getMessage());
        }
    }

    /**
     * Event handler for the New button on the Faces crud page.
     * Create a new selected TodoItem instance to enter data for.
     */
    public void onOpenNew() {
        selectedTodoItem = new TodoItem();
        selectedId = null;
    }


    /**
     * Event handler to generate fake data using DataFaker.
     *
     * @link <a href="https://www.datafaker.net/documentation/getting-started/">Getting started with DataFaker</a>
     */
    public void onGenerateData() {
        try {
            var faker = new Faker();
            selectedTodoItem = TodoItem.of(faker);
            selectedTodoItem.setId(selectedId);
        } catch (Exception e) {
            Messages.addGlobalError("Error generating data {0}", e.getMessage());
        }

    }

    /**
     * Event handler for Save button to create or update data.
     */
    public void onSave() {
        try {

            // If selectedId is null then create new data otherwise update current data
            if (selectedId == null) {
                TodoItem createdTodoItem = todoItemService.createTodoItem(selectedTodoItem);

                // Send a Faces info message that create was successful
                Messages.addGlobalInfo("Create was successful. {0}", createdTodoItem.getId());
                // Reset the selected instance to null
                selectedTodoItem = null;

            } else {
                todoItemService.updateTodoItem(selectedTodoItem);

                Messages.addGlobalInfo("Update was successful");

            }

            // Fetch a list of objects from the data source
            todoItems = todoItemService.getAllTodoItems();
            PrimeFaces.current().ajax().update("dialogs:messages", "form:dt-TodoItems");

            // Hide the PrimeFaces dialog
            PrimeFaces.current().executeScript("PF('manageTodoItemDialog').hide()");
        } catch (RuntimeException ex) { // handle application generated exceptions
            Messages.addGlobalError(ex.getMessage());
        } catch (Exception ex) {    // handle system generated exceptions
            Messages.addGlobalError("Save not successful.");
            handleException(ex);
        }

    }

    /**
     * Event handler for Delete to delete selected data.
     */
    public void onDelete() {
        try {
            // Get the unique name of the Json object to delete
            selectedId = selectedTodoItem.getId();
            todoItemService.deleteTodoItemById(selectedId);
            Messages.addGlobalInfo("Delete was successful for id of {0}", selectedId);
            // Fetch new data from the data source
            todoItems = todoItemService.getAllTodoItems();

            PrimeFaces.current().ajax().update("dialogs:messages", "form:dt-TodoItems");
        } catch (RuntimeException ex) { // handle application generated exceptions
            Messages.addGlobalError(ex.getMessage());
        } catch (Exception ex) {    // handle system generated exceptions
            Messages.addGlobalError("Delete not successful.");
            handleException(ex);
        }

    }

    /**
     * This method is used to handle exceptions and display root cause to user.
     *
     * @param ex The Exception to handle.
     */
    protected void handleException(Exception ex) {
        StringBuilder details = new StringBuilder();
        Throwable causes = ex;
        while (causes.getCause() != null) {
            details.append(ex.getMessage());
            details.append("    Caused by:");
            details.append(causes.getCause().getMessage());
            causes = causes.getCause();
        }
        Messages.create(ex.getMessage()).detail(details.toString()).error().add("errors");
    }

}
