/*
 * Copyright 2018 Daniel Underhay & Matthew Daley.
 *
 * This file is part of Walrus.
 *
 * Walrus is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Walrus is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Walrus.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.bugfuzz.android.projectwalrus.device.ui;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.bugfuzz.android.projectwalrus.R;
import com.bugfuzz.android.projectwalrus.card.carddata.CardData;
import com.bugfuzz.android.projectwalrus.device.CardDevice;

public class SingleCardDataIODialogFragment extends DialogFragment {

    private OnCancelCallback onCancelCallback;

    public static SingleCardDataIODialogFragment show(Activity activity, String fragmentTag,
                                                      Class<? extends CardDevice> cardDeviceClass,
                                                      Class<? extends CardData> cardDataClass,
                                                      Mode mode, int callbackId) {
        SingleCardDataIODialogFragment dialog = new SingleCardDataIODialogFragment();

        Bundle args = new Bundle();
        args.putString("card_device_class", cardDeviceClass.getName());
        args.putString("card_data_class", cardDataClass.getName());
        args.putInt("mode", mode.ordinal());
        args.putInt("callback_id", callbackId);
        dialog.setArguments(args);

        dialog.show(activity.getFragmentManager(), fragmentTag);
        return dialog;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Mode mode = Mode.values()[getArguments().getInt("mode")];

        CardDataIOView cardDataIOView = new CardDataIOView(getActivity());
        cardDataIOView.setPadding(0, 60, 0, 10);
        try {
            // noinspection unchecked
            cardDataIOView.setCardDeviceClass(
                    (Class<? extends CardDevice>) Class.forName(
                            getArguments().getString("card_device_class")));
        } catch (ClassNotFoundException ignored) {
        }
        try {
            // noinspection unchecked
            cardDataIOView.setCardDataClass(
                    (Class<? extends CardData>) Class.forName(
                            getArguments().getString("card_data_class")));
        } catch (ClassNotFoundException ignored) {
        }
        cardDataIOView.setDirection(mode == Mode.READ);

        @StringRes int title = 0;
        switch (mode) {
            case READ:
                title = R.string.waiting_for_card;
                break;

            case WRITE:
                title = R.string.writing_card;
                break;

            case EMULATE:
                title = R.string.emulating_card;
                break;
        }

        return new MaterialDialog.Builder(getActivity())
                .title(title)
                .customView(cardDataIOView, false)
                .negativeText(R.string.cancel_button)
                .onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog,
                                        @NonNull DialogAction which) {
                        dialog.cancel();
                    }
                })
                .build();
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        super.onCancel(dialog);

        if (onCancelCallback != null)
            onCancelCallback.onCancelClick(getArguments().getInt("callback_id"));
    }

    public void setOnCancelCallback(OnCancelCallback onCancelCallback) {
        this.onCancelCallback = onCancelCallback;
    }

    public enum Mode {
        READ,
        WRITE,
        EMULATE
    }

    public interface OnCancelCallback {
        void onCancelClick(int callbackId);
    }
}
