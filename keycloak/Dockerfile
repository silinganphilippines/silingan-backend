# Keycloak 24 with PostgreSQL support for Render deployment
FROM quay.io/keycloak/keycloak:24.0.5 as builder

ENV KC_HEALTH_ENABLED=true
ENV KC_METRICS_ENABLED=true
ENV KC_DB=postgres

WORKDIR /opt/keycloak

RUN /opt/keycloak/bin/kc.sh build --db=postgres

FROM quay.io/keycloak/keycloak:24.0.5

COPY --from=builder /opt/keycloak/ /opt/keycloak/

ENTRYPOINT ["/opt/keycloak/bin/kc.sh"]
CMD ["start", "--optimized", "--hostname-strict=false", "--proxy=edge", "--http-enabled=true"]
