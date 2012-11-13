package com.lcm.view;

import com.lcm.smsSmini.R;

import android.app.Activity;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.widget.TextView;

public class HelpView extends Activity {
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.help_view);

		TextView emailTextView = (TextView)findViewById(R.id.help_email);
		emailTextView.setText(Html.fromHtml("<a href=\"mailto:cm.lim09@gatech.edu\">cm.lim09@gatech.edu</a>"));
		emailTextView.setMovementMethod(LinkMovementMethod.getInstance());
	}
}
