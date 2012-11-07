package com.lcm.view;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.BarChart.Type;
import org.achartengine.model.CategorySeries;
import org.achartengine.model.RangeCategorySeries;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.DefaultRenderer;
import org.achartengine.renderer.SimpleSeriesRenderer;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint.Align;
import android.util.Log;

public class GraphViewFactory {
	private static final String TAG = "GraphViewFactory";
	private Context mContext;

	public GraphViewFactory(Context context) {
		mContext = context;
	}

	public GraphicalView getBarChartView(int[] values) {
		int maxYValue = 0;
		for (int i = 0; i < values.length; i++) {
			if(maxYValue < values[i]) maxYValue = values[i];
		}
		
		GraphicalView bar_chart = ChartFactory.getBarChartView(mContext, getBarChartDataset(values),getRenderer(maxYValue), Type.STACKED);
		return bar_chart;
	}
	
	public GraphicalView getPieChartView(int[] values) {
		CategorySeries series = new CategorySeries(null);
        DefaultRenderer renderer = new DefaultRenderer();
        int[] colors = new int[] { Color.BLUE, Color.GREEN, Color.MAGENTA,
                                   Color.YELLOW, Color.CYAN, Color.RED };

        series.add("Cupcake", new Integer(40));
        series.add("Donut", new Integer(5));
        series.add("Eclair", new Integer(10));
        series.add("Froyo", new Integer(25));
        series.add("Gingerbread", new Integer(20));
        series.add("Honeycomb", new Integer(50));

//        renderer.setLabelsTextSize(15);
//        renderer.setShowLabels(false);
        renderer.setLegendTextSize(24);
        renderer.setShowLegend(false);
        renderer.setZoomEnabled(false);
        for (int color : colors) {
            SimpleSeriesRenderer r = new SimpleSeriesRenderer();
            r.setColor(color);
            renderer.addSeriesRenderer(r);
        }

        GraphicalView pie_chart = ChartFactory.getPieChartView(mContext, series, renderer);
        return pie_chart;
	}
	
	public XYMultipleSeriesDataset getBarChartDataset(int[] values) {
		XYMultipleSeriesDataset myData = new XYMultipleSeriesDataset();
		XYSeries dataSeries = new XYSeries("data");

		for (int i = 0; i < values.length; i++) {
			dataSeries.add(i+1, values[i]);
		}
//		myRenderer.addXTextLabel(x, text)
		myData.addSeries(dataSeries);
		
		return myData;
	}
	
	public XYMultipleSeriesRenderer getRenderer(int maxYValue) {
		XYSeriesRenderer renderer = new XYSeriesRenderer();

		renderer.setColor(Color.parseColor("#158aea"));

		XYMultipleSeriesRenderer myRenderer = new XYMultipleSeriesRenderer(); 
		myRenderer.addSeriesRenderer(renderer);
		
		myRenderer.setXAxisMin(0);
		myRenderer.setXAxisMax(7);
		myRenderer.setYAxisMin(0);
		myRenderer.setYAxisMax(maxYValue*1.1);
		
		myRenderer.setDisplayChartValues(true);
		myRenderer.setChartValuesTextSize(20);

		myRenderer.setShowGrid(true);
		myRenderer.setGridColor(Color.parseColor("#c9c9c9"));
		
		myRenderer.setPanEnabled(true, false);
		myRenderer.setPanLimits(new double[]{0, 31.5, 0, 0});

		myRenderer.setShowLegend(false);
		
		myRenderer.setXLabels(8);
		myRenderer.setYLabels(5);
		myRenderer.setLabelsTextSize(20);
		myRenderer.setYLabelsAlign(Align.CENTER);
		
		myRenderer.setShowAxes(false);
		myRenderer.setBarSpacing(0.5);
		myRenderer.setZoomEnabled(false, false);
		myRenderer.setClickEnabled(false);
		
		int[] margin = {20, 50, 50, 30};
		myRenderer.setMargins(margin);
		myRenderer.setMarginsColor(Color.parseColor("#FFFFFF"));
		
		return myRenderer;
	}

}
