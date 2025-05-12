package com.butacas.core;

public class Seat {
    public enum State { AVAILABLE, SELECTED }

    private final int id;
    private final int col;
    private final int row;
    private State state;
    private final float x;
    private final float y;

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
