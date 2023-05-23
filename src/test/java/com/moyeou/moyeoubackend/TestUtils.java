package com.moyeou.moyeoubackend;

import org.springframework.test.web.servlet.ResultActions;

public class TestUtils {
    private TestUtils() {
    }

    public static Long id(ResultActions resultActions) {
        String location = resultActions.andReturn()
                .getResponse()
                .getHeader("Location");
        String[] tokens = location.split("/");
        return Long.parseLong(tokens[tokens.length - 1]);
    }

    public static Long id(String location) {
        String[] tokens = location.split("/");
        return Long.parseLong(tokens[tokens.length - 1]);
    }

    public static String uri(ResultActions resultActions) {
        return resultActions.andReturn()
                .getResponse()
                .getHeader("Location");
    }
}
