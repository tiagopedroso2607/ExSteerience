package com.example.grupo7.daam.myexsteerience.Objects;

/**
 * Created by Tiago on 31/03/2017.
 */

public class User {

    private String email, nome;
    private String rating;

    public User() {

    }
    public User(String email, String nome, String rating) {
        this.email = email;
        this.nome = nome;
        this.rating = rating;
    }

    public String getEmail() {

        return email;
    }

    public String getNome() {
        return nome;
    }


    public String getRating() {
        return rating;
    }



}
