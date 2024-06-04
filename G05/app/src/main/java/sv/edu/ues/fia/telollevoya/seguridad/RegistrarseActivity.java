package sv.edu.ues.fia.telollevoya.seguridad;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.*;


import androidx.core.content.ContextCompat;

import java.util.Calendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import sv.edu.ues.fia.telollevoya.R;

public class RegistrarseActivity extends Activity {

    EditText txtCorreo, txtNombre, txtApellido, txtContra, txtConfirmacionContra;
    TextView lblFechaNacimiento;
    Spinner sSexo;
    String correo, nombre, apellido, contra, confirmacionContra, sexo, nacimiento;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registrarse);
        txtCorreo = findViewById(R.id.txtCorreo);
        txtNombre = findViewById(R.id.txtNombre);
        txtApellido = findViewById(R.id.txtApellido);
        sSexo = findViewById(R.id.sSexo);
        lblFechaNacimiento = findViewById(R.id.lblFechaNacimiento);
        txtContra = findViewById(R.id.txtContra);
        txtConfirmacionContra = findViewById(R.id.txtNuevaContra);

    }

    // Funciona para seleccionar el dia-mes-año de nacimiento
    public void abrirCalendario(View v)
    {
        Calendar cal = Calendar.getInstance();
        int anio = cal.get(Calendar.YEAR);
        int mes = cal.get(Calendar.MONTH);
        int dia = cal.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog dpd = new DatePickerDialog(this, R.style.DialogTheme, (view, year, month, dayOfMonth) -> {
            String fecha = dayOfMonth + "-" + (month + 1) + "-" + year;
            lblFechaNacimiento.setText(fecha);
        }, dia,mes,anio);
        dpd.show();
    }

    //FUNCIONALIDAD EL BOTON REGISTRARSE
    @SuppressLint("SetTextI18n")
    public void irRolRegistrarse(View v){
        int banderaCorreo = 0, banderaContra=0, banderaConfirmacionContra= 0, banderaApellido= 0,banderaNombre= 0, banderaNacimiento = 0,banderaTotal= 1;

        //Extracion de Informacion
        correo = txtCorreo.getText().toString().trim();
        nombre = txtNombre.getText().toString();
        apellido = txtApellido.getText().toString();
        sexo = sSexo.getSelectedItem().toString();
        contra = txtContra.getText().toString().trim();
        confirmacionContra = txtConfirmacionContra.getText().toString().trim();

        // REGEX para validar correo
        String patronCorreo = "^[A-Za-z0-9._%+-]+@[a-z.-]+\\.[a-z]{2,}$";
        // Compilar el patrón en un objeto Pattern
        Pattern pattern = Pattern.compile(patronCorreo);// Crear un objeto Matcher para aplicar el patrón al correo
        Matcher matcher = pattern.matcher(correo);

        //Validamos compos de contraseño----------------------------
        if (!contra.isEmpty()) {
            banderaContra = 1;
        } else {
            txtContra.setHintTextColor(ContextCompat.getColor(this, R.color.error)); // Cambia el color del hint a rojo
            txtContra.setHint("¡Falta contraseña!");
        }

        //Validamos compos de confirmacion contraseño ----------------------------
        if (!confirmacionContra.isEmpty()) {
            banderaConfirmacionContra = 1;
        } else {
            txtConfirmacionContra.setHintTextColor(ContextCompat.getColor(this, R.color.error));// Cambia el color del hint a rojo
            txtConfirmacionContra.setHint("¡Falta confirmar su contraseña!");
        }

        //Validamos la confirmacion de la contraseña ----------------------------
        if(!contra.equals(confirmacionContra)){
            txtConfirmacionContra.setText("");
            txtConfirmacionContra.setHintTextColor(ContextCompat.getColor(this, R.color.error)); // Cambia el color del hint a rojo
            txtConfirmacionContra.setHint("¡Contraseña no parecida!");
            banderaTotal = 0;
        }

        //Obtenemos la fecha de nacimiento
        nacimiento = lblFechaNacimiento.getText().toString();
        if (nacimiento.isEmpty() || nacimiento.equals("¡Falta Seleccionar fecha!"))
        {
            lblFechaNacimiento.setText("¡Falta Seleccionar fecha!");
            lblFechaNacimiento.setTextColor(ContextCompat.getColor(this, R.color.error));
            lblFechaNacimiento.requestFocus();
        }
        else
        {
            banderaNacimiento = 1;
        }

        //Validamos compos de apellido----------------------------
        if (!apellido.isEmpty()) {
            banderaApellido = 1;
        } else {
            txtApellido.setHintTextColor(ContextCompat.getColor(this, R.color.error)); // Cambia el color del hint a rojo
            txtApellido.setHint("¡Falta colocar el apellido!");
            txtApellido.requestFocus();
        }

        //Validamos compos de nombre----------------------------
        if (!nombre.isEmpty()) {
            banderaNombre = 1;
        } else {
            txtNombre.setHintTextColor(ContextCompat.getColor(this, R.color.error)); // Cambia el color del hint a rojo
            txtNombre.setHint("¡Falta colocar el nombre!");
            txtNombre.requestFocus();
        }

        // Validar si el correo cumple con el patrón ---------------------------------
        if (matcher.matches()) {
            banderaCorreo = 1;
        } else {
            txtCorreo.setHintTextColor(ContextCompat.getColor(this, R.color.error));
            txtCorreo.setHint("¡Correo invalido!");
            txtCorreo.setText("");// Cambia el texto del hint a "FALTA CORREO"
            txtCorreo.requestFocus();
        }

        //VALIDAMOS SI DAMOS EL PASE----------------------------
        if (banderaCorreo==1 && banderaNombre==1 && banderaApellido==1 && banderaContra==1 && banderaConfirmacionContra==1 && banderaNacimiento ==1 && banderaTotal==1 ) {
            // Crear un Intent para iniciar la actividad "RegistrarseActivity"
            Intent intent = new Intent(this, rolRegistrarseActivity.class);

            // Envio Informacion
            intent.putExtra("correo", correo);
            intent.putExtra("nombre", nombre);
            intent.putExtra("apellido", apellido);
            intent.putExtra("contraseña", contra);
            intent.putExtra("sexo", sexo);
            intent.putExtra("nacimiento", nacimiento);
            startActivity(intent);
        }
    }
}