package com.pii.library_app.user.model;

import java.util.Arrays;

public enum Role {

    USER,

    ADMIN,

    ;

    public static String[] roles() {
        return Arrays.stream(values())
                .map(Role::name)
                .toArray(String[]::new);
    }
}
