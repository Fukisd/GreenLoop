package org.greenloop.circularfashion.entity.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Login request details")
public class LoginRequest {

    @NotBlank(message = "Email or username is required")
    @Schema(description = "User's email address or username", example = "john.doe@example.com", required = true)
    private String emailOrUsername;

    @NotBlank(message = "Password is required")
    @Size(min = 8, message = "Password must be at least 8 characters long")
    @Schema(description = "User's password", example = "SecurePass123!", required = true, minLength = 8)
    private String password;

    @Schema(description = "Remember me option", example = "false", defaultValue = "false")
    private Boolean rememberMe = false;

    @Schema(description = "Device ID for tracking", example = "device-123-abc")
    private String deviceId;

    @Schema(description = "Device name", example = "iPhone 15 Pro")
    private String deviceName;

    @Schema(description = "User agent string", example = "Mozilla/5.0 (iPhone; CPU iPhone OS 17_0 like Mac OS X)")
    private String userAgent;

    @Schema(description = "IP address", example = "192.168.1.100")
    private String ipAddress;
} 
