package ru.yandex.practicum.filmorate.storage.film.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.FilmIdException;
import ru.yandex.practicum.filmorate.exceptions.IncorrectParameterException;
import ru.yandex.practicum.filmorate.exceptions.UserIdException;
import ru.yandex.practicum.filmorate.model.film.Film;
import ru.yandex.practicum.filmorate.model.film.Genre;
import ru.yandex.practicum.filmorate.model.film.SearchBy;
import ru.yandex.practicum.filmorate.storage.director.DirectorStorage;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.film.GenreDao;
import ru.yandex.practicum.filmorate.storage.film.MpaDao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.*;

@Slf4j
@Component
@RequiredArgsConstructor
public class FilmDbStorage implements FilmStorage {

    private final JdbcTemplate jdbcTemplate;
    private final MpaDao mpaStorage;
    private final GenreDao genreStorage;
    private final DirectorStorage directorStorage;

    @Override
    public Collection<Film> findAllTopFilms(Integer count, Integer genreId, Integer year) {
        String sql = "SELECT * " +
                "FROM FILMS AS f " +
                "LEFT JOIN LIKES AS L on F.FILM_ID = L.FILM_ID " +
                "LEFT JOIN FILM_GENRE AS FG on F.FILM_ID = FG.FILM_ID " +
                "WHERE genre_id = ? and EXTRACT(YEAR FROM CAST(release_date AS date)) = ? " +
                "GROUP BY F.FILM_ID " +
                "ORDER BY COUNT(L.FILM_ID) " +
                "DESC , f.FILM_ID LIMIT ?";
        return jdbcTemplate.query(sql, ((rs, rowNum) -> makeFilm(rs)), genreId, year, count);
    }

    @Override
    public Collection<Film> findAllTopIfGenre(Integer count, Integer genreId) {
        String sql = "SELECT * " +
                "FROM FILMS AS f " +
                "LEFT JOIN LIKES AS L on F.FILM_ID = L.FILM_ID " +
                "LEFT JOIN FILM_GENRE AS FG on F.FILM_ID = FG.FILM_ID " +
                "WHERE genre_id = ? " +
                "GROUP BY F.FILM_ID " +
                "ORDER BY COUNT(L.FILM_ID) " +
                "DESC , f.FILM_ID LIMIT ?";
        return jdbcTemplate.query(sql, ((rs, rowNum) -> makeFilm(rs)), genreId, count);
    }

    @Override
    public Collection<Film> findAllTopIfYear(Integer count, Integer year) {
        String sql = "SELECT * " +
                "FROM FILMS AS f " +
                "LEFT JOIN LIKES AS L on F.FILM_ID = L.FILM_ID " +
                "WHERE EXTRACT(YEAR FROM CAST(release_date AS date)) = ? " +
                "GROUP BY F.FILM_ID " +
                "ORDER BY COUNT(L.FILM_ID) " +
                "DESC , f.FILM_ID LIMIT ?";
        return jdbcTemplate.query(sql, ((rs, rowNum) -> makeFilm(rs)), year, count);
    }

    @Override
    public Collection<Film> findTopFilms(Integer count) {
        String sql = "SELECT * " +
                "FROM FILMS AS f " +
                "LEFT JOIN LIKES AS L on F.FILM_ID = L.FILM_ID " +
                "GROUP BY F.FILM_ID, L.USER_ID " +
                "ORDER BY COUNT(L.FILM_ID) " +
                "DESC LIMIT ?";
        return jdbcTemplate.query(sql, ((rs, rowNum) -> makeFilm(rs)), count);
    }

    @Override
    public List<Film> getRecommendations(Integer id) {
        String maxUserIntersection = "SELECT l.user_id u_id, " +
                "COUNT(l.user_id) cnt " +
                "FROM Likes l WHERE l.user_id <> ? " + // ID остальных пользователей
                "AND l.film_id IN (" + // которые поставили лайки тем же фильмам, что и пользователь в запросе
                "SELECT ll.film_id FROM " +
                " Likes ll WHERE ll.user_id = ?)" +
                "GROUP BY l.user_id " + // группировка, так как используем аггрегирующую функцию
                "ORDER BY cnt DESC " + // сортируем по убыванию
                "LIMIT 1 "; // выбираем максимальное совпадение

        Integer recommendedUserId = jdbcTemplate.query(maxUserIntersection, rs -> {
            if (rs.next()) {
                return rs.getInt("u_id");
            } else {
                return null;
            }
        }, id, id);
        if (recommendedUserId == null) {
            return Collections.emptyList();
        }

        String recommendedFilmsSql = "SELECT * FROM Films fm " + // все фильмы
                "LEFT JOIN Likes lk ON fm.film_id = lk.film_id " +
                "WHERE lk.user_id = ? " +
                "AND lk.film_id NOT IN (" + // и которым наш пользователь не ставил лайк
                "SELECT llk.film_id FROM LIKES llk " +
                "WHERE llk.user_id = ?)";
        return jdbcTemplate.query(recommendedFilmsSql, (rs, rowNum) -> makeFilm(rs), recommendedUserId, id);
    }

    @Override
    public List<Film> getCommonFilms(Integer userId, Integer friendId) {
        String sql = "SELECT f.film_id, " +
                "f.name, " +
                "f.description, " +
                "f.release_date, " +
                "f.duration, " +
                "f.rating_id, " +
                "COUNT(l.film_id) l_cnt " +
                "FROM FILMS f " +
                "LEFT JOIN LIKES l ON l.film_id = f.film_id " +
                "WHERE f.film_id IN (" +
                "SELECT ff.film_id film_id " +
                "FROM FILMS ff " +
                "LEFT JOIN LIKES ll ON ff.film_id = ll.film_id " +
                "WHERE ll.user_id = ? " +
                "INTERSECT " +
                "SELECT fff.film_id " +
                "FROM FILMS fff " +
                "LEFT JOIN LIKES lll ON fff.film_id = lll.film_id " +
                "WHERE lll.user_id = ?" +
                ") " +
                "GROUP BY f.film_id, f.name, f.description, f.release_date, f.duration, f.rating_id " +
                "ORDER BY l_cnt DESC";
        log.info(
                "Выполнение SQL запроса [{}] для получения фильмов, понравившися пользователю с id {}",
                sql,
                userId
        );
        try {
            List<Film> films = jdbcTemplate.query(sql, (rs, rowNum) -> makeFilm(rs), userId, friendId);
            log.info("Получение фильмов из БД, понравившися пользователю с id={}  {}", userId, films);
            return films;
        } catch (DataIntegrityViolationException ex) {
            String msg = String.format(
                    "Любо пользователь с id=%d, либо друг с  id=%d не найден в БД.",
                    userId,
                    friendId
            );
            throw new UserIdException(msg);
        }
    }

    @Override
    public List<Film> search(String query, SearchBy by) {
        StringBuilder sql = new StringBuilder("SELECT f.film_id, " +
                "f.name, " +
                "f.description, " +
                "f.release_date, " +
                "f.duration, " +
                "f. rating_id," +
                "d.director_id " +
                "FROM films f " +
                "LEFT JOIN likes l ON f.film_id=l.film_id " +
                "LEFT JOIN film_director fd ON f.film_id=fd.film_id " +
                "LEFT JOIN directors d ON fd.director_id=d.director_id ");
        switch (by) {
            case TITLE:
                sql.append("WHERE f.name LIKE CONCAT('%',?,'%') ");
                break;
            case DIRECTOR:
                sql.append("WHERE d.name LIKE CONCAT('%',?,'%') ");
                break;
            case TITLE_OR_DIRECTOR:
                sql.append("WHERE f.name LIKE CONCAT('%',?1,'%') OR d.name LIKE CONCAT('%',?1,'%') ");
                break;
        }
        sql.append("GROUP BY f.film_id, f.name, f.description, f.release_date, f.duration, f.rating_id, d.director_id " +
                "ORDER BY COUNT(L.FILM_ID) DESC ");
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
            Set<Genre> newGenres = new HashSet<>();
            for (Genre genre : genres) {
                newGenres.add(genreStorage.getById(genre.getId()));
                genreStorage.createGenreByFilm(genre.getId(), film.getId());
            }
            film.getGenres().clear();
            film.getGenres().addAll(newGenres);
            setDirectors(film);
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
        directorStorage.deleteDirectorByFilm(film);
        setDirectors(film);
        return true;
    }

    @Override
    public void delete(Integer filmId) {
        String sql = "DELETE FROM FILMS WHERE FILM_ID=?";
        jdbcTemplate.update(sql, filmId);
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
    public Collection<Film> getFilmsByDirectorId(int id, String sortBy) {
        String sql;
        if (sortBy.equals("likes")) {
            sql = "SELECT F.* FROM FILMS F JOIN FILM_DIRECTOR FD ON F.FILM_ID = FD.FILM_ID LEFT JOIN LIKES L ON F.FILM_ID = L.FILM_ID WHERE FD.DIRECTOR_ID = ? GROUP BY F.FILM_ID ORDER BY COUNT(L.FILM_ID) DESC;";
        } else if (sortBy.equals("year")) {
            sql = "SELECT * FROM FILMS WHERE FILM_ID IN (SELECT FILM_ID FROM FILM_DIRECTOR WHERE DIRECTOR_ID = ?) ORDER BY FILMS.RELEASE_DATE;";
        } else {
            throw new IncorrectParameterException("Неверный параметр запроса");
        }
        return jdbcTemplate.query(sql, ((rs, rowNum) -> makeFilm(rs)), id);
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
        setRate(film);
        return film;
    }

    private void setMpa(Film film, int mpa) {
        film.setMpa(mpaStorage.getById(mpa));
    }

    private void setGenre(Film film) {
        film.getGenres().clear();
        film.getGenres().addAll(genreStorage.getGenresByFilm(film.getId()));
    }

    private void setDirectors(Film film) {
        directorStorage.setDirectorsInDb(film);
        film.getDirectors().clear();
        film.getDirectors().addAll(directorStorage.getDirectorsByFilm(film.getId()));
    }

    private void setRate(Film film) {
        String sql = "SELECT COUNT(USER_ID) FROM LIKES WHERE FILM_ID = ?";
        SqlRowSet resultSet = jdbcTemplate.queryForRowSet(sql, film.getId());
        if (resultSet.next()) {
            film.setRate(resultSet.getInt("count(user_id)"));
        }
    }
}
