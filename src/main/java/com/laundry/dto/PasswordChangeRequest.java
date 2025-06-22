package com.laundry.dto;

import lombok.*;

@Data
public class PasswordChangeRequest {
    private String oldPassword;
    private String newPassword;

}
