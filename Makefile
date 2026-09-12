.PHONY: help build test clean \
        run-config run-discovery run-gateway run-product run-order run-inventory \
        run-all stop-all dev \
        watch-config watch-discovery watch-gateway watch-product watch-order watch-inventory \
        run-infra stop-infra restart-infra \
        logs ps verify

MVN := ./mvnw
LOG_DIR := .logs

# --------------------------------------------------
# Colors
# --------------------------------------------------
C_CONFIG    := \033[36m
C_DISCOVERY := \033[35m
C_GATEWAY   := \033[33m
C_PRODUCT   := \033[32m
C_ORDER     := \033[34m
C_INVENTORY := \033[31m
C_RESET     := \033[0m

# --------------------------------------------------
# Help
# --------------------------------------------------

help:
	@echo ""
	@echo "ShopSphere development commands"
	@echo ""
	@echo "Everyday dev loop:"
	@echo "  make dev                Run everything in the foreground, live logs, Ctrl+C to stop"
	@echo "  make watch-<service>    Recompile <service> on save so DevTools hot-restarts it"
	@echo "                          (config|discovery|gateway|product|order|inventory)"
	@echo ""
	@echo "Build:"
	@echo "  make build              Build all services"
	@echo "  make test                Run all tests"
	@echo "  make verify              Run Maven verification"
	@echo "  make clean                Clean build artifacts"
	@echo ""
	@echo "Individual services (background loop still available):"
	@echo "  make run-config | run-discovery | run-gateway | run-product | run-order | run-inventory"
	@echo "  make run-all             Start all services + infra in the background (see .logs/)"
	@echo "  make stop-all             Stop all services + infrastructure"
	@echo ""
	@echo "Infrastructure:"
	@echo "  make run-infra | stop-infra | restart-infra"
	@echo "  make ps                   Show running containers"
	@echo "  make logs                 Follow container logs"
	@echo ""

# --------------------------------------------------
# Maven
# --------------------------------------------------

build:
	$(MVN) clean package -DskipTests

test:
	$(MVN) test

verify:
	$(MVN) clean verify

clean:
	$(MVN) clean

# --------------------------------------------------
# Health-check helper
# usage: $(call wait_http,label,url,timeout_seconds)
# --------------------------------------------------

define wait_http
	@echo "Waiting for $(1)..."; \
	tries=$(3); \
	until curl -sf $(2) >/dev/null 2>&1; do \
		tries=$$((tries - 1)); \
		if [ $$tries -le 0 ]; then \
			echo "$(1) did not become healthy in time"; exit 1; \
		fi; \
		sleep 1; \
	done; \
	echo "$(1) is up."
endef

# --------------------------------------------------
# Individual services
# --------------------------------------------------

run-config:
	$(MVN) -pl services/config-server spring-boot:run

run-discovery:
	$(MVN) -pl services/discovery-server spring-boot:run

run-gateway:
	$(MVN) -pl services/api-gateway spring-boot:run

run-product:
	$(MVN) -pl services/product-service spring-boot:run

run-order:
	$(MVN) -pl services/order-service spring-boot:run

run-inventory:
	$(MVN) -pl services/inventory-service spring-boot:run

# --------------------------------------------------
# Watch (hot reload trigger) — requires `entr`
#   brew install entr   |   apt install entr
# Pair with the matching `make run-<service>` in another terminal.
# --------------------------------------------------

watch-config:
	@$(call watch_impl,services/config-server)

watch-discovery:
	@$(call watch_impl,services/discovery-server)

watch-gateway:
	@$(call watch_impl,services/api-gateway)

watch-product:
	@$(call watch_impl,services/product-service)

watch-order:
	@$(call watch_impl,services/order-service)

watch-inventory:
	@$(call watch_impl,services/inventory-service)

define watch_impl
	command -v entr >/dev/null 2>&1 || { echo "entr not found. Install: brew install entr | apt install entr"; exit 1; }; \
	echo "Watching $(1) — saving a .java file recompiles and DevTools restarts the running instance."; \
	find $(1)/src -name '*.java' | entr -r $(MVN) -pl $(1) compile -o -q
endef

# --------------------------------------------------
# Infrastructure
# --------------------------------------------------

run-infra:
	docker compose up -d
	$(call wait_http,PostgreSQL (product),http://localhost:5432,1)
	@echo "(Postgres/Kafka readiness is enforced by compose healthchecks; 'docker compose ps' to check.)"

stop-infra:
	docker compose down

restart-infra:
	docker compose down
	docker compose up -d

ps:
	docker compose ps

logs:
	docker compose logs -f

# --------------------------------------------------
# dev: foreground, colorized, aggregated, one Ctrl+C kills everything
# --------------------------------------------------

dev: run-infra
	@echo "Starting all services in the foreground. Press Ctrl+C to stop everything."
	@trap 'echo; echo "Shutting down..."; kill 0' INT TERM; \
	( $(MVN) -pl services/config-server spring-boot:run 2>&1 | sed -u "s/^/$(C_CONFIG)[config]$(C_RESET)    /" ) & \
	$(call wait_http,Config Server,http://localhost:8888/actuator/health,60); \
	( $(MVN) -pl services/discovery-server spring-boot:run 2>&1 | sed -u "s/^/$(C_DISCOVERY)[discovery]$(C_RESET) /" ) & \
	$(call wait_http,Discovery Server,http://localhost:8761/actuator/health,60); \
	( $(MVN) -pl services/api-gateway spring-boot:run 2>&1 | sed -u "s/^/$(C_GATEWAY)[gateway]$(C_RESET)   /" ) & \
	( $(MVN) -pl services/product-service spring-boot:run 2>&1 | sed -u "s/^/$(C_PRODUCT)[product]$(C_RESET)   /" ) & \
	( $(MVN) -pl services/order-service spring-boot:run 2>&1 | sed -u "s/^/$(C_ORDER)[order]$(C_RESET)     /" ) & \
	( $(MVN) -pl services/inventory-service spring-boot:run 2>&1 | sed -u "s/^/$(C_INVENTORY)[inventory]$(C_RESET) /" ) & \
	wait

# --------------------------------------------------
# Background run-all / stop-all (kept for CI / scripting use)
# --------------------------------------------------

run-all: run-infra
	@mkdir -p $(LOG_DIR)
	@echo "Starting Config Server..."
	@$(MVN) -pl services/config-server spring-boot:run > $(LOG_DIR)/config-server.log 2>&1 &
	$(call wait_http,Config Server,http://localhost:8888/actuator/health,60)
	@echo "Starting Discovery Server..."
	@$(MVN) -pl services/discovery-server spring-boot:run > $(LOG_DIR)/discovery-server.log 2>&1 &
	$(call wait_http,Discovery Server,http://localhost:8761/actuator/health,60)
	@echo "Starting API Gateway..."
	@$(MVN) -pl services/api-gateway spring-boot:run > $(LOG_DIR)/api-gateway.log 2>&1 &
	@echo "Starting Product Service..."
	@$(MVN) -pl services/product-service spring-boot:run > $(LOG_DIR)/product-service.log 2>&1 &
	@echo "Starting Order Service..."
	@$(MVN) -pl services/order-service spring-boot:run > $(LOG_DIR)/order-service.log 2>&1 &
	@echo "Starting Inventory Service..."
	@$(MVN) -pl services/inventory-service spring-boot:run > $(LOG_DIR)/inventory-service.log 2>&1 &
	@echo ""
	@echo "All services are starting. Tail logs with:"
	@echo "  tail -f $(LOG_DIR)/*.log"

stop-all:
	@echo "Stopping all services..."
	-@pkill -f 'spring-boot:run' 2>/dev/null || true
	@$(MAKE) stop-infra
	@echo "All services and infrastructure stopped."