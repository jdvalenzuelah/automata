# Automata

[![Build Status](https://travis-ci.com/jdvalenzuelah/automata.svg?branch=master)](https://travis-ci.com/jdvalenzuelah/automata)

## Docs

## Architecture

## Requirements
### Install Java SDK 11
- Use [sdkman](http://sdkman.io/)
```sh
$ curl -s "https://get.sdkman.io" | bash
$ source "$HOME/.sdkman/bin/sdkman-init.sh"
$ sdk version
$ sdk install java
```

### Install Gradle 5.3 or higher
```sh
$ sdk update
$ sdk install gradle
```

## Usage
```sh
$ git clone https://github.com/jdvalenzuelah/automata.git ${YOUR_PROJECT_NAME}
$ cd ${YOUR_PROJECT_NAME}
$ ./gradlew clean build
```

### To use web ui to generate nfa, dfa and test regex
Extract the dist
```sh
$ tar -xf build/distributions/kotlin-based-compiler-1.0.0.tar
```
Run compiled
```sh
$ ./kotlin-based-compiler-1.0.0/bin/kotlin-based-compiler
```

Server will be listening on `http://localhost:7659/`

![closure graph](./.examples/webui.png)

logs will be saved to `kotlin-based-compiler.log`
