package top.wuhunyu.cap.server.web;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

/**
 * cap server 启动器
 *
 * @author wuhunyu
 * @date 2025/06/16 20:57
 **/

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
public class CapServerWebApplication {

    public static void main(String[] args) {
        SpringApplication.run(CapServerWebApplication.class, args);
    }

}
