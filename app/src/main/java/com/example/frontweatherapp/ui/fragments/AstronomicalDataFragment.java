package com.example.frontweatherapp.ui.fragments;

import android.os.Bundle;
import android.util.Log; // Importar Log para depuración
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.frontweatherapp.R;
import com.example.frontweatherapp.models.AstronomicalTimes;
import com.example.frontweatherapp.models.Location; // Aunque Location no se usa directamente aquí, se mantiene la importación si es necesaria en el futuro.

import java.text.ParseException; // Importar ParseException
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone; // Importar TimeZone

public class AstronomicalDataFragment extends Fragment {

    private static final String TAG = "AstroDataFragment"; // TAG para los logs

    private TextView tvDate;
    private TextView tvSunriseTime;
    private TextView tvSunsetTime;
    private TextView tvSolarNoon;
    private TextView tvDayLength;
    private TextView tvCivilTwilightBegin;
    private TextView tvCivilTwilightEnd;
    private TextView tvNauticalTwilightBegin;
    private TextView tvNauticalTwilightEnd;
    private TextView tvAstronomicalTwilightBegin;
    private TextView tvAstronomicalTwilightEnd;
    private TextView tvLocationName;

    public static AstronomicalDataFragment newInstance(AstronomicalTimes astronomicalTimes, String locationName) {
        AstronomicalDataFragment fragment = new AstronomicalDataFragment();
        Bundle args = new Bundle();
        args.putSerializable("astronomicalTimes", astronomicalTimes);
        args.putString("locationName", locationName);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView: Iniciando creación de la vista para AstronomicalDataFragment.");
        View view = inflater.inflate(R.layout.fragment_astronomical_data, container, false);

        tvDate = view.findViewById(R.id.tvDate);
        tvSunriseTime = view.findViewById(R.id.tvSunriseTime);
        tvSunsetTime = view.findViewById(R.id.tvSunsetTime);
        tvSolarNoon = view.findViewById(R.id.tvSolarNoon);
        tvDayLength = view.findViewById(R.id.tvDayLength);
        tvCivilTwilightBegin = view.findViewById(R.id.tvCivilTwilightBegin);
        tvCivilTwilightEnd = view.findViewById(R.id.tvCivilTwilightEnd);
        tvNauticalTwilightBegin = view.findViewById(R.id.tvNauticalTwilightBegin);
        tvNauticalTwilightEnd = view.findViewById(R.id.tvNauticalTwilightEnd);
        tvAstronomicalTwilightBegin = view.findViewById(R.id.tvAstronomicalTwilightBegin);
        tvAstronomicalTwilightEnd = view.findViewById(R.id.tvAstronomicalTwilightEnd);
        tvLocationName = view.findViewById(R.id.tvLocationName);

        // Verificar que todas las vistas se inicializaron correctamente
        if (tvDate == null) Log.e(TAG, "Error: tvDate es NULL");
        if (tvSunriseTime == null) Log.e(TAG, "Error: tvSunriseTime es NULL");
        if (tvSunsetTime == null) Log.e(TAG, "Error: tvSunsetTime es NULL");
        if (tvSolarNoon == null) Log.e(TAG, "Error: tvSolarNoon es NULL");
        if (tvDayLength == null) Log.e(TAG, "Error: tvDayLength es NULL");
        if (tvCivilTwilightBegin == null) Log.e(TAG, "Error: tvCivilTwilightBegin es NULL");
        if (tvCivilTwilightEnd == null) Log.e(TAG, "Error: tvCivilTwilightEnd es NULL");
        if (tvNauticalTwilightBegin == null) Log.e(TAG, "Error: tvNauticalTwilightBegin es NULL");
        if (tvNauticalTwilightEnd == null) Log.e(TAG, "Error: tvNauticalTwilightEnd es NULL");
        if (tvAstronomicalTwilightBegin == null) Log.e(TAG, "Error: tvAstronomicalTwilightBegin es NULL");
        if (tvAstronomicalTwilightEnd == null) Log.e(TAG, "Error: tvAstronomicalTwilightEnd es NULL");
        if (tvLocationName == null) Log.e(TAG, "Error: tvLocationName es NULL");

        if (getArguments() != null) {
            Log.d(TAG, "onCreateView: Argumentos recibidos.");
            AstronomicalTimes astronomicalTimes = (AstronomicalTimes) getArguments().getSerializable("astronomicalTimes");
            String locationName = getArguments().getString("locationName");

            if (astronomicalTimes != null) {
                Log.d(TAG, "onCreateView: AstronomicalTimes no es nulo. Actualizando UI.");
                updateUI(astronomicalTimes, locationName);
            } else {
                Log.w(TAG, "onCreateView: AstronomicalTimes es nulo en los argumentos.");
            }
            if (locationName == null) {
                Log.w(TAG, "onCreateView: locationName es nulo en los argumentos.");
            }
        } else {
            Log.w(TAG, "onCreateView: No se recibieron argumentos en el Bundle.");
        }

        return view;
    }

    private void updateUI(AstronomicalTimes astronomicalTimes, String locationName) {
        Log.d(TAG, "updateUI: Iniciando actualización de la UI con datos astronómicos.");
        Log.d(TAG, "updateUI: AstronomicalTimes recibido: " + astronomicalTimes.toString());
        Log.d(TAG, "updateUI: Location Name recibido: " + locationName);

        // Formatear la fecha actual
        SimpleDateFormat dateFormat = new SimpleDateFormat("EEEE d 'de' MMMM", new Locale("es", "ES"));
        String currentDate = dateFormat.format(new Date());
        tvDate.setText(currentDate);
        Log.d(TAG, "updateUI: Fecha actual establecida: " + currentDate);

        // Actualizar tiempos de sol con corrección de zona horaria
        String sunriseTime = formatTime(astronomicalTimes.getSunrise(), "Sunrise");
        tvSunriseTime.setText(sunriseTime);
        Log.d(TAG, "updateUI: Sunrise Time establecido: " + sunriseTime);

        String sunsetTime = formatTime(astronomicalTimes.getSunset(), "Sunset");
        tvSunsetTime.setText(sunsetTime);
        Log.d(TAG, "updateUI: Sunset Time establecido: " + sunsetTime);

        String solarNoon = formatTime(astronomicalTimes.getSolarNoon(), "Solar Noon");
        tvSolarNoon.setText(solarNoon);
        Log.d(TAG, "updateUI: Solar Noon establecido: " + solarNoon);

        String dayLength = astronomicalTimes.getDayLength();
        tvDayLength.setText(dayLength);
        Log.d(TAG, "updateUI: Day Length establecido: " + dayLength);

        // Mapear tiempos de crepúsculo
        String civilTwilightBegin = formatTime(astronomicalTimes.getCivilTwilightBegin(), "Civil Twilight Begin");
        tvCivilTwilightBegin.setText(civilTwilightBegin);
        Log.d(TAG, "updateUI: Civil Twilight Begin establecido: " + civilTwilightBegin);

        String civilTwilightEnd = formatTime(astronomicalTimes.getCivilTwilightEnd(), "Civil Twilight End");
        tvCivilTwilightEnd.setText(civilTwilightEnd);
        Log.d(TAG, "updateUI: Civil Twilight End establecido: " + civilTwilightEnd);

        String nauticalTwilightBegin = formatTime(astronomicalTimes.getNauticalTwilightBegin(), "Nautical Twilight Begin");
        tvNauticalTwilightBegin.setText(nauticalTwilightBegin);
        Log.d(TAG, "updateUI: Nautical Twilight Begin establecido: " + nauticalTwilightBegin);

        String nauticalTwilightEnd = formatTime(astronomicalTimes.getNauticalTwilightEnd(), "Nautical Twilight End");
        tvNauticalTwilightEnd.setText(nauticalTwilightEnd);
        Log.d(TAG, "updateUI: Nautical Twilight End establecido: " + nauticalTwilightEnd);

        String astronomicalTwilightBegin = formatTime(astronomicalTimes.getAstronomicalTwilightBegin(), "Astronomical Twilight Begin");
        tvAstronomicalTwilightBegin.setText(astronomicalTwilightBegin);
        Log.d(TAG, "updateUI: Astronomical Twilight Begin establecido: " + astronomicalTwilightBegin);

        String astronomicalTwilightEnd = formatTime(astronomicalTimes.getAstronomicalTwilightEnd(), "Astronomical Twilight End");
        tvAstronomicalTwilightEnd.setText(astronomicalTwilightEnd);
        Log.d(TAG, "updateUI: Astronomical Twilight End establecido: " + astronomicalTwilightEnd);

        if (locationName != null && !locationName.isEmpty()) {
            tvLocationName.setText(locationName);
            Log.d(TAG, "updateUI: Nombre de ubicación establecido: " + locationName);
        } else {
            tvLocationName.setText("Ubicación desconocida");
            Log.w(TAG, "updateUI: locationName era nulo o vacío, se estableció 'Ubicación desconocida'.");
        }
        Log.d(TAG, "updateUI: Actualización de la UI de datos astronómicos finalizada.");
    }

    /**
     * Formatea una cadena de tiempo (asumida en UTC) a la zona horaria local de Chile (America/Santiago).
     * @param time La cadena de tiempo en formato "hh:mm:ss a" (ej. "11:34:36 AM") asumida en UTC.
     * @param fieldName Nombre del campo para el log (ej. "Sunrise").
     * @return La cadena de tiempo formateada en la zona horaria local de Chile.
     */
    private String formatTime(String time, String fieldName) {
        Log.d(TAG, "formatTime: Formateando " + fieldName + ". Entrada: '" + time + "'");
        if (time == null || time.isEmpty()) {
            Log.w(TAG, "formatTime: " + fieldName + " es nulo o vacío, retornando '--:--'.");
            return "--:--"; // O un valor por defecto adecuado
        }
        try {
            // Formato de entrada: "hh:mm:ss a" (ej. "11:34:36 AM")
            SimpleDateFormat inputFormat = new SimpleDateFormat("hh:mm:ss a", Locale.US);
            inputFormat.setTimeZone(TimeZone.getTimeZone("UTC")); // Indicar que la cadena de entrada es UTC

            Date date = inputFormat.parse(time); // Parsear la fecha en UTC
            Log.d(TAG, "formatTime: " + fieldName + " - Fecha parseada (UTC): " + date);

            // Formato de salida: "hh:mm a" con la zona horaria de Chile
            SimpleDateFormat outputFormat = new SimpleDateFormat("hh:mm a", new Locale("es", "ES")); // Usar Locale.getDefault() o Locale("es", "ES")
            outputFormat.setTimeZone(TimeZone.getTimeZone("America/Santiago")); // Convertir a la zona horaria de Santiago, Chile

            String formattedTime = outputFormat.format(date);
            Log.d(TAG, "formatTime: " + fieldName + " - Salida formateada (America/Santiago): " + formattedTime);
            return formattedTime;
        } catch (ParseException e) {
            Log.e(TAG, "Error al parsear la hora para " + fieldName + ": '" + time + "' - " + e.getMessage(), e);
            return time; // Retorna el string original si hay un error de parseo
        } catch (Exception e) {
            Log.e(TAG, "Error inesperado al formatear la hora para " + fieldName + ": '" + time + "' - " + e.getMessage(), e);
            return time; // Retorna el string original si hay un error inesperado
        }
    }
}
