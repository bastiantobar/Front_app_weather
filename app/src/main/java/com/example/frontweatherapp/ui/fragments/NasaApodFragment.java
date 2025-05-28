package com.example.frontweatherapp.ui.fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.frontweatherapp.R;
import com.example.frontweatherapp.models.NasaApod; // Importar el modelo NasaApod
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso; // Importar Picasso

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class NasaApodFragment extends Fragment {

    private static final String TAG = "NasaApodFragment";
    private TextView tvApodTitle, tvApodDate, tvApodCopyright, tvApodExplanation, tvApodVideoMessage;
    private ImageView ivApodImage;
    private Button btnViewApodHd;
    private ProgressBar progressBarApod;
    private TextView tvApodError;

    private NasaApod nasaApodData; // Para almacenar los datos de APOD

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_nasa_apod, container, false);

        // Inicializar vistas
        tvApodTitle = view.findViewById(R.id.tvApodTitle);
        tvApodDate = view.findViewById(R.id.tvApodDate);
        tvApodCopyright = view.findViewById(R.id.tvApodCopyright);
        tvApodExplanation = view.findViewById(R.id.tvApodExplanation);
        tvApodVideoMessage = view.findViewById(R.id.tvApodVideoMessage);
        ivApodImage = view.findViewById(R.id.ivApodImage);
        btnViewApodHd = view.findViewById(R.id.btnViewApodHd);
        progressBarApod = view.findViewById(R.id.progressBarApod);
        tvApodError = view.findViewById(R.id.tvApodError);

        // Ocultar elementos de error y carga al inicio
        tvApodError.setVisibility(View.GONE);
        progressBarApod.setVisibility(View.GONE);

        // Obtener los datos de NasaApod del Bundle
        if (getArguments() != null && getArguments().containsKey("nasaApodData")) {
            nasaApodData = (NasaApod) getArguments().getSerializable("nasaApodData");
            if (nasaApodData != null) {
                displayApodData(nasaApodData);
            } else {
                showError("No se pudieron cargar los datos de la Imagen Astronómica del Día.");
            }
        } else {
            showError("No se recibieron datos de la Imagen Astronómica del Día.");
        }

        return view;
    }

    /**
     * Muestra los datos de la APOD en la interfaz de usuario.
     * @param apodData El objeto NasaApod a mostrar.
     */
    private void displayApodData(NasaApod apodData) {
        tvApodTitle.setText(apodData.getTitle());
        tvApodExplanation.setText(apodData.getExplanation());

        // Formatear la fecha
        try {
            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            SimpleDateFormat outputFormat = new SimpleDateFormat("dd 'de' MMMM 'de' yyyy", new Locale("es", "ES")); // Formato en español
            Date date = inputFormat.parse(apodData.getDate());
            tvApodDate.setText(outputFormat.format(date));
        } catch (ParseException e) {
            Log.e(TAG, "Error al parsear la fecha de APOD: " + e.getMessage());
            tvApodDate.setText(apodData.getDate()); // Mostrar la fecha sin formatear en caso de error
        }

        // Mostrar copyright si existe
        if (apodData.getCopyright() != null && !apodData.getCopyright().isEmpty()) {
            tvApodCopyright.setText("© " + apodData.getCopyright().trim());
            tvApodCopyright.setVisibility(View.VISIBLE);
        } else {
            tvApodCopyright.setVisibility(View.GONE);
        }

        // Modificación: Priorizar la URL y manejar mediaType para video o imagen (o desconocido)
        if (apodData.getUrl() != null && !apodData.getUrl().isEmpty()) {
            if ("video".equalsIgnoreCase(apodData.getMediaType())) {
                // Si es un video, ocultar imagen y mostrar mensaje de video
                ivApodImage.setVisibility(View.GONE);
                tvApodVideoMessage.setVisibility(View.VISIBLE);
                progressBarApod.setVisibility(View.GONE); // No hay imagen para cargar
                Log.d(TAG, "APOD es un video. Mostrando mensaje de video.");

                // Configurar el botón para abrir la URL del video
                btnViewApodHd.setText("Ver Video");
                btnViewApodHd.setOnClickListener(v -> openUrlInBrowser(apodData.getUrl()));
                btnViewApodHd.setVisibility(View.VISIBLE);
            } else {
                // Si tiene URL y no es un video explícito (o mediaType es null), intentar cargar como imagen
                ivApodImage.setVisibility(View.VISIBLE);
                tvApodVideoMessage.setVisibility(View.GONE);
                progressBarApod.setVisibility(View.VISIBLE); // Mostrar barra de carga de imagen
                Log.d(TAG, "APOD tiene URL y no es video explícito. Intentando cargar como imagen.");

                // Cargar imagen con Picasso
                Picasso.get()
                        .load(apodData.getUrl())
                        .placeholder(R.drawable.ic_image_placeholder) // Un drawable de placeholder si tienes uno
                        .error(R.drawable.ic_image_error) // Un drawable de error si tienes uno
                        .into(ivApodImage, new Callback() {
                            @Override
                            public void onSuccess() {
                                progressBarApod.setVisibility(View.GONE);
                                Log.d(TAG, "Imagen APOD cargada exitosamente.");
                            }

                            @Override
                            public void onError(Exception e) {
                                progressBarApod.setVisibility(View.GONE);
                                Log.e(TAG, "Error al cargar la imagen APOD desde URL: " + apodData.getUrl() + ", " + e.getMessage());
                                showError("Error al cargar la imagen. Inténtalo de nuevo.");
                                ivApodImage.setImageResource(R.drawable.ic_image_error); // Mostrar icono de error
                            }
                        });

                // Configurar el botón para abrir la URL HD (si existe)
                if (apodData.getHdurl() != null && !apodData.getHdurl().isEmpty()) {
                    btnViewApodHd.setText("Ver Imagen en HD");
                    btnViewApodHd.setOnClickListener(v -> openUrlInBrowser(apodData.getHdurl()));
                    btnViewApodHd.setVisibility(View.VISIBLE);
                } else {
                    btnViewApodHd.setVisibility(View.GONE);
                }
            }
        } else {
            // No URL disponible para APOD
            Log.e(TAG, "URL de APOD es nula o vacía.");
            showError("La Imagen Astronómica del Día no está disponible (URL no encontrada).");
        }
    }

    /**
     * Abre una URL en el navegador web del dispositivo.
     * @param url La URL a abrir.
     */
    private void openUrlInBrowser(String url) {
        try {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            startActivity(browserIntent);
        } catch (Exception e) {
            Log.e(TAG, "Error al abrir URL en el navegador: " + e.getMessage());
            Toast.makeText(getContext(), "No se pudo abrir el enlace.", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Muestra un mensaje de error y oculta otros elementos de la UI.
     * @param message El mensaje de error a mostrar.
     */
    private void showError(String message) {
        tvApodError.setText(message);
        tvApodError.setVisibility(View.VISIBLE);
        progressBarApod.setVisibility(View.GONE);
        ivApodImage.setVisibility(View.GONE);
        tvApodVideoMessage.setVisibility(View.GONE);
        btnViewApodHd.setVisibility(View.GONE);
        tvApodTitle.setVisibility(View.GONE);
        tvApodDate.setVisibility(View.GONE);
        tvApodCopyright.setVisibility(View.GONE);
        tvApodExplanation.setVisibility(View.GONE);
        Log.e(TAG, "APOD Error: " + message);
    }
}