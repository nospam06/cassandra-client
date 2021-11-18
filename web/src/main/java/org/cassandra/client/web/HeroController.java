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
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200", maxAge = 3600)
@RequestMapping(value = "/api/heroes", produces = MediaType.APPLICATION_JSON_VALUE)
public class HeroController {
    private final HeroDaoService daoService;

    @GetMapping
    public Collection<Hero> findAll(@RequestParam(required = false) String name) {
        Collection<Hero> all = daoService.findAll();
        if (name == null) {
            return all;
        }
        String lowerCase = name.toLowerCase();
        return all.stream().filter(h -> h.getName().toLowerCase().contains(name)).collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public Hero getOne(@PathVariable Integer id) {
        return daoService.findOne(id);
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public Hero add(@RequestBody Hero hero) {
        return daoService.add(hero);
    }


    @PutMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public Hero update(@RequestBody Hero hero) {
        return daoService.update(hero);
    }

    @DeleteMapping("/{id}")
    public void deleteOne(@PathVariable Integer id) {
        daoService.delete(id);
    }

}
