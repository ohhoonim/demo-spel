package com.ohhoonim.demo_spel.reference;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.SimpleEvaluationContext;
import org.springframework.util.StringUtils;

import com.ohhoonim.demo_spel.inventor.Inventor;
import com.ohhoonim.demo_spel.inventor.Society;

/*
 * 눈에 띄는 것들만 테스트 코드 작성
 */
public class LanguageReferencesTest {

    @Test
    public void rootTest() {
        // collection이나 map이 아닌 경우
        // object
        var inventor = new Inventor("Albert Einstein", "Germany");
        var parser = new SpelExpressionParser();
        // inventer instance(object)가 '#root' 가 된다. 
        var expression = parser.parseExpression("#root['name']");
        var name = expression.getValue(inventor, String.class);

        assertThat(name).isEqualTo("Albert Einstein");
    }

    // spring framework 6.2 부터 indexing into custom structure를 지원한다고 하는데
    // 자세한 내용은 아래 url 문서 참조
    // https://docs.spring.io/spring-framework/reference/core/expressions/language-ref/properties-arrays.html#expressions-indexing-custom

    @Test
    public void inlineListTest() {
        var parser = new SpelExpressionParser();
        List numbers = (List) parser
                .parseExpression("{1, 2, 3}")
                .getValue(); // inline list

        assertThat(numbers).containsExactly(1, 2, 3);
    }

    @Test
    public void inlineMapTest() {
        var parser = new SpelExpressionParser();
        var map = (Map) parser
                .parseExpression("{name:'Albert Einstein', contry:'Germany'}")
                .getValue(); // inline map

        assertThat(map.get("contry")).isEqualTo("Germany");
    }

    @Test
    public void arrayContructionTest() {
        var parser = new SpelExpressionParser();
        var array = (int[]) parser
                .parseExpression("new int[3]")
                .getValue(); // array contruction

        assertThat(array.length).isEqualTo(3);
    }

    @Test
    public void methodsTest() {
        var society = new Society();
        var parser = new SpelExpressionParser();
        parser.parseExpression("isMember('ohhoonim')")
                .getValue(society, Boolean.class); // method call
        assertThat(society.isMember("ohhoonim")).isFalse();
    }

    @Test
    public void relationalOperatiorTest() {
        var parser = new SpelExpressionParser();
        var expression = parser.parseExpression("1 < 2");
        var result = expression.getValue(Boolean.class);

        assertThat(result).isTrue();
        // 아래처럼 바꿔쓸수도 있다
        // lt (<)
        // gt (>)
        // le (<=)
        // ge (>=)
        // eq (==)
        // ne (!=)
    }

    @Test
    public void betweenTest() {
        var parser = new SpelExpressionParser();
        var expression = parser.parseExpression("1 between { 0, 2 }");
        var result = expression.getValue(Boolean.class);

        assertThat(result).isTrue();
    }

    @Test
    public void instanceOfTest() {
        var parser = new SpelExpressionParser();
        // 타입을 적어줄때는 T()를 사용한다.
        var expression = parser
                .parseExpression("new String() instanceof T(java.lang.String)");
        var result = expression.getValue(Boolean.class);

        assertThat(result).isTrue();
    }

    @Test
    public void matchesTest() {
        var parser = new SpelExpressionParser();
        // matches 를 사용하면 정규표현식으로 매칭을 할 수 있다
        var expression = parser
                .parseExpression("'5.0067' matches '^-?\\d+(\\.\\d{4})?$'");
        var result = expression.getValue(Boolean.class);

        assertThat(result).isTrue();
    }

    @Test
    public void logicalOperatorTest() {
        var parser = new SpelExpressionParser();
        // and(&&), or(||), not(!)
        var expression = parser.parseExpression("true and false");
        var result = expression.getValue(Boolean.class);

        assertThat(result).isFalse();
    }

    @Test
    public void stringOperatorTest() {
        var parser = new SpelExpressionParser();
        // +  문자열 합치기
        // *  문자열 반복 
        // -  문자 빼기 : 한문자만 가능
        var expression = parser.parseExpression("'h' - 3");
        var result = expression.getValue(String.class);

        assertThat(result).isEqualTo("e");
    }

    @Test
    public void mathematicalOperatorTest() {
        var parser = new SpelExpressionParser();
        // +, -, *, /, %, div(몫), mod(나머지)
        // 일반적으로 알고 있는 산술 연산자 전부
        var expression = parser.parseExpression("5 * 2");
        var result = expression.getValue(Integer.class);

        assertThat(result).isEqualTo(10);
    }

    @Test
    public void assignmentOperatorTest() {
        var inventor = new Inventor("Albert Einstein", "Germany");
        var parser = new SpelExpressionParser();
        var expression = parser.parseExpression("name = 'ohhoonim'");
        var result = expression.getValue(inventor, String.class);

        assertThat(result).isEqualTo("ohhoonim");
    }

    @Test
    public void overloadOperatorTest() {
        // 생략 
    }

    @Test
    public void typeTest() {
        var parser = new SpelExpressionParser();
        // T()를 사용하여 타입을 지정할 수 있다.
        var expression = parser.parseExpression("T(java.math.RoundingMode).CEILING < T(java.math.RoundingMode).FLOOR");
        var result = expression.getValue(Boolean.class);

        assertThat(result).isTrue();
    }

    @Test
    public void constructorTest() {
        var parser = new SpelExpressionParser();
        // new를 사용하여 객체를 생성할 수 있다.
        var expression = parser
                .parseExpression("new com.ohhoonim.demo_spel.inventor.Inventor('Albert Einstein', 'Germany')");
        var result = expression.getValue(Inventor.class);

        assertThat(result.getName()).isEqualTo("Albert Einstein");
    }

    @Test
    public void variableTest() {
        List<Integer> primes = List.of(2, 3, 5, 7, 11, 13, 17);

        ExpressionParser parser = new SpelExpressionParser();
        EvaluationContext context = SimpleEvaluationContext.forReadWriteDataBinding().build();
        // context에 변수 선언
        context.setVariable("primes", primes);

        // # : 변수명 앞에 붙여준다
        // ?{ } : selection
        // #this : 익히 알고 있는 this 
        // #root : 인스턴스
        String expression = "#primes.?[#this > 10]";

        // Evaluates to a list containing [11, 13, 17].
        List<Integer> primesGreaterThanTen = parser.parseExpression(expression)
                .getValue(context, List.class);

        assertThat(primesGreaterThanTen).containsExactly(11, 13, 17);

    }

    @Test
    public void functionTest() throws NoSuchMethodException, SecurityException {
        ExpressionParser parser = new SpelExpressionParser();

        EvaluationContext context = SimpleEvaluationContext.forReadOnlyDataBinding().build();
        // 사용법은 변수사용과 동일, parameter를 맞춰줘야한다.
        context.setVariable("replaceString",
                StringUtils.class.getMethod("replace", 
                    String.class, String.class, String.class));

        // evaluates to "olleh"
        String helloWorldReversed = parser
                .parseExpression("#replaceString('hello', 'h', 'M')")
                .getValue(context, String.class);

        assertThat(helloWorldReversed).isEqualTo("Mello");
    }

    @Test
    public void varagsInvocationTest() {
        ExpressionParser parser = new SpelExpressionParser();
        // 가변인자
        String helloWorldReversed = parser
                .parseExpression("'%s is color #%d'.formatted('blue', 1)")
                .getValue(String.class);

        assertThat(helloWorldReversed).isEqualTo("blue is color #1");
    }

    @Test
    public void 나머지_Test() {
        // 생략
        
        // Bean reference
        // Ternary Operator
        // Elvis Operator
        // Safe Navigation Operator
        // Collection Selection
        // Collection Projection
        // Expression Template
    }




}
