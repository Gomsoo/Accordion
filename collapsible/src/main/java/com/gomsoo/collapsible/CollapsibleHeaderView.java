package com.gomsoo.collapsible;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.support.annotation.ColorRes;
import android.support.annotation.DrawableRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

/**
 *
 * Created by Gomsoo on 2018-02-14.
 */
public class CollapsibleHeaderView extends CardView implements
        CollapsibleLayout.HandlerWithMarkView {

    static final int BOLD = 1;
    static final int ITALIC = 2;

    private MarkPosition mMarkPosition;
    private OnMarkPositionChangedListener mOnMarkPositionChangedListener;

    private View mHeaderDefaultContainer;
    private FrameLayout mHeaderCustomContainer;
    private View mCustomHeaderView;

    private Title mTitle;
    private TextView mTitleView;
    private FrameLayout mTitleCustomContainer;
    private View mCustomTitleView;

    private View mColorBandView;
    private boolean mIsShowColorBand;

    private Drawable mMark;

    public interface OnMarkPositionChangedListener {
        void onMarkPositionChanged(MarkPosition position);
    }

    public enum MarkPosition {
        START, END
    }

    static class Title {
        CharSequence text;
        boolean bold;
        boolean italic;
        int unit = TypedValue.COMPLEX_UNIT_SP;
        float size = 15;
        int color = Color.BLACK;
    }

    public CollapsibleHeaderView(@NonNull Context context) {
        this(context, null);
    }

    public CollapsibleHeaderView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CollapsibleHeaderView(@NonNull Context context,
                                 @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mTitle = new Title();

        initializeChildren(context);
        initializeProperties(context, attrs, defStyleAttr);
        initializeChildProperties();
    }

    private void initializeChildren(@NonNull Context context) {
        removeAllViews();
        inflate(context, R.layout.collapsible_header_view, this);

        mHeaderDefaultContainer = findViewById(R.id.collapsibleHeaderDefaultContainer);
        mHeaderCustomContainer = findViewById(R.id.collapsibleHeaderCustomContainer);
        mTitleView = findViewById(R.id.collapsibleHeaderTitleView);
        mTitleCustomContainer = findViewById(R.id.collapsibleHeaderTitleCustomContainer);
        mColorBandView = findViewById(R.id.collapsibleHeaderColorBand);
    }

    private void initializeProperties(@NonNull Context context,
                                      @Nullable AttributeSet attrs, int defStyleAttr) {
        TypedArray a = context.getTheme().obtainStyledAttributes(
                attrs, R.styleable.CollapsibleHeaderView, defStyleAttr, 0);
        try {
            mMarkPosition = a.getInteger(R.styleable.CollapsibleHeaderView_collapsible_markPosition, 0) == 0 ? MarkPosition.START : MarkPosition.END;
            mIsShowColorBand = a.getBoolean(R.styleable.CollapsibleHeaderView_collapsible_showColorBand, true);
            mTitle.text = a.getText(R.styleable.CollapsibleHeaderView_collapsible_title);
            int styleFlag = a.getInteger(R.styleable.CollapsibleHeaderView_collapsible_titleStyle, 0);
            mTitle.bold = (styleFlag & BOLD) != 0;
            mTitle.italic = (styleFlag & ITALIC) != 0;
            mTitle.size = a.getDimensionPixelSize(R.styleable.CollapsibleHeaderView_collapsible_titleSize, -1);
            mTitle.unit = TypedValue.COMPLEX_UNIT_PX;
            if (mTitle.size == -1) {
                mTitle.size = 15;
                mTitle.unit = TypedValue.COMPLEX_UNIT_SP;
            }
            mTitle.color = a.getColor(R.styleable.CollapsibleHeaderView_collapsible_titleColor, mTitle.color);
            mMark = a.getDrawable(R.styleable.CollapsibleHeaderView_collapsible_mark);

            int customHeaderId = a.getResourceId(R.styleable.CollapsibleHeaderView_collapsible_customHeaderLayout, -1);
            if (customHeaderId != -1)
                mCustomHeaderView = LayoutInflater.from(getContext())
                        .inflate(customHeaderId, mHeaderCustomContainer, false);

            int customTitleId = a.getResourceId(R.styleable.CollapsibleHeaderView_collapsible_customTitleLayout, -1);
            if (customTitleId != -1)
                mCustomTitleView = LayoutInflater.from(getContext())
                        .inflate(customTitleId, mTitleCustomContainer, false);
        } finally {
            a.recycle();
        }
    }

    private void initializeChildProperties() {
        if (mCustomHeaderView != null)
            attachCustomHeaderView();
        else if (mCustomTitleView != null)
            attachCustomTitleView();
        else
            applyTitle();

        applyColorBandVisibility();
        applyMarkPosition();
        applyMark();
    }

    @Override
    public View getExpandedMarkView() {
        if (mMarkPosition == MarkPosition.START)
            return findViewById(R.id.collapsibleHeaderStartMarkView);
        else if (mMarkPosition == MarkPosition.END)
            return findViewById(R.id.collapsibleHeaderEndMarkView);
        return null;
    }

    public void setTitle(CharSequence title) {
        mTitle.text = title;
        applyTitle();
    }

    public void setTitle(@StringRes int stringResId) {
        mTitle.text = getContext().getText(stringResId);
        applyTitle();
    }

    public void setTitleBold(boolean bold) {
        mTitle.bold = bold;
        applyTitle();
    }

    public void setTitleItalic(boolean italic) {
        mTitle.italic = italic;
        applyTitle();
    }

    public void setTitle(CharSequence title, boolean bold, boolean italic) {
        mTitle.text = title;
        mTitle.bold = bold;
        mTitle.italic = italic;
        applyTitle();
    }

    public void setTitle(@StringRes int titleResId, boolean bold, boolean italic) {
        mTitle.text = getContext().getText(titleResId);
        mTitle.bold = bold;
        mTitle.italic = italic;
        applyTitle();
    }

    public void setTitleSize(float size) {
        mTitle.unit = TypedValue.COMPLEX_UNIT_SP;
        mTitle.size = size;
        applyTitle();
    }

    public void setTitleSize(int unit, float size) {
        mTitle.unit = unit;
        mTitle.size = size;
        applyTitle();
    }

    public void setTitleColor(int color) {
        mTitle.color = color;
        applyTitle();
    }

    public void setTitleColorByResource(@ColorRes int colorResId) {
        mTitle.color = ContextCompat.getColor(getContext(), colorResId);
        applyTitle();
    }

    public void setShowColorBand(boolean show) {
        mIsShowColorBand = show;
        applyColorBandVisibility();
    }

    public void setHeaderView(@LayoutRes int layoutResId) {
        mCustomHeaderView = LayoutInflater.from(getContext())
                .inflate(layoutResId, mHeaderCustomContainer, false);
        attachCustomHeaderView();
    }

    public void setHeaderView(View view) {
        mCustomHeaderView = view;
        attachCustomHeaderView();
    }

    public void setTitleView(@LayoutRes int layoutResId) {
        mCustomTitleView = LayoutInflater.from(getContext())
                .inflate(layoutResId, mTitleCustomContainer, false);
        attachCustomTitleView();
    }

    public void setTitleView(View view) {
        mCustomTitleView = view;
        attachCustomTitleView();
    }

    public void setMarkPosition(MarkPosition position) {
        mMarkPosition = position;
        applyMarkPosition();
        if (mOnMarkPositionChangedListener != null)
            mOnMarkPositionChangedListener.onMarkPositionChanged(mMarkPosition);
    }

    public void setMarkPositionToEnd(boolean end) {
        setMarkPosition(end ? MarkPosition.END : MarkPosition.START);
    }

    public void setMark(Drawable mark) {
        mMark = mark;
        applyMark();
    }

    public void setMark(@DrawableRes int markResId) {
        mMark = getResources().getDrawable(markResId, null);
        applyMark();
    }

    public void setOnMarkPositionChangedListener(OnMarkPositionChangedListener listener) {
        mOnMarkPositionChangedListener = listener;
    }

    private void attachCustomHeaderView() {
        if (mHeaderCustomContainer == null || mCustomHeaderView == null) return;

        mHeaderCustomContainer.addView(mCustomHeaderView);
        mHeaderDefaultContainer.setVisibility(View.INVISIBLE);
        mHeaderCustomContainer.setVisibility(View.VISIBLE);
    }

    private void attachCustomTitleView() {
        if (mTitleCustomContainer == null || mCustomTitleView == null) return;

        mTitleCustomContainer.addView(mCustomTitleView);
        mHeaderDefaultContainer.setVisibility(View.VISIBLE);
        mHeaderCustomContainer.setVisibility(View.INVISIBLE);
        mTitleView.setVisibility(View.INVISIBLE);
        mTitleCustomContainer.setVisibility(View.VISIBLE);
    }

    private void applyTitle() {
        mHeaderCustomContainer.setVisibility(View.INVISIBLE);
        mTitleCustomContainer.setVisibility(View.INVISIBLE);
        findViewById(R.id.collapsibleHeaderDefaultContainer).setVisibility(View.VISIBLE);
        mTitleView.setVisibility(View.VISIBLE);

        mTitleView.setText(mTitle.text);

        mTitleView.setTypeface(mTitleView.getTypeface(), mTitle.bold ?
                (mTitle.italic ? Typeface.BOLD_ITALIC : Typeface.BOLD) :
                (mTitle.italic ? Typeface.ITALIC : Typeface.NORMAL));

        mTitleView.setTextSize(mTitle.unit, mTitle.size);
        mTitleView.setTextColor(mTitle.color);
    }

    private void applyColorBandVisibility() {
        if (mColorBandView == null) return;
        mColorBandView.setVisibility(mIsShowColorBand ? View.VISIBLE : View.GONE);
    }

    private void applyMarkPosition() {
        findViewById(R.id.collapsibleHeaderStartMarkView)
                .setVisibility(mMarkPosition == MarkPosition.END ? GONE : VISIBLE);
        findViewById(R.id.collapsibleHeaderEndMarkView)
                .setVisibility(mMarkPosition == MarkPosition.END ? VISIBLE : GONE);
    }

    private void applyMark() {
        if (mMark == null) return;
        ((ImageView) findViewById(R.id.collapsibleHeaderEndMarkView)).setImageDrawable(mMark);
        ((ImageView) findViewById(R.id.collapsibleHeaderStartMarkView)).setImageDrawable(mMark);
    }

    void setTitleObject(Title title) {
        mTitle = title;
        applyTitle();
    }
}
