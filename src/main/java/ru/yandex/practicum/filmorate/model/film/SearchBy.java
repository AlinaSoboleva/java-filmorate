package ru.yandex.practicum.filmorate.model.film;

public enum SearchBy {
    title("f.name"),
    director("d.name");

    private final String columnName;

    SearchBy(String columnName) {
        this.columnName = columnName;
    }

    public String getColumnName() {
        return columnName;
    }

    public String toSql() {
        return getColumnName() + " LIKE CONCAT('%',?1,'%') ";
    }
}