package com.example.Histogram;

import java.util.Random;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.PointStyle;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import android.app.ActionBar.LayoutParams;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class Graph extends Activity {
	private XYMultipleSeriesDataset mDataset = new XYMultipleSeriesDataset();
	  private XYMultipleSeriesRenderer mRenderer = new XYMultipleSeriesRenderer();
	  private XYSeries mCurrentSeries;
	  private XYSeriesRenderer mCurrentRenderer;
	  private GraphicalView mChartView;
	  
	  String[] data = new String[256];
	  	  
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.graph);
	    final Intent i;
		i = getIntent();//����Ʈ�� �޾ƿ�
		
		final int Max = i.getIntExtra("Average", 0);//Lowest GSV in the region
		mRenderer.setApplyBackgroundColor(true);
	    mRenderer.setBackgroundColor(Color.argb(100, 50, 50, 50));
	    mRenderer.setAxisTitleTextSize(60);
	    mRenderer.setChartTitleTextSize(60);
	    mRenderer.setLabelsTextSize(20);
	    mRenderer.setLegendTextSize(0);
	    mRenderer.setXLabels(20);
	    mRenderer.setMargins(new int[] { 20, 20, 20, 0 });
	    mRenderer.setZoomButtonsVisible(false);
	    mRenderer.setZoomEnabled(false, false);//Ȯ�� ����
	    mRenderer.setZoomEnabled(false);//Ȯ�� ����
	    mRenderer.setPanEnabled(false);//�׷��� �� �̵� ����
	    mRenderer.setPointSize(5);
	    
	    RelativeLayout layout = (RelativeLayout) findViewById(R.id.graphview);
	      mChartView = ChartFactory.getLineChartView(this, mDataset, mRenderer);
	      // enable the chart click events
	      mRenderer.setClickEnabled(false);
	      mRenderer.setSelectableBuffer(10);
	      layout.addView(mChartView, new LayoutParams(LayoutParams.MATCH_PARENT,//FILL_PARENT,
	          LayoutParams.MATCH_PARENT));//FILL_PARENT));
	    
	      String seriesTitle = "Realative GSV = " + Max;
          // create a new series of data
          XYSeries series = new XYSeries(seriesTitle);
          mRenderer.setXTitle(seriesTitle);
          mDataset.addSeries(series);
          mCurrentSeries = series;
          // create a new renderer for the new series
          XYSeriesRenderer renderer = new XYSeriesRenderer();
          mRenderer.addSeriesRenderer(renderer);
          // set some renderer properties
          renderer.setPointStyle(PointStyle.CIRCLE);
          renderer.setFillPoints(true);
          renderer.setDisplayChartValues(true);
          renderer.setDisplayChartValuesDistance(10);
          
          Random mRandom = new Random();
          int color = mRandom.nextInt(5);
     	renderer.setColor(Color.YELLOW);
      	 mCurrentRenderer = renderer;
          
          double x = 0;
          double y = 0;
          
          data = i.getStringArrayExtra("data");//data��̿� ���� ���� ����.
          final int h = i.getIntExtra("Height",0);//��������
  	    Log.w("testest", "testest");
  	    for(int j=0; j<h; j++){//256��ŭ �ݺ���.
      	try {
  	          x = x+1;//�̺κ��� x���� 1�� �߰��ϴ� �κ�.
  	        } catch (NumberFormatException e) {
  	          return;
  	        }
  	        try {
  	        	y =  Double.parseDouble(data[j].toString());
  	        	//y�࿡ �Է��� ������ �ִ� �κ�. Array. for�����ν� �Է��� ����ŭ ���.
  	        } catch (NumberFormatException e) {
  	          return;
  	        }
  	        // add a new data point to the current series
  	        mCurrentSeries.add(x, y);
  	        mChartView.repaint();
  	        }
	  }
}

