package br.com.aceleragep.biblioteca.controllers;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import br.com.aceleragep.biblioteca.configs.ControllerConfig;
import br.com.aceleragep.biblioteca.converts.LivroConvert;
import br.com.aceleragep.biblioteca.dtos.inputs.LivroInput;
import br.com.aceleragep.biblioteca.dtos.outputs.LivroOutput;
import br.com.aceleragep.biblioteca.entities.LivroEntity;
import br.com.aceleragep.biblioteca.services.LivroService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;

@RestController
@RequestMapping(ControllerConfig.PRE_URL + "/livros")
public class LivroController {

	@Autowired
	private LivroConvert livroConvert;

	@Autowired
	private LivroService livroService;

	@Operation(parameters = {
			@Parameter(in = ParameterIn.QUERY, description = "Paginas a ser carregada", name = "page", content = @Content(schema = @Schema(type = "integer", defaultValue = "0"))),
			@Parameter(in = ParameterIn.QUERY, description = "Quantidade de registros", name = "size", content = @Content(schema = @Schema(type = "integer", defaultValue = "5"))),
			@Parameter(in = ParameterIn.QUERY, description = "Ordenacao dos registros", name = "sort", content = @Content(array = @ArraySchema(schema = @Schema(type = "string")))) })

	@ResponseStatus(code = HttpStatus.CREATED)
	@PostMapping
	public LivroOutput cria(@RequestBody @Valid LivroInput livroInput) {
		LivroEntity livroEntity = livroConvert.inputToNewEntity(livroInput);
		LivroEntity livroCriado = livroService.cria(livroEntity);
		return livroConvert.entityToOutput(livroCriado);
	}

	@PutMapping("/{id}")
	public LivroOutput alterar(@PathVariable Long id, @RequestBody @Valid LivroInput livroInput) {
		LivroEntity livroLocalizado = livroService.buscaLivroPeloId(id);
		livroConvert.copyInputToEntity(livroLocalizado, livroInput);
		LivroEntity livroAlterado = livroService.altera(livroLocalizado);
		return livroConvert.entityToOutput(livroAlterado);
	}

	@DeleteMapping("/{id}")
	@ResponseStatus(code = HttpStatus.NO_CONTENT)
	public void remove(@PathVariable Long id) {
		LivroEntity livroLocalizado = livroService.buscaLivroPeloId(id);
		livroService.deleta(livroLocalizado);
	}

	@GetMapping("/{id}")
	public LivroOutput buscarPeloId(@PathVariable Long id) {
		LivroEntity livroLocalizado = livroService.buscaLivroPeloId(id);
		return livroConvert.entityToOutput(livroLocalizado);
	}

	@GetMapping
	public Page<LivroOutput> listaTodos(
			@PageableDefault(sort = "titulo", direction = Direction.ASC, page = 0, size = 10) @Parameter(hidden = true) Pageable paginacao) {
		Page<LivroEntity> livrosLocalizados = livroService.listaTodos(paginacao);
		return livroConvert.listPageEntityToListPageOutput(livrosLocalizados);
	}

}