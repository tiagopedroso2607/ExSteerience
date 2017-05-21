package com.example.grupo7.daam.myexsteerience.Objects;

/**
 * Created by Tiago on 10/05/2017.
 */

public class UserInfo {


    private String nrVotosRecebidos, nrVotosFeitos, nrComentarios, mediaRatingDado, nrPost;
    private String dataCriacao;

    public UserInfo() {

    }
    public UserInfo(String nrVotosRecebidos, String nrVotosFeitos, String dataCriacao, String nrComentarios, String mediaRatingDado, String nrPost) {
        this.nrVotosRecebidos = nrVotosRecebidos;
        this.nrVotosFeitos = nrVotosFeitos;
        this.dataCriacao = dataCriacao;
        this.nrComentarios = nrComentarios;
        this.mediaRatingDado = mediaRatingDado;
        this.nrPost = nrPost;
    }

    public String getNrPost() {
        return nrPost;
    }

    public void setNrPost(String nrPost) {
        this.nrPost = nrPost;
    }

    public String getNrVotosRecebidos() {
        return nrVotosRecebidos;
    }

    public void setNrVotosRecebidos(String nrVotosRecebidos) {
        this.nrVotosRecebidos = nrVotosRecebidos;
    }

    public String getNrVotosFeitos() {
        return nrVotosFeitos;
    }

    public void setNrVotosFeitos(String nrVotosFeitos) {
        this.nrVotosFeitos = nrVotosFeitos;
    }

    public String getDataCriacao() {
        return dataCriacao;
    }

    public void setDataCriacao(String dataCriacao) {
        this.dataCriacao = dataCriacao;
    }

    public String getNrComentarios() {
        return nrComentarios;
    }

    public void setNrComentarios(String nrComentarios) {
        this.nrComentarios = nrComentarios;
    }

    public String getMediaRatingDado() {
        return mediaRatingDado;
    }

    public void setMediaRatingDado(String mediaRatingDado) {
        this.mediaRatingDado = mediaRatingDado;
    }
}
