package com.trovian.security;

public final class TenantContext {

    private static final ThreadLocal<Long> CURRENT_TENANT = new ThreadLocal<>();

    private TenantContext() {}

    public static void setClienteId(Long clienteId) {
        CURRENT_TENANT.set(clienteId);
    }

    public static Long getClienteId() {
        return CURRENT_TENANT.get();
    }

    public static void clear() {
        CURRENT_TENANT.remove();
    }
}
