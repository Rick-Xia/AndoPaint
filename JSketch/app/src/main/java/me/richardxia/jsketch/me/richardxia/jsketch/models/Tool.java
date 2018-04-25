package me.richardxia.jsketch.me.richardxia.jsketch.models;

import android.graphics.Color;
import java.util.Observable;

// code based on the hellomvc4 example
public class Tool extends Observable {
    double thickness;
    ToolType current_tool;
    int colour;
    PaintShape selected_shape;

    public Tool() {
        current_tool = ToolType.SELECTION;
        selected_shape = null;
        colour = Color.parseColor("#E51400");
        this.thickness = 3.0;
        setChanged();
    }

    public double getThickness()
    {
        return thickness;
    }

    public ToolType getCurrentTool()
    {
        return current_tool;
    }

    public int getColour()
    {
        return colour;
    }

    public void setSelectedShape(PaintShape shape)
    {
        selected_shape = shape;
        setChanged();
        notifyObservers();
    }

    public PaintShape getSelectedShape()
    {
        return this.selected_shape;
    }

    public void setCurrentTool(ToolType tool)
    {
        current_tool = tool;
        setChanged();
        notifyObservers();
    }

    public void setThickness(double thickness)
    {
        this.thickness = thickness;
        setChanged();
        notifyObservers();
    }

    public void setColour(int colour)
    {
        this.colour = colour;
        setChanged();
        notifyObservers();
    }
}
