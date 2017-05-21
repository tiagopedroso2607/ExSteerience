package com.example.grupo7.daam.myexsteerience.Objects;

/**
 * Created by Tiago on 07/04/2017.
 */

public class Post {

    private String titulo;
    private String descricao;
    private String autor;
    private String categoria;
    private String data;
    private String cidade;
    private String uidAutor;
    private String idPost;



    public Post() {
    }

    public Post(String titulo, String descricao, String autor, String categoria, String data, String cidade, String uidAutor, String idPost) {
        this.titulo = titulo;
        this.descricao = descricao;
        this.autor = autor;
        this.categoria = categoria;
        this.data = data;
        this.cidade = cidade;
        this.uidAutor = uidAutor;
        this.idPost = idPost;
    }

    public String getIdPost() {
        return idPost;
    }

    public void setIdPost(String idPost) {
        this.idPost = idPost;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getAutor() {
        return autor;
    }

    public void setAutor(String autor) {
        this.autor = autor;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getCidade() {
        return cidade;
    }

    public void setCidade(String cidade) {
        this.cidade = cidade;
    }

    public String getUidAutor() {
        return uidAutor;
    }

    public void setUidAutor(String uidAutor) {
        this.uidAutor = uidAutor;
    }
}

