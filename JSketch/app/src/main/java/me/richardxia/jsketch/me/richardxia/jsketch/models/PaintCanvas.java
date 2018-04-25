package me.richardxia.jsketch.me.richardxia.jsketch.models;

import android.graphics.Matrix;

import java.util.*;

// code based on the hellomvc4 example
public class PaintCanvas extends Observable {
    private ArrayList<PaintShape> shapes = new ArrayList<PaintShape>();

    public PaintCanvas() {
        setChanged();
    }

    public void addShape(PaintShape shape)
    {
        shapes.add(shape);
        setChanged();
        notifyObservers();
    }

    public void removeShape(PaintShape shape)
    {
        shapes.remove(shape);
        setChanged();
        notifyObservers();
    }

    public PaintShape getShape(Matrix matrix, int x, int y)
    {
        for(int i = shapes.size() - 1; i > -1; i--)
        {
            PaintShape shape = shapes.get(i);
            if(shape.contains(matrix, x, y))
            {
                return shape;
            }
        }

        return null;
    }

    public ArrayList<PaintShape> getShapes()
    {
        return this.shapes;
    }

    public void setShapes(ArrayList<PaintShape> shapes)
    {
        this.shapes = shapes;
        setChanged();
        notifyObservers();
    }
}