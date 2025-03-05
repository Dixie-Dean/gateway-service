package dev.dixie.model.dto;

import lombok.*;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class ImagerPostDTO {
    private String id;
    private String user;
    @ToString.Exclude
    private byte[] image;
    private String message;
    private LocalDateTime creationTime;
    private LocalDateTime expirationTime;
    private String link;
}
