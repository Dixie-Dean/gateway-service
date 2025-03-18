package dev.dixie.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.DefaultResponseErrorHandler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

public class ResponseErrorHandler extends DefaultResponseErrorHandler {

    @Override
    public void handleError(ClientHttpResponse response) throws IOException {
        if (response.getStatusCode().equals(HttpStatus.NOT_FOUND)) {
            throw new ImagerPostNotFoundException();
        } else if (response.getStatusCode().equals(HttpStatus.BAD_REQUEST)) {
            throw new ImagerPostValidationException(readExceptionMessage(response));
        } else {
            super.handleError(response);
        }
    }

    private String readExceptionMessage(ClientHttpResponse response) {
        try {
            return new BufferedReader(new InputStreamReader(response.getBody(), StandardCharsets.UTF_8))
                    .lines().collect(Collectors.joining("\n"));
        } catch (IOException exception) {
            return "Couldn't handle exception | " + exception.getMessage();
        }
    }
}
