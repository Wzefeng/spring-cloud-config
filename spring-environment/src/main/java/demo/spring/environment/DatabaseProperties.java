package demo.spring.environment;

import lombok.Data;

@Data
public class DatabaseProperties {

    private String jdbcUrl;

    private String username;

    private String password;

}
