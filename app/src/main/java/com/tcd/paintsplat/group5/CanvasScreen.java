package com.tcd.paintsplat.group5;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;


public class CanvasScreen extends View {
    private int xPos, yPos;
    private final int rectWidth, rectHeight; //bitmap rectangle dimensions
    //private final Bitmap rectBitmap; //bitmap of the rectangle
    private Bitmap rectBitmap; //bitmap of the rectangle (not final)

    //boundary points
    private final float boundaryScreenStart, boundaryScreenTop, boundaryScreenEnd, boundaryScreenBottom;
    private int xDir = 1; //to detect the direction the rectangle moves in on x-axis
    private int yDir = 1; //to detect the direction the rectangle moves in on y-axis
    private Paint splatPaint; // Paint instance for the paint splat
    private boolean[] rect_sections = new boolean[100]; //Boolean array to keep track of what sections have been hit
    private float[] section_width_array = new float[10];
    private float[] section_height_array= new float[10];
    private float section_width;
    private float section_height;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    public CanvasScreen(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

//        get the device's screen width and height in pixels
        int screenWidth = context.getResources().getDisplayMetrics().widthPixels;
        int screenHeight = context.getResources().getDisplayMetrics().heightPixels;

//        to make rectangle dimensions proportional to the device's screen size
        rectWidth = (int) (0.4 * screenWidth);
        rectHeight = (int) (0.3f * screenHeight);

//      get the min and max x and y points allowed for the screen boundaries
        boundaryScreenStart = 0.05f * screenWidth;
        boundaryScreenTop = 0.05f * screenHeight;
        boundaryScreenEnd = 0.95f * screenWidth;
        boundaryScreenBottom = 0.95f * screenHeight;

        xPos = (int) (boundaryScreenStart + 50);
        yPos = (int)(boundaryScreenTop + 20);


        section_height =rectHeight/10;
        section_width =rectWidth/10;
        for(int i =0;i<10;i++){

            section_height_array[i] = section_height*i;
            section_width_array[i] = section_width*i;

        }


        /*
         * configure the square bitmap
         * the reason for using bitmap instead of canvas' rect is
         * the difficulty of moving the rect without changing the size
         * based on the official documentation https://developer.android.com/reference/android/graphics/Bitmap.Config
         * it is recommended to use ARGB_8888 whenever possible
         * */
        Bitmap.Config bitConfig = Bitmap.Config.ARGB_8888;
        rectBitmap = Bitmap.createBitmap(rectWidth, rectHeight, bitConfig);
        Canvas canvas = new Canvas(rectBitmap);
        canvas.drawColor(getResources().getColor(R.color.colorWhite)); // setting the color of bitmap rectangle

        //canvas.drawRect(xPos + 100, yPos + 100, xPos + (rectWidth/10), yPos + (rectWidth/10), splatPaint);

        splatPaint = new Paint();
        splatPaint.setColor(getResources().getColor(R.color.colorTimer));
        //canvas.drawRect(xPos + 100, yPos + 100, xPos + (rectWidth/10), yPos + (rectWidth/10), splatPaint);

        DatabaseReference referenceCanvas = database.getReference("canvas_1");
        //addPostEventListener(referenceCanvas);
        referenceCanvas.addChildEventListener(new ChildEventListener() {


            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                System.out.println(snapshot);
                String indexOfClick = snapshot.getKey();
                updateBitmap(Integer.parseInt(indexOfClick));
                //UpdateLocalDb
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    @Override
    protected void onDraw(Canvas canvas) {

        canvas.drawBitmap(rectBitmap, xPos, yPos, new Paint());
        //canvas.drawBitmap(updateBitmap(), xPos, yPos, new Paint());
        super.onDraw(canvas);
    }
    /*
     * function to move the rectangle bitmap across the screen
     * without going out of bounds
     * */
    public void moveBitmap(float speed, float rateChange) {
        float change = speed * rateChange;
        if (xPos < boundaryScreenStart || (xPos + rectWidth) > boundaryScreenEnd) {
            xDir *= -1; //make it move in the opposite Direction
        }
        if (yPos < (boundaryScreenTop) || (yPos + rectHeight) > boundaryScreenBottom) {
            yDir *= -1; //make it move in the opposite Direction
        }
        // changes the positions according to the rate of change and direction
        xPos += (change * xDir);
        yPos += (change * yDir);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        //canvas.drawRect(xPos, yPos, xPos+200,yPos+300, splatPaint);
        System.out.println(event.getX());
        System.out.println(event.getY());
        float touch_x = event.getX();
        float touch_y = event.getY();

        if ( (touch_x > xPos) && (touch_x < xPos+rectWidth) &&
                (touch_y > yPos) && (touch_y < yPos+rectHeight)) {

            float relative_x = touch_x - xPos; //the x coordinate within the rectangle
            int x_section_coord = (int) Math.floor(relative_x / (rectWidth/10));
            System.out.println(x_section_coord);

            float relative_y = touch_y - yPos; //the x coordinate within the rectangle
            int y_section_coord = (int) Math.floor(relative_y / (rectHeight/10));

            int section_hit = (x_section_coord)+(y_section_coord*10);

            System.out.println("HIT SECTION: "+section_hit);

            if (!(rect_sections[section_hit] == true)) {
                rect_sections[section_hit] = true;


                String reference = "canvas_1"+"/"+section_hit;
                DatabaseReference ref = database.getReference(reference);
                ref.setValue("True");



            }
        }


        //TODO: detect touch, check if on canvas, paint, no overlap, count score
        return super.onTouchEvent(event);
    }


    public void updateBitmap(int rect_section){

        rectBitmap = rectBitmap.copy(Bitmap.Config.ARGB_8888, true);

        Canvas canvas = new Canvas(rectBitmap);

        //From the index of the boolean array, we can work out what area in the rectangle needs to be filled
        int x_column = rect_section % 10;
        int y_row = (int) Math.floor(rect_section / 10);


        canvas.drawRect(section_width_array[x_column],section_height_array[y_row],
                section_width_array[x_column]+section_width,section_height_array[y_row]+section_height, splatPaint);



    }
}
