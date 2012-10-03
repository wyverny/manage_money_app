package com.lcm.view;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.achartengine.chart.AbstractChart;
import org.achartengine.chart.DoughnutChart;
import org.achartengine.chart.LineChart;
import org.achartengine.chart.PointStyle;
import org.achartengine.model.MultipleCategorySeries;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.DefaultRenderer;
import org.achartengine.renderer.SimpleSeriesRenderer;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import com.lcm.data.MonthlyData;
import com.lcm.data.ParsedData;
import com.lcm.data.ParsedDataManager;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint.Align;
import android.util.Log;

public class ChartMaker {
	private static final String TAG = "ChartMaker";
	private ParsedDataManager parsedDataManager;
	private Context mContext;
	
	public ChartMaker(Context context) {
		mContext = context;
		parsedDataManager = ParsedDataManager.getParsedDataManager(context);
	}
	
	protected AbstractChart drawDoughnutChart(Date from, Date to, Date turning) {
		// TODO: these data need to come from the database
//		Date to = new Date();
//		Log.e(TAG,"To: " + to);
//		Date from = new Date(new GregorianCalendar(to.getYear()+1900, to.getMonth(), 25).getTimeInMillis());
//		Log.e(TAG,"From: " + from);
		ArrayList<ParsedData> data = parsedDataManager.getParsedDataFromDatabase(from,to);
//		if(data!=null) {
//			for(int i=0; i<data.size(); i++) {
//				Log.e(TAG,"Parsed Data: " + data.get(i));
//			}
//		}
		
		List<double[]> values = new ArrayList<double[]>();
		values.add(new double[] { 13, 14, 11, 10, 19 });
		values.add(new double[] { 10, 9, 14, 20, 11 });
		List<String[]> titles = new ArrayList<String[]>();
		titles.add(new String[] { "P1", "P2", "P3", "P4", "P5" });
		titles.add(new String[] { "P1", "P2", "P3", "P4", "P5" });
		// by this line

		int[] colors = new int[] { Color.BLUE, Color.GREEN, Color.MAGENTA,
				Color.YELLOW, Color.CYAN };
		DefaultRenderer renderer = buildCategoryRenderer(colors);
		renderer.setApplyBackgroundColor(true);
		renderer.setBackgroundColor(Color.rgb(222, 222, 200));
		renderer.setLabelsColor(Color.GRAY);
		// return ChartFactory.getDoughnutChartIntent(context,
		// buildMultipleCategoryDataset(
		// "Project budget", titles, values), renderer, "Doughnut chart demo");
		return new DoughnutChart(buildMultipleCategoryDataset("Project budget",
				titles, values), renderer);
	}

	protected AbstractChart drawLineChart(Date from, Date to, Date turning) {
		// TODO: these data need to come from the database
		
		int[] expenseInt = null;
		double maxExpense = 0;
		
		MonthlyData monthlyData = new MonthlyData(mContext, from, to, turning);
		expenseInt = monthlyData.accumulateExpense();
		double[] expense = new double[expenseInt.length];
		System.arraycopy(expenseInt, 0, expense, 0, expenseInt.length-1);
		maxExpense = monthlyData.getTotalExpense() + monthlyData.getTotalExpense()/20;
		
		String[] titles = new String[] { "Use" };//, "Corfu", "Thassos", "Skiathos" };
		List<double[]> x_axis = new ArrayList<double[]>();
		for (int i = 0; i < titles.length; i++) {
			//TODO: x-axis: each date
			double[] x_item = new double[expense.length];
			for(int j=1; j<=expense.length; j++) {
//				x_item[j-1] = (monthlyData==null)? j : monthlyData.getEachDate(j-1);
				x_item[j-1] = j;
			}
			x_axis.add(x_item);
		}
		
		List<double[]> values = new ArrayList<double[]>();
		values.add(expense);
		// by this line
		
		int[] colors = new int[] { Color.BLUE }; //, Color.GREEN, Color.CYAN, Color.YELLOW };
		PointStyle[] styles = new PointStyle[] { PointStyle.CIRCLE }; //, PointStyle.DIAMOND, PointStyle.TRIANGLE, PointStyle.SQUARE };
		XYMultipleSeriesRenderer renderer = buildRenderer(colors, styles);
		int length = renderer.getSeriesRendererCount();
		for (int i = 0; i < length; i++) {
			((XYSeriesRenderer) renderer.getSeriesRendererAt(i))
					.setFillPoints(true);
		}
		setChartSettings(renderer, "Expense", "Month",
				"Won", 1, expense.length, 0, maxExpense, Color.LTGRAY, Color.LTGRAY);
		renderer.setXLabels((int)expense.length/2);
		renderer.setYLabels((int)maxExpense/10000);
		renderer.setShowGrid(true);
		renderer.setXLabelsAlign(Align.LEFT);
		renderer.setYLabelsAlign(Align.LEFT);
		renderer.setZoomButtonsVisible(false);
		renderer.setPanEnabled(false, false);
//		renderer.setMargins(new int[]{15,15,15,15});
//		renderer.setPanLimits(new double[] { 0, 32, 0, 40 });
//		renderer.setZoomLimits(new double[] { 0, 32, 0, 40 });
		
//		Intent intent = ChartFactory.getLineChartIntent(context, buildDataset(titles, x, values),
//		        renderer, "Average temperature");
		return new LineChart(buildDataset(titles, x_axis, values), renderer);
	}

	/**
	 * Builds a category renderer to use the provided colors.
	 * 
	 * @param colors the colors
	 * @return the category renderer
	 */
	protected DefaultRenderer buildCategoryRenderer(int[] colors) {
		DefaultRenderer renderer = new DefaultRenderer();
		renderer.setLabelsTextSize(15);
		renderer.setLegendTextSize(15);
		renderer.setMargins(new int[] { 20, 30, 15, 0 });
		for (int color : colors) {
			SimpleSeriesRenderer r = new SimpleSeriesRenderer();
			r.setColor(color);
			renderer.addSeriesRenderer(r);
		}
		return renderer;
	}

	/**
	 * Builds an XY multiple series renderer.
	 * 
	 * @param colors the series rendering colors
	 * @param styles the series point styles
	 * @return the XY multiple series renderers
	 */
	protected XYMultipleSeriesRenderer buildRenderer(int[] colors,
			PointStyle[] styles) {
		XYMultipleSeriesRenderer renderer = new XYMultipleSeriesRenderer();
		renderer.setAxisTitleTextSize(16);
		renderer.setChartTitleTextSize(20);
		renderer.setLabelsTextSize(15);
		renderer.setLegendTextSize(15);
		renderer.setPointSize(5f);
		renderer.setMargins(new int[] { 20, 30, 15, 0 });
		int length = colors.length;
		for (int i = 0; i < length; i++) {
			XYSeriesRenderer r = new XYSeriesRenderer();
			r.setColor(colors[i]);
			r.setPointStyle(styles[i]);
			renderer.addSeriesRenderer(r);
		}
		return renderer;
	}

	protected MultipleCategorySeries buildMultipleCategoryDataset(String title,
			List<String[]> titles, List<double[]> values) {
		MultipleCategorySeries series = new MultipleCategorySeries(title);
		int k = 0;
		for (double[] value : values) {
			series.add(2007 + k + "", titles.get(k), value);
			k++;
		}
		return series;
	}

	  /**
	   * Sets a few of the series renderer settings.
	   * 
	   * @param renderer the renderer to set the properties to
	   * @param title the chart title
	   * @param xTitle the title for the X axis
	   * @param yTitle the title for the Y axis
	   * @param xMin the minimum value on the X axis
	   * @param xMax the maximum value on the X axis
	   * @param yMin the minimum value on the Y axis
	   * @param yMax the maximum value on the Y axis
	   * @param axesColor the axes color
	   * @param labelsColor the labels color
	   */
	  protected void setChartSettings(XYMultipleSeriesRenderer renderer, String title, String xTitle,
	      String yTitle, double xMin, double xMax, double yMin, double yMax, int axesColor,
	      int labelsColor) {
	    renderer.setChartTitle(title);
	    renderer.setXTitle(xTitle);
	    renderer.setYTitle(yTitle);
	    renderer.setXAxisMin(xMin);
	    renderer.setXAxisMax(xMax);
	    renderer.setYAxisMin(yMin);
	    renderer.setYAxisMax(yMax);
	    renderer.setAxesColor(axesColor);
	    renderer.setLabelsColor(labelsColor);
	  }	
	  
	  /**
	   * Builds an XY multiple dataset using the provided values.
	   * 
	   * @param titles the series titles
	   * @param xValues the values for the X axis
	   * @param yValues the values for the Y axis
	   * @return the XY multiple dataset
	   */
	  protected XYMultipleSeriesDataset buildDataset(String[] titles, List<double[]> xValues,
	      List<double[]> yValues) {
	    XYMultipleSeriesDataset dataset = new XYMultipleSeriesDataset();
	    int length = titles.length;
	    for (int i = 0; i < length; i++) {
	      XYSeries series = new XYSeries(titles[i]);
	      double[] xV = xValues.get(i);
	      double[] yV = yValues.get(i);
	      int seriesLength = xV.length;
	      for (int k = 0; k < seriesLength; k++) {
	        series.add(xV[k], yV[k]);
	      }
	      dataset.addSeries(series);
	    }
	    return dataset;
	  }
}
