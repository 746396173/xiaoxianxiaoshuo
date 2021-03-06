package com.qy.reader.common.widgets.reader.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import androidx.annotation.IntDef;

@IntDef({
        SlideMode.OVERLAP,
        SlideMode.FOLLOW
})
@Retention(RetentionPolicy.SOURCE)
public @interface SlideMode {
    /**
     * 滑动覆盖模式
     */
    int OVERLAP = 1;

    /**
     * 滑动跟随模式
     */
    int FOLLOW = 2;
}