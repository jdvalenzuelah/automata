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

### To generate a graph from a regex expresion
Extract the dist
```sh
$ tar -xf build/distributions/kotlin-based-compiler-1.0.0.tar
```
run passing regex and destination path
```sh
$ ./kotlin-based-compiler-1.0.0/bin/kotlin-based-compiler -e "a(a|b)*b" -o ~/Desktop/regex.png
```
by default, it will generate a nfa:

![closure graph](./.examples/regex.png)

to generate a dfa (generated using subset construction from nfa) pass `--dfa` option
```sh
$ ./kotlin-based-compiler-1.0.0/bin/kotlin-based-compiler -e "a(a|b)*b" -o ~/Desktop/regex2.png --dfa
```
![closure graph](./.examples/regex2.png)
