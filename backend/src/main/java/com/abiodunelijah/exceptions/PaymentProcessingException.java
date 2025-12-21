package com.abiodunelijah.exceptions;

public class PaymentProcessingException extends RuntimeException{
    public PaymentProcessingException(String message){
        super(message);
    }
}
