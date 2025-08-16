package org.demon.vulmanage.converter;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;

@Component
public class StringToLocalDateTimeConverter implements Converter<String, LocalDateTime> {

    // 支持的日期格式列表
    private static final List<DateTimeFormatter> FORMATTERS = Arrays.asList(
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"),
            DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"),
            DateTimeFormatter.ofPattern("yyyy-MM-dd")
    );

    @Override
    public LocalDateTime convert(String source) {
        for (DateTimeFormatter formatter : FORMATTERS) {
            try {
                return LocalDateTime.parse(source, formatter);
            } catch (Exception e) {
                // 尝试下一种格式
            }
        }
        throw new IllegalArgumentException("无法解析日期时间: " + source);
    }
}