/*
* Copyright (C) 2014 The Android Open Source Project
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*      http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/

package org.escoladeltreball.pt21_martin_pol;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.io.IOException;
import java.util.List;


public class CustomAdapterE extends RecyclerView.Adapter<CustomAdapterE.EViewHolder> {
    private static final String TAG = "test";

    private List<String> mDataSet;
    // TODO: 30/01/20 canviar el DataSet a una classe amb dos Strings,  displayname i id, per les gravacions...

    private OnItemClickListener mListener;

    public interface OnItemClickListener {
        void onItemClick(int position);
        void onLongItemClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }

    public CustomAdapterE(List<String> dataSet) {
        mDataSet = dataSet;
    }


    public static class EViewHolder extends RecyclerView.ViewHolder {
        private final TextView data;
        //private final ImageView altres;

        public EViewHolder(View v, final OnItemClickListener listener) {
            super(v);
            data = (TextView) v.findViewById(R.id.firstLine);

            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            //passem el event a la interfície...
                            listener.onItemClick(position);
                          //  listener.onLongItemClick(position);
                        }
                    }
                }

            });
            v.setOnLongClickListener(new View.OnLongClickListener(){
                @Override
                public boolean onLongClick(View v) {
                    if (listener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            //passem el event a la interfície...
                            // TODO: el expandable aquí,
                            listener.onLongItemClick(position);
                        }
                    }
                    return true;
                }
            });

            Log.d(TAG, "ViewHolder: " + getAdapterPosition());
        }

      }


    @Override
    public EViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        // Create a new view.


        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.so_layout, viewGroup, false);

        return new EViewHolder(v,mListener);
    }

    public void remove(int position) {
        mDataSet.remove(position);
        notifyItemRemoved(position);
    }


    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(EViewHolder holder, final int position) {
    //  Log.d(TAG, "Element " + position + " set.");

        final String data=mDataSet.get(position);

        holder.data.setText(String.valueOf(data) );

        }


    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {

        return mDataSet.size();
    }
}
