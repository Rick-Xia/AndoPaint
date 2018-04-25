package me.richardxia.jsketch;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Camera;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.view.*;

import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

import me.richardxia.jsketch.me.richardxia.jsketch.models.PaintCanvas;
import me.richardxia.jsketch.me.richardxia.jsketch.models.PaintShape;
import me.richardxia.jsketch.me.richardxia.jsketch.models.Tool;
import me.richardxia.jsketch.me.richardxia.jsketch.models.ToolType;

@SuppressLint("ViewConstructor")
public class CanvasView extends View implements Observer {
    private PaintCanvas paint_canvas;
    private Tool tool;

    private ScaleGestureDetector scale_detector;
    private float scale_factor = 1.0f;

    float initial_touch_x;
    float initial_touch_y;

    Camera camera = new Camera();
    Matrix canvas_matrix;

    public CanvasView(Tool tool, PaintCanvas paint_canvas, Context context) {
        super(context);

        this.tool = tool;

        this.paint_canvas = paint_canvas;
        this.paint_canvas.addObserver(this);

        this.setWillNotDraw(false);
        this.setWillNotCacheDrawing(true);

        scale_detector = new ScaleGestureDetector(context, new ScaleListener());
    }

    @SuppressWarnings("deprecation")
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.save(); //Google devs do it, so I'm gonna do it too
        canvas.drawColor(Color.DKGRAY);

        camera.applyToCanvas(canvas);
        canvas_matrix = canvas.getMatrix(); // this may be costly since it returns a new copy, we call onDraw a lot...
        canvas.scale(scale_factor, scale_factor);

        ArrayList<PaintShape> shapes = this.paint_canvas.getShapes();
        for(PaintShape shape : shapes)
        {
            shape.drawShape(canvas, tool.getSelectedShape());
        }

        canvas.restore();
    }

    @Override
    protected void onSizeChanged (int width, int height, int old_width, int old_height)
    {
        super.onSizeChanged(width, height, old_width, old_height);

        //TODO: figure out way to use zoom and panning to fix shapes from being clipped
    }

    // this is from https://developer.android.com/guide/topics/ui/custom-components.html
    // fills up the parent container
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int parent_width = MeasureSpec.getSize(widthMeasureSpec);
        int parent_height = MeasureSpec.getSize(heightMeasureSpec);

        this.setMeasuredDimension(parent_width, parent_height);
    }

    // scaling logic taken from Android SDK, https://developer.android.com/training/gestures/scale.html
    @Override
    public boolean onTouchEvent(MotionEvent e) {
        super.onTouchEvent(e);

        //System.out.println(e);

        scale_detector.onTouchEvent(e);

        // we are doing a gesture
        if(e.getPointerCount() > 1 || scale_detector.isInProgress())
        {
            tool.setSelectedShape(null);
            return true;
        }

        // needed to get canvas matrix
        this.invalidate();

        int x = (int)(e.getX()/ scale_factor);
        int y = (int) (e.getY() / scale_factor);

        PaintShape selected_shape = tool.getSelectedShape();
        ToolType selected_tool = tool.getCurrentTool();

        int action = e.getAction();

        if(action == MotionEvent.ACTION_DOWN)
        {
            initial_touch_x = e.getX();
            initial_touch_y = e.getY();

            if (selected_tool == ToolType.SQUARE || selected_tool == ToolType.LINE || selected_tool == ToolType.CIRCLE)
            {
                PaintShape new_shape = new PaintShape(x, y);
                new_shape.setBorderColour(tool.getColour());
                new_shape.setThickness(tool.getThickness());

                if (selected_tool == ToolType.SQUARE)
                {
                    new_shape.setRectangle(1, 1);
                }
                else if(selected_tool == ToolType.CIRCLE)
                {
                    new_shape.setCircle(1);
                }
                else
                {
                    new_shape.setLine(x + 1, y + 1);
                }

                tool.setSelectedShape(new_shape);
                paint_canvas.addShape(new_shape);

                this.invalidate();
            }

            if(selected_tool == ToolType.SELECTION)
            {
                selected_shape = paint_canvas.getShape(canvas_matrix, x, y);
                tool.setSelectedShape(selected_shape);

                if(selected_shape != null)
                {
                    tool.setThickness(selected_shape.getThickness());
                    tool.setColour(selected_shape.getFillColour());

                    selected_shape.setTouchPosition(x, y);
                }
            }
            else if(selected_tool == ToolType.ERASER)
            {
                selected_shape = paint_canvas.getShape(canvas_matrix,x, y);

                if(selected_shape != null)
                {
                    this.tool.setSelectedShape(null);
                    this.paint_canvas.removeShape(selected_shape);

                    this.invalidate();
                }
            }
            else if(selected_tool == ToolType.BUCKET)
            {
                selected_shape = paint_canvas.getShape(canvas_matrix, x, y);

                if(selected_shape != null)
                {
                    selected_shape.setFillColour(this.tool.getColour());
                    this.tool.setSelectedShape(null);

                    this.invalidate();
                }
            }
        }
        else if(action == MotionEvent.ACTION_MOVE)
        {
            if(selected_shape != null)
            {
                if (selected_tool == ToolType.SQUARE || selected_tool == ToolType.LINE || selected_tool == ToolType.CIRCLE)
                {
                    selected_shape.createShape(x, y);
                }
                else if(selected_tool == ToolType.SELECTION)
                {
                    selected_shape.moveShape(x, y);
                }

                this.invalidate();
            }
            else if(!(selected_tool == ToolType.SQUARE || selected_tool == ToolType.LINE || selected_tool == ToolType.CIRCLE) && (selected_tool == ToolType.SELECTION))
            {
                camera.translate(e.getX() - initial_touch_x, e.getY() - initial_touch_y, 0.0f);
                initial_touch_x = e.getX();
                initial_touch_y = e.getY();

                this.invalidate();
            }
        }
        else if(action == MotionEvent.ACTION_UP)
        {
            if(selected_shape != null)
            {
                if (selected_tool == ToolType.SQUARE || selected_tool == ToolType.LINE || selected_tool == ToolType.CIRCLE)
                {
                    selected_shape.createShape(x, y);
                    tool.setSelectedShape(null);
                }
                else if(selected_tool == ToolType.SELECTION)
                {
                    selected_shape.moveShape(x, y);
                }
            }
        }

        return true;
    }

    @Override
    public void update(Observable o, Object arg)
    {
        //force a redraw
       // this.invalidate();
    }

    // code taken from Android SDK, https://developer.android.com/training/gestures/scale.html
    private class ScaleListener
            extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            scale_factor *= detector.getScaleFactor();

            // Don't let the object get too small or too large.
            scale_factor = Math.max(0.1f, Math.min(scale_factor, 3.0f));

            invalidate();
            return true;
        }
    }
}
