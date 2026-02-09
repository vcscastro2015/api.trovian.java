package com.trovian.exception;

public class ComissaoNotFoundException extends ResourceNotFoundException {

    public ComissaoNotFoundException(Long id) {
        super("Comissão", "ID", id);
    }
}
