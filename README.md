wildfly ext packages - packaged as Galleon feature-pack
==============================================================================

mvn clean install




`spatial` layer
---------------------------------



`config-ext` layer
---------------------------------

`kafka-streams` layer
---------------------------------


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