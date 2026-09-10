.PHONY: help build test clean \
        run-config run-discovery run-gateway run-product \
        run-infra stop-infra restart-infra \
        logs ps \
        verify

# --------------------------------------------------
# Configuration
# --------------------------------------------------

MVN := ./mvnw

# --------------------------------------------------
# Help
# --------------------------------------------------

help:
	@echo ""
	@echo "ShopSphere development commands"
	@echo ""
	@echo "Build:"
	@echo "  make build           Build all services"
	@echo "  make test            Run all tests"
	@echo "  make verify          Run Maven verification"
	@echo "  make clean           Clean build artifacts"
	@echo ""
	@echo "Services:"
	@echo "  make run-config      Run Config Server"
	@echo "  make run-discovery   Run Eureka Discovery Server"
	@echo "  make run-gateway     Run API Gateway"
	@echo "  make run-product     Run Product Service"
	@echo ""
	@echo "Infrastructure:"
	@echo "  make run-infra       Start infrastructure"
	@echo "  make stop-infra      Stop infrastructure"
	@echo "  make restart-infra   Restart infrastructure"
	@echo "  make ps              Show running containers"
	@echo "  make logs            Follow container logs"
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
# Services
# --------------------------------------------------

run-config:
	$(MVN) -pl services/config-server spring-boot:run

run-discovery:
	$(MVN) -pl services/discovery-server spring-boot:run

run-gateway:
	$(MVN) -pl services/api-gateway spring-boot:run

run-product:
	$(MVN) -pl services/product-service spring-boot:run

# --------------------------------------------------
# Infrastructure
# --------------------------------------------------

run-infra:
	docker compose up -d

stop-infra:
	docker compose down

restart-infra:
	docker compose down
	docker compose up -d

ps:
	docker compose ps

logs:
	docker compose logs -f