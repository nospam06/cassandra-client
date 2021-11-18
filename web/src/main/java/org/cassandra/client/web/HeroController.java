package org.cassandra.client.web;

import lombok.RequiredArgsConstructor;
import org.cassandra.client.data.Hero;
import org.cassandra.client.data.HeroDaoService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200", maxAge = 3600)
@RequestMapping(value = "/api/heroes", produces = MediaType.APPLICATION_JSON_VALUE)
public class HeroController {
    private final HeroDaoService daoService;

    @GetMapping
    public Collection<Hero> getOne(@RequestParam(required = false) Integer id) {
        if  (id == null) {
            return daoService.findAll();
        }
        return Optional.ofNullable(daoService.findOne(id)).map(List::of).orElse(null);
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public Hero add(@RequestBody Hero hero) {
        return daoService.add(hero);
    }

    @DeleteMapping("/{id}")
    public void deleteOne(@PathVariable Integer id) {
        daoService.delete(id);
    }

}
