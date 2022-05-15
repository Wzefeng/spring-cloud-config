package demo.spring.environment;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.core.env.PropertySource;

@org.springframework.context.annotation.PropertySource(name = "extendsPropertySource", value = "classpath:/WEB-INF/user.properties", encoding = "UTF-8")
@Configuration
public class StandardEnvironmentDemo {

    public static void main(String[] args) {
        AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext();
        ctx.register(StandardEnvironmentDemo.class);
        ctx.scan("demo.spring.environment");

        ctx.refresh();

        printEnvironmentPropertySources(ctx.getEnvironment());

        System.out.println("================");
        User user = ctx.getBean(User.class);
        System.out.println(user);

        ctx.close();
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

            MapPropertySource mapPropertySource = (MapPropertySource) propertySource;

            for (String propertyName : mapPropertySource.getPropertyNames()) {
                System.out.printf("\t%s -> %s\n", propertyName, mapPropertySource.getProperty(propertyName));
            }
        }
    }
}
