package ru.yandex.practicum.filmorate.model.film;

public enum SearchBy {
    TITLE,
    DIRECTOR,
    TITLE_OR_DIRECTOR;

    public static SearchBy fromString(String value) {
        if (value.equalsIgnoreCase("director")) {
            return DIRECTOR;
        } else if (value.equalsIgnoreCase("title")) {
            return TITLE;
        } else if (value.equalsIgnoreCase("director,title") || value.equalsIgnoreCase("title,director")) {
            return TITLE_OR_DIRECTOR;
        }
        throw new IllegalArgumentException("Не корректный запрос" + value);
    }
}