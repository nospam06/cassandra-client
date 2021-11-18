package org.cassandra.client.data;

import java.util.Collection;

public interface HeroDaoService {
    Collection<Hero> findAll();

    Hero findOne (Integer id);

    Hero add(Hero hero);

    void delete(Integer id);
}
