package demo.spring.environment;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Data
public class User {

    @Value("${user.username}")
    private String username;

    @Value("${user.age}")
    private String age;
}
