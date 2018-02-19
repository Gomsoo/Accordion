package com.gomsoo.collapsible.sample;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.gomsoo.collapsible.CollapsibleLayout;
import com.gomsoo.collapsible.R;

/**
 *
 * Created by Gomsoo on 2018-02-19.
 */
public class CollapsibleLayoutFragment extends Fragment implements TextWatcher {

    CollapsibleLayout mCollapsibleLayout;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_collapsible_layout, container, false);

        Button button = rootView.findViewById(R.id.button);
        CollapsibleLayout collapsibleLayoutOnly = rootView.findViewById(R.id.collapsibleLayoutOnly);
        collapsibleLayoutOnly.setHandler(button);
        collapsibleLayoutOnly.setMarkViewForAnimation(rootView.findViewById(R.id.image));

        mCollapsibleLayout = rootView.findViewById(R.id.collapsibleLayout);
        mCollapsibleLayout.setHandler(rootView.findViewById(R.id.collapsibleHeaderView));

        EditText durationEditText = rootView.findViewById(R.id.durationEditText);
        durationEditText.addTextChangedListener(this);

        return rootView;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        long duration = 300;
        try {
            duration = Long.parseLong(s.toString());
        } catch (NumberFormatException e) {
            duration = 300;
        } finally {
            mCollapsibleLayout.setAnimationDuration(duration);
        }
    }

    @Override
    public void afterTextChanged(Editable s) {
    }
}