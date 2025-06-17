
import java.util.Random;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Cliente cliente = new Cliente();
        Scanner scanner = new Scanner(System.in);
        Random random = new Random();

        System.out.println("=== Bienvenido al Sistema de Banco UPV ===");
        System.out.print("1. Ingrese su nombre para la cuenta: ");
        cliente.name = scanner.nextLine();

        // Asignar número de cuenta aleatorio
        cliente.rifaNumeroCuenta = random.nextInt(cliente.numeroCuenta.length);
        System.out.println("- Su No. cuenta: " +
            cliente.numeroCuenta[cliente.rifaNumeroCuenta]);

        System.out.println("\n2. Debido a temas de seguridad, tiene que\n" +
            "   cambiar el NIP de su tarjeta. NIP actual: 1111");
        System.out.println("Ingrese un número:");
        System.out.println("1) Cambiar NIP");
        System.out.println("2) Iniciar sesión");
        String select = scanner.nextLine();

        if ("1".equals(select)) {
            cliente.cambiarNIP();
            cliente.iniciarSesion();
        } else if ("2".equals(select)) {
            cliente.iniciarSesion();
        } else {
            System.out.println("Opción no válida. Cerrando aplicación.");
        }
    }
}
