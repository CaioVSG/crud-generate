package com.example.crud.generator;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;

import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;



@Mojo(name = "generate")
public class CrudGeneratorMojo extends AbstractMojo {
        @Parameter(property = "entity", required = true)
        private String entity;

        @Parameter(property = "basePackage")
        private String basePackage;

        public void execute() throws MojoExecutionException {
            if (entity == null || entity.isEmpty()) {
                throw new MojoExecutionException("Nome da entidade não pode ser vazio.");
            }
            if (basePackage == null || basePackage.isEmpty()) {
                getLog().info("Detectando automaticamente o basePackage...");
                basePackage = PomXmlParser.findBasePackage();
                getLog().info("BasePackage detectado: " + basePackage);
            }

            try {
                String basePath = "src/main/java/" + basePackage.replace(".", "/");
                generateEntity(basePath);
                generateRepository(basePath);
                generateException(basePath);
                generateService(basePath);
                generateRequest(basePath);
                generateResponse(basePath);
                generateController(basePath);
                getLog().info("CRUD para " + entity + " gerado com sucesso!");
            } catch (IOException e) {
                throw new MojoExecutionException("Erro ao gerar CRUD", e);
            }
        }

        private void generateEntity(String basePath) throws IOException {
            String content = """
            package %s.%s;
        
            import jakarta.persistence.*;
            import lombok.AllArgsConstructor;
            import lombok.NoArgsConstructor;
            import lombok.Getter;
            import lombok.Setter;
        
            @Entity
            @Getter @Setter @AllArgsConstructor @NoArgsConstructor
            public class %s {
                @Id
                @GeneratedValue(strategy = GenerationType.IDENTITY)
                private Long id;
            }
            """.formatted(basePackage, CrudPaths.ENTITY_PACKAGE, entity);

            writeFile(CrudPaths.getFullPath(basePath, CrudPaths.ENTITY_PACKAGE, entity + ".java"), content);
        }

        private void generateRepository(String basePath) throws IOException {
            String content = """
            package %s.%s;
            
            import %s.%s.%s;
            import org.springframework.data.jpa.repository.JpaRepository;
            import org.springframework.stereotype.Repository;
            
            @Repository
            public interface %sRepository extends JpaRepository<%s, Long> {
            }
            """.formatted(basePackage, CrudPaths.REPOSITORY_PACKAGE, basePackage, CrudPaths.ENTITY_PACKAGE, entity, entity, entity);

            writeFile(CrudPaths.getFullPath(basePath, CrudPaths.REPOSITORY_PACKAGE, entity + "Repository.java"), content);
        }

        private void generateException(String basePath) throws IOException {
            String content = """
            package %s.%s;
            
            import org.springframework.data.crossstore.ChangeSetPersister;
            import org.springframework.web.bind.annotation.ResponseStatus;
            
            @ResponseStatus(value = org.springframework.http.HttpStatus.NOT_FOUND, reason = "%s não encontrado")
            public class %sNotFoundException extends ChangeSetPersister.NotFoundException {
            
            }
            """.formatted(basePackage, CrudPaths.EXCEPTION_PACKAGE, entity, entity);

            writeFile(CrudPaths.getFullPath(basePath, CrudPaths.EXCEPTION_PACKAGE, entity + "NotFoundException.java"), content);
        }

        private void generateService(String basePath) throws IOException {
            String content = """
            package %s.%s;
           \s
            import %s.%s.%s;
            import %s.%s.%sRepository;
            import %s.%s.%sNotFoundException;
            import org.modelmapper.ModelMapper;
            import org.springframework.stereotype.Service;
            import lombok.RequiredArgsConstructor;
            import java.util.List;
           \s
            @Service @RequiredArgsConstructor
            public class %sService {
                private final %sRepository repository;
                private final ModelMapper modelMapper;
           \s
                public List<%s> listar() {
                    return repository.findAll();
                }
           \s
                public %s buscar(Long id) throws %sNotFoundException {
                    return repository.findById(id).orElseThrow(%sNotFoundException::new);
                }
           \s
                public %s salvar(%s entity) {
                    return repository.save(entity);
                }
           \s
                public %s editar(Long id, %s entity) throws %sNotFoundException {
                    %s %s = buscar(id);
                    modelMapper.map(entity, %s);
                    return repository.save(%s);
                }
           \s
                public void deletar(Long id){
                    repository.deleteById(id);
                }
            }
           \s""".formatted(basePackage, CrudPaths.SERVICE_PACKAGE, basePackage, CrudPaths.ENTITY_PACKAGE, entity,
                    basePackage, CrudPaths.REPOSITORY_PACKAGE, entity, basePackage, CrudPaths.EXCEPTION_PACKAGE, entity, entity, entity,  entity, entity, entity,
                    entity, entity, entity, entity, entity, entity, entity, entity.toLowerCase(), entity.toLowerCase(), entity.toLowerCase());

            writeFile(CrudPaths.getFullPath(basePath, CrudPaths.SERVICE_PACKAGE, entity + "Service.java"), content);
        }

        private void generateRequest(String basePath) throws IOException {
            String content = """
                package %s.%s.%s;
                
                import %s.%s.%s;
                
                import lombok.AllArgsConstructor;
                import lombok.Getter;
                import lombok.NoArgsConstructor;
                import lombok.Setter;
                import org.modelmapper.ModelMapper;
                
                @Getter @Setter @AllArgsConstructor @NoArgsConstructor
                public class %sRequest {
                
                    public %s convertToEntity(%sRequest %sRequest, ModelMapper modelMapper) {
                                return modelMapper.map(%sRequest, %s.class);
                            }
                }
                """.formatted(basePackage, CrudPaths.DTO_PACKAGE, entity.toLowerCase(), basePackage,CrudPaths.ENTITY_PACKAGE, entity, entity, entity, entity, entity.toLowerCase(), entity.toLowerCase(), entity);

            writeFile(CrudPaths.getFullPath(basePath, CrudPaths.DTO_PACKAGE + "." + entity.toLowerCase(), entity + "Request.java"), content);
        }

        private void generateResponse(String basePath) throws IOException {
            String content = """
                package %s.%s.%s;
                
                import %s.%s.%s;
                
                import lombok.Getter;
                import lombok.Setter;
                import org.modelmapper.ModelMapper;
                
                @Getter @Setter
                public class %sResponse {
                
                    public %sResponse(%s %s, ModelMapper modelMapper){
                        if (%s == null) throw new IllegalArgumentException("%s não pode ser nulo");
                        else modelMapper.map(%s, this);
                    }
                }
                """.formatted(basePackage, CrudPaths.DTO_PACKAGE, entity.toLowerCase(), basePackage,CrudPaths.ENTITY_PACKAGE, entity, entity, entity, entity, entity.toLowerCase(), entity.toLowerCase(), entity, entity.toLowerCase());

            writeFile(CrudPaths.getFullPath(basePath, CrudPaths.DTO_PACKAGE + "." + entity.toLowerCase(), entity + "Response.java"), content);
        }

        private void generateController(String basePath) throws IOException {
            String content = """
            package %s.%s;
           \s
            import %s.%s.%s;
            import %s.%s.%sService;
            import %s.%s.%s.%sResponse;
            import %s.%s.%s.%sRequest;
            import %s.%s.%sNotFoundException;
           \s
           \s
            import org.modelmapper.ModelMapper;
            import org.springframework.web.bind.annotation.*;
            import org.springframework.http.ResponseEntity;
           \s
            import jakarta.validation.Valid;
            import lombok.RequiredArgsConstructor;
            import java.util.List;
            import org.springframework.http.HttpStatus;
           \s
            @RestController
            @RequiredArgsConstructor
            @RequestMapping("/%s")
            public class %sController {
                private final %sService service;
                private final ModelMapper modelMapper;
           \s
           \s
                @GetMapping
                public List<%sResponse> listar() {
                    return service.listar().stream().map(%s -> new %sResponse(%s, modelMapper)).toList();
                }
           \s
                @GetMapping("/{id}")
                public ResponseEntity<%sResponse> buscar(@PathVariable Long id) throws %sNotFoundException {
                    %s response = service.buscar(id);
                    return new ResponseEntity<>(new %sResponse(response, modelMapper), HttpStatus.OK);
                }
           \s
                @PostMapping
                public ResponseEntity<%sResponse> salvar(@Valid @RequestBody %sRequest entity) {
                    %s response = service.salvar(entity.convertToEntity(entity, modelMapper));
                    return new ResponseEntity<>(new %sResponse(response, modelMapper), HttpStatus.CREATED);
                }
           \s
                @PatchMapping("/{id}")
                public ResponseEntity<%sResponse> editar(@PathVariable Long id, @Valid @RequestBody %sRequest entity) throws %sNotFoundException {
                    %s response = service.editar(id, entity.convertToEntity(entity, modelMapper));
                    return new ResponseEntity<>(new %sResponse(response, modelMapper), HttpStatus.OK);
                }
           \s
                @DeleteMapping("/{id}")
                public ResponseEntity<Void> delete(@PathVariable Long id) {
                    service.deletar(id);
                    return new ResponseEntity<>(HttpStatus.NO_CONTENT);
                }
            }
           \s""".formatted(basePackage, CrudPaths.CONTROLLER_PACKAGE, basePackage, CrudPaths.ENTITY_PACKAGE, entity,
                    basePackage, CrudPaths.SERVICE_PACKAGE, entity, basePackage, CrudPaths.DTO_PACKAGE, entity.toLowerCase(), entity, basePackage, CrudPaths.DTO_PACKAGE,
                    entity.toLowerCase(), entity, basePackage, CrudPaths.EXCEPTION_PACKAGE, entity, entity.toLowerCase(), entity, entity, entity, entity.toLowerCase(), entity, entity.toLowerCase(),
                    entity, entity, entity, entity, entity, entity, entity, entity, entity, entity, entity, entity, entity);

            writeFile(CrudPaths.getFullPath(basePath, CrudPaths.CONTROLLER_PACKAGE, entity + "Controller.java"), content);
        }

        private void writeFile(String path, String content) throws IOException {
            File file = new File(path);
            file.getParentFile().mkdirs();
            try (FileWriter writer = new FileWriter(file)) {
                writer.write(content);
            }
        }




}
