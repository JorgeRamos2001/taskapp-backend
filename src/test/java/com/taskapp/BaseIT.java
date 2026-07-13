package com.taskapp;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.taskapp.auth.entity.User;
import com.taskapp.auth.entity.enums.UserProvider;
import com.taskapp.auth.repository.UserRepository;
import com.taskapp.auth.security.JwtService;
import com.taskapp.config.TestcontainersConfig;
import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.client.RestClient;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@Import(TestcontainersConfig.class)
public abstract class BaseIT {

    @LocalServerPort
    private int port;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private Flyway flyway;

    protected RestClient restClient;
    protected ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        flyway.clean();
        flyway.migrate();
        restClient = RestClient.builder()
                .baseUrl("http://localhost:" + port)
                .defaultStatusHandler(HttpStatusCode::isError, (req, res) -> {})
                .build();
    }

    protected HttpHeaders authHeader(String email, String password) {
        User user = userRepository.findByEmail(email).orElseGet(() -> {
            User newUser = User.builder()
                    .name("Test User")
                    .email(email)
                    .password(passwordEncoder.encode(password))
                    .provider(UserProvider.LOCAL)
                    .build();
            return userRepository.save(newUser);
        });
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(jwtService.generateAccessToken(user));
        return headers;
    }
}
