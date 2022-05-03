# InteropEHRate Remote-to-Device (R2D) Backup Protocol's HR Exchange Mobile Library
Reference Implementation of the R2D Backup protocol specified by the InteropEHRate project.

## Installation Guide

The process of integrating the `m-r2db-e` library is quite straightforward, as it is provided as a `war` file, and is hosted in the project's Nexus repository.

In case a gradle project is created, the following line needs to be inserted in the dependencies section of the build.gradle file:
```
implementation(group:'eu.interopehrate', name:mr2db, version: '0.3.7')
```

If the development team importing the library is using Maven instead of Gradle, the same dependency must be expressed with the following Maven syntax:
```
<dependency>
	<groupId>eu.interopehrate</groupId>
	<artifactId>mr2db</artifactId>
	<version>0.3.7</version>
</dependency>
```