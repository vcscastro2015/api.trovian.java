package com.trovian.security;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Order(Ordered.LOWEST_PRECEDENCE)
@Slf4j
public class TenantAspect {

    @PersistenceContext
    private EntityManager entityManager;

    @Around("@within(org.springframework.stereotype.Service)")
    public Object applyTenant(ProceedingJoinPoint pjp) throws Throwable {
        Long clienteId = TenantContext.getClienteId();
        if (clienteId != null) {
            entityManager.createNativeQuery(
                    "SELECT set_config('app.cliente_id', :id, true)"
            ).setParameter("id", clienteId.toString())
             .getSingleResult();
        }
        return pjp.proceed();
    }
}
