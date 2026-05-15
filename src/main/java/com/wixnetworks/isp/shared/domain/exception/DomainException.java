package com.wixnetworks.isp.shared.domain.exception;




public abstract class DomainException extends RuntimeException {

    protected DomainException(String message) {
        super(message);
    }
    protected DomainException(String message, Throwable causa) {
        super(message, causa);
    }
}