package com.example.ressenger;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import yuku.ambilwarna.AmbilWarnaDialog;

public class FontBottomSheet extends BottomSheetDialogFragment implements AdapterView.OnItemSelectedListener {

    private BottomSheetListener listener;
    public Spinner fontSpinner, sizeSpinner;
    Context mContext;
    Button colorPickerButton;
    public int fontColor;

    public  FontBottomSheet(Context context)
    {
        mContext=context;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View v = inflater.inflate(R.layout.layout_font, container, false);
        Button okButton = v.findViewById(R.id.okButtonFont);
        fontSpinner = v.findViewById(R.id.fontSpinner);
        sizeSpinner = v.findViewById(R.id.sizeSpinner);
        colorPickerButton = v.findViewById(R.id.colorPickerButton);
        fontColor = Color.parseColor(ChatFragment.color);
        colorPickerButton.setBackgroundColor(fontColor);
        ArrayAdapter<CharSequence> fontAdapter = ArrayAdapter.createFromResource(mContext, R.array.fonts, android.R.layout.simple_spinner_item);
        fontAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        fontSpinner.setAdapter(fontAdapter);
        fontSpinner.setSelection(ChatFragment.preferredFont);
        colorPickerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openColorPicker();
            }
        });
        ArrayAdapter<CharSequence> sizeAdapter = ArrayAdapter.createFromResource(mContext, R.array.sizes, android.R.layout.simple_spinner_item);
        sizeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sizeSpinner.setAdapter(sizeAdapter);
        sizeSpinner.setSelection(ChatFragment.textSize);

        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ChatFragment.preferredFont =fontSpinner.getSelectedItemPosition();
                ChatFragment.textSize = sizeSpinner.getSelectedItemPosition();
                dismiss();
            }
        });

        return v;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    public interface BottomSheetListener
    {
        void onFontSelected(String text);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(mContext);
    }

    void openColorPicker()
    {
        AmbilWarnaDialog colorPicker = new AmbilWarnaDialog(mContext, fontColor, new AmbilWarnaDialog.OnAmbilWarnaListener() {
            @Override
            public void onCancel(AmbilWarnaDialog dialog) {

            }

            @Override
            public void onOk(AmbilWarnaDialog dialog, int color) {
                fontColor = color;
                colorPickerButton.setBackgroundColor(color);
                ChatFragment.color = String.format("#%06X", 0xFFFFFF & fontColor);
            }
        });

        colorPicker.show();
    }
}
