package org.example.cargopackingsystem.model;

public class Cargo {
    private int id;
    private int width;
    private int height;
    private int x;
    private int y;

    public Cargo() {} // Порожній конструктор для Spring

    public Cargo(int id, int width, int height) {
        this.id = id;
        this.width = width;
        this.height = height;
    }

    public void rotate() {
        int temp = width;
        width = height;
        height = temp;
    }

    // Геттери та сеттери
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getWidth() { return width; }
    public void setWidth(int width) { this.width = width; }
    public int getHeight() { return height; }
    public void setHeight(int height) { this.height = height; }
    public int getX() { return x; }
    public void setX(int x) { this.x = x; }
    public int getY() { return y; }
    public void setY(int y) { this.y = y; }
    public int getArea() { return width * height; }
}