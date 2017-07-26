/*
 * Copyright 2017 Abyx (https://abyx.be)
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

package com.abyx.loyalty.extra.recycler;

import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * See https://github.com/vpaliyX/MultiChoiceMode-RecyclerView/blob/master/multiplechoice/src/main/java/com/vpaliy/multiplechoice/BaseAdapter.java
 *
 * @author Vasyl Paliy
 */
public abstract class BaseAdapter<T extends BaseAdapter.BaseViewHolder> extends RecyclerView.Adapter<T> {
    private static final String TAG = BaseAdapter.class.getSimpleName();
    private static final String KEY = "baseAdapter:stateTracker";
    private final StateTracker tracker;
    private MultiMode mode;
    private boolean isOnResume=true;
    private boolean isScreenRotation=false;
    private boolean isAnimationEnabled=false;


    public BaseAdapter(@NonNull MultiMode mode, boolean isAnimationEnabled) {
        this.mode=mode;
        mode.setAdapter(this);
        this.isAnimationEnabled=isAnimationEnabled;
        this.tracker=new StateTracker();

    }

    //This constructor has to be called only to restore the previous state
    public BaseAdapter(@NonNull MultiMode mode, boolean isAnimationEnabled, @NonNull Bundle savedInstanceState) {
        this.mode=mode;
        mode.setAdapter(this);
        this.isAnimationEnabled=isAnimationEnabled;
        tracker=savedInstanceState.getParcelable(KEY);
        if(tracker==null) {
            throw new IllegalArgumentException("You didn't save the state of adapter");
        }

        isScreenRotation=true;
        isOnResume=false;

        if(tracker.getCheckedItemCount()>0) {
            mode.turnOn();
        }else {
            isScreenRotation = false;
        }

        notifyDataSetChanged();
    }

    public void onResume() {
        if(isOnResume) {
            if (tracker.getCheckedItemCount() > 0) {
                mode.turnOn();
                mode.update(tracker.getCheckedItemCount());
            }
        }
        isOnResume=true;
    }

    public boolean isMultiModeActivated() {
        return mode.isActivated();
    }

    public boolean isChecked(int position) {
        int state=tracker.getStateFor(position);
        return state==StateTracker.ENTER || state==StateTracker.ANIMATED;
    }

    public void checkAll(boolean animate) {
        if(!mode.isActivated()) {
            mode.turnOn();
        }
        for(int index=0;index<getItemCount();index++) {
            tracker.setStateFor(index,animate?StateTracker.ENTER:StateTracker.ANIMATED);
            notifyItemChanged(index);
        }
        mode.update(tracker.getCheckedItemCount());
    }

    public void unCheckAll(boolean animate) {
        for(int index=0;index<getItemCount();index++) {
            if(isChecked(index)) {
                tracker.setStateFor(index, animate ? StateTracker.EXIT : StateTracker.DEFAULT);
                notifyItemChanged(index);
            }
        }

        if(mode.isActivated()) {
            mode.turnOff();
        }
    }

    private void update(int[] updateIndices) {
        for(int index:updateIndices) {
            notifyItemChanged(index);
        }
    }

    public int[] getAllCheckedForDeletion() {
        int[] result=tracker.getSelectedItemArray(true);
        update(result);
        if(mode.isActivated()) {
            mode.turnOff();
        }
        //shift the items
        int itemShift=0;
        int jIndex=result.length;
        int[] resultArray=new int[jIndex];

        for(int index=0;index<jIndex;index++,itemShift++)
            resultArray[index]=result[index]-itemShift;

        return resultArray;
    }

    public int[] getAllChecked(boolean cancel) {
        int[] result=tracker.getSelectedItemArray(cancel);
        if(cancel) {
            update(result);
            if (mode.isActivated()) {
                mode.turnOff();
            }
        }
        return result;
    }

    public abstract void removeAt(int index);

    public void saveState(@NonNull Bundle outState) {
        if(mode.isActivated()) {
            mode.turnOff();
        }
        tracker.saveState(KEY,outState);
    }

    public abstract class BaseViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener, View.OnLongClickListener{

        public BaseViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);

        }

        public final void determineState() {
            if(isScreenRotation) {
                isScreenRotation=false;
                if(mode.isActivated()) {
                    mode.update(tracker.getCheckedItemCount());
                }
            }

            if(isAnimationEnabled) {
                switch (tracker.getStateFor(getAdapterPosition())) {
                    case StateTracker.ENTER:
                        enterState();
                        break;
                    case StateTracker.ANIMATED:
                        animatedState();
                        break;
                    case StateTracker.EXIT:
                        exitState();
                        break;
                    default:
                        defaultState();
                }
            }
            updateBackground();
        }

        public abstract void updateBackground();

        @CallSuper
        public void enterState() {
            tracker.setStateFor(getAdapterPosition(),StateTracker.ANIMATED);
        }


        public void animatedState() {

        }

        @CallSuper
        public void exitState() {
            tracker.setStateFor(getAdapterPosition(),StateTracker.DEFAULT);
        }

        public void defaultState() {

        }

        @Override
        @CallSuper
        public void onClick(View view) {
            if(mode.isActivated()) {
                tracker.check(getAdapterPosition());
                mode.update(tracker.getCheckedItemCount());
                if (tracker.getCheckedItemCount() == 0) {
                    mode.turnOff();
                }
                determineState();
            }
        }

        public abstract void onBindData();

        @Override
        @CallSuper
        public  boolean onLongClick(View view) {
            if(!mode.isActivated()) {
                mode.turnOn();
            }
            return false;
        }
    }

}