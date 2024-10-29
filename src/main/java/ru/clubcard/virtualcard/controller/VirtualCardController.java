package ru.clubcard.virtualcard.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.clubcard.virtualcard.entity.card.dto.ColorBasedVirtualCardDTO;
import ru.clubcard.virtualcard.entity.card.dto.ImageBasedVirtualCardDTO;
import ru.clubcard.virtualcard.entity.card.dto.VirtualCardDTO;
import ru.clubcard.virtualcard.service.VirtualCardService;

@RestController
@RequestMapping("/virtual-card")
@RequiredArgsConstructor
@Validated
@Tag(name = "1. Virtual Card Controller")
public class VirtualCardController {
    private final VirtualCardService virtualCardService;

    @Operation(summary = "Create new Virtual Card")
    @ApiResponse(responseCode = "200", description = "OK",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = ColorBasedVirtualCardDTO.class)))
    @PostMapping("/post")
    public ResponseEntity<?> postCard(
            @Parameter(description = "ID of User owning Virtual Card")
            @NotNull @RequestParam Long userId) {
        return virtualCardService.createCard(userId);
    }

    @Operation(summary = "Save Image for Image Based Virtual Card")
    @ApiResponse(responseCode = "200", description = "OK",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = ImageBasedVirtualCardDTO.class)))
    @ApiResponse(responseCode = "400", description = "Bad Request",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    examples = @ExampleObject("{ \"message\": \"Some Bad Request message.\" }")))
    @PostMapping(value = "/image/post/{virtualCardId}", consumes = "multipart/form-data")
    public ResponseEntity<?> postImage(
            @Parameter(description = "ID of Image Based Virtual Card")
            @NotNull @PathVariable Long virtualCardId,
            @Parameter(description = "Image to be Saved")
            @NotNull @RequestParam MultipartFile image) {
        return virtualCardService.saveImage(virtualCardId, image);
    }

    @Operation(summary = "Get Image of Image Based Virtual Card")
    @ApiResponse(responseCode = "200", description = "OK",
            content = @Content(
                    mediaType = MediaType.IMAGE_JPEG_VALUE))
    @ApiResponse(responseCode = "400", description = "Bad Request",
            content = @Content())
    @GetMapping(value = "/image/get/{virtualCardId}", produces = MediaType.IMAGE_JPEG_VALUE)
    public ResponseEntity<?> getImage(
            @Parameter(description = "ID of Image Based Virtual Card")
            @NotNull @PathVariable Long virtualCardId) {
        return virtualCardService.getImage(virtualCardId);
    }

    @Operation(summary = "Get Virtual Card by ID")
    @ApiResponse(responseCode = "200", description = "OK",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(oneOf = {ColorBasedVirtualCardDTO.class, ImageBasedVirtualCardDTO.class})))
    @ApiResponse(responseCode = "400", description = "Bad Request",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    examples = @ExampleObject("{ \"message\": \"Some Bad Request message.\" }")))
    @ApiResponse(responseCode = "404", description = "Not Found",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    examples = @ExampleObject("{ \"message\": \"Virtual Card not found.\" }")))
    @GetMapping("/get")
    public ResponseEntity<?> getCard(
            @Parameter(description = "ID of Virtual Card")
            @NotNull @RequestParam Long id) {
        return virtualCardService.getCard(id);
    }

    @Operation(summary = "Put update for Virtual Card")
    @ApiResponse(responseCode = "200", description = "OK",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(oneOf = {ColorBasedVirtualCardDTO.class, ImageBasedVirtualCardDTO.class})))
    @ApiResponse(responseCode = "400", description = "Bad Request",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    examples = @ExampleObject("{ \"message\": \"Some Bad Request message.\" }")))
    @ApiResponse(responseCode = "404", description = "Not Found",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    examples = @ExampleObject("{ \"message\": \"Virtual Card not found.\" }")))
    @PutMapping("/put")
    public ResponseEntity<?> putCard(@Valid @RequestBody VirtualCardDTO request) {
        return virtualCardService.putCard(request);
    }

    @Operation(summary = "Patch update for Virtual Card")
    @ApiResponse(responseCode = "200", description = "OK",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(oneOf = {ColorBasedVirtualCardDTO.class, ImageBasedVirtualCardDTO.class})))
    @ApiResponse(responseCode = "400", description = "Bad Request",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    examples = @ExampleObject("{ \"message\": \"Some Bad Request message.\" }")))
    @ApiResponse(responseCode = "404", description = "Not Found",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    examples = @ExampleObject("{ \"message\": \"Virtual Card not found.\" }")))
    @PatchMapping("/patch")
    public ResponseEntity<?> patchCard(@RequestBody VirtualCardDTO request) {
        return virtualCardService.patchCard(request);
    }

    @Operation(summary = "Delete the Virtual Card")
    @ApiResponse(responseCode = "200", description = "OK",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    examples = @ExampleObject("{ \"message\": \"Virtual Card deleted\" }")))
    @ApiResponse(responseCode = "400", description = "Bad Request",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    examples = @ExampleObject("{ \"message\": \"Some Bad Request message.\" }")))
    @ApiResponse(responseCode = "404", description = "Not Found",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    examples = @ExampleObject("{ \"message\": \"Virtual Card not found.\" }")))
    @DeleteMapping("/delete")
    public ResponseEntity<?> deleteCard(
            @Parameter(description = "ID of Virtual Card")
            @NotNull @RequestParam Long id) {
        return virtualCardService.deleteCard(id);
    }
}
