package tech.qmates.openchat.web.servlets;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.eclipse.jetty.http.MimeTypes;
import tech.qmates.openchat.domain.entity.RegisteredUser;
import tech.qmates.openchat.domain.usecase.GetAllUserUseCase;
import tech.qmates.openchat.domain.usecase.RegisterUserUseCase;
import tech.qmates.openchat.web.AppFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import static jakarta.servlet.http.HttpServletResponse.*;

public class UsersServlet {

    private final ObjectMapper objectMapper = new ObjectMapper();

    public void handleGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        GetAllUserUseCase usecase = new GetAllUserUseCase(AppFactory.getUserRepository());
        List<RegisteredUser> users = usecase.run();
        List<HashMap<String, Object>> listOfMapUsers = users.stream()
            .map(user -> new HashMap<String, Object>() {{
                put("id", user.uuid().toString());
                put("username", user.username());
                put("about", user.about());
            }})
            .collect(Collectors.toList());
        jsonResponse(SC_OK, listOfMapUsers, response);
    }

    public void handlePost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Map<String, Object> requestBody = stringJsonToMap(request.getInputStream());
        String username = (String) requestBody.get("username");
        String password = (String) requestBody.get("password");
        String about = (String) requestBody.get("about");

        try {
            RegisterUserUseCase usecase = new RegisterUserUseCase(AppFactory.getUserRepository());
            UUID storedUserUUID = usecase.run(username, password, about);

            jsonResponse(SC_CREATED, new HashMap<>() {{
                put("id", storedUserUUID.toString());
                put("username", username);
                put("about", about);
            }}, response);
        } catch (RegisterUserUseCase.UsernameAlreadyInUseException e) {
            textResponse(SC_BAD_REQUEST, "Username already in use.", response);
        }
    }

    protected Map<String, Object> stringJsonToMap(ServletInputStream inputStream) throws IOException {
        //@formatter:off
        TypeReference<HashMap<String, Object>> targetType = new TypeReference<>() { };
        return objectMapper.readValue(inputStream, targetType);
        //@formatter:on
    }

    protected void jsonResponse(
        int statusCode,
        Object responseBody,
        HttpServletResponse response
    ) throws IOException {
        String jsonResponseBody = objectMapper.writeValueAsString(responseBody);
        response.setContentType(MimeTypes.Type.APPLICATION_JSON.toString());
        response.setStatus(statusCode);
        response.getWriter().print(jsonResponseBody);
    }

    protected void textResponse(int statusCode, String text, HttpServletResponse response) throws IOException {
        response.setContentType(MimeTypes.Type.TEXT_PLAIN_UTF_8.toString());
        response.setStatus(statusCode);
        response.getWriter().print(text);
    }

}
