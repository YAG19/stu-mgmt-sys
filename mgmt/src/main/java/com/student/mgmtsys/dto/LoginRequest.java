package com.student.mgmtsys.dto;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Data
@NoArgsConstructor
public class LoginRequest {

    private String username;
    private String password;
}
