package com.example.market.dto;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RegisterDTO {
    @NotBlank(message = "用户不能为空")
    @Size(min = 3,max = 20,message = "用户名长度3-20")
    private String username;
    @NotBlank(message = "密码不能为空")
    @Size(min = 6,message = "密码至少6位")
    private String password;
    private String phone;
    private String nickname;
}
