package com.daya.app.backend.payload;

import java.util.UUID;

public record AuthenticatedUserPayload(

        Long id,

        UUID uid,

        String primaryEmail

) {
}