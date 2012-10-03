package com.lcm.view;

import com.lcm.smsSmini.R;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

public class GraphView extends View {
	private Paint mTextPaint;
	private String mText = "";
	private int mAscent;
	
	public GraphView(Context context) {
		super(context);
		initGraphView();
	}
	
	public GraphView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initGraphView();
		
		TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.GraphStyle);
		
		CharSequence s = a.getString(R.styleable.GraphStyle_text);
		if(s != null) {
			setText(s.toString());
		}
		
		setTextColor(a.getColor(R.styleable.GraphStyle_textColor,0xFF000000));
		
		int textSize = a.getDimensionPixelOffset(R.styleable.GraphStyle_textSize,0);
		if(textSize>0) {
			setTextSize(textSize);
		}
	}

	private void setTextSize(int size) {
		mTextPaint.setTextSize(size);
		requestLayout();
		invalidate();
	}

	private void setTextColor(int color) {
		mTextPaint.setColor(color);
		invalidate();
	}

	private void setText(String text) {
		mText = text;
		requestLayout();
		invalidate();
	}

	private void initGraphView() {
		mTextPaint = new Paint();
		mTextPaint.setAntiAlias(true);
		mTextPaint.setTextSize(16);
		mTextPaint.setColor(0xFF000000);
		setPadding(3,3,3,3);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		canvas.drawText(mText, getPaddingLeft(), getPaddingTop()-mAscent, mTextPaint);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		setMeasuredDimension(measureWidth(widthMeasureSpec),measureHeight(heightMeasureSpec));
	}

	private int measureWidth(int measureSpec) {
		int result=0;
		int specMode = MeasureSpec.getMode(measureSpec);
		int specSize = MeasureSpec.getSize(measureSpec);
		
		if(specMode == MeasureSpec.EXACTLY) {
			result = specSize;
		} else {
			result = (int)mTextPaint.measureText(mText) + getPaddingLeft() + getPaddingRight();
			if(specMode == MeasureSpec.AT_MOST) {
				result = Math.min(result, specSize);
			}
		}
		return result;
	}
	
	private int measureHeight(int measureSpec) {
		int result=0;
		int specMode = MeasureSpec.getMode(measureSpec);
		int specSize = MeasureSpec.getSize(measureSpec);
		
		mAscent = (int)mTextPaint.ascent();
		if(specMode == MeasureSpec.EXACTLY) {
			result = specSize;
		} else {
			result = (int)(-mAscent+mTextPaint.descent()) + getPaddingTop() + getPaddingBottom();
			if(specMode == MeasureSpec.AT_MOST) {
				result = Math.min(result, specSize);
			}
		}
		return result;
	}
}
