package dev.dixie.model.dto;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class ImagerPostUploadData {
    private String email;
    private String message;
    private long ttl;
}
