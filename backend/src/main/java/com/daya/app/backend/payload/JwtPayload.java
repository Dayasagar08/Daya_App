package com.daya.app.backend.payload;

import java.util.UUID;

public record JwtPayload(

        UUID uid,

        String subject

) {
}