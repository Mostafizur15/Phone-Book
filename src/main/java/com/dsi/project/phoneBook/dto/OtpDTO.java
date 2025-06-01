package com.dsi.project.phoneBook.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class OtpDTO {
    @NotEmpty
    @NotBlank
    private String otp;
}
