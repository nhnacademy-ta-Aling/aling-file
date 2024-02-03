package kr.aling.file.common.util;

import static kr.aling.file.common.util.FileSizeUtil.calculateFileSize;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class FileSizeUtilTest {

    @ParameterizedTest(name = "{0} byte is equal : {1}")
    @MethodSource("fileSizeTestCases")
    @DisplayName("파일 사이즈 계산기 테스트")
    void fileSizeCalculatorTest(Long fileByteSize, String calculateFileSizeResult) {
        assertThat(calculateFileSizeResult).isEqualTo(calculateFileSize(fileByteSize));
    }

    private static Stream<Arguments> fileSizeTestCases() {
        return Stream.of(
                Arguments.of(0L, "0B"),
                Arguments.of(1023L, "1023B"),
                Arguments.of(1024L, "1KB"),
                Arguments.of(1_048_575L, "1023KB"),
                Arguments.of(1_048_576L, "1MB"),
                Arguments.of(1_073_741_823L, "1023MB"),
                Arguments.of(1_073_741_824L, "1GB"),
                Arguments.of(1_099_511_627_775L, "1023GB"),
                Arguments.of(1_099_511_627_776L, "1TB")
        );
    }
}