package com.lcm.view;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import com.lcm.data.MonthlyData;
import com.lcm.data.SettingsPreference;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

/**
 * ������ : ���ϳ� (���)
 * ���� ��¥ : 2011.02.26
 * �÷��� : ���� 7 64bit, ��Ŭ����, �ȵ���̵� SDK(2.1 ver7)
 * 
 * ������Ʈ : 2011.03.12
 * ���� : �������� �߰��Ͽ� ���� ���� ����
 * ������ ��¥�� �۾��� ���� ����̹��� ���� ����
 * 
 * ������Ʈ : 2011.06.20
 * ���� : �� ���ο� 14�Ͼ� ǥ���ϴ� �͵� �ϰ� ������ ��ƴٴ� ���� �־
 * �ּ��� �߰��ϰ� 4�� 14ĭ�� �غý��ϴ�.
 * ���θ�� �ؼ� ������ �����ϴ°� �� �˾Ƽ�....
**/

public class GsCalendar {
	// / �翬�� �̾߱����� ���ٿ� 7�Ͼ� �ȵ� ��� ����ó���� ������� �ʾҴ� -_-;;
	public final static int ROWS = 7; // / ��/���
	public final static int COLS = 7; // / ĭ/����
	private static final String TAG = "GsCalendar";

	// final static int ROWS = 4 ; /// ��/���
	// final static int COLS = 14 ; /// ĭ/����
	//

	private Context mContext; // / context

	private LinearLayout m_targetLayout; // / �޷��� ���� ���̾ƿ�
	private Button[] m_controlBtn; // / �޷� ��Ʈ���� ��ư 4�� [���⵵, �����⵵, ����, ������]
	private TextView[] m_viewTv; // / �� �� �� ǥ���� �ؽ�Ʈ�� 3��[��, �� , ��]

	private Calendar m_Calendar; // / ����� �޷�

	private LinearLayout[] m_lineLy; // / 7���� ����ǥ�� + �ִ� 6��
	private LinearLayout[] m_cellLy; // / 7ĭ
	private TextView[] m_cellTextBtn; // / �� ĭ���� ���� �ؽ�Ʈ�� (��ưó�� �̺�Ʈ �ַ��� Btn �̶� �ٿ���)
	private TextView[] m_cellStatBtn;
	// / ��� ��ư���� �ϰ������ ��ư�� �ؽ�Ʈ ������ �׾ �����¿� ������ ����
	// / �ؽ�Ʈ�� ©���� TextView�� ���� �� �ۿ� ����

	private LinearLayout[] m_horizontalLine; // / ��輱 ���� ����
	private LinearLayout[] m_verticalLine; // / ��輱 ���� ����

	private int m_startPos; // / ������ ��� ���� �� ��ġ
	private int m_lastDay; // / �� ���� ��������
	private int m_selDay; // / ���� ���õ� ��¥
	
	private MonthlyData monthlyData;
	private Date[] dates;

	// //////////////////////////////////////

	private float m_displayScale; // / ȭ�� ����� ���� �ؽ�Ʈ ũ�� ������ �����
	private float m_textSize; // / �ؽ�Ʈ ������(�� ������ ������ ������)
	private float m_topTextSize; // / �����ؽ�Ʈ ������(���� �������� ������)

	private int m_tcHeight = 50; // / ���� ���� �κ� ��ĭ�� ����
	private int m_cWidth = 50; // / ��ĭ�� ����
	private int m_cHeight = 50; // / ��ĭ�� ����
	private int m_lineSize = 1; // / ��輱�� ����

	static public class gsCalendarColorParam {
		int m_lineColor = 0xff000000; // / ��輱 ��
		int m_cellColor = 0xffffffff; // / ĭ�� ����
		int m_topCellColor = 0xffcccccc; // / ���� ����
		int m_textColor = 0xff000000; // / �۾���
		int m_sundayTextColor = 0xffff0000; // / �Ͽ��� �۾���
		int m_saturdayTextColor = 0xff0000ff; // / ����� �۾���
		int m_topTextColor = 0xff000000; // / ���� �۾���
		int m_topSundayTextColor = 0xffff0000; // / ���� �Ͽ��� �۾���
		int m_topSaturdatTextColor = 0xff0000ff; // / ���� ����� �۾���

		int m_todayCellColor = 0x999999ff; // / ���ó�¥�� ����
		int m_todayTextColor = 0xffffffff; // / ���ó�¥�� �۾���
	}

	private gsCalendarColorParam m_colorParam;

	// / ������ �����ϰ� ������ bgcolor�� ó����( ���� ���������� )
	private Drawable m_bgImgId = null; // / ��ü ����̹���
	private Drawable m_cellBgImgId = null; // / ��ĭ�� ��� �̹���
	private Drawable m_topCellBgImgId = null; // / ��� ���� ���� �κ��� ��� �̹���

	private Drawable m_todayCellBgImgId = null; // / ���� ��¥�� ��� �̹���

	// / ��ܿ� ǥ���ϴ� ���� �ؽ�Ʈ
	private String[] m_dayText = { "��", "��", "ȭ", "��", "��", "��", "��" };

	// /////////////////////////////////////////

	private Button m_preYearBtn; // / ���⵵ ��ư
	private Button m_nextYearBtn; // / �����⵵ ��ư
	private Button m_preMonthBtn; // / ���� ��ư
	private Button m_nextMonthBtn; // / ������ ��ư

	private TextView m_yearTv; // / �� ǥ�ÿ� �ؽ�Ʈ
	private TextView m_mothTv; // / �� ǥ�ÿ� �ؽ�Ʈ
	private TextView m_dayTv; // / ��¥ ǥ�ÿ� �ؽ�Ʈ

	// / ������ MMdd�������� �ִ´�.
	// / ������ 2�� 4 5 6�̶��
	// / [0204] [0205] [0206] �̷��� ����
	private ArrayList<Integer> m_holiDay = new ArrayList<Integer>();

	public GsCalendar(Context context, LinearLayout layout, Calendar calendar) {
		this(context,layout);
		m_Calendar = calendar;
	}
	
	// / ������
	public GsCalendar(Context context, LinearLayout layout) {
		// / context����
		mContext = context;

		// / Ÿ�� ���̾ƿ� ����
		m_targetLayout = layout;

		// / ���� ��¥�� �޷� ����
		m_Calendar = Calendar.getInstance();
		m_Calendar.add(Calendar.MONTH, 1);

		// / ǥ���� ������ ���̾� ����
		m_lineLy = new LinearLayout[COLS]; // / 7���� ���̾ƿ� ����
		m_cellLy = new LinearLayout[COLS * ROWS]; // / �׾ȿ� �ٴ� 7���� �� 49���� ���̾ƿ� ����
		m_cellTextBtn = new TextView[COLS * ROWS]; // / ������ ������ ��ư�� ����
		m_cellStatBtn = new TextView[COLS * ROWS];
		m_horizontalLine = new LinearLayout[COLS - 1]; // / ���� ���м� ���̾ƿ�
		m_verticalLine = new LinearLayout[(COLS - 1) * ROWS]; // / ���� ���м� ���̾ƿ�

		// / ȭ���� ũ�⿡ ���� ������
		m_displayScale = context.getResources().getDisplayMetrics().density;

		m_topTextSize = m_displayScale * 11.0f;
		m_textSize = m_displayScale * 10.0f;

		m_colorParam = new gsCalendarColorParam();

		// ROWS = 7 ; /// ��/���
		// COLS = 14 ; /// ĭ/����
	}

	// / �޷��� �����Ѵ�.( ��� �ɼǵ�[�÷���, �ؽ�Ʈ ũ�� ��]�� ������ �Ŀ� �������� ��� �� �� ȣ��)
	public void initCalendar() {
		createViewItem();
		setLayoutParams();
		setLineParam();
		updateMonthlyData();
		setContentext();
		setOnEvent();
		printView();
	}

	// / �÷��� �Ķ���� ����
	public void setColorParam(gsCalendarColorParam param) {
		m_colorParam = param;
	}

	// / ������� �� �̹����� ����
	public void setBackground(Drawable bg) {
		m_bgImgId = bg;
	}

	public void setCellBackground(Drawable bg) {
		m_cellBgImgId = bg;
	}

	public void setTopCellBackground(Drawable bg) {
		m_topCellBgImgId = bg;
	}

	public void setCalendarSize(int width, int height) {
		m_cWidth = (width - (m_lineSize * COLS - 1)) / COLS;
		m_cHeight = (height - (m_lineSize * ROWS - 1)) / ROWS;
		m_tcHeight = (height - (m_lineSize * COLS - 1)) / COLS;
	}

	public void setCellSize(int cellWidth, int cellHeight, int topCellHeight) {
		m_cWidth = cellWidth;
		m_cHeight = cellHeight;
		m_tcHeight = topCellHeight;
	}

	public void setTopCellSize(int topCellHeight) {
		m_tcHeight = topCellHeight;
	}

	public void setCellSize(int allCellWidth, int allCellHeight) {
		m_cWidth = allCellWidth;
		m_cHeight = allCellHeight;
		m_tcHeight = allCellHeight;
	}

	public void setTextSize(float size) {
		m_topTextSize = m_displayScale * size;
		m_textSize = m_displayScale * size;
	}

	public void setTextSize(float textSize, float topTextSize) {
		m_topTextSize = m_displayScale * topTextSize;
		m_textSize = m_displayScale * textSize;
	}

	public void redraw() {
		m_targetLayout.removeAllViews();
		initCalendar();
	}

	// ////////////////// ������ ��¥ĭ�� ��ȭ�� �ִ� �Լ� //////////////////////////
	// / �̳༮�� �ҷ������� ���´� ��¥�� ���÷� ���õǾ��ְų� ���� �������� ����
	// / �׷����� m_cellLy[ ��¥ + m_startPos ].setTextColor( ) ;
	// / m_startPos�� ������ ������ ��¥�� ���ϸ� �ش� ��¥ĭ�� ������� �ٲ� �� ����
	// / ////////////////////////////////////////////////////////////////////
	// / ���õ� ��¥ĭ�� ��ȭ�� �ֱ����� �Լ� 1ȣ
	public void setSelectedDay(int cellColor, int textColor) {
		m_colorParam.m_todayCellColor = cellColor;
		m_colorParam.m_todayTextColor = textColor;
		m_cellTextBtn[m_Calendar.get(Calendar.DAY_OF_MONTH) + m_startPos - 1]
				.setTextColor(textColor);
		m_cellTextBtn[m_Calendar.get(Calendar.DAY_OF_MONTH) + m_startPos - 1]
				.setBackgroundColor(cellColor);
		m_cellStatBtn[m_Calendar.get(Calendar.DAY_OF_MONTH) + m_startPos - 1]
		              .setTextColor(textColor);
		m_cellStatBtn[m_Calendar.get(Calendar.DAY_OF_MONTH) + m_startPos - 1]
		              .setBackgroundColor(cellColor);
	}

	// / ���õ� ��¥ĭ�� ��ȭ�� �ֱ����� �Լ� 2ȣ
	public void setSelectedDayTextColor(int textColor) {
		m_colorParam.m_todayTextColor = textColor;
		m_cellTextBtn[m_Calendar.get(Calendar.DAY_OF_MONTH) + m_startPos - 1]
				.setTextColor(textColor);
		m_cellStatBtn[m_Calendar.get(Calendar.DAY_OF_MONTH) + m_startPos - 1]
		              .setTextColor(textColor);
	}

	// / ���õ� ��¥ĭ�� ��ȭ�� �ֱ����� �Լ� 3ȣ
	public void setSelectedDay(Drawable bgimg) {
		m_todayCellBgImgId = bgimg;
		m_colorParam.m_todayCellColor = 0x00000000;
		m_cellTextBtn[m_Calendar.get(Calendar.DAY_OF_MONTH) + m_startPos - 1]
				.setBackgroundDrawable(bgimg);
		m_cellStatBtn[m_Calendar.get(Calendar.DAY_OF_MONTH) + m_startPos - 1]
		              .setBackgroundDrawable(bgimg);
		Log.d("===", (m_Calendar.get(Calendar.DAY_OF_MONTH) - 1) + "");
	}

	// /////////////////////////// ������ ó�� ///////////////////////
	// / ������ MMdd�������� �ִ´�.
	// / ������ 2�� 4 5 6�̶��
	// / [0204] [0205] [0206] �̷��� ����
	public void addHoliday(int holiday_MMdd) {
		m_holiDay.add(holiday_MMdd);
	}

	// / ������ ����Ʈ�� �������鼭 �ش� ��¥�� �Ͽ��ϰ� ���� ������ ����
	public void applyHoliday() {
		// / ���� �޷��� ���� ����
		Integer iMonth = m_Calendar.get(Calendar.MONTH) + 1;

		// / ���Ϸ� ����� ��� ��¥ ���� ���� ����
		for (int k = 0; k < m_holiDay.size(); k++) {
			int holiday = m_holiDay.get(k); // / ���� ���� ���Ѵ���
			if (holiday / 100 == iMonth) // / ���� ������ ���
			{
				// / �ش� ��¥�� ���� �÷��� ����
				m_cellTextBtn[holiday % 100 + m_startPos]
						.setTextColor(m_colorParam.m_sundayTextColor);
				m_cellStatBtn[holiday % 100 + m_startPos]
				              .setTextColor(m_colorParam.m_sundayTextColor);
			}
		}
	}

	// / ������ ����ŭ ���� �������� ��¥ ǥ��Ǵ� �� + ��輱 �ټ�
	public void createViewItem() {
		// / 7���̸� ��輱 ���α��� ���ؼ� 13���� �����ؾ��Ѵ�.
		for (int i = 0; i < ROWS * 2 - 1; i++) {
			// / ¦�������϶���
			if (i % 2 == 0) {
				// / ������ 13�� ���������� ������ ǥ�õǴ� ������ 7����
				m_lineLy[i / 2] = new LinearLayout(mContext);
				m_targetLayout.addView(m_lineLy[i / 2]); // ���� ���̾ƿ��� �ڽ����� ���

				// / ��¥ǥ�� ĭ�� ��輱 ���ؼ� 13���� ĭ�� ����
				for (int j = 0; j < COLS * 2 - 1; j++) {

					// / �޷� ������ ���� ĭ
					if (j % 2 == 0) {
						int pos = ((i / 2) * COLS) + (j / 2);

//						Log.d("pos1", "" + pos);
						m_cellLy[pos] = new LinearLayout(mContext);
						m_cellLy[pos].setOrientation(LinearLayout.VERTICAL);
						m_cellTextBtn[pos] = new TextView(mContext);
						m_cellStatBtn[pos] = new TextView(mContext);
						m_lineLy[i / 2].addView(m_cellLy[pos]);
						m_cellLy[pos].addView(m_cellTextBtn[pos]);
						m_cellLy[pos].addView(m_cellStatBtn[pos]);

					} else // / �̰� �ܼ��� ��輱
					{
						int pos = ((i / 2) * (COLS - 1)) + (j - 1) / 2;

//						Log.d("pos2", "" + pos);
						m_verticalLine[pos] = new LinearLayout(mContext);
						m_lineLy[i / 2].addView(m_verticalLine[pos]);
					}
				}
			} else {// / �̰� ������ ��輱
				m_horizontalLine[(i - 1) / 2] = new LinearLayout(mContext);
				m_targetLayout.addView(m_horizontalLine[(i - 1) / 2]);
			}
		}
	}

	// / ���̾ƿ��� ��ư�� ����, �۾��� �� ViewParams�� ����
	public void setLayoutParams() {
		// / ���� ���̾ƿ��� ���η� ����
		m_targetLayout.setOrientation(LinearLayout.VERTICAL);
		// / ���� ��ü ����� ������ �־���
		if (m_bgImgId != null) {
			m_targetLayout.setBackgroundDrawable(m_bgImgId);
		}

		// / ������ ����ŭ ���� �������� ��¥ ǥ��Ǵ� �� + ��輱 �ټ�
		for (int i = 0; i < ROWS * 2 - 1; i++) {
			if (i % 2 == 0) {
				// / �� ������ �����ϴ� ���̾ƿ����� ���η� ����~
				LinearLayout.LayoutParams param_row = 
					new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT,
							LayoutParams.FILL_PARENT);// / ���̾ƿ� ������� warp_content�� ����
				param_row.weight = 1;
				m_lineLy[i / 2].setOrientation(LinearLayout.HORIZONTAL);
				m_lineLy[i / 2].setLayoutParams(param_row); 
						

				// / ��ĭ��ĭ �ɼ��� ����
				for (int j = 0; j < COLS; j++) {
					int cellnum = ((i / 2) * COLS) + j;
					// / ��ĭ��ĭ�� �����ϴ� ���̾ƿ� ������� ���� wrap_content�� ����
					LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(
							LayoutParams.FILL_PARENT,
							LayoutParams.FILL_PARENT);
					
					param.weight = 1;
					// param.setMargins( 1, 1, 1, 1 ) ; /// ������ 1�� �༭ ������ �׸���.
					m_cellLy[cellnum].setLayoutParams(param);
					m_cellLy[cellnum].setOrientation(LinearLayout.VERTICAL);
					// / ��ĭ��ĭ ���� ��ư

					// / ���ϴ� ���� �۾��� �۾� ũ�� �����ϴ� �κ�

					// / ù������ ��ȭ��������� ǥ���ϴ� �κ�
					if (i == 0) {
						// / ���� ǥ���ϴ� �κ��� ���� ����
						m_cellTextBtn[cellnum]
								.setLayoutParams(new LinearLayout.LayoutParams(
										m_cWidth, m_tcHeight));

						// / ���� �۾���
						if (m_topCellBgImgId != null) {
							m_cellLy[cellnum]
									.setBackgroundDrawable(m_topCellBgImgId);
						} else {
							m_cellLy[cellnum]
									.setBackgroundColor(m_colorParam.m_topCellColor);
						}

						// / ����ϰ� �Ͽ����� �ٸ� �÷��� ǥ���Ѵ�.
						switch (j) {
						case 0:
							m_cellTextBtn[cellnum]
									.setTextColor(m_colorParam.m_topSundayTextColor);
							m_cellStatBtn[cellnum]
						            .setTextColor(m_colorParam.m_topSundayTextColor);
							break;
						case 6:
							m_cellTextBtn[cellnum]
									.setTextColor(m_colorParam.m_topSaturdatTextColor);
							m_cellStatBtn[cellnum]
							        .setTextColor(m_colorParam.m_topSaturdatTextColor);
							break;
						default:
							m_cellTextBtn[cellnum]
									.setTextColor(m_colorParam.m_topTextColor);
							m_cellStatBtn[cellnum]
							        .setTextColor(m_colorParam.m_topTextColor);
							break;
						}

						// / �۾� ũ��
						m_cellTextBtn[cellnum].setTextSize(m_topTextSize);
						m_cellStatBtn[cellnum].setTextSize(m_topTextSize);
					} else // / ���ϴ� ��¥ ǥ���ϴ� �κ�
					{

						// / ���� ǥ�õǴ� �κ��� ���̿� ����
//						m_cellTextBtn[cellnum]
//								.setLayoutParams(new LinearLayout.LayoutParams(
//										m_cWidth, m_cHeight));
						m_cellStatBtn[cellnum]
					            .setLayoutParams(new LinearLayout.LayoutParams(
				            		    m_cWidth, m_cHeight,1f));
						m_cellTextBtn[cellnum]
						              .setLayoutParams(new LinearLayout.LayoutParams(
						            		  m_cWidth, m_cHeight, 2f)); //(int)(((double)m_cHeight)*3.0/5.0),3f));
						m_cellTextBtn[cellnum].setGravity(Gravity.TOP);
						m_cellStatBtn[cellnum].setGravity(Gravity.BOTTOM);
//						m_cellStatBtn[cellnum]
//						              .setLayoutParams(new LinearLayout.LayoutParams(
//						            		  m_cWidth, (int)(((double)m_cHeight)*1.0/4.0)));

						// / bg�� �۾���
						if (m_cellBgImgId != null) {
							m_cellLy[cellnum]
									.setBackgroundDrawable(m_cellBgImgId);
						} else {
							m_cellLy[cellnum]
									.setBackgroundColor(m_colorParam.m_cellColor);
						}

						// / ����ϰ� �Ͽ����� �ٸ� �÷��� ǥ���Ѵ�.
						switch (j) {
//						case 0:
//							m_cellTextBtn[cellnum]
//									.setTextColor(m_colorParam.m_sundayTextColor);
//							break;
//						case 6:
//							m_cellTextBtn[cellnum]
//									.setTextColor(m_colorParam.m_saturdayTextColor);
//							break;
						default:
							m_cellTextBtn[cellnum]
									.setTextColor(m_colorParam.m_textColor);
							m_cellStatBtn[cellnum]
						            .setTextColor(m_colorParam.m_textColor);
							break;
						}

						// / �۾� ũ��
						m_cellTextBtn[cellnum].setTextSize(m_textSize*0.9f);
						m_cellStatBtn[cellnum].setTextSize(m_textSize*0.6f);
					}

				}
			}
		}
	}

	public void setLineParam() {
		for (int i = 0; i < ROWS - 1; i++) {
			m_horizontalLine[i].setBackgroundColor(m_colorParam.m_lineColor); // /
																				// ���λ�
			m_horizontalLine[i].setLayoutParams( // / ���� �����̴ϱ� ���δ� �� ���δ� �β���ŭ
					new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT,
							m_lineSize));
		}
		for (int i = 0; i < ROWS; i++) {
			for (int j = 0; j < COLS - 1; j++) {
				int pos = (i * (COLS - 1)) + j;
				m_verticalLine[pos]
						.setBackgroundColor(m_colorParam.m_lineColor); // / ���λ�
				m_verticalLine[pos].setLayoutParams( // / ���� �����̴ϱ� ���δ� ��~ ���δ�
														// �β���ŭ
						new LinearLayout.LayoutParams(m_lineSize,
								LayoutParams.FILL_PARENT));
			}
		}
	}

	// / �޷��� �����ϴ� �� �� ���� �����ϱ�
	public void setContentext() {
		// / �޷��� �ϳ� �����ؼ� �۾��Ѵ�.
		Calendar iCal = (Calendar) m_Calendar.clone();

		// / ��¥�� ��~
		m_selDay = iCal.get(Calendar.DATE);

		// / ��¥�� 1�� �����Ͽ� ���� 1���� ���� �������� ����
//		iCal.set(Calendar.DATE, 1);
		// getting start day
		SharedPreferences sPref =  mContext.getSharedPreferences(SettingsPreference.PREFERENCES_NAME, 0);
		int startingDate = Integer.parseInt(sPref.getString(SettingsPreference.PREF_CAL_FROM, "25"));
//		int startingDate = 25;
		iCal.set(Calendar.DATE, startingDate);
		iCal.add(Calendar.MONTH, -1);
		// / ����ǥ���ϴ� �� �� 7ĭ + ������ ù �����ϴ� ĭ��
		m_startPos = COLS + iCal.get(Calendar.DAY_OF_WEEK) - Calendar.SUNDAY;

		// / 1�� ���ؼ� ������ 1�Ϸ� ������ٰ� 1���� ���� ���� ���������� ������
		iCal.add(Calendar.MONTH, 1);
		iCal.add(Calendar.DATE, -1*startingDate);

		m_lastDay = iCal.get(Calendar.DAY_OF_MONTH); // / �ش� ���� �������� ��~
		Log.e(TAG,"m_lastDay = " + (m_lastDay));

		// / 0���� 6��ĭ������ ��ȭ���������~ �� ä������
		for (int k = 0; k < COLS; k++) {
			m_cellTextBtn[k].setText(m_dayText[k % 7]);
		}

		// / 7������ ó�� ������ġ �������� �������� ä��
		for (int i = COLS; i < m_startPos; i++) {
			m_cellTextBtn[i].setText("");
			m_cellStatBtn[i].setText("");
			m_cellLy[i].setBackgroundColor(Color.WHITE);
		}

		// / ������ġ���ʹ� 1���� �ؼ� ���� ������������ ���ڷ� ä��
		Log.e(TAG,"m_startPos = " + (m_lastDay-startingDate));
		for (int i = 0; i < (m_lastDay-startingDate+1); i++) {
//			Log.e(TAG,"1setText at " + (i + m_startPos) + " as " + (i + startingDate));
			m_cellTextBtn[i + m_startPos].setText((i + startingDate) + "");
			int expense = monthlyData.getDatesExpense((i + startingDate));
			String exp = (expense/1000!=0)? (expense/1000)+"k" : "0";
			m_cellStatBtn[i + m_startPos].setText(exp+"");
			m_cellLy[i + m_startPos].setBackgroundColor(getColor(expense));
		}
		
		m_cellTextBtn[(m_lastDay-startingDate)+1 + m_startPos].setText((dates[2].getMonth()+1) + "/1");
		int expense = monthlyData.getDatesExpense(1);
		String exp = (expense/1000!=0)? (expense/1000)+"k" : "0";
		m_cellStatBtn[(m_lastDay-startingDate)+1 + m_startPos].setText(exp+"");
		m_cellLy[(m_lastDay-startingDate)+1 + m_startPos].setBackgroundColor(getColor(expense));
		
		for (int i = 1; i < startingDate-1; i++) {
//			Log.e(TAG,"2setText at " + (i + (m_lastDay-startingDate) + m_startPos) + " as " + (i + 1));
			m_cellTextBtn[i + (m_lastDay-startingDate+1) + m_startPos].setText((i + 1) + "");
			expense = monthlyData.getDatesExpense(i+1);
			exp = (expense/1000!=0)? (expense/1000)+"k" : "0";
			m_cellStatBtn[i + (m_lastDay-startingDate+1) + m_startPos].setText(exp + "");
			m_cellLy[i + (m_lastDay-startingDate+1) + m_startPos].setBackgroundColor(getColor(expense));
		}

		// / ������������ �������� �������� ä��
		for (int i = m_startPos + m_lastDay; i < COLS * ROWS; i++) {
			m_cellTextBtn[i].setText("");
			m_cellStatBtn[i].setText("");
			m_cellLy[i].setBackgroundColor(Color.WHITE);
		}
	}

	private int getColor(int expense) {
		
		int max = 700000/30;
		if(expense > max*1.5) return Color.RED; 
		if(expense < max*1.5 && expense > max ) return Color.YELLOW; 
//		if(expense < max && expense > 0 ) return Color.GREEN;
		return Color.WHITE;
	}

	// / �� ��ư�鿡 setOnClickListener �ֱ�
	public void setOnEvent() {
		// / ��ȭ��������� ���ִ� �κп��� ������ ������ �ʿ� ����
		for (int i = COLS; i < COLS * ROWS; i++) {
			final int k = i;
			Button.OnClickListener onClickListener = new Button.OnClickListener() {
				@Override
				public void onClick(View v) {

					// XXX ��ư�� �����ִ� ��¥�� ���� ��
					if (m_cellTextBtn[k].getText().toString().length() > 0) {
						String text = m_cellTextBtn[k].getText().toString();
						if(text.contains("/")) text = text.split("/")[1];
						m_Calendar.set(Calendar.DATE,
								Integer.parseInt(text));
						if (m_dayTv != null)
							m_dayTv.setText(m_Calendar
									.get(Calendar.DAY_OF_MONTH) + "");
						printView();
//						v.setBackgroundColor(Color.RED);
						myClickEvent(m_Calendar.get(Calendar.YEAR),
								m_Calendar.get(Calendar.MONTH),
								m_Calendar.get(Calendar.DAY_OF_MONTH));
					}
				}
			};
			m_cellTextBtn[i].setOnClickListener(onClickListener);
			m_cellStatBtn[i].setOnClickListener(onClickListener);
		}
	}

	// / �޷��� ��� ���� �� �� ���� �������
	public void printView() {
		// / �ؽ�Ʈ ����� ������ �� �ؽ�Ʈ �信�ٰ� �� �� ���� �������
		if (m_yearTv != null)
			m_yearTv.setText(m_Calendar.get(Calendar.YEAR) + "");
		if (m_mothTv != null) {
			// int imonth = iCal.get( Calendar.MONTH ) ;
			m_mothTv.setText((m_Calendar.get(Calendar.MONTH)) + "");
		}
		if (m_dayTv != null) {
			SharedPreferences sPref =  mContext.getSharedPreferences(SettingsPreference.PREFERENCES_NAME, 0);
			int startingDate = Integer.parseInt(sPref.getString(SettingsPreference.PREF_CAL_FROM, "25"));
			m_dayTv.setText(""+startingDate);
			//m_dayTv.setText(m_Calendar.get(Calendar.DAY_OF_MONTH) + "");
		}

	}

	// / �⵵�� ���� ��~ ��~��
	public void preYear() {
		m_Calendar.add(Calendar.YEAR, -1);
		updateMonthlyData();
		setContentext();
		printView();
	}

	public void nextYear() {
		m_Calendar.add(Calendar.YEAR, 1);
		updateMonthlyData();
		setContentext();
		printView();
	}

	public void preMonth() {
		m_Calendar.add(Calendar.MONTH, -1);
		updateMonthlyData();
		setContentext();
		printView();
	}

	public void nextMonth() {
		m_Calendar.add(Calendar.MONTH, 1);
		updateMonthlyData();
		setContentext();
		printView();
	}

	// / �ؽ�Ʈ�並 �־��ָ� ���� �ѷ��� (��� ��������� �ȻѸ�)
	public void setViewTarget(TextView[] tv) {
		m_yearTv = tv[0];
		m_mothTv = tv[1];
		m_dayTv = tv[2];
	}

	// / ��ư�� �־��ָ� �˾Ƽ� �ɼ� �־��� (���ó� ��� ������ �̺�Ʈ �ȳ���)
	public void setControl(Button[] btn) {
		m_preYearBtn = btn[0];
		m_nextYearBtn = btn[1];
		m_preMonthBtn = btn[2];
		m_nextMonthBtn = btn[3];

		if (m_preYearBtn != null)
			m_preYearBtn.setOnClickListener(new Button.OnClickListener() {
				@Override
				public void onClick(View v) {
					preYear();
				}
			});
		if (m_nextYearBtn != null)
			m_nextYearBtn.setOnClickListener(new Button.OnClickListener() {
				@Override
				public void onClick(View v) {
					nextYear();
				}
			});
		if (m_preMonthBtn != null)
			m_preMonthBtn.setOnClickListener(new Button.OnClickListener() {
				@Override
				public void onClick(View v) {
					preMonth();
				}
			});
		if (m_nextMonthBtn != null)
			m_nextMonthBtn.setOnClickListener(new Button.OnClickListener() {
				@Override
				public void onClick(View v) {
					nextMonth();
				}
			});
	}

	// / ���ϴ� ������ ��¥�� ������
	// / ��)
	// / String today = getData( "yyyy-MM-dd" )�̷���?
	public String getData(String format) {
		SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.US);
		return sdf.format(new Date(m_Calendar.getTimeInMillis()));
	}

	// / �޷¿��� ��¥�� Ŭ���ϸ� �� �Լ��� �θ���.
	public void myClickEvent(int yyyy, int MM, int dd) {
		Log.d("yyyy", "" + yyyy);
		int MMM = MM;
		Log.d("MM", "" + MM);
		Log.d("dd", "" + dd);
		// TODO: to invoke the activity that enables user edit data of selected day
		SharedPreferences sPref =  mContext.getSharedPreferences(SettingsPreference.PREFERENCES_NAME, 0);
		int startingDate = Integer.parseInt(sPref.getString(SettingsPreference.PREF_CAL_FROM, "25"));
		if(dd>=startingDate) MMM--;
		Toast.makeText(mContext, "" + yyyy +"�� "+ (MMM+1) +"�� "+ dd + "�� ��볻��", Toast.LENGTH_SHORT).show();
		// start handleParsedData class
		Intent handleParsedData = new Intent(mContext,HandleParsedData.class);
		handleParsedData.putExtra("Year", yyyy);
		handleParsedData.putExtra("Month", MMM);
		handleParsedData.putExtra("Date", dd);
		mContext.startActivity(handleParsedData);
	}

	public int pixelToDip(int arg) {
		m_displayScale = mContext.getResources().getDisplayMetrics().density;
		return (int) (arg * m_displayScale);
	}

	public gsCalendarColorParam getBasicColorParam() {
		return new gsCalendarColorParam();
	}
	
	public void updateMonthlyData() {
		Util util = new Util();
		Log.e(TAG,"getFromTo: " + m_Calendar.get(Calendar.YEAR) +", " + m_Calendar.get(Calendar.MONTH));
		SharedPreferences sPref =  mContext.getSharedPreferences(SettingsPreference.PREFERENCES_NAME, 0);
		int startingDate = Integer.parseInt(sPref.getString(SettingsPreference.PREF_CAL_FROM, "25"));
		dates = util.getFromTo(m_Calendar.get(Calendar.YEAR), m_Calendar.get(Calendar.MONTH)-1, startingDate);
		Log.e(TAG,"Update MonthlyData: from " + dates[0] + " throughout " + dates[1] + " to " + dates[2]);
		monthlyData = new MonthlyData(mContext, dates[0], dates[1], dates[2]);
	}
}