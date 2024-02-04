package kr.aling.file.aspect;

import javax.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * API 요청, 응답을 로그를 남기는 AOP.
 *
 * @author : 박경서
 * @since : 1.0
 **/
@Slf4j
@Aspect
@Component
public class LoggingAspect {

    /**
     * controller 패키지 메서드 실행 전 동작. <br>
     * API 요청에 대한 Method, URI 로깅.
     */
    @Before("execution(* kr.aling.file.*.controller..*(..))")
    public void doRequestLogging() {
        HttpServletRequest request = getHttpServletRequest();

        String uri = request.getRequestURI();
        log.info("API REQUEST : [{}] {}", request.getMethod(), uri);
    }

    /**
     * controller 패키지 메서드 실행 후 동작. <br>
     * API 요청에 대한 URI, 응답 Status 로깅.
     *
     * @param result ResponseEntity
     */
    @AfterReturning(pointcut = "execution(* kr.aling.file.*.controller..*(..))", returning = "result")
    public void doResponseLogging(ResponseEntity<?> result) {
        HttpServletRequest request = getHttpServletRequest();

        log.info("API RESPONSE : {} [{}]", request.getRequestURI(), result.getStatusCode());
    }

    /**
     * API 응답 중 Exception 발생 후 RestControllerAdvice 에서 처리 시 동작. <br>
     * API 요청에 대한 URI, Status 로깅.
     *
     * @param result ResponseEntity<?> 값
     */
    @AfterReturning(pointcut = "execution(* kr.aling.file.common.advice.AlingFileControllerAdvice..*(..))",
            returning = "result")
    public void doErrorResponseLogging(ResponseEntity<?> result) {
        HttpServletRequest request = getHttpServletRequest();

        log.warn("API RESPONSE : {} [{}]", request.getRequestURI(), result.getStatusCode());
    }

    /**
     * HttpServletRequest 가져 오는 메서드.
     *
     * @return HttpServletRequest
     */
    private static HttpServletRequest getHttpServletRequest() {
        return ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
    }
}
