package com.example.crud.generator;

public class CrudPaths {
    public static final String ENTITY_PACKAGE = "models";  // Pasta das entidades
    public static final String REPOSITORY_PACKAGE = "dados";  // Pasta dos repositórios
    public static final String SERVICE_PACKAGE = "servicos";  // Pasta dos serviços
    public static final String CONTROLLER_PACKAGE = "comunicacao.controllers";  // Pasta dos controllers
    public static final String DTO_PACKAGE = "comunicacao.dto";  // Pasta dos DTOs
    public static final String EXCEPTION_PACKAGE = "exceptions";  // Pasta das exceções

    public static String getFullPath(String basePath, String subPackage, String fileName) {
        return basePath + "/" + subPackage.replace(".", "/") + "/" + fileName;
    }
}
