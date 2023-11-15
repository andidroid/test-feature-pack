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
        implements
        // org.wildfly.extension.elytron.capabilities._private.SecurityEventListener,
        java.util.function.Consumer<org.wildfly.security.auth.server.event.SecurityEvent> {

    private static final Logger LOGGER = LoggerFactory.getLogger(TestCustomSecurityEventListener.class);

    public TestCustomSecurityEventListener() {
        LOGGER.info("initialize TestCustomSecurityEventListener");
    }

    @Override
    public void accept(SecurityEvent t) {

        LOGGER.info(t.getInstant().toString() + " - " + t.getSecurityIdentity().toString());
    }

}
