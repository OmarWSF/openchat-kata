package acceptance.tech.qmates.openchat.web;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.Test;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class LoginRouteAcceptanceTest extends BaseOpenChatRouteAcceptanceTest{
    @Test
    void successfulLogin() throws IOException, InterruptedException {
        String username = "pippo";
        String password = "password";
        String about = "forza roma";
        String existingUserId = registerUser(username, password, about);

        HttpRequest request = requestBuilderFor("/login")
                .POST(bodyFor(Map.of(
                        "username", username,
                        "password", password
                )))
                .build();
        HttpResponse<String> response = send(request);

        assertEquals(200, response.statusCode());
        assertContentType("application/json", response);
        Map<String, Object> responseBody = stringJsonToMap(response.body());
        assertEquals(username, responseBody.get("username"));
        assertEquals(existingUserId, responseBody.get("id"));
        assertEquals(about, responseBody.get("about"));
    }
}
