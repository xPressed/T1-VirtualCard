package ru.clubcard.virtualcard;

import com.google.zxing.qrcode.QRCodeWriter;
import io.swagger.v3.oas.annotations.ExternalDocumentation;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(info = @Info(title = "T1-VirtualCard",
        description = "Virtual Club Cards and QR Codes Application"),
        externalDocs = @ExternalDocumentation(description = "xPressed / Maxim Zvyagincev - GitHub", url = "https://github.com/xPressed"))
public class ApplicationConfig {
    @Bean
    public QRCodeWriter qrCodeWriter() {
        return new QRCodeWriter();
    }
}
