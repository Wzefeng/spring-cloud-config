package demo.spring.cloud.controller;

import demo.spring.cloud.properties.DatasourceProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class EchoController {

    @Value("${user.username:}")
    private String username;

    @Autowired
    private DatasourceProperties datasourceProperties;

    @GetMapping("/echoValue")
    public String echoValue() {
        return username;
    }

    @GetMapping("/echoDatasourceProperties")
    public DatasourceProperties echoDatasourceProperties() {
        return datasourceProperties;
    }


}
