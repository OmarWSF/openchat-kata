package acceptance;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
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

    @Test
    void retrieveEmptyUserListWithNoRegisteredUser() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(5))
            .build();

        HttpRequest request = HttpRequest.newBuilder()
            .GET()
            .uri(URI.create("http://localhost:8000/users"))
            .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        assertEquals("[]", response.body());
    }

    @Test
    void registerUser() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(5))
            .build();

        HttpRequest request = HttpRequest.newBuilder()
            .POST(HttpRequest.BodyPublishers.ofString(
                "{\"username\":\"pippo\", \"password\":\"pluto123\", \"about\":\"About pippo user.\"}"
            ))
            .uri(URI.create("http://localhost:8000/users"))
            .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode());
        Map<String, Object> responseBody = stringJsonToMap(response.body());
        assertEquals("pippo", responseBody.get("username"));
        assertEquals("About pippo user.", responseBody.get("about"));
        assertDoesNotThrow(() -> UUID.fromString((String) responseBody.get("id")));
    }

    private Map<String, Object> stringJsonToMap(String body) throws IOException {
        TypeReference<HashMap<String, Object>> targetType = new TypeReference<>() { };
        return objectMapper.readValue(body, targetType);
    }
}
