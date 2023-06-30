package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class ErrorResponse {
    private String error;

    public String getError() {
        return error;
    }
}
