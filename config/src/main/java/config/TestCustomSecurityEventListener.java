package config;

import org.wildfly.security.auth.server.event.SecurityEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * subsystem=elytron / custom-security-event-listener
 * 
 * 
 * https://github.com/wildfly/wildfly/blob/main/docs/src/main/asciidoc/_elytron/Custom_Components.adoc
 * 
 */
public class TestCustomSecurityEventListener
        implements java.util.function.Consumer<org.wildfly.security.auth.server.event.SecurityEvent> {

    private static final Logger LOGGER = LoggerFactory.getLogger(TestCustomSecurityEventListener.class);

    @Override
    public void accept(SecurityEvent t) {

        LOGGER.info(t.getSecurityIdentity().toString());
    }

}
