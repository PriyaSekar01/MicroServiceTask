package com.miroservicescompany.exception;

import java.io.Serializable;

public class CompanyServiceException extends RuntimeException implements Serializable {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public CompanyServiceException(String message) {
        super(message);
    }

    public CompanyServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}
