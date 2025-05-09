package com.example.cercleculturalandroid.models.core;

public class Seat {

    public enum State {
        AVAILABLE, SELECTED
    }

    public int id;
    public int espaiId;
    public int numerat;
    public String fila;
    public int columna;
    public State state;
    public float x, y;

    public Seat(int id, int espaiId, int numerat, String fila, int columna, State state, float x, float y) {
        this.id = id;
        this.espaiId = espaiId;
        this.numerat = numerat;
        this.fila = fila;
        this.columna = columna;
        this.state = state;
        this.x = x;
        this.y = y;
    }
}
