package com.ejemplos.controller;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import javax.print.attribute.standard.DateTimeAtCompleted;

import org.springframework.boot.json.YamlJsonParser;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.ejemplos.DTO.CreateProductoDTO;
import com.ejemplos.DTO.ProductoDTO;
import com.ejemplos.DTO.ProductoDTOConverter;
import com.ejemplos.excepciones.ApiError;
import com.ejemplos.excepciones.ProductoNotFoundException;
import com.ejemplos.modelo.Producto;
import com.ejemplos.modelo.ProductoRepositorio;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor  //lombok crea el constructor
public class ProductoController {
	
	// se declara como final pq no se va a modificar este repositorio
	private final ProductoRepositorio productoRepositorio; 
	private final ProductoDTOConverter productoDTOConverter;
	
	//se inyecta solo al crear el bean controlador
	//Dentro de la carpeta resources está data.sql
	
/******************************************************************************************************************/

	/**
	 * Obtenemos todos los productos
	 * 
	 * @return 404 si no hay productos, 200 y lista de productos si hay uno o más
	 */
	//Si voy a http://localhost:8080/producto me ejecuta este método
	@GetMapping("/producto")
	public ResponseEntity<?> obtenerTodos() {

		List<Producto> result=productoRepositorio.findAll();
		
		if(result.isEmpty()) {
			//Devolvemos una respuesta (404) com instancia de ResponseEntity
			return ResponseEntity.notFound().build();
		}
		else {
			//Devolvemos una lista del ProductoDTO, el map aplica a cada elemento el método convertirADto de la clase productoDTOConverter
			List<ProductoDTO> dtoList=result.stream().map(productoDTOConverter::convertirADto).collect(Collectors.toList());
			return ResponseEntity.ok(dtoList);
		}
		
		
//		List<Producto> result=productoRepositorio.findAll();
//		
//		if(result.isEmpty()) {
//			//Devolvemos una respuesta (404) com instancia de ResponseEntity
//			return ResponseEntity.notFound().build();
//		}
//		else {
//			//Devolvemos el código 200 más la lista de productos
//			return ResponseEntity.ok(result);
//		}
		
	}
	
	
/******************************************************************************************************************/

	/*
	 * Obtenemos un producto en base a su ID
	 * 
	 * @param id
	 * @return 404 si no encuentra el producto, 200 y el producto si lo encuentra
	 */
	//@PathVariable: permite inyectar un fragmento de la URL en una varialble
	//pasa el valor del id de la URL al método como parámetro donde esté @PathVariable
	@GetMapping("/producto/{id}")
	public ResponseEntity<?> obtenerUno(@PathVariable Long id) {

		//Obtenemos un
		Producto result=productoRepositorio.findById(id).orElse(null);
		//notfound es el 404
		//Si el producto no existe
		if (result== null) {
			//Lanza la excepcion que hemos creado
			throw new ProductoNotFoundException(id);
		}
		else {
			//Aquí como solo obtenemos un producto simplemente llamo al método conevrtir y le paso el result
			ProductoDTO productoDTO=productoDTOConverter.convertirADto(result);
			//y devuelvo el productoDTO
			return ResponseEntity.ok(productoDTO);
		}
		
		
	
//		Producto result=productoRepositorio.findById(id).orElse(null);
//		//notfound es el 404
//		if (result== null) {
//			return ResponseEntity.notFound().build();
//		}
//		else {
//			return ResponseEntity.ok(result);
//		}
//		
		
	}

/******************************************************************************************************************/
	/*
	 * Insertamos un nuevo producto
	 * 
	 * @param nuevo
	 * @return 201 y el producto insertado
	 * si hay algún tipo de fallo ya se programará
	 */
	//@RequestBody permite inyectar el cuerpo de la petición en un objeto
	//Guardo en "nuevo" lo que venga en el body de la peticion
	@PostMapping("/producto")
	public ResponseEntity<?> nuevoProducto(@RequestBody CreateProductoDTO nuevo) {
		Producto prod=productoDTOConverter.convertirAProd(nuevo);
		Producto saved=productoRepositorio.save(prod);
		return ResponseEntity.status(HttpStatus.CREATED).body(saved);
	}

//Sin modelMapper
//	public ResponseEntity<?> nuevoProducto(@RequestBody Producto nuevo) {
//		Producto saved=productoRepositorio.save(nuevo);
//		return ResponseEntity.status(HttpStatus.CREATED).body(saved);
//	}
	

/******************************************************************************************************************/
	//Actualizar no cambia nada al usar modelMapper
	/*
	 * Actualizamos un producto
	 * 
	 * @param editar
	 * @param id
	 * @return 200 OK si la edición tiene éxito, 404 si no se encuentra el producto
	 * 
	 */
	@PutMapping("producto/{id}")
	public ResponseEntity<?> editarProducto(@RequestBody Producto editar, @PathVariable Long id) {
		if(productoRepositorio.existsById(id)) {
			editar.setId(id);
			Producto updated=productoRepositorio.save(editar);
			return ResponseEntity.ok(updated);
		}
		else {
			return ResponseEntity.notFound().build();
		}
	}
	
//Esto no lo vamos  usar pero es para ver otra forma de programarlo, con lamda

//	public ResponseEntity<?> editarProducto(@RequestBody Producto editar, @PathVariable Long id) {
//		
//		return productoRepositorio.findById(id).map(p -> {
//			p.setNombre(editar.getNombre());
//			p.setPrecio(editar.getPrecio());
//			return ResponseEntity.ok(productoRepositorio.save(p));
//		}).orElseGet(() -> {
//			return ResponseEntity.notFound().build();
//		});
//	}
	
/******************************************************************************************************************/
	//Actualizar no cambia nada al usar modelMapper
	
	/*
	 * Borramos un producto
	 * 
	 * @param id
	 * @return Código 204(borrado) sin contenido
	 * 
	 */
	@DeleteMapping("producto/{id}")
	public ResponseEntity<?> borrarProducto(@PathVariable Long id) {
		if(productoRepositorio.existsById(id)) {
			//Producto result=productoRepositorio.findById(id).get();
			productoRepositorio.deleteById(id);
			return ResponseEntity.noContent().build();
		}
		else {
			return ResponseEntity.notFound().build();
		}
	}
	
/******************************************************************************************************************/
	
	//Cuando se produzca un error de este tipo se ejecuta este método
	//Captura la excepción con este método y devuelve un objeto ApiError
	//No es necesario llamar al método, directamente detecta la excepción
	@ExceptionHandler(ProductoNotFoundException.class)
	public ResponseEntity<ApiError> handleProductoNoEncontradoException(ProductoNotFoundException ex){
		ApiError apiError = new ApiError();
		apiError.setEstado(HttpStatus.NOT_FOUND);
		apiError.setFecha(LocalDateTime.now());
		apiError.setMensaje(ex.getMessage());
		
		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(apiError);
	}
	
	
	

}
