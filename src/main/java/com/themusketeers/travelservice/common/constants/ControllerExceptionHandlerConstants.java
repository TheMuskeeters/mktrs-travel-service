/*----------------------------------------------------------------------------*/
/* Source File:   CONTROLLEREXCEPTIONHANDLERCONSTANTS.JAVA                    */
/* Copyright (c), 2024 The Musketeers                                         */
/*----------------------------------------------------------------------------*/
/*-----------------------------------------------------------------------------
 History
 Jul.05/2024  COQ  File created.
 -----------------------------------------------------------------------------*/
package com.themusketeers.travelservice.common.constants;

/**
 * Constants associated with Controller Exception Handler.
 *
 * @author COQ - Carlos Adolfo Ortiz Q.
 */
public class ControllerExceptionHandlerConstants {

    /**
     * Global
     */
    public static final String REST_CLIENT_API_CALL_ISSUE = "Rest Client API call issue.";

    /*
     * Error Category
     */
    public static final String ERROR_CATEGORY_GENERIC = "Generic";
    public static final String ERROR_CATEGORY_PARAMETERS = "Parameters";

    /*
     * Title
     */
    public static final String TITLE_BAD_REQUEST_ON_PAYLOAD = "Bad Request on payload";
    public static final String TITLE_VALIDATION_ERROR_ON_SUPPLIED_PAYLOAD = "Validation error on supplied payload";
    public static final String TITLE_TODO_NOT_FOUND = "Todo not found.";

    /*
     * Property
     */
    public static final String PROPERTY_TIMESTAMP = "timestamp";
    public static final String PROPERTY_ERROR_CATEGORY = "errorCategory";
    public static final String PROPERTY_ERRORS = "errors";

    /**
     * Utility class, thus no constructor allowed.
     */
    private ControllerExceptionHandlerConstants() {
    }
}
