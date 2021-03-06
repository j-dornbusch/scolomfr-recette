# scolomfr-recette

## General purpose

A web+shell application designed to assist in the recipe work of the vocabularies used in the ScoLOMFR metadata resource standard.

## Requirements

scolomfr-recette requires **Java 8** for execution. 

## Installation

### From sources

#### Fetch source code

```shell
git clone https://github.com/j-dornbusch/scolomfr-recette.git
```
#### Build war

Duplicate by hand [user properties template](src/main/resources/user.properties.dist), remove .dist extension and type in the logins and password of users allowed to access the application in privileged mode (i.e. to report false positives). 
Then :

```shell
mvn clean install
```

Deploy target/recette.war into a Web application container like Tomcat.

Provide your tomcat credentials as local maven settings.

```xml
<server>
      <id>server-identifier-1234</id>
      <username>tomcat-user</username>
      <password>tomcat-pass</password>
</server>
```
Update pom.xml with your tomcat parameters

```xml
<plugin>
				<groupId>org.apache.tomcat.maven</groupId>
          <!--...-->
				<configuration>
          <!--...-->
					<url>http://tomcat-managers-text-host/manager/text</url>
					<server>server-identifier-1234</server>
          <!--...-->
      </configuration>
</plugin>
```

Then :

```shell
mvn tomcat7:deploy
```
An exemple of Tomcat context file is given [here](doc/recette.xml). It must contain "ver" and "dir" parameters (see [below](#parameters)) 

#### Build web static ressources if necessary

If modified, web static resources like js, css files need to be rebuilt by [Gulp](http://gulpjs.com/)

Move to webapp resources directory.

```shell
cd scolomfr-recette/src/main/webapp/WEB-INF/resources
bower install bower.json
gulp
```
If asked by bower to choose a dependency version number, opt for the newest.

Install one of these missing components if requested (Ubuntu users may need a sudo) :

```shell
npm install -g bower 
npm install --global gulp-cli
npm install --save-dev gulp
npm install --save-dev gulp-less
npm install --save-dev browser-sync
npm install --save-dev gulp-header
npm install --save-dev gulp-clean-css
npm install --save-dev gulp-rename
npm install --save-dev gulp-uglify
```

#### Build shell version

```shell
mvn mvn -f pom-cli.xml package
```

Launch shell version through command line :

```shell
java -Dver=3.2.0 -Ddir=doc/scolomfr -jar target/recette.one-jar.jar
```
#### Parameters

Both web version and shell version expect two parameters at startup.

* ver : default version of vocabularies

If not provided, all commands must be accompanied by the "--version" option.

* dir : relative path to vocabularies directory

Any subfolder of this directory will be treated as a version of the vocabularies if it contains a manifest in yaml format (see for example : [3.2 manifest](doc/scolomfr/scolomfr-v-3-2-0/manifest.yml))

#### Quality measurement

Launch a sonar instance and type :

```shell
mvn clean verify sonar:sonar
```
Unit tests code coverage is measured by Jacoco.

## Shell version

### Features

They are basically the same as those of the web version.

List sources in vdex format for version 3.0 :

```shell
> sources --version 3.1.0 --format vdex
```
Testcases reference :

```shell
> tests
```
Launches test a7 on version 3.2, vocabulary 14 :

```shell
> Test a7 --version 3.2.0 --skostype skosxl --vocabulary http://data.education.fr/voc/scolomfr/scolomfr-voc-014
```
Launches a21 comparison test of version 3.2 and 3.1 :

```shell
> Test a21 --version 3.2.0 --version2 3.1.0 --skostype skos
```

Otherwise, ```help```, ```help test```, ```help sources``` will allow you to access some documentation

### Shell version limitations

'a15' testcase (spellchecking) is not functional in this version.

