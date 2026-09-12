# Cold build only — this is the image Tilt loads on `tilt up` / on a
# pom.xml change. Day-to-day source edits never re-run this: Tilt's
# live_update syncs changed files into the running container instead.
#
# Built with -f against this service's own pom.xml (not -pl/-am from the
# reactor root) so the Docker build context can stay scoped to just this
# service directory — the reactor root would otherwise require every
# sibling module to exist on disk just to validate the module list.
FROM eclipse-temurin:25-jdk AS build
WORKDIR /workspace
COPY . .
RUN ./mvnw -q -f services/order-service/pom.xml dependency:go-offline
RUN ./mvnw -q -f services/order-service/pom.xml compile

FROM eclipse-temurin:25-jdk
WORKDIR /workspace
COPY --from=build /root/.m2 /root/.m2
COPY --from=build /workspace /workspace
EXPOSE 8082
CMD ["./mvnw", "-o", "-f", "services/order-service/pom.xml", "spring-boot:run", "-Dspring-boot.run.fork=false"]
