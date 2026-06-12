package it.restaurant.api.config;

import jakarta.validation.constraints.NotNull;
import java.nio.file.Path;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "restaurant")
public class RestaurantProperties {

    @NotNull
    private Path dataDir = Path.of("data");

    private Demo demo = new Demo();

    public Path getDataDir() {
        return dataDir;
    }

    public void setDataDir(Path dataDir) {
        this.dataDir = dataDir;
    }

    public Demo getDemo() {
        return demo;
    }

    public void setDemo(Demo demo) {
        this.demo = demo;
    }

    public static class Demo {
        private boolean enabled = true;
        private boolean resetEnabled = false;

        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }

        public boolean isResetEnabled() {
            return resetEnabled;
        }

        public void setResetEnabled(boolean resetEnabled) {
            this.resetEnabled = resetEnabled;
        }
    }
}
