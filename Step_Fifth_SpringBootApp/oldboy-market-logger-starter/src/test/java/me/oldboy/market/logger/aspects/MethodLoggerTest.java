package me.oldboy.market.logger.aspects;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(OutputCaptureExtension.class)
@SpringBootTest(classes = TestContextConfig.class)
class MethodLoggerTest {

    @Autowired
    private TestTargetService testTargetService;

    /**
     * Тестирует, что Aspect перехватывает метод, логирует начало и конец,
     * а также корректно возвращает результат метода и замеряет время.
     */
    @Test
    void loggableMethods_aspectLogsStartTimeAndExecutionTime_withReturnExpectedValue_Test(CapturedOutput output) throws InterruptedException {
        int value = 5;
        String expectedMethodName = "TestTargetService.performOperation(..)";
        String startLog = String.format("Calling method (начало метода): %s", expectedMethodName);
        String endLogPattern = String.format("Execution of method (обработка и завершение метода) %s finished. " +
                "Execution time is (время работы метода в мс.)", expectedMethodName);

        int result = testTargetService.performOperation(value);

        assertThat(value * 2).isEqualTo(result);
        assertThat(output.toString()).contains(startLog);
        assertThat(output.toString()).contains(endLogPattern);
        assertThat(output.toString()).matches("(?s).*Execution time is \\(время работы метода в мс.\\) (\\d+) ms.*");
    }

    /**
     * Тестирует, что Aspect не срабатывает на методе без аннотации @EnableLog.
     */
    @Test
    void notAnnotationLoggableMethod_testAspectIgnoredUnLoggedMethods_Test(CapturedOutput output) {
        int value = 5;
        int result = testTargetService.unLoggedOperation(value);

        assertThat(value + 1).isEqualTo(result);

        String unLoggedMethodName = "TestTargetService.unLoggedOperation(..)";
        assertThat(output.toString().contains(unLoggedMethodName)).isFalse();
    }
}