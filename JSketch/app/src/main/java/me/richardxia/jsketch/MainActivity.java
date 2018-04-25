package me.richardxia.jsketch;

import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.*;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ScrollView;

import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

import me.richardxia.jsketch.me.richardxia.jsketch.models.PaintCanvas;
import me.richardxia.jsketch.me.richardxia.jsketch.models.PaintShape;
import me.richardxia.jsketch.me.richardxia.jsketch.models.Tool;
import me.richardxia.jsketch.me.richardxia.jsketch.models.ToolType;

public class MainActivity extends AppCompatActivity implements Observer {
    private PaintCanvas paint_canvas;
    private CanvasView canvas;
    private Tool tool;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.setContentView(R.layout.activity_main);

        this.tool = new Tool();
        this.tool.addObserver(this);
        this.paint_canvas = new PaintCanvas();
        this.canvas = new CanvasView(this.tool, paint_canvas, this);

        View main_activity = findViewById(R.id.layout_main);
        main_activity.setWillNotCacheDrawing(true);

        ScrollView canvas_placeholder = (ScrollView)findViewById(R.id.layout_canvas);
        canvas.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT));
        canvas_placeholder.addView(canvas);

        ImageButton thickness_button = (ImageButton)findViewById(R.id.ib_thickness);
        thickness_button.setTag(R.drawable.drawable_small_thickness);

        ImageButton button = (ImageButton) findViewById(R.id.ib_eraser);

        button.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                paint_canvas.setShapes(new ArrayList<PaintShape>());
                tool.setCurrentTool(null);
                return true;
            }
        });
    }

    @Override
    public void update(Observable o, Object arg)
    {
        //TODO: update colour and tool for selected changes
        int colour = this.tool.getColour();

        if(colour == Color.parseColor("#E51400"))
        {
            changeSelectedColourButton(findViewById(R.id.ib_red));
        }
        else if(colour == Color.parseColor("#60A917"))
        {
            changeSelectedColourButton(findViewById(R.id.ib_green));
        }
        else if(colour == Color.parseColor("#0050EF"))
        {
            changeSelectedColourButton(findViewById(R.id.ib_blue));
        }
        else
        {
            changeSelectedColourButton(findViewById(R.id.ib_yellow));
        }

        ImageButton thickness_button = (ImageButton)findViewById(R.id.ib_thickness);
        double thickness = tool.getThickness();
        if(thickness == 3.0)
        {
            thickness_button.setImageResource(R.drawable.drawable_small_thickness);
            thickness_button.setTag(R.drawable.drawable_small_thickness);
        }
        else if(thickness == 5.0)
        {
            thickness_button.setImageResource(R.drawable.drawable_medium_thickness);
            thickness_button.setTag(R.drawable.drawable_medium_thickness);
        }
        else if(thickness == 10.0)
        {
            thickness_button.setImageResource(R.drawable.drawable_large_thickness);
            thickness_button.setTag(R.drawable.drawable_large_thickness);
        }

        View main_activity = findViewById(R.id.layout_main);
        main_activity.postInvalidate();

        this.canvas.postInvalidate();
    }

    @Override
    public void onSaveInstanceState(Bundle saved_instance)
    {
        super.onSaveInstanceState(saved_instance);

        saved_instance.putParcelableArrayList("paint_shapes_list", paint_canvas.getShapes());
    }

    @Override
    public void onRestoreInstanceState(Bundle saved_instance)
    {
        super.onRestoreInstanceState(saved_instance);

        ArrayList<PaintShape> shapes = saved_instance.getParcelableArrayList("paint_shapes_list");

        paint_canvas.setShapes(shapes);

        this.canvas.invalidate();
    }

    public void ib_selection_click(View v)
    {
        tool.setCurrentTool(ToolType.SELECTION);
        tool.setSelectedShape(null);

        changeSelectedToolButton(v);
    }

    public void ib_eraser_click(View v)
    {
        tool.setCurrentTool(ToolType.ERASER);
        tool.setSelectedShape(null);

        changeSelectedToolButton(v);
    }

    public void ib_rectangle_click(View v) {
        tool.setCurrentTool(ToolType.SQUARE);
        tool.setSelectedShape(null);

        changeSelectedToolButton(v);
    }

    public void ib_fill_click(View v)
    {
        tool.setCurrentTool(ToolType.BUCKET);
        tool.setSelectedShape(null);

        changeSelectedToolButton(v);
    }

    public void ib_circle_click(View v)
    {
        tool.setCurrentTool(ToolType.CIRCLE);
        tool.setSelectedShape(null);

        changeSelectedToolButton(v);
    }

    public void ib_line_click(View v)
    {
        tool.setCurrentTool(ToolType.LINE);
        tool.setSelectedShape(null);

        changeSelectedToolButton(v);
    }

    public void ib_colour_click(View v)
    {
        int colour = ((ColorDrawable)v.getBackground()).getColor();
        tool.setColour(colour);

        changeSelectedColourButton(v);

        PaintShape shape = tool.getSelectedShape();

        if(shape != null)
        {
            shape.setBorderColour(tool.getColour());
            canvas.invalidate();
        }
    }

    public void ib_thickness_click(View v)
    {
        ImageButton button = (ImageButton)v;

        int resource_id = (Integer)button.getTag();

        if(resource_id ==  R.drawable.drawable_small_thickness)
        {
            button.setImageResource(R.drawable.drawable_medium_thickness);
            button.setTag(R.drawable.drawable_medium_thickness);

            tool.setThickness(5.0);
        }
        else if(resource_id == R.drawable.drawable_medium_thickness)
        {
            button.setImageResource(R.drawable.drawable_large_thickness);
            button.setTag(R.drawable.drawable_large_thickness);

            tool.setThickness(10.0);
        }
        else
        {
            button.setImageResource(R.drawable.drawable_small_thickness);
            button.setTag(R.drawable.drawable_small_thickness);

            tool.setThickness(3.0);
        }

        PaintShape shape = tool.getSelectedShape();

        if(shape != null)
        {
            shape.setThickness(tool.getThickness());
            canvas.invalidate();
        }
    }

    private void changeSelectedColourButton(View selected_view)
    {
        ImageButton button = (ImageButton)findViewById(R.id.ib_red);
        button.setForeground(null);

        button = (ImageButton)findViewById(R.id.ib_blue);
        button.setForeground(null);

        button = (ImageButton)findViewById(R.id.ib_green);
        button.setForeground(null);

        button = (ImageButton)findViewById(R.id.ib_yellow);
        button.setForeground(null);

        button = (ImageButton)selected_view;
        button.setForeground(getDrawable(R.drawable.drawable_border));

        View main_activity = findViewById(R.id.layout_main);
        main_activity.postInvalidate();
    }

    private void changeSelectedToolButton(View selected_view)
    {
        ImageButton button = (ImageButton)findViewById(R.id.ib_selection);
        button.setForeground(null);

        button = (ImageButton)findViewById(R.id.ib_eraser);
        button.setForeground(null);

        button = (ImageButton)findViewById(R.id.ib_fill);
        button.setForeground(null);

        button = (ImageButton)findViewById(R.id.ib_rectangle);
        button.setForeground(null);

        button = (ImageButton)findViewById(R.id.ib_circle);
        button.setForeground(null);

        button = (ImageButton)findViewById(R.id.ib_line);
        button.setForeground(null);

        button = (ImageButton)selected_view;
        button.setForeground(getDrawable(R.drawable.drawable_border));

        View main_activity = findViewById(R.id.layout_main);
        main_activity.postInvalidate();
    }
}