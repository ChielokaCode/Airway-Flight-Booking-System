package org.chielokacode.airwaycc.airwaybackendcc.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SendInviteEmailRequestDto {
    private String email;
}
