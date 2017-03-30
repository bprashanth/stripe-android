package com.stripe.android.view;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.VisibleForTesting;
import android.util.AttributeSet;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.stripe.android.R;
import com.stripe.android.model.Card;
import com.stripe.android.model.Source;
import com.stripe.android.model.SourceCardData;

import java.util.Locale;

import static com.stripe.android.util.StripeTextUtils.isBlank;
import static com.stripe.android.view.CardInputWidget.BRAND_RESOURCE_MAP;

/**
 * Widget to display (non-editable) card sources
 */
public class CardDisplayWidget extends FrameLayout {

    private String mDisplayString;
    private Source mSource;

    private ImageView mBrandDisplayView;
    private TextView mTextView;

    public CardDisplayWidget(Context context) {
        super(context);
        initView(null);
    }

    public CardDisplayWidget(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(attrs);
    }

    public CardDisplayWidget(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(attrs);
    }

    public void setCard(Source cardSource) {
        if (!Source.CARD.equals(cardSource.getType())) {
            return;
        }

        mSource = cardSource;
        updateString();
        updateIcon();
    }

    void initView(AttributeSet attrs) {
        inflate(getContext(), R.layout.card_display_widget, this);
        mBrandDisplayView = (ImageView) findViewById(R.id.iv_brand_icon);
        mTextView = (TextView) findViewById(R.id.tv_card_display);
    }

    @VisibleForTesting
    String getDisplayString() {
        return mDisplayString;
    }

    void updateString() {
        SourceCardData data = getSourceCardData();

        if (data == null || isBlank(data.getBrand()) || isBlank(data.getLast4())) {
            mDisplayString = "";
        } else {
            mDisplayString = String.format(Locale.ENGLISH,
                    getContext().getString(R.string.card_ending_in),
                    data.getBrand(),
                    data.getLast4());
        }

        mTextView.setText(mDisplayString);
    }

    void updateIcon() {
        SourceCardData data = getSourceCardData();
        if (data == null || data.getBrand() == null || Card.UNKNOWN.equals(data.getBrand())) {
            Drawable icon  = getResources().getDrawable(R.drawable.ic_unknown);
            mBrandDisplayView.setImageDrawable(icon);
            return;
        }

        mBrandDisplayView.setImageResource(BRAND_RESOURCE_MAP.get(data.getBrand()));
    }

    SourceCardData getSourceCardData() {
        if (mSource == null
                || mSource.getSourceTypeModel() == null
                || !(mSource.getSourceTypeModel() instanceof SourceCardData)) {
            return null;
        }

        return (SourceCardData) mSource.getSourceTypeModel();
    }

    static String createExpiryDateString(int expiryMonth, int expiryYear) {
        return "blerg";
    }
}
