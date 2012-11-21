package com.lcm.view;

import java.util.Calendar;
import java.util.HashMap;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.BarChart;
import org.achartengine.chart.PieChart;
import org.achartengine.chart.XYChart;
import org.achartengine.chart.BarChart.Type;
import org.achartengine.model.CategorySeries;
import org.achartengine.model.RangeCategorySeries;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.DefaultRenderer;
import org.achartengine.renderer.SimpleSeriesRenderer;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYMultipleSeriesRenderer.Orientation;
import org.achartengine.renderer.XYSeriesRenderer;

import com.lcm.smsSmini.R;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint.Align;
import android.util.Log;

public class GraphViewFactory {
	private static final String TAG = "GraphViewFactory";
	private Context mContext;
	private String[] colours = { "#7CFC00","#FFF700","#FFFACD","#FDD5B1",
			"#D3D3D3","#F08080","#F56991","#90EE90","#FFB6C1","#B38B6D", "#FBA0E3"};

	public GraphViewFactory(Context context) {
		mContext = context;
	}

	public LcmGraphicalView getBarChartView(int[] values) {
		int maxYValue = 0;
		for (int i = 0; i < values.length; i++) {
			if(maxYValue < values[i]) maxYValue = values[i];
		}
		
		XYChart chart = new BarChart(getBarChartDataset(values),getRenderer(maxYValue), Type.STACKED);
		return new LcmGraphicalView(mContext, chart);
	}
	
	public LcmGraphicalView getPieChartView(HashMap<String,Integer> datas) {
		CategorySeries series = new CategorySeries("이번달 항목별 사용량");
        DefaultRenderer renderer = new DefaultRenderer();
//        int[] colors = new int[] { Color.BLUE, Color.GREEN, Color.MAGENTA,
//                                   Color.YELLOW, Color.CYAN, Color.RED };

        int size = 0;
        for (String category : datas.keySet()) {
        	if(datas.get(category)!=0) {
        		size++;
        		series.add(category,datas.get(category));
        	}			
		}
//        series.add("Cupcake", new Integer(40));
//        series.add("Donut", new Integer(5));
//        series.add("Eclair", new Integer(10));
//        series.add("Froyo", new Integer(25));
//        series.add("Gingerbread", new Integer(20));
//        series.add("Honeycomb", new Integer(50));

        renderer.setLabelsTextSize(30);
        renderer.setLabelsColor(Color.BLACK);
//        renderer.setShowLabels(false);
//        renderer.setLegendTextSize(50);
        renderer.setShowLegend(false);
        renderer.setZoomEnabled(false);
        renderer.setAntialiasing(true);
//        renderer
        
        
        for(int i=0; i<size; i++) {
//        for (int color : colors) {
        	SimpleSeriesRenderer r = new SimpleSeriesRenderer();
            r.setColor(Color.parseColor(colours[i]));
            renderer.addSeriesRenderer(r);
        }
        
        renderer.setMargins(new int[] {60,0,0,0});
//        renderer.setChartTitle("이번달 항목별 사용");
        renderer.setChartTitleTextSize(50);
        PieChart chart = new PieChart(series, renderer);
        return new LcmGraphicalView(mContext, chart);
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

		renderer.setColor(Color.parseColor("#6495ED"));
//		renderer.setColor(Color.BLACK);

		XYMultipleSeriesRenderer myRenderer = new XYMultipleSeriesRenderer(); 
		myRenderer.addSeriesRenderer(renderer);
		
		myRenderer.setXAxisMin(0);
		myRenderer.setXAxisMax(7);
		myRenderer.setYAxisMin(1);
		myRenderer.setYAxisMax(maxYValue*1.1);
		
		myRenderer.setDisplayChartValues(true);
		myRenderer.setChartValuesTextSize(20);

		myRenderer.setShowGrid(true);
		myRenderer.setGridColor(Color.BLACK);
		
		myRenderer.setPanEnabled(false, false);
		myRenderer.setPanLimits(new double[]{0, 31.5, 0, 0});

		myRenderer.setShowLegend(false);
		
		myRenderer.setXLabels(0);
		myRenderer.setYLabels(5);
		myRenderer.setLabelsTextSize(25);
		myRenderer.setYLabelsAlign(Align.CENTER);
		myRenderer.setYLabelsAngle(-20);
		myRenderer.setYLabelsColor(0, Color.BLACK);
		myRenderer.setXLabelsColor(Color.BLACK);
		
		myRenderer.setShowAxes(true);
		myRenderer.setBarSpacing(0.5);
		myRenderer.setZoomEnabled(false, false);
		myRenderer.setClickEnabled(false);
		
//		myRenderer.setChartTitle("지난달 별 총 사용");
        myRenderer.setChartTitleTextSize(50);
        myRenderer.setLabelsColor(Color.BLACK);
		
		int[] margin = {20, 50, 20, 15};
		myRenderer.setMargins(margin);
		myRenderer.setMarginsColor(Color.parseColor("#BCD4E6"));
		
		myRenderer.clearXTextLabels();
		Calendar thisMonth = Calendar.getInstance();
		for(int i=6; i>0; i--) {			
			myRenderer.addXTextLabel(i, (thisMonth.get(Calendar.MONTH)+1)+"월");
			thisMonth.add(Calendar.MONTH,-1);
		}
		
		return myRenderer;
	}

}
