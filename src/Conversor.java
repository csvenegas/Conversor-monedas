import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.Locale;
import java.util.Map;
import java.util.Scanner;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

public class Conversor {

    private static final String API_KEY = "55b56b9b16bf87d5d0d4a11c";
    private static final String API_URL = "https://v6.exchangerate-api.com/v6/"
            + API_KEY + "/latest/USD";
    private static ExchangeRateResponse rates;

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        try {
            rates = fetchRates();
        } catch (Exception e) {
            System.err.println("Error al descargar tasas de cambio: " + e.getMessage());
            return;
        }

        int opcion;
        do {
            mostrarMenu();
            opcion = sc.nextInt();
            System.out.println();

            switch (opcion) {
                case 1: convertir("USD", "ARS", sc); break;
                case 2: convertir("ARS", "USD", sc); break;
                case 3: convertir("USD", "BRL", sc); break;
                case 4: convertir("BRL", "USD", sc); break;
                case 5: convertir("USD", "COP", sc); break;
                case 6: convertir("COP", "USD", sc); break;
                case 7: convertir("USD", "CLP", sc); break;
                case 8: convertir("CLP", "USD", sc); break;
                case 9: System.out.println("¡Hasta luego!"); break;
                default: System.out.println("Opción inválida. Intenta de nuevo.");
            }
            System.out.println();
        } while (opcion != 9);

        sc.close();
    }

    private static void mostrarMenu() {
        System.out.println("*******************************************");
        System.out.println("Sea bienvenido/a al Conversor de Moneda =]");
        System.out.println();
        System.out.println("1) Dólar ⇒ Peso argentino");
        System.out.println("2) Peso argentino ⇒ Dólar");
        System.out.println("3) Dólar ⇒ Real brasileño");
        System.out.println("4) Real brasileño ⇒ Dólar");
        System.out.println("5) Dólar ⇒ Peso colombiano");
        System.out.println("6) Peso colombiano ⇒ Dólar");
        System.out.println("7) Dólar  ⇒ Peso chileno");
        System.out.println("8) Peso chileno ⇒ Dólar");
        System.out.println("9) Salir");
        System.out.print("Elija una opción válida: ");
    }

    private static void convertir(String from, String to, Scanner sc) {
        System.out.printf("Ingrese monto en %s: ", from);
        double monto = sc.nextDouble();

        Double rateFromUSD = rates.conversionRates.get(from);
        Double rateToUSD   = rates.conversionRates.get(to);

        if (rateFromUSD == null || rateToUSD == null) {
            System.out.println("No hay datos de conversión para esas monedas.");
            return;
        }

        double montoEnUSD = monto / rateFromUSD;
        double montoDestino = montoEnUSD * rateToUSD;

        System.out.printf(LocaleSettings.localeES(),
                "%.2f %s = %.2f %s%n",
                monto, from, montoDestino, to
        );
    }

    private static ExchangeRateResponse fetchRates() throws Exception {
        URL url = new URI(API_URL).toURL();
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");

        try (Reader reader = new InputStreamReader(conn.getInputStream())) {
            return new Gson().fromJson(reader, ExchangeRateResponse.class);
        }
    }

    private static class ExchangeRateResponse {
        @SerializedName("result")
        String result;

        @SerializedName("base_code")
        String baseCode;

        @SerializedName("conversion_rates")
        Map<String, Double> conversionRates;
    }

    private static class LocaleSettings {
        static java.util.Locale localeES() {
            return Locale.forLanguageTag("es-ES");
        }
    }
}
