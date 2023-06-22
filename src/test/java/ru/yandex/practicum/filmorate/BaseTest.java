package ru.yandex.practicum.filmorate;

import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;

@SpringBootTest
@AutoConfigureTestDatabase
@Sql(scripts = {"/schema.sql", "/filmTestData.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = "/dropAll.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
public class BaseTest {
}
