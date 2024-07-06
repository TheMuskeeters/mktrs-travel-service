/*----------------------------------------------------------------------------*/
/* Source File:   GLOBALCONTROLLEREXCEPTIONHANDLER.JAVA                       */
/* Copyright (c), 2024 The Musketeers                                         */
/*----------------------------------------------------------------------------*/
/*-----------------------------------------------------------------------------
 History
 Jul.05/2024  COQ  File created.
 -----------------------------------------------------------------------------*/
package com.themusketeers.travelservice.common.exception.handler;

import static com.themusketeers.travelservice.common.constants.ControllerExceptionHandlerConstants.ERROR_CATEGORY_GENERIC;
import static com.themusketeers.travelservice.common.constants.ControllerExceptionHandlerConstants.ERROR_CATEGORY_PARAMETERS;
import static com.themusketeers.travelservice.common.constants.ControllerExceptionHandlerConstants.PROPERTY_ERRORS;
import static com.themusketeers.travelservice.common.constants.ControllerExceptionHandlerConstants.PROPERTY_ERROR_CATEGORY;
import static com.themusketeers.travelservice.common.constants.ControllerExceptionHandlerConstants.PROPERTY_TIMESTAMP;
import static com.themusketeers.travelservice.common.constants.ControllerExceptionHandlerConstants.REST_CLIENT_API_CALL_ISSUE;
import static com.themusketeers.travelservice.common.constants.ControllerExceptionHandlerConstants.TITLE_BAD_REQUEST_ON_PAYLOAD;
import static com.themusketeers.travelservice.common.constants.ControllerExceptionHandlerConstants.TITLE_VALIDATION_ERROR_ON_SUPPLIED_PAYLOAD;
import static com.themusketeers.travelservice.common.constants.GlobalConstants.COLON_SPACE_DELIMITER;

import java.net.URI;
import java.time.Instant;
import java.util.stream.Stream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.support.WebExchangeBindException;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;
import org.springframework.web.reactive.result.method.annotation.ResponseEntityExceptionHandler;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * Put in a global place the exception handling mechanism, this is shared among all the REST Controllers defined
 * in the application. Annotate a handler with the {@link ExceptionHandler} annotation to indicate which error
 * message should return as the response when it is raised.
 *
 * <p>Check some useful reference links
 * <ul>
 * <li><a href="https://www.baeldung.com/global-error-handler-in-a-spring-rest-api">Global Error Handler in A Spring Rest Api</a></li>
 * <li><a href="https://www.youtube.com/watch?v=4YyJUS_7rQE">Spring 6 and Problem Details</a></li>
 * <li><a href="https://mkyong.com/spring-boot/spring-rest-error-handling-example/">Spring REST Error Handling Example</a></li>
 * </ul>
 * </p>
 *
 * @author COQ - Carlos Adolfo Ortiz Quirós
 */
@RestControllerAdvice
public class GlobalControllerExceptionHandler extends ResponseEntityExceptionHandler {
    private static final Logger log = LoggerFactory.getLogger(GlobalControllerExceptionHandler.class);

    /**
     * Handles {@link RestClient} {@link HttpClientErrorException} when external request gives an unsuccessful HTTP error code, such as 404.
     *
     * @param e Instance to the whole problem.
     * @return An ErrorResponse with the Problem Detail information.
     * @see RuntimeException
     * @see HttpClientErrorException
     * @see ProblemDetail
     * @see ResponseEntity
     */
    @ExceptionHandler(HttpClientErrorException.class)
    public Mono<ResponseEntity<Object>> handleTodoNotFoundException(RuntimeException e, ServerWebExchange exchange) {
        log.error(REST_CLIENT_API_CALL_ISSUE, e);

        var httpStatus = HttpStatus.NOT_FOUND;
        var problemDetail = ProblemDetail.forStatusAndDetail(httpStatus, e.getMessage());
        var instanceURL = exchange.getRequest().getURI().getPath();

        problemDetail.setType(URI.create(instanceURL));
        problemDetail.setInstance(URI.create(instanceURL));
        problemDetail.setProperty(PROPERTY_ERROR_CATEGORY, ERROR_CATEGORY_GENERIC);
        problemDetail.setProperty(PROPERTY_TIMESTAMP, Instant.now());

        // Pending how to extract all headers to comply with HttpHeaders class. May.28/2024
        var httpHeaders = new HttpHeaders();
        return this.createResponseEntity(problemDetail, httpHeaders, httpStatus, exchange);
    }

    @Override
    protected Mono<ResponseEntity<Object>> handleWebExchangeBindException(WebExchangeBindException ex,
                                                                          HttpHeaders headers,
                                                                          HttpStatusCode status,
                                                                          ServerWebExchange exchange) {
        var instanceURL = exchange.getRequest().getURI().getPath();
        var problemDetail = ProblemDetail.forStatusAndDetail(status, TITLE_VALIDATION_ERROR_ON_SUPPLIED_PAYLOAD);

        problemDetail.setTitle(TITLE_BAD_REQUEST_ON_PAYLOAD);
        problemDetail.setType(URI.create(instanceURL));
        problemDetail.setInstance(URI.create(instanceURL));
        problemDetail.setProperty(PROPERTY_TIMESTAMP, Instant.now());
        problemDetail.setProperty(PROPERTY_ERROR_CATEGORY, ERROR_CATEGORY_PARAMETERS);
        problemDetail.setProperty(PROPERTY_ERRORS, Stream.concat(
                ex.getBindingResult()
                    .getFieldErrors()
                    .stream()
                    .map(field -> field.getField() + COLON_SPACE_DELIMITER + field.getDefaultMessage()),
                ex.getBindingResult()
                    .getGlobalErrors()
                    .stream()
                    .map(field1 -> field1.getObjectName() + COLON_SPACE_DELIMITER + field1.getDefaultMessage()))
            .sorted()
            .toList());

        return this.createResponseEntity(problemDetail, headers, status, exchange);
    }
}
