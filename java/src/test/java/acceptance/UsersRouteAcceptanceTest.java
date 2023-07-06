package acceptance;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class UsersRouteAcceptanceTest {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final HttpClient httpClient = HttpClient.newBuilder()
        .connectTimeout(Duration.ofSeconds(5))
        .build();

    @Test
    void retrieveEmptyUserListWithNoRegisteredUser() throws IOException, InterruptedException {
        HttpRequest request = requestBuilderFor("/users").GET().build();

        HttpResponse<String> response = send(request);

        assertEquals(200, response.statusCode());
        assertEquals("[]", response.body());
    }

    @Test
    void registerUser() throws IOException, InterruptedException {
        HttpRequest request = requestBuilderFor("/users")
            .POST(bodyFor(new HashMap<>() {{
                put("username", "pippo");
                put("password", "pluto123");
                put("about", "About pippo user.");
            }}))
            .build();

        HttpResponse<String> response = send(request);

        assertEquals(201, response.statusCode());
        Map<String, Object> responseBody = stringJsonToMap(response.body());
        assertEquals("pippo", responseBody.get("username"));
        assertEquals("About pippo user.", responseBody.get("about"));
        assertDoesNotThrow(() -> UUID.fromString((String) responseBody.get("id")));
    }

    @Test
    void usernameAlreadyExist() throws IOException, InterruptedException {
        HttpResponse<String> response = send(requestBuilderFor("/users")
            .POST(bodyFor(new HashMap<>() {{
                put("username", "pippo");
                put("password", "pluto123");
                put("about", "About pippo user.");
            }})).build());
        assertEquals(201, response.statusCode());

        response = send(requestBuilderFor("/users")
            .POST(bodyFor(new HashMap<>() {{
                put("username", "pippo");
                put("password", "cinesca123");
                put("about", "Another about.");
            }})).build());
        assertEquals(400, response.statusCode());
        assertEquals("Username already in use.", response.body());
    }


    private HttpRequest.BodyPublisher bodyFor(Object requestBody) throws JsonProcessingException {
        return HttpRequest.BodyPublishers.ofString(objectMapper.writeValueAsString(requestBody));
    }

    private static HttpRequest.Builder requestBuilderFor(String route) {
        return HttpRequest.newBuilder().uri(URI.create("http://localhost:8000" + route));
    }

    private HttpResponse<String> send(HttpRequest request) throws IOException, InterruptedException {
        return httpClient.send(request, HttpResponse.BodyHandlers.ofString());
    }

    private Map<String, Object> stringJsonToMap(String body) throws IOException {
        TypeReference<HashMap<String, Object>> targetType = new TypeReference<>() { };
        return objectMapper.readValue(body, targetType);
    }
}
