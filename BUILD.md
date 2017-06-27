# Build

This project builds using [Gradle](https://gradle.org/install) with a Makefile entrypoint. 

# Common Tasks

**Prepare Docker Image** - `make image`

**Release Docker Image** - `make release`

# Gradle Tasks

Running gradle tasks directly allows for fine grain task execution. 

**Build War File** - `./gradlew war`

**Run Unit Tests** - `./gradlew test`

**Build Eclipse project** - `./gradlew eclipse`

