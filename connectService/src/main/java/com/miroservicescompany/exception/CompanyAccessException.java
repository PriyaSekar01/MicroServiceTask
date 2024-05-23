package com.miroservicescompany.exception;

public class CompanyAccessException extends RuntimeException {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public CompanyAccessException(String message) {
        super(message);
    }

    public CompanyAccessException(String message, Throwable cause) {
        super(message, cause);
    }
}

