package com.ohhoonim.demo_spel.evaluation;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.SpelCompilerMode;
import org.springframework.expression.spel.SpelParserConfiguration;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.SimpleEvaluationContext;

import com.ohhoonim.demo_spel.inventor.Inventor;
import com.ohhoonim.demo_spel.inventor.Society;

public class EvaluationTest {

    @Test
    @DisplayName("리터럴 문자열 표현식을 평가하는 방법")
    public void literalEvalTest() {
        ExpressionParser parser = new SpelExpressionParser();
        Expression exp = parser.parseExpression("'Hello world'");
        String message = (String) exp.getValue();

        assertThat(message).isEqualTo("HelloWorld");

    }

    @Test
    public void inventorMembersTest() {
        var inventor1 = new Inventor("Albert Einstein", "Germany");
        var inventor2 = new Inventor("Marie Curie", "Poland");

        var society = new Society();
        society.getMembers().add(inventor1);
        society.getMembers().add(inventor2);

        assertThat(society.isMember("Marie Curie")).isTrue();
    }

    @Test
    @DisplayName("리터럴 문자열 메소드 호출 평가")
    public void literalMethodEvalTest() {
        var parser = new SpelExpressionParser();
        var expression = parser.parseExpression("'Hello world'.concat('!')");
        var message = expression.getValue(String.class);
        assertThat(message).isEqualTo("Hello world!"); // <- !(느낌표)가 합쳐졌다.
    }

    @Test
    @DisplayName("리터럴의 byte 속성에 접근")
    public void literalByteEvalTest() {
        var parser = new SpelExpressionParser();
        var expression = parser.parseExpression("'Hello world'.bytes"); // getBytes() 메소드 호출
        var message = expression.getValue(byte[].class);
        assertThat(message).isEqualTo("Hello world".getBytes());
    }

    @Test
    @DisplayName("문자열 생성자 호출")
    public void stringCustomConstructorEvalTest() {
        var parser = new SpelExpressionParser();
        var expression = parser.parseExpression("new String('Hello world').toUpperCase()");
        var message = expression.getValue(String.class);
        assertThat(message).isEqualTo("HELLO WORLD");
    }

    @Test
    @DisplayName("SpEL의 일반적인 사용법")
    public void spelGeneralEvalTest() {
        var inventor = new Inventor("Albert Einstein", "Germany");

        var parser = new SpelExpressionParser();
        var expression = parser.parseExpression("name");
        var name = expression.getValue(inventor, String.class);

        assertThat(name).isEqualTo("Albert Einstein");
    }

    /*
     * SimpleEvaluationContext 
     * SpEL 언어 구문의 전체 범위를 필요로 하지 않고 의미 있게 제한되어야 하는 
     * 표현식 범주에 대한 필수 SpEL 언어 기능 및 구성 옵션의 하위 집합을 제공합니다. 
     * 데이터 바인딩 표현식 및 속성 기반 필터 등을 포함하되 이에 국한되지 않습니다.
     * 
     * level of support:
     * - 읽기 전용 액세스를 위한 데이터 바인딩
     * - 읽기 및 쓰기 액세스를 위한 데이터 바인딩
     * - 사용자 지정 PropertyAccessor(일반적으로 리플렉션 기반이 아님), DataBindingPropertyAccessor와 결합될 수 있음
     */

    /*
     * SandardEvaluationContext
     * SpEL 언어 기능 및 구성 옵션의 전체 세트를 제공합니다. 
     * 이를 사용하여 기본 루트 객체를 지정하고 사용 가능한 모든 평가 관련 전략을 구성할 수 있습니다.      
     */

    @Test
    @DisplayName("EvaluationContext와 type conversion")
    public void evaluationContextTypeConversionTest() {
        record Simple(List<Boolean> booleanList) {
        }
        var simple = new Simple(new ArrayList<>());
        simple.booleanList.add(true);

        assertThat(simple.booleanList().get(0))
                .isTrue();
        ;

        var context = SimpleEvaluationContext.forReadOnlyDataBinding().build();

        var parser = new SpelExpressionParser();
        parser.parseExpression("booleanList[0]")
                .setValue(context, simple, "false");
        // list 요소가 boolean인것은 알아서 변환해준다.

        assertThat(simple.booleanList().get(0))
                .isFalse();
    }

    @Test
    @DisplayName("Parser Configuration")
    public void parserConfigurationTest() {
        // SpelParserConfiguration을 사용하면 참조값을 자동으로 초기화하거나
        // 컬렉션의 사이즈를 자동으로 늘릴수 있다. 
        record Demo(List<String> list) {
        }

        var config = new SpelParserConfiguration(true, true);
        var parser = new SpelExpressionParser(config);
        var expression = parser.parseExpression("list[3]");

        Demo demo = new Demo(new ArrayList<>());

        Object resultList = expression.getValue(demo);

        assertThat(resultList).isEqualTo("");
        // list의 index 3에 접근했지만 에러가 발생하지 않는다
    }

    @Test
    @DisplayName("SpelCompilerMode")
    public void spelComiplerModeTest() {
        // SpEL 컴파일러는 반복적인 평가에서도 타입 정보가 변경되지 않는 표현식에 할 때 효과적이다

        // default는 비활성화 되어있다.
        // SpelCompilerMode.MIXED
        // SpelCompilerMode.IMMEDIATE
        // SpelCompilerMode.COMMON

        record Message(String payload) {
        }

        var config = new SpelParserConfiguration(
                SpelCompilerMode.IMMEDIATE,
                this.getClass().getClassLoader());

        var parser = new SpelExpressionParser(config);
        var expression = parser.parseExpression("payload");

        Message demo = new Message("hi, there!");

        var payload = expression.getValue(demo);

        assertThat(payload).isEqualTo("hi, there!");
    }

}
