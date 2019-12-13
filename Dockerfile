# Build the utilities project
FROM openjdk:8 AS utilities

# Build the utilities project
WORKDIR /opt/year4000/utilities
COPY . .
RUN ./gradlew --no-daemon :sponge:assemble

# Copy the mod over from the build process
FROM year4000/minecraft:spongevanilla AS minecraft

COPY --from=utilities /opt/year4000/utilities/sponge/build/libs/utilities-*-all.jar mods/utilities.jar
