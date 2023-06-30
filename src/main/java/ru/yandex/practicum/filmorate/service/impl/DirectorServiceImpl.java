package ru.yandex.practicum.filmorate.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.film.Director;
import ru.yandex.practicum.filmorate.service.DirectorService;
import ru.yandex.practicum.filmorate.storage.director.DirectorStorage;

import java.util.Collection;

@RequiredArgsConstructor
@Service
@Slf4j
public class DirectorServiceImpl implements DirectorService {

    private final DirectorStorage directorStorage;

    public Collection<Director> findAll() {
        return directorStorage.findAll();
    }

    public Director getDirectorById(int id) {
        directorStorage.validationId(id);
        return directorStorage.getDirectorById(id);
    }

    public Director create(Director director) {
        directorStorage.create(director);
        return director;
    }

    public Director update(Director director) {
        directorStorage.update(director);
        return director;
    }

    public void delete(int id) {
        directorStorage.delete(id);
    }
}
