# ShopSphere — Tilt dev environment
#
# `tilt up` brings up infra (Kafka + 3 Postgres) and all 6 Spring services in
# the `shopsphere` namespace on the local `kind` cluster.
#
# Hot reload: each service's Dockerfile builds an image whose CMD is
# `mvnw spring-boot:run` (not a fat jar), with DevTools on the classpath.
# live_update syncs changed source straight into the running container and
# re-runs `mvn compile`; DevTools notices the updated classes and restarts
# the app in-process. The image itself is only rebuilt when pom.xml changes.

# Kept low on purpose: 6 concurrent `mvn compile` cold builds competing for
# CPU with Kafka's controller-catchup handshake on a resource-constrained
# kind node is a real failure mode, not a theoretical one — that's what was
# behind the Kafka crash-loop. This trades slower initial `tilt up` for a
# broker that actually gets a fair shot at booting.
update_settings(max_parallel_updates=2)

k8s_yaml('k8s/namespace.yaml')

# --------------------------------------------------
# Infrastructure
# --------------------------------------------------

k8s_yaml([
    'k8s/infra/postgres-product.yaml',
    'k8s/infra/postgres-order.yaml',
    'k8s/infra/postgres-inventory.yaml',
    'k8s/infra/kafka.yaml',
])

for name in ['postgres-product', 'postgres-order', 'postgres-inventory', 'kafka']:
    k8s_resource(name, labels=['infra'])

# --------------------------------------------------
# Helper: one docker_build + k8s_yaml + k8s_resource block per service
# --------------------------------------------------

def java_service(name, path, port, deps=[]):
    docker_build(
        'shopsphere/' + name,
        '.',
        dockerfile=path + '/Dockerfile',
        live_update=[
            sync(path + '/src', '/workspace/' + path + '/src'),
            sync(path + '/pom.xml', '/workspace/' + path + '/pom.xml'),
            run(
                './mvnw -o -q -f ' + path + '/pom.xml compile',
                trigger=[path + '/src', path + '/pom.xml'],
            ),
        ],
        # Only these paths matter to this image; other services, config-repo,
        # and k8s/ shouldn't trigger a rebuild-vs-live_update decision here.
        only=[path, 'pom.xml', 'mvnw', '.mvn'],
    )
    k8s_yaml('k8s/app/' + name + '.yaml')
    k8s_resource(
        name,
        port_forwards=port,
        resource_deps=deps,
        labels=['app'],
    )

# --------------------------------------------------
# Config server — also needs the config-repo baked in; live_update syncs it
# too, so editing config-repo hot-restarts config-server the same way editing
# Java source does.
# --------------------------------------------------

docker_build(
    'shopsphere/config-server',
    '.',
    dockerfile='services/config-server/Dockerfile',
    live_update=[
        sync('services/config-server/src', '/workspace/services/config-server/src'),
        sync('config-repo', '/workspace/config-repo'),
        run(
            './mvnw -o -q -f services/config-server/pom.xml compile',
            trigger=['services/config-server/src'],
        ),
    ],
    only=['services/config-server', 'config-repo', 'pom.xml', 'mvnw', '.mvn'],
)
k8s_yaml('k8s/app/config-server.yaml')
k8s_resource('config-server', port_forwards='8888:8888', labels=['app'])

# --------------------------------------------------
# Everything else, wired up with its real startup dependencies. Tilt draws
# this graph in the UI and won't call a resource ready until its deps are.
# --------------------------------------------------

java_service('discovery-server', 'services/discovery-server', '8761:8761')
java_service('api-gateway', 'services/api-gateway', '8080:8080', deps=['discovery-server'])
java_service(
    'product-service', 'services/product-service', '8081:8081',
    deps=['config-server', 'discovery-server', 'postgres-product'],
)
java_service(
    'order-service', 'services/order-service', '8082:8082',
    deps=['config-server', 'discovery-server', 'postgres-order', 'kafka'],
)
java_service(
    'inventory-service', 'services/inventory-service', '8083:8083',
    deps=['config-server', 'discovery-server', 'postgres-inventory', 'kafka'],
)
