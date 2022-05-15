package demo.spring.environment;

import org.springframework.context.support.ClassPathXmlApplicationContext;

public class PropertySourceResolvingDemo {

    public static void main(String[] args) {
        String location = "classpath:/WEB-INF/application-placeHolderResolving.xml";
        ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext(location);

        DatabaseProperties dbProperties = ctx.getBean(DatabaseProperties.class);
        System.out.println(dbProperties);

        StandardEnvironmentDemo.printEnvironmentPropertySources(ctx.getEnvironment());

        ctx.close();
    }

}
