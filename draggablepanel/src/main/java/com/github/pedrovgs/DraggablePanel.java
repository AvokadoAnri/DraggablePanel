/*
 * Copyright (C) 2014 Pedro Vicente Gómez Sánchez.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.pedrovgs;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.AttributeSet;
import android.widget.FrameLayout;

/**
 * Custom view created to handle DraggableView using fragments. With this custom view the client code can configure the
 * top and bottom fragment and other elements like: top fragment height, top fragment margin right, top fragment x
 * scale factor, top fragment y scale factor, top fragment margin bottom and enable or disable horizontal alpha effect.
 *
 * @author Pedro Vicente Gómez Sánchez.
 */
public class DraggablePanel extends FrameLayout {

    private static final float DEFAULT_TOP_FRAGMENT_HEIGHT = 200;
    private static final float DEFAULT_SCALE_FACTOR = 2;
    private static final float DEFAULT_TOP_FRAGMENT_MARGIN = 0;
    private static final boolean DEFAULT_ENABLE_HORIZONTAL_ALPHA_EFFECT = true;
    private static final boolean DEFAULT_TOP_FRAGMENT_RESIZE = false;

    private DraggableView draggableView;
    private DraggableListener draggableListener;

    private FragmentManager fragmentManager;
    private Fragment topFragment;
    private Fragment bottomFragment;
    private float topFragmentHeight;
    private float topFragmentMarginRight;
    private float topFragmentMarginBottom;
    private float xScaleFactor;
    private float yScaleFactor;
    private boolean enableHorizontalAlphaEffect;
    private boolean topFragmentResize;


    public DraggablePanel(Context context) {
        super(context);
    }

    public DraggablePanel(Context context, AttributeSet attrs) {
        super(context, attrs);
        initializeAttrs(attrs);
    }


    public DraggablePanel(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initializeAttrs(attrs);
    }

    /**
     * Configure the FragmentManager used to attach top and bottom fragment inside the view.
     *
     * @param fragmentManager
     */
    public void setFragmentManager(FragmentManager fragmentManager) {
        this.fragmentManager = fragmentManager;
    }

    /**
     * Configure the Fragment that will work as draggable element inside this custom view. This Fragment has to be
     * configured before initialize the view.
     *
     * @param topFragment used as draggable element.
     */
    public void setTopFragment(Fragment topFragment) {
        this.topFragment = topFragment;
    }

    /**
     * Configure the Fragment that will work as secondary element inside this custom view. This Fragment has to be
     * configured before initialize the view.
     *
     * @param bottomFragment used as secondary element.
     */
    public void setBottomFragment(Fragment bottomFragment) {
        this.bottomFragment = bottomFragment;
    }

    /**
     * Configure the height associated to the top Fragment used inside the view as draggable element.
     *
     * @param topFragmentHeight in pixels.
     */
    public void setTopViewHeight(float topFragmentHeight) {
        this.topFragmentHeight = topFragmentHeight;
    }

    /**
     * Configure the horizontal scale factor applied when the top fragment is dragged to the bottom of the custom view.
     *
     * @param xScaleFactor
     */
    public void setXScaleFactor(float xScaleFactor) {
        this.xScaleFactor = xScaleFactor;
    }

    /**
     * Configure the vertical scale factor applied when the top fragment is dragged to the bottom of the custom view.
     *
     * @param yScaleFactor
     */
    public void setYScaleFactor(float yScaleFactor) {
        this.yScaleFactor = yScaleFactor;
    }

    /**
     * Configure the top Fragment margin right applied when the view has been minimized.
     *
     * @param topFragmentMarginRight in pixels.
     */
    public void setTopFragmentMarginRight(float topFragmentMarginRight) {
        this.topFragmentMarginRight = topFragmentMarginRight;
    }

    /**
     * Configure the top Fragment margin bottom applied when the view has been minimized.
     *
     * @param topFragmentMarginBottom in pixels.
     */
    public void setTopFragmentMarginBottom(float topFragmentMarginBottom) {
        this.topFragmentMarginBottom = topFragmentMarginBottom;
    }

    /**
     * Configure the DraggableListener that is going to be invoked when the view be minimized, maximized, closed to the
     * left or right.
     *
     * @param draggableListener
     */
    public void setDraggableListener(DraggableListener draggableListener) {
        this.draggableListener = draggableListener;
    }

    /**
     * Configure the disabling of the alpha effect applied when the view is being dragged horizontally.
     *
     * @param enableHorizontalAlphaEffect to enable or disable the effect.
     */
    public void setEnableHorizontalAlphaEffect(boolean enableHorizontalAlphaEffect) {
        this.enableHorizontalAlphaEffect = enableHorizontalAlphaEffect;
    }

    /**
     * Configure top fragment to be resized instead of scaled when the view is dragged vertically.
     *
     * @param topFragmentResize to enable or disable the resize effect.
     */
    public void setTopFragmentResize(boolean topFragmentResize) {
        this.topFragmentResize = topFragmentResize;
    }

    /**
     * Close the custom view applying an animation to close the view to the left side of the screen.
     */
    public void closeToLeft() {
        draggableView.closeToLeft();
    }

    /**
     * Close the custom view applying an animation to close the view to the right side of the screen.
     */
    public void closeToRight() {
        draggableView.closeToRight();
    }

    /**
     * Maximize the custom view applying an animation to return the view to the initial position.
     */
    public void maximize() {
        draggableView.maximize();
    }

    /**
     * Minimize the custom view applying an animation to put the top fragment on the bottom right corner of the screen.
     */
    public void minimize() {
        draggableView.minimize();
    }

    /**
     * Apply all the custom view configuration and inflate the main widgets. The view won't be visible if this method
     * is not called.
     * <p/>
     * FragmentManager, top Fragment and bottom Fragment have to be configured before initialize this view. If not,
     * this method will throw and IllegalStateException.
     */
    public void initializeView() {
        checkFragmentConsistency();
        checkSupportFragmentManagerConsistency();

        inflate(getContext(), R.layout.draggable_panel, this);
        draggableView = (DraggableView) findViewById(R.id.draggable_view);
        draggableView.setTopViewHeight(topFragmentHeight);
        draggableView.setFragmentManager(fragmentManager);
        draggableView.attachTopFragment(topFragment);
        draggableView.setXTopViewScaleFactor(xScaleFactor);
        draggableView.setYTopViewScaleFactor(yScaleFactor);
        draggableView.setTopViewMarginRight(topFragmentMarginRight);
        draggableView.setTopViewMarginBottom(topFragmentMarginBottom);
        draggableView.attachBottomFragment(bottomFragment);
        draggableView.setDraggableListener(draggableListener);
        draggableView.setHorizontalAlphaEffectEnabled(enableHorizontalAlphaEffect);
        draggableView.setTopViewResize(topFragmentResize);
    }

    /**
     * Checks if the top Fragment is maximized.
     *
     * @return true if the view is maximized.
     */
    public boolean isMaximized() {
        return draggableView.isMaximized();
    }

    /**
     * Checks if the top Fragment is minimized.
     *
     * @return true if the view is minimized.
     */
    public boolean isMinimized() {
        return draggableView.isMinimized();
    }

    /**
     * Checks if the top Fragment closed at the right place.
     *
     * @return true if the view is closed at right.
     */
    public boolean isClosedAtRight() {
        return draggableView.isClosedAtRight();
    }

    /**
     * Checks if the top Fragment is closed at the left place.
     *
     * @return true if the view is closed at left.
     */
    public boolean isClosedAtLeft() {
        return draggableView.isClosedAtLeft();
    }

    /**
     * Initialize the xml configuration based on styleable attributes
     *
     * @param attrs to analyze.
     */
    private final void initializeAttrs(AttributeSet attrs) {
        TypedArray attributes = getContext().obtainStyledAttributes(attrs, R.styleable.draggable_panel);
        this.topFragmentHeight = attributes.getDimension(R.styleable.draggable_panel_top_fragment_height, DEFAULT_TOP_FRAGMENT_HEIGHT);
        this.xScaleFactor = attributes.getFloat(R.styleable.draggable_panel_x_scale_factor, DEFAULT_SCALE_FACTOR);
        this.yScaleFactor = attributes.getFloat(R.styleable.draggable_panel_y_scale_factor, DEFAULT_SCALE_FACTOR);
        this.topFragmentMarginRight = attributes.getDimension(R.styleable.draggable_panel_top_fragment_margin_right, DEFAULT_TOP_FRAGMENT_MARGIN);
        this.topFragmentMarginBottom = attributes.getDimension(R.styleable.draggable_panel_top_fragment_margin_bottom, DEFAULT_TOP_FRAGMENT_MARGIN);
        this.enableHorizontalAlphaEffect = attributes.getBoolean(R.styleable.draggable_panel_enable_horizontal_alpha_effect, DEFAULT_ENABLE_HORIZONTAL_ALPHA_EFFECT);
        this.topFragmentResize = attributes.getBoolean(R.styleable.draggable_panel_top_fragment_resize,DEFAULT_TOP_FRAGMENT_RESIZE);
        attributes.recycle();
    }

    /**
     * Validate FragmentManager configuration. If is not initialized, this method will throw an
     * IllegalStateException.
     */
    private void checkSupportFragmentManagerConsistency() {
        if (fragmentManager == null) {
            throw new IllegalStateException("You have to set the support FragmentManager before initialize DraggablePanel");
        }
    }

    /**
     * Validate top and bottom Fragment configuration. If are not initialized, this method will throw an
     * IllegalStateException.
     */
    private void checkFragmentConsistency() {
        if (topFragment == null || bottomFragment == null) {
            throw new IllegalStateException("You have to set top and bottom fragment before initialize DraggablePanel");
        }
    }

}
