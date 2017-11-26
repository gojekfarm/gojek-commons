# CHANGELOG

## 2.0.0 - 24/11/2017
* Converted amqp, guice, jpa, cache to dropwizard bundles
* Added a module for job processing

## 1.0.0 - 07/11/2017
This version has gone for a major version change. The project name has been changed to gojek-commons and hence the group and artifact names.
* Upgraded the dependent jar (hibernate, dropwizard, guava, guice etc.) versions to the latest
* Renamed the classes PiantaEventBus to CoreEventBus and PiantaException to CoreException. Make sure to change in referencing projects