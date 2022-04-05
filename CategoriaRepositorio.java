package com.ejemplos.modelo;

import org.springframework.data.jpa.repository.JpaRepository;

											//Nombre de la entidad y tipo del campo clave
public interface CategoriaRepositorio extends JpaRepository<Categoria, Long> {

}
