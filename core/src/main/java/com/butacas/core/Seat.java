package com.butacas.core;

public class Seat {
    public enum State { AVAILABLE, SELECTED }

    private final int id, col, row;
    private State state;
    private final float x, y;

    public Seat(int id, int col, int row, State state, float x, float y) {
        this.id = id;
        this.col = col;
        this.row = row;
        this.state = state;
        this.x = x;
        this.y = y;
    }

    public int getId() { return id; }
    public int getCol() { return col; }
    public int getRow() { return row; }
    public State getState() { return state; }
    public void setState(State state) { this.state = state; }
    public float getX() { return x; }
    public float getY() { return y; }
}
