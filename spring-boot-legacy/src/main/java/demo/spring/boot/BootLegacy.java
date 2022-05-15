package demo.spring.boot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.*;

import java.util.HashMap;
import java.util.Map;

@SpringBootApplication
public class BootLegacy {

    public static void main(String[] args) {

        Map<String, Object> defaultProperties = new HashMap<>();
        defaultProperties.put("address", "广东广州");
        ConfigurableApplicationContext ctx = new SpringApplicationBuilder().sources(BootLegacy.class).properties(defaultProperties).run(args);

        printEnvironmentPropertySources(ctx.getEnvironment());
    }

    public static void printEnvironmentPropertySources(ConfigurableEnvironment environment) {
        // 获取当前 Environment
        System.out.println("当前 Environment 为: " + environment.getClass().getName());

        // 获取当前 Environment 配置属性源
        MutablePropertySources propertySources = environment.getPropertySources();
        int i = 1;
        for (PropertySource<?> propertySource : propertySources) {
            System.out.println("================");
            System.out.printf("属性源 %d: %s - %s\n", i++, propertySource.getName(), propertySource.getClass().getName());

            if (!(propertySource instanceof EnumerablePropertySource)) {
                continue;
            }
            EnumerablePropertySource eps = (EnumerablePropertySource) propertySource;

            for (String propertyName : eps.getPropertyNames()) {
                System.out.printf("\t%s -> %s\n", propertyName, eps.getProperty(propertyName));
            }
        }
    }

}
