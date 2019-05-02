package com.ndexondeck.soapbuilder.exceptions;

/**
 * Created by Nduka Ohadoma on 5/1/2019.
 * To distinguish errors from this package
 */
public class SoapBuilderException extends RuntimeException {

    public SoapBuilderException(String message, Throwable cause) {
        super(message, cause);
    }
}
