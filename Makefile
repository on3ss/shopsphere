.PHONY: help build test verify clean \
        cluster-up cluster-down cluster-status \
        dev down

MVN := ./mvnw
CLUSTER := shopsphere

help:
	@echo ""
	@echo "ShopSphere development commands"
	@echo ""
	@echo "Cluster + dev loop:"
	@echo "  make cluster-up      Create the local kind cluster (one-time / after cluster-down)"
	@echo "  make dev             tilt up — builds images, applies k8s manifests, hot reload on save"
	@echo "  make down            tilt down — tears down the app/infra resources, keeps the cluster"
	@echo "  make cluster-down    Delete the kind cluster entirely"
	@echo "  make cluster-status  kubectl get pods -n shopsphere"
	@echo ""
	@echo "Build (no cluster needed):"
	@echo "  make build           Build all services"
	@echo "  make test            Run all tests"
	@echo "  make verify          Run Maven verification"
	@echo "  make clean           Clean build artifacts"
	@echo ""

# --------------------------------------------------
# Cluster lifecycle
# --------------------------------------------------

cluster-up:
	@if kind get clusters 2>/dev/null | grep -qx $(CLUSTER); then \
		echo "kind cluster '$(CLUSTER)' already exists."; \
	else \
		kind create cluster --name $(CLUSTER) --config kind-config.yaml; \
	fi

cluster-down:
	kind delete cluster --name $(CLUSTER)

cluster-status:
	kubectl get pods -n shopsphere

# --------------------------------------------------
# Dev loop — Tilt owns build order, health checks, log aggregation and
# hot reload; see the Tiltfile.
# --------------------------------------------------

dev: cluster-up
	tilt up

down:
	tilt down

# --------------------------------------------------
# Plain Maven — useful in CI or when you just want a build, no cluster
# --------------------------------------------------

build:
	$(MVN) clean package -DskipTests

test:
	$(MVN) test

verify:
	$(MVN) clean verify

clean:
	$(MVN) clean
