package com.ghstudios.android.components;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

/*
 * Draws a sharpness level by values
 *
 * Max sharpness units combined should not exceed the value of int maxsharpness
 */
public class DrawSharpness extends View {

    // Error Tag
    private static final String TAG = "DrawSharpness";

    private int mRed1;
	private int mOrange1;
	private int mYellow1;
	private int mGreen1;
	private int mBlue1;
	private int mWhite1;
	private int mPurple1;
    private int mRed2;
    private int mOrange2;
    private int mYellow2;
    private int mGreen2;
    private int mBlue2;
    private int mWhite2;
    private int mPurple2;
    private int mRed3;
    private int mOrange3;
    private int mYellow3;
    private int mGreen3;
    private int mBlue3;
    private int mWhite3;
    private int mPurple3;

    private int mheight;
    private int mwidth;

    private final int maxsharpness = 45;

	public static int orangeColor = Color.rgb(255, 150, 0);
    public static int purpleColor = Color.rgb(120, 81, 169);
    public static int blueColor = Color.rgb(20,131,208);

	Paint paint = new Paint();

    public DrawSharpness(Context context, AttributeSet attrs){
        super(context, attrs);

        // If previewing, set some fake display
        if (isInEditMode()) {
            init(new int[] {6,5,11,9,4,0,0},
                    new int[] {6,5,11,9,6,0,0},
                    new int[]{ 6,5,11,9,6,3,0});
        }
    }

	public void init(int[] sharpness1, int[] sharpness2, int[]sharpness3) {

        // Assign sharpness array 1
		mRed1 = sharpness1[0];
		mOrange1 = sharpness1[1];
		mYellow1 = sharpness1[2];
		mGreen1 = sharpness1[3];
		mBlue1 = sharpness1[4];
		mWhite1 = sharpness1[5];
		mPurple1 = sharpness1[6];

        // Assign sharpness array 2
        mRed2 = sharpness2[0];
        mOrange2 = sharpness2[1];
        mYellow2 = sharpness2[2];
        mGreen2 = sharpness2[3];
        mBlue2 = sharpness2[4];
        mWhite2 = sharpness2[5];
        mPurple2 = sharpness2[6];

        mRed3 = sharpness3[0];
        mOrange3 = sharpness3[1];
        mYellow3 = sharpness3[2];
        mGreen3 = sharpness3[3];
        mBlue3 = sharpness3[4];
        mWhite3 = sharpness3[5];
        mPurple3 = sharpness3[6];
	}

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width, height;

        // Width should be no greater than 500px
        width = Math.min(500, MeasureSpec.getSize(widthMeasureSpec));
        // Height should be no greater than 50px
        height = Math.min(60, MeasureSpec.getSize(heightMeasureSpec));

        mwidth = width;
        mheight = height;

        setMeasuredDimension(width, height);
    }

    @Override
    public void requestLayout() {
        /*
         * Do nothing here
         */
    }

    @Override
	public void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // Margins are defined by height/7. 7 can be changed.
        int margins = (int) Math.floor(mheight/8);

        int outer_margin = margins;   //Margin on the outside
        int inner_margin = 3;   //Margin between elements

        // Scale factor is used to multiply sharpness values to make sure full sharpness fills the bar
        // Must be a float to retain accuracy until pixel conversion
        float scalefactor = (float) (mwidth-(outer_margin*2))/maxsharpness;
        // specify the width of each bar
        int barwidth = (int) (scalefactor * maxsharpness) + (outer_margin*2);

        int totalBarHeight = (mheight - (2*outer_margin) - (2*inner_margin));
        int barheight = 0;
        int mainBarHeight = 0;
        int subBarHeight = 0;

        //3 Possible cases of rounding
        //The number of pixels available to the 3 bars is
        //   No extra pixels - Perfect (We give 2 pixels from each of the sub bars to the main bar)
        //   1 Extra pixel - We give the extra pixel to the main bar
        //   2 Extra pixels - give an extra pixel to each of the sub bars, reducing the diff in size)
        if(totalBarHeight % 3 == 0) {
            barheight = (int) (totalBarHeight / 3);
            mainBarHeight = barheight+4;
            subBarHeight = barheight-2;
        }
        else if(totalBarHeight % 3 == 1){
            //1 Extra Pixed - Give it to main bar
            barheight = (int) (totalBarHeight / 3);
            mainBarHeight = barheight+5;
            subBarHeight = barheight-2;
        }
        else if(totalBarHeight % 3 == 2){
            //2 Extra pixel
            barheight = (int) (totalBarHeight / 3);
            mainBarHeight = barheight+4;
            subBarHeight = barheight-1;
        }


        // Draw the background
        paint.setColor(Color.BLACK);
        paint.setStrokeWidth(4);
        canvas.drawRect(0, 0, barwidth, mheight, paint);

        // Draw top bar
        int bartop = outer_margin;
        int barbottom = (int) Math.floor(outer_margin+mainBarHeight);
        drawBar(canvas, outer_margin, scalefactor, bartop, barbottom,
                mRed1, mOrange1, mYellow1, mGreen1, mBlue1, mWhite1, mPurple1);

        // Draw bottom bar
        int bartop2 = (int) Math.floor(barbottom+inner_margin);
        int barbottom2 = (int) Math.floor(bartop2+subBarHeight);
        drawBar(canvas, outer_margin, scalefactor, bartop2, barbottom2,
                mRed2, mOrange2, mYellow2, mGreen2, mBlue2, mWhite2, mPurple2);

        int bartop3 = (int) Math.floor(barbottom2+inner_margin);
        int barbottom3 = (int) Math.floor(bartop3+subBarHeight);
        drawBar(canvas, outer_margin, scalefactor, bartop3, barbottom3,
                mRed3, mOrange3, mYellow3, mGreen3, mBlue3, mWhite3, mPurple3);

	}

    private void drawBar(Canvas canvas, int margins, float scalefactor, int bartop, int barbottom,
                         int ired, int iorange, int iyellow,
                         int igreen, int iblue, int iwhite, int ipurple){

        // Run through the bar and accumulate sharpness
        int start = margins;
        int end = start + (int) (ired*scalefactor);
        paint.setStrokeWidth(0);
        paint.setColor(Color.RED);
        canvas.drawRect(start, bartop, end, barbottom, paint);

        start = end;
        end = end + (int) (iorange*scalefactor);
        paint.setColor(orangeColor);
        canvas.drawRect(start, bartop, end, barbottom, paint);

        start = end;
        end = end + (int) (iyellow*scalefactor);
        paint.setColor(Color.YELLOW);
        canvas.drawRect(start, bartop, end, barbottom, paint);

        start = end;
        end = end + (int) (igreen*scalefactor);
        paint.setColor(Color.GREEN);
        canvas.drawRect(start, bartop, end, barbottom, paint);

        start = end;
        end = end + (int) (iblue*scalefactor);
        paint.setColor(blueColor);
        canvas.drawRect(start, bartop, end, barbottom, paint);

        start = end;
        end = end + (int) (iwhite*scalefactor);
        paint.setColor(Color.WHITE);
        canvas.drawRect(start, bartop, end, barbottom, paint);

        start = end;
        end = end + (int) (ipurple*scalefactor);
        paint.setColor(purpleColor);
        canvas.drawRect(start, bartop, end, barbottom, paint);
    }

}