package org.franco.config;

import jakarta.annotation.Priority;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.container.ContainerResponseContext;
import jakarta.ws.rs.container.ContainerResponseFilter;
import jakarta.ws.rs.ext.Provider;
import org.jboss.logging.Logger;

@Provider
@ApplicationScoped
@Priority(Priorities.AUTHENTICATION - 100)
public class RequestLoggingFilter implements ContainerRequestFilter, ContainerResponseFilter {

    private static final Logger LOG = Logger.getLogger(RequestLoggingFilter.class);
    private static final String START_TIME_PROPERTY = "request-start-time";

    @Override
    public void filter(ContainerRequestContext requestContext) {
        requestContext.setProperty(START_TIME_PROPERTY, System.currentTimeMillis());
    }

    @Override
    public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext) {
        Object rawStart = requestContext.getProperty(START_TIME_PROPERTY);
        if (!(rawStart instanceof Long startedAt)) {
            LOG.debugv("method={0} path={1} status={2} durationMs=n/a (start time not recorded)",
                    requestContext.getMethod(),
                    requestContext.getUriInfo().getPath(),
                    responseContext.getStatus());
            return;
        }
        long durationMs = System.currentTimeMillis() - startedAt;
        LOG.infov("method={0} path={1} status={2} durationMs={3}",
                requestContext.getMethod(),
                requestContext.getUriInfo().getPath(),
                responseContext.getStatus(),
                durationMs);
    }
}
