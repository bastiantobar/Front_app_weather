package com.example.frontweatherapp.ui.fragments;

import android.app.Dialog;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.frontweatherapp.R;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;

public class LoadingDialogFragment extends DialogFragment {

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        // Inflar el layout del diálogo
        View view = LayoutInflater.from(getContext()).inflate(R.layout.dialog_loading, null);

        return new MaterialAlertDialogBuilder(requireContext())
                .setView(view)
                .setCancelable(false)  // No se puede cerrar hasta que termine la carga
                .create();
    }
}
