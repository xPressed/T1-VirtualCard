package ru.clubcard.virtualcard.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.clubcard.virtualcard.entity.card.dto.ColorBasedVirtualCardDTO;
import ru.clubcard.virtualcard.entity.card.dto.ImageBasedVirtualCardDTO;
import ru.clubcard.virtualcard.entity.qrcode.QRCode;
import ru.clubcard.virtualcard.service.QRCodeService;

@RestController
@RequestMapping("/qr-code")
@RequiredArgsConstructor
@Validated
@Tag(name = "2. QR Code Controller")
public class QRCodeController {
    private final QRCodeService qrCodeService;

    @Operation(summary = "Create new QR Code")
    @ApiResponse(responseCode = "200", description = "OK",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    examples = @ExampleObject("{ \"message\": \"QR Code generated.\" }")))
    @ApiResponse(responseCode = "400", description = "Bad Request",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    examples = @ExampleObject("{ \"message\": \"Some Bad Request message.\" }")))
    @ApiResponse(responseCode = "404", description = "Not Found",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    examples = @ExampleObject("{ \"message\": \"Virtual Card not found.\" }")))
    @PostMapping("/post")
    public ResponseEntity<?> postQRCode(
            @Parameter(description = "ID of Virtual Card")
            @NotNull @RequestParam Long virtualCardId,
            @Parameter(description = "Is new QR Code disposable (For single use)")
            @NotNull @RequestParam boolean isDisposable) {
        return qrCodeService.generate(virtualCardId, isDisposable);
    }

    @Operation(summary = "Get image of QR Code")
    @ApiResponse(responseCode = "200", description = "OK",
            content = @Content(
                    mediaType = MediaType.IMAGE_JPEG_VALUE))
    @ApiResponse(responseCode = "400", description = "Bad Request",
            content = @Content())
    @ApiResponse(responseCode = "404", description = "Not Found",
            content = @Content())
    @ApiResponse(responseCode = "500", description = "Internal Server Error",
            content = @Content())
    @GetMapping(value = "/get-image/{qrCodeId}", produces = MediaType.IMAGE_JPEG_VALUE)
    public ResponseEntity<?> getQRCode(
            @Parameter(description = "ID of QR Code")
            @NotNull @PathVariable Long qrCodeId) {
        return qrCodeService.get(qrCodeId);
    }

    @Operation(summary = "Get QR Code Info")
    @ApiResponse(responseCode = "200", description = "OK",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = QRCode.class)))
    @ApiResponse(responseCode = "400", description = "Bad Request",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    examples = @ExampleObject("{ \"message\": \"Some Bad Request message.\" }")))
    @ApiResponse(responseCode = "404", description = "Not Found",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    examples = @ExampleObject("{ \"message\": \"QR Code not found.\" }")))
    @GetMapping("/get-info/{qrCodeId}")
    public ResponseEntity<?> getInfo(
            @Parameter(description = "ID of QR Code")
            @NotNull @PathVariable Long qrCodeId) {
        return qrCodeService.getInfo(qrCodeId);
    }

    @Operation(summary = "Get all Virtual Card QR Codes Info")
    @ApiResponse(responseCode = "200", description = "OK",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = QRCode[].class)))
    @ApiResponse(responseCode = "400", description = "Bad Request",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    examples = @ExampleObject("{ \"message\": \"Some Bad Request message.\" }")))
    @GetMapping("/get-info/all/{virtualCardId}")
    public ResponseEntity<?> getAllInfo(
            @Parameter(description = "ID of Virtual Card")
            @NotNull @PathVariable Long virtualCardId) {
        return qrCodeService.getAllInfo(virtualCardId);
    }

    @Operation(summary = "Check Virtual Card by QR Code")
    @ApiResponse(responseCode = "200", description = "OK",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(oneOf = {ColorBasedVirtualCardDTO.class, ImageBasedVirtualCardDTO.class})))
    @ApiResponse(responseCode = "400", description = "Bad Request",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    examples = @ExampleObject("{ \"message\": \"Some Bad Request message.\" }")))
    @ApiResponse(responseCode = "403", description = "Forbidden",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    examples = @ExampleObject("{ \"message\": \"QR Code expired!\" }")))
    @ApiResponse(responseCode = "404", description = "Not Found",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    examples = @ExampleObject("{ \"message\": \"QR Code not found.\" }")))
    @GetMapping("/check/{qrCodeId}")
    public ResponseEntity<?> checkQRCode(
            @Parameter(description = "ID of QR Code")
            @NotNull @PathVariable Long qrCodeId) {
        return qrCodeService.check(qrCodeId);
    }

    @Operation(summary = "Delete the QR Code")
    @ApiResponse(responseCode = "200", description = "OK",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    examples = @ExampleObject("{ \"message\": \"QR Code deleted.\" }")))
    @ApiResponse(responseCode = "400", description = "Bad Request",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    examples = @ExampleObject("{ \"message\": \"Some Bad Request message.\" }")))
    @ApiResponse(responseCode = "404", description = "Not Found",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    examples = @ExampleObject("{ \"message\": \"QR Code not found.\" }")))
    @DeleteMapping("/delete/{qrCodeId}")
    public ResponseEntity<?> deleteQRCode(
            @Parameter(description = "ID of QR Code")
            @NotNull @PathVariable Long qrCodeId) {
        return qrCodeService.delete(qrCodeId);
    }
}
