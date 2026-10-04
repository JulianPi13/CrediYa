package com.crediya.dao;

import java.util.List;
import java.util.Optional;

/**
 * Contrato genérico de acceso a datos (patrón DAO).
 * Los servicios trabajan con esta abstracción y no con SQL directamente
 * (principio de inversión de dependencias).
 *
 * @param <T> entidad que se guarda
 */
public interface CrudDAO<T> {

    /** Guarda la entidad y devuelve el id generado por la base de datos. */
    int guardar(T entidad);

    List<T> listar();

    Optional<T> buscarPorId(int id);
}
