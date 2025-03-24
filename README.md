# CRUD Generator - Plugin Maven

## Descrição

O **CRUD Generator** é um plugin Maven projetado para automatizar a geração de CRUDs (Create, Read, Update, Delete) em projetos Spring Boot. Ele cria automaticamente as classes necessárias, incluindo:

- **Entidade (Model)**
- **Repositório (Repository)**
- **Serviço (Service)**
- **Controlador (Controller)**
- **DTOs (Data Transfer Objects)**
- **Exceções personalizadas**

Esse plugin reduz significativamente o tempo de desenvolvimento, padronizando a estrutura de código e garantindo boas práticas.

---

## Instalação e Configuração

Para utilizar o **CRUD Generator**, faça o clone do repositório e execute o seguinte comando:

```sh
mvn clean install
```


Em seguida, adicione o seguinte trecho ao `pom.xml` de seu projeto:

```xml
<build>
    <plugins>
        <plugin>
            <groupId>com.example.crud</groupId>
            <artifactId>crud-generator</artifactId>
            <version>1.0.0</version>
            <executions>
                <execution>
                    <goals>
                        <goal>generate</goal>
                    </goals>
                </execution>
            </executions>
        </plugin>
    </plugins>
</build>
```

Caso o plugin ainda não esteja no seu repositório Maven local, você pode instalá-lo manualmente:

```sh
mvn install
```

---

## Uso

Para gerar um CRUD para uma entidade, utilize o seguinte comando:

```sh
mvn crud-generator:generate -Dentity=NomeDaEntidade
```

Se desejar especificar um pacote base diferente, utilize:

```sh
mvn crud-generator:generate -Dentity=NomeDaEntidade -DbasePackage=br.edu.ufape.sguPraeService
```

O plugin buscará automaticamente o `groupId` e `artifactId` do `pom.xml` para determinar a estrutura correta do pacote.

### Exemplo

Se o `pom.xml` contiver:

```
<groupId>com.example</groupId>
<artifactId>demo</artifactId>
```

O pacote gerado será `com.example.demo` e os arquivos serão criados dentro das pastas correspondentes:

```
/src/main/java/com/example/demo/
    ├── models/NomeDaEntidade.java
    ├── dados/NomeDaEntidadeRepository.java
    ├── servicos/NomeDaEntidadeService.java
    ├── comunicacao/controllers/NomeDaEntidadeController.java
    ├── comunicacao/dto/NomeDaEntidadeRequest.java
    ├── comunicacao/dto/NomeDaEntidadeResponse.java
    ├── exceptions/NomeDaEntidadeNotFoundException.java
```

---

## Personalização

Os caminhos das pastas podem ser configurados na classe `CrudPaths`. Caso queira modificar a estrutura, basta alterar os valores nesta classe:

```java
public class CrudPaths {
    public static final String ENTITY_PACKAGE = "models";
    public static final String REPOSITORY_PACKAGE = "dados";
    public static final String SERVICE_PACKAGE = "servicos";
    public static final String CONTROLLER_PACKAGE = "comunicacao.controllers";
    public static final String DTO_PACKAGE = "comunicacao.dto";
    public static final String EXCEPTION_PACKAGE = "exceptions";
}
```

Por exemplo, se quiser armazenar controllers em `controllers/` ao invés de `comunicacao/controllers/`, basta modificar:

```java
public static final String CONTROLLER_PACKAGE = "controllers";
```

---

## Contribuição

Sinta-se à vontade para contribuir com melhorias e novas funcionalidades. Para isso:

1. Faça um **fork** do repositório.
2. Crie uma **branch** para sua funcionalidade (`git checkout -b minha-feature`).
3. Envie um **pull request** quando estiver pronto.

---

