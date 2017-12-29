package org.talend.components.servicenow;

import static io.specto.hoverfly.junit.core.HoverflyMode.CAPTURE;
import static io.specto.hoverfly.junit.core.HoverflyMode.SIMULATE;
import static io.specto.hoverfly.junit.core.SimulationSource.classpath;

import io.specto.hoverfly.junit.core.Hoverfly;
import io.specto.hoverfly.junit.core.HoverflyConfig;
import io.specto.hoverfly.junit.core.HoverflyMode;

import java.nio.file.Paths;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSocketFactory;

import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

public class ApiSimulationRule implements TestRule, AutoCloseable {

    private final static SSLSocketFactory defaultContext = HttpsURLConnection.getDefaultSSLSocketFactory();

    private final static String exportSimulationBaseFolder = "target/simulations";

    private Hoverfly hoverfly;

    public ApiSimulationRule(final HoverflyMode mode, final HoverflyConfig config) {
        Runtime.getRuntime().addShutdownHook(new Thread(this::close));

        hoverfly = new Hoverfly(config, mode);
        hoverfly.start();
        HttpsURLConnection.setDefaultSSLSocketFactory(hoverfly.getSslConfigurer().getSslContext().getSocketFactory());
    }

    public Statement apply(Statement base, Description description) {
        return statement(base, description);
    }

    private Statement statement(final Statement base, final Description description) {
        return new Statement() {

            @Override
            public void evaluate() throws Throwable {
                before(description);
                try {
                    base.evaluate();
                } finally {
                    after(description);
                }
            }
        };
    }

    private void before(final Description description) {
        if (SIMULATE.equals(hoverfly.getMode())) {
            hoverfly.simulate(classpath("hoverfly/" + getSimulationFileName(description)));
        }
    }

    private void after(final Description description) {
        if (CAPTURE.equals(hoverfly.getMode())) {
            hoverfly.exportSimulation(Paths.get(exportSimulationBaseFolder, getSimulationFileName(description)));
        }
    }

    private String getSimulationFileName(final Description description) {
        return description.getClassName() + "-" + description.getMethodName() + ".json";
    }

    @Override
    public void close() {
        HttpsURLConnection.setDefaultSSLSocketFactory(defaultContext);
    }
}
