package io.heartpattern.javagpt;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.module.jsonSchema.JsonSchemaGenerator;
import io.heartpattern.javagpt.dto.ChatRequest;
import io.heartpattern.javagpt.dto.ChatResponse;
import io.heartpattern.javagpt.dto.Message;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

class JavaGptInvocationHandler implements InvocationHandler {
    private final String apiKey;
    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final ObjectMapper objectMapper = new ObjectMapper()
            .setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE);
    private final JsonSchemaGenerator jsonSchemaGenerator = new JsonSchemaGenerator(objectMapper);

    public JavaGptInvocationHandler(String apiKey) {
        this.apiKey = apiKey;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        String className = proxy.getClass().getName();
        String methodName = method.getName();
        String returnType = method.getReturnType().getName();
        String returnTypeDescription = objectMapper.writeValueAsString(jsonSchemaGenerator.generateSchema(method.getReturnType()));

        Gpt gpt = method.getDeclaredAnnotation(Gpt.class);
        String description = gpt == null ? null : gpt.value();

        StringBuilder builder = new StringBuilder();
        builder.append("Evaluate method. Emit only return value in result field of json format without and description or decorator\n");
        builder.append("Class: ").append(className).append("\n");
        builder.append("Method: ").append(methodName).append("\n");
        if (description != null)
            builder.append("Method description: ").append(description).append("\n");
        builder.append("Return type: ").append(returnType).append("\n");
        builder.append("Return type json schema: ").append(returnTypeDescription).append("\n");
        builder.append("Arguments: ").append("\n");
        for (int i = 0; i < args.length; i++) {
            Object arg = args[i];
            String argName = method.getParameters()[i].getName();
            String argValue = objectMapper.writeValueAsString(arg);
            builder.append("  ").append(argName).append(": ").append(argValue).append("\n");
        }

        String prompt = builder.toString();

        for (int i = 0; ; i++) {
            ChatRequest request = new ChatRequest(
                    "gpt-3.5-turbo",
                    List.of(
                            new Message(Message.Role.USER, prompt)
                    )
            );

            ChatResponse response = objectMapper.readValue(
                    httpClient.send(
                            HttpRequest.newBuilder()
                                    .uri(new URI("https://api.openai.com/v1/chat/completions"))
                                    .header("Authorization", "Bearer " + apiKey)
                                    .header("Content-Type", "application/json")
                                    .POST(
                                            HttpRequest.BodyPublishers.ofString(
                                                    objectMapper.writeValueAsString(request)
                                            )
                                    )
                                    .build(),
                            HttpResponse.BodyHandlers.ofString()
                    ).body(),
                    ChatResponse.class
            );

            String rawResponse = response.choices().get(0).message().content();

            try {
                JsonNode node = objectMapper.readTree(rawResponse);
                return objectMapper.treeToValue(node.get("result"), method.getReturnType());
            } catch (Exception e) {
                if (i == 2) {
                    throw new Exception("Invalid chat gpt response: " + rawResponse, e);
                }
            }
        }
    }
}
