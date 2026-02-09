package com.trovian.exception;

public class ClienteNotFoundException extends ResourceNotFoundException {

    public ClienteNotFoundException(Long id) {
        super("Cliente", "ID", id);
    }
}
