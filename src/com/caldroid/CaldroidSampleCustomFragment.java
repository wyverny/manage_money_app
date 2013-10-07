package com.caldroid;

import com.roomorama.caldroid.CaldroidFragment;
import com.roomorama.caldroid.CaldroidGridAdapter;

public class CaldroidSampleCustomFragment extends CaldroidFragment {

	@Override
	public CaldroidGridAdapter getNewDatesGridAdapter(int month, int year, int startingDay) {
		// TODO Auto-generated method stub
		return new CaldroidSampleCustomAdapter(getActivity(), month, year, startingDay,
				getCaldroidData(), extraData);
	}

}
