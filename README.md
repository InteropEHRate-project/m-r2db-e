# Remote-to-Device Backup (R2DB) Protocol's HR Exchange Mobile Library
Reference Implementation of the R2D Backup protocol specified by the InteropEHRate project.

## Installation Guide

The process of integrating the `m-r2db-e` library is quite straightforward, as it is provided as a `war` file, and is hosted in the project's Nexus repository.

In case a gradle project is created, the following line needs to be inserted in the dependencies section of the build.gradle file:
```
implementation(group:'eu.interopehrate', name:mr2db, version: '1.0.0')
```

If the development team importing the library is using Maven instead of Gradle, the same dependency must be expressed with the following Maven syntax:
```
<dependency>
	<groupId>eu.interopehrate</groupId>
	<artifactId>mr2db</artifactId>
	<version>0.3.7</version>
</dependency>
```

## User Guide
Using the `m-r2db-e` library means obtaining an instance of the class `MR2DBackup` and then invoking its methods to upload encrypted health data to the S-EHR Cloud, as well as downloading and decrypting such data from it. How to obtain an instance of `MR2DBackup`, is shown in the following example:
```
 MR2DBackup mr2db = new MR2DBackup();
 ```
In addition, an instance of the `SEHRCloudClient` class needs to be instantiated that is responsible for the connection to the preferred S-EHR Cloud provider of the citizen, as shown in the code snippet below:
```
SEHRCloudInterface cloudInterface = SEHRCloudClient.getClient().create(SEHRCloudInterface.class);
```

The exact methods provided by the `m-r2db-e` library are listed below:
* `register`: Citizen registers to the S-EHR Cloud.
* `login`: Citizen logs into the S-EHR Cloud they have previously registered.
* `downloadConsentStore`: Citizen downloads the consent that allows the S-EHR Cloud provider to store the citizen’s health data in an encrypted manner.
* `signAndUploadConsentStore`: Citizen digitally signs the consent that allows the S-EHR Cloud provider to store the citizen’s health data in an encrypted manner and uploads it in the S-EHR Cloud.
* `downloadConsentShare`: Citizen downloads the consent that allows the S-EHR Cloud provider to share the citizen’s health data with HCPs from authorised Healthcare Institutions.
* `signAndUploadConsentShare`: Citizen digitally signs the consent that allows the S-EHR Cloud provider to share the citizen’s health data with HCPs from authorised Healthcare Institutions and uploads it in the S-EHR Cloud.
* `withdrawConsentShare`: Citizen revokes the consent that allows the S-EHR Cloud provider to share the citizen’s health data with HCPs from authorised Healthcare Institutions.
* `removeAccount`: Citizen removes his/her account from the S-EHR Cloud and their health data is dropped.
* `create`: Encrypts (using the R2D Encrypted Communication library ) and uploads health data to the S-EHR Cloud.
* `get`: Downloads and decrypts (using the R2D Encrypted Communication library) health data that is already uploaded on the S-EHR Cloud. If the data is not found an error message is received.
* `listBuckets`: Returns a list of the buckets that are related to a Citizen.
* `listObjects`: Returns a list of objects in a specific bucket.
* `getBundlesInfo`: Citizen downloads metadata for a specific health data stored in the S-EHR Cloud.
* `getAuditInfo`: Citizen downloads audit information from the S-EHR Cloud.
