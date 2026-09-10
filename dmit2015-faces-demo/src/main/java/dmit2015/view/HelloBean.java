package dmit2015.view;

import jakarta.enterprise.context.RequestScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Named;
import jakarta.validation.constraints.NotBlank;

@Named
@RequestScoped
public class HelloBean {

    @NotBlank(message = "User Input value is required")
    private String userInput;

    public String getUserInput() {
        return userInput;
    }

    public void setUserInput(String userInput) {
        this.userInput = userInput;
    }

    public String getMessage() {
//        return "Hello, " + userInput;
        return String.format("Hello, %s", userInput);
    }

    public String onSubmit() {
        String message = String.format("Action Hello, %s", userInput);
        var facesMessage = new FacesMessage(message);
        FacesContext.getCurrentInstance().addMessage(null, facesMessage);
        return null;
    }

}
