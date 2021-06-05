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

Extract the dist
```sh
$ tar -xf build/distributions/kotlin-based-compiler-1.0.0.tar
```

### cli tool usage to generate a scanner based on an CoCo/R .atg file specification

run passing spec atg file and destination path
```sh
$ ./kotlin-based-compiler-1.0.0/bin/kotlin-based-compiler .examples/atgs/KotlinHexNumber.ATG kotlin/org/github/compiler/generated
```

the generated code will include the kotlin specification of the atg file ro be used by the scanner.

### To use the scanner

recompile code, now with the generated bits as main class and extract dist
```sh
$ ./gradlew -PmainClass=org.github.compiler.generated.HexNumberSpecKt clean build
$ tar -xf build/distributions/kotlin-based-compiler-1.0.0.tar 
```

now run passing a file to scan, this will print the recognized tokens
```sh
$ ./kotlin-based-compiler-1.0.0/bin/kotlin-based-compiler .examples/atgs/tests/hexnumber.txt 
```
