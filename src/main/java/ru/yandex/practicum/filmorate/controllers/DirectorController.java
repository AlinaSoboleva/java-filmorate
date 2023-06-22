package ru.yandex.practicum.filmorate.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.film.Director;
import ru.yandex.practicum.filmorate.service.DirectorService;

import javax.validation.Valid;
import java.util.Collection;

@RestController
@RequestMapping("/directors")
@RequiredArgsConstructor
public class DirectorController {
    private final DirectorService directorService;

    @GetMapping
    public Collection<Director> findAll() {
        return directorService.findAll();
    }

    @GetMapping("/{directorId}")
    public Director getDirectorById(@PathVariable("directorId") Integer id) {
        return directorService.getDirectorById(id);
    }

    @PostMapping
    public ResponseEntity<Director> create(@Valid @RequestBody Director director) {
        System.out.println(director);
        return new ResponseEntity<>(directorService.create(director), HttpStatus.OK);
    }

    @PutMapping
    public ResponseEntity<Director> update(@Valid @RequestBody Director director) {
        Director updatedDirector = directorService.update(director);
        if (updatedDirector == null) {
            return new ResponseEntity<>(director, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(director, HttpStatus.OK);
    }

    @DeleteMapping("/{directorId}")
    public void deleteDirector(@PathVariable("directorId")  Integer id) {
        directorService.delete(id);
    }
}
