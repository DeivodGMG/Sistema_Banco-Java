
import java.time.LocalDate;
import java.util.Scanner;

public class Banco {
    protected LocalDate fecha = LocalDate.now();
    protected Scanner scanner = new Scanner(System.in);
    // Muestra el menú principal con datos del usuario
    public void mostrarMenu(String nombre, String numeroCuenta, double saldo) {
        System.out.println("\n** Menú de Operaciones **");
        System.out.println("Usuario: " + nombre);
        System.out.println("No. cuenta: " + numeroCuenta);
        System.out.println("Fecha: " + fecha);
        System.out.println("Saldo: " + saldo);
        System.out.println("1) Depositar");
        System.out.println("2) Retirar");
        System.out.println("3) Transferir");
        System.out.println("4) Cambiar NIP");
        System.out.println("5) Ver historial de transacciones");
        System.out.println("6) Salir");
        System.out.print("Seleccione una opción: ");
    }

    // Método para cambiar el NIP
    public void modificarNIP() {
        System.out.println("¡Cambio de NIP realizado con éxito!");
    }
}