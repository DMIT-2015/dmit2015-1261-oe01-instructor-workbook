package common.security.config;

import jakarta.inject.Inject;
import jakarta.security.enterprise.authentication.mechanism.http.openid.OpenIdConstant;
import jakarta.security.enterprise.identitystore.openid.OpenIdContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Optional;

@WebServlet("/callback")
public class CallbackServlet extends HttpServlet {
    @Inject
    private OpenIdContext _oidContext;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        if (_oidContext != null) {
            Optional<String> originalRequest = _oidContext.getStoredValue(request, response, OpenIdConstant.ORIGINAL_REQUEST);
            String originalRequestString = originalRequest.orElseThrow();
            response.sendRedirect(originalRequestString);
        }

    }
}