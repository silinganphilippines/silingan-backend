# Keycloak 24 with PostgreSQL support for Render deployment
FROM quay.io/keycloak/keycloak:24.0.5 as builder

# Enable health and metrics support
ENV KC_HEALTH_ENABLED=true
ENV KC_METRICS_ENABLED=true

# Configure PostgreSQL database
ENV KC_DB=postgres

WORKDIR /opt/keycloak

# Build optimized Keycloak with PostgreSQL
RUN /opt/keycloak/bin/kc.sh build --db=postgres

# Production image
FROM quay.io/keycloak/keycloak:24.0.5

COPY --from=builder /opt/keycloak/ /opt/keycloak/

ENTRYPOINT ["/opt/keycloak/bin/kc.sh"]
CMD ["start", "--optimized", "--hostname-strict=false", "--proxy=edge", "--http-enabled=true"]
