package com.bugfuzz.android.projectwalrus.ui;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.bugfuzz.android.projectwalrus.R;
import com.bugfuzz.android.projectwalrus.data.Card;
import com.bugfuzz.android.projectwalrus.data.CardData;

public class WalrusCardView extends FrameLayout {

    private Card card;

    private TextView nameView, humanReadableTextView;
    public EditText editableNameView;
    private ImageView logoView;

    public WalrusCardView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        init(attrs, defStyle);
    }

    public WalrusCardView(Context context, AttributeSet attrs) {
        super(context, attrs);

        init(attrs, 0);
    }

    public WalrusCardView(Context context) {
        super(context);

        init(null, 0);
    }

    private void init(AttributeSet attrs, int defStyle) {
        View view = inflate(getContext(), R.layout.view_walruscard, null);
        addView(view);

        nameView = view.findViewById(R.id.name);
        editableNameView = view.findViewById(R.id.editableName);
        logoView = view.findViewById(R.id.logo);
        humanReadableTextView = view.findViewById(R.id.humanReadableText);

        editableNameView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                card.name = editableNameView.getText().toString();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.WalrusCardView, defStyle, 0);
        setEditable(a.getBoolean(R.styleable.WalrusCardView_editable, false));
        a.recycle();
    }

    public void setCard(Card newCard) {
        card = newCard;

        nameView.setText(card.name);
        editableNameView.setText(card.name);
        if (card.cardData != null) {
            logoView.setImageDrawable(ContextCompat.getDrawable(getContext(),
                    card.cardData.getClass().getAnnotation(CardData.Metadata.class).icon()));
            humanReadableTextView.setText(card.cardData.getHumanReadableText());
        }

        invalidate();
        requestLayout();
    }

    public void setEditable(boolean editable) {
        nameView.setVisibility(editable ? GONE : VISIBLE);
        editableNameView.setVisibility(editable ? VISIBLE : GONE);
    }
}
