package org.hameister.repository;

import org.hameister.model.Temperatur;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Created by hameister on 14.05.16.
 */
@Repository
public interface TemperaturRepository extends JpaRepository<Temperatur, Long> {
}
