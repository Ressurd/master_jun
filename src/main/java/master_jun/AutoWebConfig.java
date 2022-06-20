package master_jun;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import static org.springframework.context.annotation.ComponentScan.*;

// 설정정보이기 때문에 @Configuration 필요
@Configuration

// 컴포넌트 스캔을 사용하려면 먼저 @ComponentScan 을 설정 정보에 붙여주면 됨
// @Component 라는 애노테이션이 붙은 클래스를 찾아 자동으로 스프링 빈에 등록해줌
@ComponentScan(
        // 제외할 필터 적용 : @Configuration 을 일단 제외(예제를 안전히 유지하기 위해서)
        excludeFilters = @Filter(type = FilterType.ANNOTATION, classes = Configuration.class))
public class AutoWebConfig {
}