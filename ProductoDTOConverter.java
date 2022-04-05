package com.ejemplos.DTO;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import com.ejemplos.modelo.Producto;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ProductoDTOConverter {

	private final ModelMapper modelMapper;
	
	public ProductoDTO convertirADto(Producto producto) {
		return modelMapper.map(producto, ProductoDTO.class);
	}
	
	public Producto convertirAProd(CreateProductoDTO createProductoDTO) {
		return modelMapper.map(createProductoDTO, Producto.class);
	}
	
}
