package ru.yandex.practicum.filmorate.storage.film;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.Exceptions.FilmIdException;
import ru.yandex.practicum.filmorate.model.film.Mpa;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;

@Component
public class MpaStorage {
    private final Logger log = LoggerFactory.getLogger(MpaStorage.class);
    private final JdbcTemplate jdbcTemplate;

    public MpaStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Collection<Mpa> findAll() {
        String sql = "select * from RATING";
        return jdbcTemplate.query(sql, ((rs, rowNum) -> makeMpa(rs)));
    }

    public Mpa getById(int id) {
        validationId(id);
        String sql = "select * from RATING where RATING_ID = ?";
        List<Mpa> genreList = jdbcTemplate.query(sql, ((rs, rowNum) -> makeMpa(rs)), id);
        if (genreList.isEmpty()) {
            log.info("Рейтинг с id {} не найден", id);
            return null;
        } else {
            log.info("Найден рейтинг {}", genreList.get(0).getName());
            return genreList.get(0);
        }
    }

    private Mpa makeMpa(ResultSet rs) throws SQLException {
        return new Mpa(rs.getInt("rating_id"), rs.getString("name"));
    }

    public void validationId(Integer id) {
        String sql = "SELECT COUNT(*) FROM RATING WHERE RATING_ID = ?";
        SqlRowSet resultSet = jdbcTemplate.queryForRowSet(sql, id);
        if (resultSet.next()) {
            if (resultSet.getInt("count(*)") == 0) {
                throw new FilmIdException(String.format("Рейтинг с id %s не существует", id));
            }
        }
    }
}
