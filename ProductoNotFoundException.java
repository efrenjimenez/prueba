package com.ejemplos.excepciones;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND) //Spring
public class ProductoNotFoundException extends RuntimeException{

	private static final long serialVersionUID=1L;
	
	public ProductoNotFoundException(Long id) {
		super("No se puede encontrar el producto con la Id: "+id);
	}
	
}
