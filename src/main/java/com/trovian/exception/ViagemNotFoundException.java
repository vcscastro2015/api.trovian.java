package com.trovian.exception;

public class ViagemNotFoundException extends ResourceNotFoundException {

    public ViagemNotFoundException(Long id) {
        super("Viagem", "ID", id);
    }
}
