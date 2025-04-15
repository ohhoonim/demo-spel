package com.ohhoonim.demo_spel.inBean;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import org.springframework.test.context.TestPropertySource;
import org.springframework.web.client.RestClient;

@SpringBootTest
@TestPropertySource("classpath:application-test.properties")
public class ExpressionInBeanDefinitionsTest {
    // 환경 변수에 대해 이해하기 
    // environment : org.springframework.core.env.Environment
    // systemProperties
    // systemEnvironment

    @Autowired
    private Environment environment;

    @Test
    public void environmentTest() {
        // 애플리케이션 속성
        assertThat(environment.getProperty("java.version"))
                .isEqualTo("21.0.3");
    }

    @Test
    public void systemPropertiesTest() {
        // JVM의 시스템 속성
        // System.getProperties() : java.util.Properties
        // spring의 Environment에서도 읽을 수 있다.
        assertThat(System.getProperty("java.version"))
                .isEqualTo("21.0.3");
    }

    @Test
    public void systemEnvironmentTest() {
        // 운영체제 시스템 환경변수
        // System.getenv() : java.util.Map
        // spring의 Environment에서도 읽을 수 있다.
        assertThat(System.getenv("JAVA_HOME"))
                .isEqualTo("/Library/Java/JavaVirtualMachines/temurin-21.jdk/Contents/Home");
    }

    // SpEL을 통해 시스템 속성 및 환경 변수를 읽어오는 방법

    @Value("#{ systemProperties['java.version'] }")
    private String javaVersion;

    @Value("#{ systemEnvironment['JAVA_HOME'] }")
    private String javaHome;

    @Value("#{ 2 + 3 }")
    private int simpleExpression;

    @Test
    public void testSystemProperties() {
        // SpEL을 통해 시스템 속성(java.version)을 읽어오는지 확인
        assertThat(javaVersion).isEqualTo("21.0.3");
    }

    @Test
    public void testSystemEnvironment() {
        // SpEL을 통해 시스템 환경 변수(JAVA_HOME)을 읽어오는지 확인
        assertThat(javaHome).isEqualTo("/Library/Java/JavaVirtualMachines/temurin-21.jdk/Contents/Home");
    }

    @Test
    public void testSimpleExpression() {
        // 간단한 SpEL 표현식 테스트
        assertThat(simpleExpression).isEqualTo(5);
    }

    @Value("#{environment['ohhoonim.name']}")
    private String ohhoonimName;

    @Test
    public void applicationPropertyTest() {
        // SpEL을 통해 application-test.properties의 속성(ohhoonim.name)을 읽어오는지 확인
        assertThat(ohhoonimName).isEqualTo("ohhoonim");
    }

    @TestConfiguration
    public static class TestConfig {

        @Bean
        public RestClient restClient(RestClient.Builder builder,
                @Value("#{environment['ohhoonim.restclient.url']}") String url) {
                // @Value("${ohhoonim.restclient.url}") String url) {
                // "${}" 는 application(.properties, .yml) 속성값을 가져올 때 사용한다.
            return builder.baseUrl(url).build();
        }

    }

    // @Autowired
    // private RestClient restClient;

    // "#{}"을 사용하면 빈주입도 가능하다 
    @Value("#{restClient}")
    private RestClient restClient;
    
    @Test
    public void testBeanProperty() {
        assertThat(restClient)
                .isInstanceOf(RestClient.class);

    }

}
