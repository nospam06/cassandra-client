package org.cassandra.client.data;

import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Collection;
import java.util.LinkedHashMap;

@Component
public class HeroDaoServiceImpl implements HeroDaoService {
    private static final LinkedHashMap<Integer, Hero> HEROES = new LinkedHashMap<>();

    @PostConstruct
    private void init() {
        HEROES.put(1, new Hero(1, "Julian"));
        HEROES.put(2, new Hero(2, "Peyton"));
    }

    @Override
    public Collection<Hero> findAll() {
        return HEROES.values();
    }

    @Override
    public Hero findOne(Integer id) {
        return HEROES.get(id);
    }

    @Override
    public Hero add(Hero hero) {
        return HEROES.keySet().stream()
                .max(Integer::compareTo).stream().findFirst()
                .map(id -> new Hero(id + 1, hero.getName())).map(h -> {
                    HEROES.put(h.getId(), h);
                    return h;
                }).orElse(null);
    }

    @Override
    public Hero update(Hero hero) {
        HEROES.put(hero.getId(), hero);
        return hero;
    }

    @Override
    public void delete(Integer id) {
        HEROES.remove(id);
    }
}
