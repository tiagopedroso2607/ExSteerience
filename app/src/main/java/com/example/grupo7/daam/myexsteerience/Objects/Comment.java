package com.example.grupo7.daam.myexsteerience.Objects;

/**
 * Created by Tiago on 25/04/2017.
 */

public class Comment {

    private String uidAutor;
    private String nomeAutor;
    private String comentario;
    private String ratingAutor;
    private String ratingComentario;

    public Comment() {
    }

    public Comment(String uidAutor, String nomeAutor, String comentario, String ratingAutor, String ratingComentario) {
        this.uidAutor = uidAutor;
        this.nomeAutor = nomeAutor;
        this.comentario = comentario;
        this.ratingAutor = ratingAutor;
        this.ratingComentario = ratingComentario;
    }


    public String getUidAutor() {
        return uidAutor;
    }

    public void setUidAutor(String uidAutor) {
        this.uidAutor = uidAutor;
    }

    public String getNomeAutor() {
        return nomeAutor;
    }

    public void setNomeAutor(String nomeAutor) {
        this.nomeAutor = nomeAutor;
    }

    public String getComentario() {
        return comentario;
    }

    public void setComentario(String comentario) {
        this.comentario = comentario;
    }

    public String getRatingAutor() {
        return ratingAutor;
    }

    public void setRatingAutor(String ratingAutor) {
        this.ratingAutor = ratingAutor;
    }

    public String getRatingComentario() {
        return ratingComentario;
    }

    public void setRatingComentario(String ratingComentario) {
        this.ratingComentario = ratingComentario;
    }
}
