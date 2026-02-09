package com.trovian.exception;

public class MotoristaNotFoundException extends ResourceNotFoundException {

    public MotoristaNotFoundException(Long id) {
        super("Motorista", "ID", id);
    }
}
