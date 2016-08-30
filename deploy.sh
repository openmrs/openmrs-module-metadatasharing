#!/usr/bin/env bash

mvn org.openmrs.maven.plugins:openmrs-sdk-maven-plugin:3.2.1:setup-sdk -DbatchAnswers=n
mvn org.openmrs.maven.plugins:openmrs-sdk-maven-plugin:3.2.1:release -DdefaultVersions -DbintrayUsername=$BINTRAY_USERNAME -DbintrayApiKey=$BINTRAY_API_KEY -DgithubUsername=$GITHUB_USERNAME -DgithubPassword=$GITHUB_PASSWORD
