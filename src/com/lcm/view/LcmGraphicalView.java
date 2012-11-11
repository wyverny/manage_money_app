package com.lcm.view;

import org.achartengine.ITouchHandler;
import org.achartengine.chart.AbstractChart;
import org.achartengine.chart.RoundChart;
import org.achartengine.chart.XYChart;
import org.achartengine.model.Point;
import org.achartengine.model.SeriesSelection;
import org.achartengine.renderer.DefaultRenderer;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.tools.FitZoom;
import org.achartengine.tools.PanListener;
import org.achartengine.tools.Zoom;
import org.achartengine.tools.ZoomListener;

import com.lcm.smsSmini.R;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Build;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

public class LcmGraphicalView extends View {
	  private static final String TAG = "LcmGraphicalView";
	/** The chart to be drawn. */
	  private AbstractChart mChart;
	  /** The chart renderer. */
	  private DefaultRenderer mRenderer;
	  /** The view bounds. */
	  private Rect mRect = new Rect();
	  /** The user interface thread handler. */
	  private Handler mHandler;
	  /** The paint to be used when drawing the chart. */
	  private Paint mPaint = new Paint();
	  /** The touch handler. */
	  private ITouchHandler mTouchHandler;
	  /** The old x coordinate. */
	  private float oldX;
	  /** The old y coordinate. */
	  private float oldY;

	  /**
	   * Creates a new graphical view.
	   * 
	   * @param context the context
	   * @param chart the chart to be drawn
	   */
	  public LcmGraphicalView(Context context, AbstractChart chart) {
	    super(context);
	    mChart = chart;
	    mHandler = new Handler();
	    if (mChart instanceof XYChart) {
	      mRenderer = ((XYChart) mChart).getRenderer();
	    } else {
	      mRenderer = ((RoundChart) mChart).getRenderer();
	    }

	    if (mRenderer instanceof XYMultipleSeriesRenderer
	        && ((XYMultipleSeriesRenderer) mRenderer).getMarginsColor() == XYMultipleSeriesRenderer.NO_COLOR) {
	      ((XYMultipleSeriesRenderer) mRenderer).setMarginsColor(mPaint.getColor());
	    }
	    
	    
	    
//	    int version = 7;
//	    try {
//	      version = Integer.valueOf(Build.VERSION.SDK);
//	    } catch (Exception e) {
//	      // do nothing
//	    }
//	    if (version < 7) {
//	      mTouchHandler = new TouchHandlerOld(this, mChart);
//	    } else {
//	      mTouchHandler = new TouchHandler(this, mChart);
//	    }
	  }

	  /**
	   * Returns the current series selection object.
	   * 
	   * @return the series selection
	   */
	  public SeriesSelection getCurrentSeriesAndPoint() {
	    return mChart.getSeriesAndPointForScreenCoordinate(new Point(oldX, oldY));
	  }

	  /**
	   * Transforms the currently selected screen point to a real point.
	   * 
	   * @param scale the scale
	   * @return the currently selected real point
	   */
	  public double[] toRealPoint(int scale) {
	    if (mChart instanceof XYChart) {
	      XYChart chart = (XYChart) mChart;
	      return chart.toRealPoint(oldX, oldY, scale);
	    }
	    return null;
	  }

	  @Override
	  protected void onDraw(Canvas canvas) {
	    super.onDraw(canvas);
	    canvas.getClipBounds(mRect);
	    int top = 0;
	    int left = 0;
	    int width = getMeasuredWidth();
	    int height = getMeasuredHeight();
//	    Log.e(TAG,"Top: " + top + " Left: " + left + " Width: " + width + " Height: " + height);
//	    Log.e(TAG,"Top: " + canvas.getHeight() + " Width: " + canvas.getWidth());
	    mChart.draw(canvas, left, top, width, height, mPaint);
//	    mChart.draw(canvas, 0, 0, canvas.getWidth(), canvas.getHeight(), mPaint);
	  }

	  /**
	   * Adds a new pan listener.
	   * 
	   * @param listener pan listener
	   */
	  public void addPanListener(PanListener listener) {
	    mTouchHandler.addPanListener(listener);
	  }

	  /**
	   * Removes a pan listener.
	   * 
	   * @param listener pan listener
	   */
	  public void removePanListener(PanListener listener) {
	    mTouchHandler.removePanListener(listener);
	  }

	  @Override
	  public boolean onTouchEvent(MotionEvent event) {
	    if (event.getAction() == MotionEvent.ACTION_DOWN) {
	      // save the x and y so they can be used in the click and long press
	      // listeners
	      oldX = event.getX();
	      oldY = event.getY();
	    }
	    if (mRenderer != null && (mRenderer.isPanEnabled() || mRenderer.isZoomEnabled())) {
//	      if (mTouchHandler.handleTouch(event)) {
//	        return true;
//	      }
	    }
	    return super.onTouchEvent(event);
	  }

	  /**
	   * Schedule a view content repaint.
	   */
	  public void repaint() {
	    mHandler.post(new Runnable() {
	      public void run() {
	        invalidate();
	      }
	    });
	  }

	  /**
	   * Schedule a view content repaint, in the specified rectangle area.
	   * 
	   * @param left the left position of the area to be repainted
	   * @param top the top position of the area to be repainted
	   * @param right the right position of the area to be repainted
	   * @param bottom the bottom position of the area to be repainted
	   */
	  public void repaint(final int left, final int top, final int right, final int bottom) {
	    mHandler.post(new Runnable() {
	      public void run() {
	        invalidate(left, top, right, bottom);
	      }
	    });
	  }

	  /**
	   * Saves the content of the graphical view to a bitmap.
	   * 
	   * @return the bitmap
	   */
	  public Bitmap toBitmap() {
	    setDrawingCacheEnabled(false);
	    if (!isDrawingCacheEnabled()) {
	      setDrawingCacheEnabled(true);
	    }
	    if (mRenderer.isApplyBackgroundColor()) {
	      setDrawingCacheBackgroundColor(mRenderer.getBackgroundColor());
	    }
	    setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
	    return getDrawingCache(true);
	  }

}