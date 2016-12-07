package com.amphenol.utils;

import android.text.method.ReplacementTransformationMethod;

/**
 * Created by Carl on 2016-12-06 006.
 */
public class Char2BigUtil extends ReplacementTransformationMethod {

    @Override
    protected char[] getOriginal() {
        char[] aa = {'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z'};
        return aa;
    }

    @Override
    protected char[] getReplacement() {
        char[] cc = {'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z'};
        return cc;
    }

}