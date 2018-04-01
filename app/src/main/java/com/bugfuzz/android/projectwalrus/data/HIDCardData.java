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

package com.bugfuzz.android.projectwalrus.data;

import com.bugfuzz.android.projectwalrus.R;

import org.parceler.Parcel;

import java.math.BigInteger;
import java.util.Random;

@Parcel
@CardData.Metadata(
        name = "HID",
        icon = R.drawable.drawable_hid
)
public class HIDCardData extends CardData {

    public BigInteger data;

    public HIDCardData() {
    }

    public HIDCardData(BigInteger data) {
        this.data = data;
    }

    @SuppressWarnings("unused")
    public static HIDCardData newDebugInstance() {
        return new HIDCardData(new BigInteger(44, new Random()));
    }

    @Override
    public String getTypeDetailInfo() {
        return (data.bitLength() + (data.signum() == -1 ? 1 : 0)) + "-bit";
    }

    @Override
    public String getHumanReadableText() {
        return data.toString(16);
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass())
            return false;

        HIDCardData that = (HIDCardData) o;

        return data.equals(that.data);
    }

    @Override
    public int hashCode() {
        return data.hashCode();
    }
}
