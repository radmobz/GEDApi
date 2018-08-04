package com.julien.juge.photos.api.utils;

import io.vavr.Lazy;
import io.vavr.collection.List;

import java.util.Arrays;
import java.util.Objects;

/**
 * Fournit les éléments nécessaires à la création d'un path pour Alfresco.
 */
public class DocumentPath {

    /**
     * Recherche par path dans OpenCmis ne supporte pas File.separator
     */
    public static final String SEPARATOR = "/";

    private List<String> parts;
    private Lazy<String> computedPath = Lazy.of(this::compute);

    private String compute() {

        String computed = parts.mkString(SEPARATOR);

        if (!computed.startsWith(SEPARATOR)){
            computed = SEPARATOR.concat(computed);
        }

        return computed;
    }

    public DocumentPath() {
        this(List.empty());
    }

    public DocumentPath(String... parts) {
        this(List.ofAll(Arrays.asList(parts)));
    }

    public DocumentPath(java.util.List<String> parts) {
        this(List.ofAll(parts));
    }

    public DocumentPath(List<String> parts) {
        this.parts = List.ofAll(parts);
    }

    public DocumentPath(DocumentPath parent) {
        this.parts = List.ofAll(parent.parts);
    }

    public DocumentPath(DocumentPath parent, String... parts) {
        this.parts = List.ofAll(parent.parts).appendAll(List.ofAll(Arrays.asList(parts)));
    }

    public DocumentPath addPart(String part) {
        return new DocumentPath(this.parts.append(part));
    }

    public DocumentPath addParts(String... parts) {
        return new DocumentPath(this.parts.appendAll(Arrays.asList(parts)));
    }

    /**
     * @return Chemin construit à partir des parts, séparé par le SEPARATOR, et préfixé par le SEPARATOR
     */
    @Override
    public String toString() {
        return computedPath.get();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        DocumentPath that = (DocumentPath) o;
        return Objects.equals(parts, that.parts);
    }

    @Override
    public int hashCode() {

        return Objects.hash(parts);
    }
}
