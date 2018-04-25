package me.richardxia.jsketch.me.richardxia.jsketch.models;

import android.graphics.*;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.Observable;

public class PaintShape extends Observable implements Parcelable
{
    int width;
    int length;
    int radius;
    int x,y;
    int line_end_x, line_end_y;
    Paint border_paint;
    Paint fill_paint;

    static Paint selected_paint;
    static boolean selected_paint_init = false;

    double thickness;

    int touch_x, touch_y;

    ShapeType shape_type;

    private enum ShapeType {
        CIRCLE, RECTANGLE, LINE
    }

    public PaintShape(int x, int y)
    {
        this.x = x;
        this.y = y;
        this.border_paint = new Paint();
        this.border_paint.setStyle(Paint.Style.STROKE);
        this.border_paint.setAntiAlias(true);

        this.fill_paint = new Paint();
        this.fill_paint.setColor(Color.TRANSPARENT);
        this.fill_paint.setAntiAlias(true);

        if(!selected_paint_init)
        {
            selected_paint = new Paint();
            selected_paint.setColor(Color.WHITE);
            selected_paint.setAlpha(255);
            selected_paint.setStyle(Paint.Style.STROKE);
            selected_paint.setPathEffect(new DashPathEffect(new float[]{5, 10}, 0));
        }

        this.thickness = 1.0;
    }

    // needed to save and load on orientation change
    public PaintShape(Parcel copy)
    {
        this.x = copy.readInt();
        this.y = copy.readInt();

        this.shape_type = ShapeType.values()[copy.readInt()];

        this.border_paint = new Paint();
        this.border_paint.setAntiAlias(true);
        this.border_paint.setStyle(Paint.Style.STROKE);
        this.border_paint.setColor(copy.readInt());

        this.fill_paint = new Paint();
        this.fill_paint.setAntiAlias(true);
        this.fill_paint.setColor(copy.readInt());

        if(!selected_paint_init)
        {
            selected_paint = new Paint();
            selected_paint.setColor(Color.WHITE);
            selected_paint.setAlpha(255);
            selected_paint.setStyle(Paint.Style.STROKE);
            selected_paint.setPathEffect(new DashPathEffect(new float[]{5, 10}, 0));
        }

        this.thickness = copy.readDouble();
    }

    public void writeToParcel(Parcel out, int flags)
    {
        out.writeInt(this.x);
        out.writeInt(this.y);
        out.writeInt(this.shape_type.ordinal());

        out.writeInt(this.border_paint.getColor());
        out.writeInt(this.fill_paint.getColor());
        out.writeDouble(this.thickness);
    }

    //initialisers for the parcelable class
    public static final Parcelable.Creator<PaintShape> CREATOR = new Parcelable.Creator<PaintShape>() {
        public PaintShape createFromParcel(Parcel copy) {
            return new PaintShape(copy);
        }

        public PaintShape[] newArray(int size) {
            return new PaintShape[size];
        }
    };

    public void setTouchPosition(int x, int y)
    {
        this.touch_x = x;
        this.touch_y = y;
    }

    //no clue what this is about but  I need it
    public int describeContents() {return 0;}

    public int getFillColour()
    {
        int colour = this.fill_paint.getColor();

        if(colour == Color.TRANSPARENT)
        {
            return this.border_paint.getColor();
        }

        return colour;
    }

    public double getThickness()
    {
        return this.thickness;
    }

    public void setFillColour(int colour)
    {
        this.fill_paint.setColor(colour);
    }

    public void setBorderColour(int colour)
    {
        this.border_paint.setColor(colour);
    }

    public void setThickness(double thickness)
    {
        this.thickness = thickness;
    }

    public void setRectangle(int width, int length)
    {
        this.width = width;
        this.length = length;
        this.shape_type = ShapeType.RECTANGLE;
    }

    public void setCircle(int radius)
    {
        this.radius = radius;
        this.shape_type = ShapeType.CIRCLE;
    }

    public void setLine(int end_x, int end_y)
    {
        this.line_end_x = end_x;
        this.line_end_y = end_y;
        this.shape_type = ShapeType.LINE;
    }

    public boolean contains(Matrix matrix, int x, int y)
    {
        float[] points = {this.x, this.y, width, length, line_end_x, line_end_y};

        matrix.mapPoints(points);

        if(shape_type == ShapeType.RECTANGLE)
        {
            return x >= points[0] && y >= points[1] && x <= points[2] && y <= points[3];
        }
        else if(shape_type == ShapeType.CIRCLE)
        {
            // simple math from simple geometry (use a sector, then calc len of sector, then compare to radius)
            int x_center = (int)points[0];
            int y_center = (int)points[1];

            int x_delta = (x - x_center) * (x - x_center);
            int y_delta = (y - y_center) * (y - y_center);

            int total_delta = x_delta + y_delta;

            return total_delta < this.radius * this.radius;
        }
        else
        {
            //crazy length checks, thanks random wikipedia page on euclidean distances
            double dist_start_to_touch = euclidean_distance((int)points[0], (int)points[1], x, y);
            double dist_touch_to_end = euclidean_distance(x, y, (int)points[4], (int)points[5]);
            double dist_start_to_end = euclidean_distance((int)points[0], (int)points[1], (int)points[4], (int)points[5]);

            double total_approx_dist = dist_start_to_touch + dist_touch_to_end;
            double dist_delta = Math.abs(dist_start_to_end - total_approx_dist);

            return dist_delta < 1.0d;
        }
    }

    private double euclidean_distance(int x, int y, int x2, int y2)
    {
        int x_delta = (x2 - x);
        int y_delta = (y2 - y);
        return Math.sqrt((x_delta * x_delta) + (y_delta * y_delta));
    }

    public void moveShape(int x, int y)
    {
        int x_delta = x - this.touch_x;
        int y_delta = y - this.touch_y;

        if(this.shape_type == ShapeType.RECTANGLE)
        {
            this.x += x_delta;
            this.y += y_delta;

            this.width += x_delta;
            this.length += y_delta;
        }
        else if(this.shape_type == ShapeType.CIRCLE)
        {
            this.x += x_delta;
            this.y += y_delta;
        }
        else
        {
            this.x += x_delta;
            this.y += y_delta;

            this.line_end_x += x_delta;
            this.line_end_y += y_delta;
        }

        this.touch_x = x;
        this.touch_y = y;
    }

    public void createShape(int x, int y)
    {
        if(this.shape_type == ShapeType.RECTANGLE)
        {
            this.width = x;
            this.length = y;
        }
        else if(this.shape_type == ShapeType.CIRCLE)
        {
            this.radius = x - this.x;
        }
        else
        {
            this.line_end_x = x;
            this.line_end_y = y;
        }
    }

    public void drawShape(Canvas canvas, PaintShape selected_shape) {
        int colour = this.fill_paint.getColor();

        if(selected_shape == this && colour != Color.TRANSPARENT)
        {
            this.fill_paint.setAlpha(128);
        }

        Paint border_paint = (selected_shape == this) ? selected_paint : this.border_paint;
        border_paint.setStrokeWidth((float)this.getThickness());

        if(shape_type == ShapeType.RECTANGLE)
        {
            canvas.drawRect(x, y, width, length, border_paint);
            canvas.drawRect(x, y, width, length, this.fill_paint);
        }
        else if(shape_type == ShapeType.CIRCLE)
        {
            canvas.drawCircle(x, y, radius, border_paint);
            canvas.drawCircle(x, y, radius, fill_paint);
        }
        else
        {
            canvas.drawLine(x, y, line_end_x, line_end_y, border_paint);
            canvas.drawLine(x, y, line_end_x, line_end_y, fill_paint);
        }

        if(colour != Color.TRANSPARENT)
        {
            this.fill_paint.setAlpha(255);
        }
    }
}
