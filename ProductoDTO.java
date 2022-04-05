package com.ejemplos.DTO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductoDTO {

	//Lo mejor es mantener los mismos noombre de los atributos
	private long id;
	private String nombre;
	private String categoriaNombre;
	
}
