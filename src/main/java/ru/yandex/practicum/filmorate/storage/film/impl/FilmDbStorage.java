package ru.yandex.practicum.filmorate.storage.film.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.Exceptions.FilmIdException;
import ru.yandex.practicum.filmorate.model.film.Film;
import ru.yandex.practicum.filmorate.model.film.Genre;
import ru.yandex.practicum.filmorate.storage.director.DirectorStorage;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.film.GenreDao;
import ru.yandex.practicum.filmorate.storage.film.MpaDao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Collection;
import java.util.Objects;
import java.util.Set;

@Slf4j
@Component
@Primary
public class FilmDbStorage implements FilmStorage {

    private final JdbcTemplate jdbcTemplate;
    private final MpaDao mpaStorage;
    private final GenreDao genreStorage;
    private final DirectorStorage directorStorage;

    public FilmDbStorage(JdbcTemplate jdbcTemplate, MpaStorage mpaStorage, GenreStorage genreStorage, DirectorStorage directorStorage) {
        this.jdbcTemplate = jdbcTemplate;
        this.mpaStorage = mpaStorage;
        this.genreStorage = genreStorage;
        this.directorStorage = directorStorage;
    }

    @Override
    public Collection<Film> findAllTopFilms(Integer count) {
        String sql = "SELECT * FROM FILMS LEFT JOIN  LIKES L on FILMS.FILM_ID = L.FILM_ID " + "GROUP BY FILMS.FILM_ID ORDER BY COUNT(L.FILM_ID) DESC LIMIT ?";
        return jdbcTemplate.query(sql, ((rs, rowNum) -> makeFilm(rs)), count);
    }

    @Override
    public Collection<Film> search(String query, String by) {
        StringBuilder sql = new StringBuilder("SELECT *\n" +
                "FROM FILMS F\n" +
                "LEFT JOIN LIKES L ON F.FILM_ID=L.FILM_ID\n" +
                "LEFT JOIN FILM_DIRECTOR FD ON F.FILM_ID=FD.FILM_ID\n" +
                "LEFT JOIN DIRECTORS D ON FD.DIRECTOR_ID=D.DIRECTOR_ID\n");
        switch (by) {
            case ("title"):
                sql.append("WHERE F.NAME LIKE CONCAT('%',?,'%')\n");
                break;
            case ("director"):
                sql.append("WHERE D.NAME LIKE CONCAT('%',?,'%')\n");
                break;
            case ("title,director"):
                sql.append("WHERE F.NAME LIKE CONCAT('%',?1,'%') OR D.NAME LIKE CONCAT('%',?1,'%')\n");
                break;
            case ("director,title"):
                sql.append("WHERE D.NAME LIKE CONCAT('%',?1,'%') OR F.NAME LIKE CONCAT('%',?1,'%')\n");
                break;
        }
        sql.append("GROUP BY F.FILM_ID\n" + "ORDER BY COUNT(L.FILM_ID) DESC\n");
        System.out.println(sql);
        return jdbcTemplate.query(sql.toString(), ((rs, rowNum) -> makeFilm(rs)), query);
    }

    @Override
    public void create(Film film) {
        log.info("Сохранение фильма {}", film);
        if (film.getId() == 0) {
            KeyHolder holder = new GeneratedKeyHolder();
            film.setMpa(mpaStorage.getById(film.getMpa().getId()));
            String sql = "INSERT INTO FILMS(NAME, DESCRIPTION, RELEASE_DATE, DURATION, RATING_ID) " + "VALUES (?, ?, ?, ?,?)";

            jdbcTemplate.update(connection -> {
                PreparedStatement ps = connection.prepareStatement(sql, new String[]{"FILM_ID"});
                ps.setString(1, film.getName());
                ps.setString(2, film.getDescription());
                ps.setString(3, film.getReleaseDate().toString());
                ps.setInt(4, film.getDuration());
                ps.setInt(5, film.getMpa().getId());
                return ps;
            }, holder);

            film.setId(Objects.requireNonNull(holder.getKey()).intValue());
            Set<Genre> genres = film.getGenres();
            for (Genre genre : genres) {
                film.getGenres().remove(genre);
                film.getGenres().add(genreStorage.getById(genre.getId()));
                genreStorage.createGenreByFilm(genre.getId(), film.getId());
            }
            log.info("Фильм   {} сохранен", film);
        }
    }

    @Override
    public boolean update(Film film) {
        try {
            validationId(film.getId());
        } catch (FilmIdException e) {
            return false;
        }
        String sql = "UPDATE FILMS SET  NAME = ?, DESCRIPTION = ?, RELEASE_DATE = ?,DURATION=?,RATING_ID=?" + "                WHERE FILM_ID = ?;";
        jdbcTemplate.update(sql, film.getName(), film.getDescription(), film.getReleaseDate(), film.getDuration(), film.getMpa().getId(), film.getId());
        setMpa(film, film.getMpa().getId());
        Set<Genre> genres = film.getGenres();
        genreStorage.deleteAllGenresByFilm(film.getId());
        for (Genre genre : genres) {
            genreStorage.createGenreByFilm(genre.getId(), film.getId());
        }
        return true;
    }

    @Override
    public void delete(Film film) {
        String sql = "DELETE FROM FILMS WHERE FILM_ID=?";
        jdbcTemplate.update(sql, film.getId());
    }

    @Override
    public Collection<Film> getFilms() {
        String sql = "SELECT * FROM FILMS";
        return jdbcTemplate.query(sql, ((rs, rowNum) -> makeFilm(rs)));
    }

    @Override
    public Film getById(Integer id) {
        validationId(id);
        String sql = "SELECT * FROM FILMS WHERE film_id=?";
        return jdbcTemplate.query(sql, ((rs, rowNum) -> makeFilm(rs)), id).get(0);
    }


    @Override
    public Collection<Film> getFilmsByDirectorId(int id, String sortBy){
        String sql = "SELECT * FROM FILMS WHERE FILM_ID IN (SELECT FILM_ID FROM FILM_DIRECTOR WHERE DIRECTOR_ID = ?) ORDER BY FILMS.RELEASE_DATE;";
        return jdbcTemplate.query(sql, ((rs, rowNum) -> makeFilm(rs)));
    }

    @Override
    public void validationId(Integer id) {
        String sql = "SELECT COUNT(*) FROM FILMS WHERE FILM_ID=?";
        SqlRowSet resultSet = jdbcTemplate.queryForRowSet(sql, id);
        if (resultSet.next()) {
            if (resultSet.getInt("count(*)") == 0) {
                throw new FilmIdException(String.format("Фильм с id %s не существует", id));
            }
        }
    }

    private Film makeFilm(ResultSet resultSet) throws SQLException {
        int id = resultSet.getInt("film_id");
        String name = resultSet.getString("name");
        String description = resultSet.getString("description");
        LocalDate releaseDate = resultSet.getDate("release_date").toLocalDate();
        int duration = resultSet.getInt("duration");
        int mpa = resultSet.getInt("rating_id");
        Film film = new Film(name, description, releaseDate, duration);
        film.setId(id);
        setMpa(film, mpa);
        setGenre(film);
        setDirectors(film);
        return film;
    }

    private void setMpa(Film film, int mpa) {
        film.setMpa(mpaStorage.getById(mpa));
    }

    private void setGenre(Film film) {
        film.getGenres().clear();
        film.getGenres().addAll(genreStorage.getGenresByFilm(film.getId()));
    }

    private void setDirectors(Film film){
        film.getDirectors().clear();
        film.getDirectors().addAll(directorStorage.getDirectorsByFilm(film.getId()));
    }
}
