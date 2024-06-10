package sv.edu.ues.fia.telollevoya.legal;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.scroll.DefaultScrollHandle;

import java.util.Locale;

import sv.edu.ues.fia.telollevoya.R;

public class TerminosCondicionesActivity extends AppCompatActivity {
    PDFView pdfView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_terminos_condiciones);

        pdfView = findViewById(R.id.pdfViewTerminos);

        String language = Locale.getDefault().getLanguage();

        String pdfPath = getPdfPathForLanguage(language);

        pdfView.fromAsset(pdfPath)
                .defaultPage(0)
                .scrollHandle(new DefaultScrollHandle(this))
                .spacing(10)
                .load();
    }

    private String getPdfPathForLanguage(String language) {
        switch (language) {
            case "en":
                return "en/TermsConditions.pdf";
            case "es":
                return "es/TerminosyCondiciones.pdf";
            default:
                return "es/TerminosyCondiciones.pdf"; // Idioma predeterminado
        }
    }
}
