package com.ssafy.home.member;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record MemberDto(
        Long id,
        @Email @NotBlank String email,
        String name,
        String phone,
        String address,
        String role
) {
    static MemberDto from(Member member) {
        return new MemberDto(
                member.getId(),
                member.getEmail(),
                member.getName(),
                member.getPhone(),
                member.getAddress(),
                member.getRole()
        );
    }

    public record RegisterRequest(
            @Email @NotBlank String email,
            @NotBlank @Size(min = 4) String password,
            @NotBlank String name,
            String phone,
            String address
    ) {}

    public record LoginRequest(@Email @NotBlank String email, @NotBlank String password) {}

    public record UpdateRequest(
            @NotBlank String name,
            String phone,
            String address,
            String password
    ) {}
}
