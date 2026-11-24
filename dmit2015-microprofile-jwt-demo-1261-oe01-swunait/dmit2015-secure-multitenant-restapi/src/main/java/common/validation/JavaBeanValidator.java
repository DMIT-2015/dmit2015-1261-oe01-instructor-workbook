package common.validation;

import jakarta.json.Json;
import jakarta.json.JsonObjectBuilder;
import jakarta.validation.Validation;

/**
 * This class contains a single class-level (static) method checking for Jakarta Validation
 * constraint annotation violations in a JavaBean and returning a JSON object string that
 * contains the name and message of each validation error.
 * <p>
 * The following example shows how to validate a Region object named newRegion.
 * {@snippet :
 *      String errorMessage = JavaBeanValidator.validateBean(newRegion);
 *      if (errorMessage != null) {
 *          return Response
 *                  .status(Response.Status.BAD_REQUEST)
 *                  .entity(errorMessage)
 *                  .build();
 *      }
 * }
 *
 * @version 2024.06.24
 */
public class JavaBeanValidator {

    /**
     * Check the `typeInstance` parameter for Jakarta Validation constraint annotation violations
     * and return a JSON object string with the name and error message for each validation error.
     *
     * @param typeInstance The object with bean validation constraint annotations to validate.
     * @return A JSON object String with the property name and message for each validation error message.
     * @param <T> This is a generic method that can operator on a type specified by the caller.
     */
    public static <T> String validateBean(T typeInstance) {

        try (var validatorFactory = Validation.buildDefaultValidatorFactory()) {
            var constraintViolations = validatorFactory.getValidator().validate(typeInstance);
            if (!constraintViolations.isEmpty()) {
                JsonObjectBuilder jsonObjectBuilder = Json.createObjectBuilder();
                for (var singleConstraintViolation : constraintViolations) {
                    jsonObjectBuilder.add(singleConstraintViolation.getPropertyPath().toString(), singleConstraintViolation.getMessage());
                }
                return jsonObjectBuilder.build().toString();
            }
        }

        return null;
    }
}