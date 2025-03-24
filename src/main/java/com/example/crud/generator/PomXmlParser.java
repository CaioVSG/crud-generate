package com.example.crud.generator;

import org.apache.maven.plugin.MojoExecutionException;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;

public class PomXmlParser {

    public static String findBasePackage() throws MojoExecutionException {
        File pomFile = new File("pom.xml");
        if (!pomFile.exists()) {
            throw new MojoExecutionException("Arquivo pom.xml não encontrado no diretório do projeto.");
        }

        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(pomFile);
            document.getDocumentElement().normalize();

            String groupId = getTagValue(document, "groupId");
            String artifactId = getTagValue(document, "artifactId");

            if (groupId == null || artifactId == null) {
                throw new MojoExecutionException("Não foi possível encontrar <groupId> ou <artifactId> no pom.xml.");
            }

            String artifactIdCamelCase = toCamelCase(artifactId);
            return groupId + "." + artifactIdCamelCase;

        } catch (Exception e) {
            throw new MojoExecutionException("Erro ao processar pom.xml", e);
        }
    }

    private static String getTagValue(Document document, String tagName) {
        NodeList nodes = document.getElementsByTagName(tagName);
        for (int i = 0; i < nodes.getLength(); i++) {
            Node node = nodes.item(i);
            if (node.getParentNode().getNodeName().equals("project")) {
                return node.getTextContent().trim();
            }
        }
        return null;
    }

    private static String toCamelCase(String text) {
        String[] parts = text.split("[-_]");
        StringBuilder camelCase = new StringBuilder(parts[0]);

        for (int i = 1; i < parts.length; i++) {
            camelCase.append(parts[i].substring(0, 1).toUpperCase()).append(parts[i].substring(1));
        }

        return camelCase.toString();
    }
}
