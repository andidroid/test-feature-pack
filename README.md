# wildfly ext packages - packaged as Galleon feature-pack

mvn clean install

## `spatial` layer

## `config-ext` layer

## `kafka-streams` layer

####

mvn clean package
mvn versions:set -DnewVersion=0.30.0-SNAPSHOT

### TODO:

TestCustomSecurityEventListener

- register custom-security-event-listener
  <audit-logging>
  <custom-security-event-listener name="test-audit-listener" class-name="config.TestCustomSecurityEventListener" module="io.smallrye.config.ext" />
  </audit-logging>

- add security-event-listener to security-domain

Jackson

- dataformats: jackson-dataformats-text csv , properties (https://github.com/FasterXML/jackson-dataformats-text/tree/2.x)
- jsonschema:
- datatype: https://github.com/FasterXML/jackson-datatypes-misc/tree/2.x
-     jackson-dataformats-binary /protobuf/
- https://github.com/FasterXML/jackson-datatype-joda joda time
- https://github.com/FasterXML/jackson-dataformat-xml




<!-- <dependency>
  <groupId>com.fasterxml.jackson.module</groupId>
  <artifactId>jackson-module-jaxb-annotations</artifactId>
  <version>2.11.0</version>
</dependency> -->
<dependency>
  <groupId>com.fasterxml.jackson.module</groupId>
  <artifactId>jackson-module-jakarta-xmlbind-annotations</artifactId>
  <version>2.13.0</version>
</dependency>
<dependency>
  <groupId>com.fasterxml.jackson.dataformat</groupId>
  <artifactId>jackson-dataformat-xml</artifactId>
  <version>2.18.1</version>
</dependency>
