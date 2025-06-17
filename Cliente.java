import java.time.LocalDate;
import java.util.Scanner;

public class Cliente extends Banco {
    
    public String name;
    public String[] numeroCuenta = {
        "1964858474926904",
        "6962853476927904",
        "6962453576997904"
    };
    private String nip = "1111";
    public double saldo = 500;
    public int rifaNumeroCuenta;
    private Scanner scanner = new Scanner(System.in);
    int pesoArrays = 1;    
    // Inicia sesión pidiendo NIP y muestra el menú
    public void iniciarSesion() {
        System.out.println("\nHola " + name + "!");
        while (true) {
            System.out.print("Ingrese su NIP (4 dígitos): ");
            String verifyNIP = scanner.nextLine().trim();
            if (verifyNIP.matches("\\d{4}") && verifyNIP.equals(nip)) {
                gestionarOperaciones();
                break;
            } else {
                System.out.println("NIP inválido o incorrecto. Debe tener 4 dígitos.");
            }
        }
    }
//MIS VARIABLES ESPECIALES PARA EL HISTORIAL

    Double[] historialSaldo = new Double[3];
    LocalDate[] historialFecha = new LocalDate[3];
    String[] historialTipo = new String[3];
    int vecesRealizadas = 0;
    // Loop del menú de operaciones
    private void gestionarOperaciones() {
        String opcion;
        do {
            mostrarMenu(name, numeroCuenta[rifaNumeroCuenta], saldo);
            opcion = scanner.nextLine().trim();
            switch (opcion) {
                case "1": hacerDeposito(); break;
                case "2": hacerRetiro(); break;
                case "3": transferenciaMenu(); break;
                case "4": cambiarNIP(); break;
                case "5":verMovimientos(); break; 
                case "6": System.out.println("Gracias por visitar el banco UPV. ¡Hasta luego!"); break;
                
                default: System.out.println("Opción no válida. Intente nuevamente.");
            }
        } while (!opcion.equals("5"));
    }

    private void hacerDeposito() {
        System.out.println("\n- Saldo actual: " + saldo);
        System.out.print("Ingrese el monto a depositar: ");
        double monto = leerDouble();
        if (monto > 0) {
            saldo += monto;
            
            System.out.println("Saldo actualizado: " + saldo);
            historialSaldo[vecesRealizadas] = monto;
            historialTipo[vecesRealizadas] = "Deposito";
            historialFecha[vecesRealizadas] = fecha;
            vecesRealizadas = vecesRealizadas + 1;
            pesoArrays = pesoArrays + 1;
        } else {
            System.out.println("Monto inválido.");
        }
    }

    private void hacerRetiro() {
        System.out.println("\n- Saldo actual: " + saldo);
        System.out.print("Ingrese el monto a retirar: ");
        double monto = leerDouble();
        if (monto > 0 && monto <= saldo) {
            saldo -= monto;
            System.out.println("Retiro exitoso. Saldo restante: " + saldo);
            pesoArrays = pesoArrays + 1;
             historialSaldo[vecesRealizadas] = monto;
            historialTipo[vecesRealizadas] = "Retiro";
            historialFecha[vecesRealizadas] = fecha;
            vecesRealizadas = vecesRealizadas + 1;
        } else {
            System.out.println("Monto inválido o saldo insuficiente.");
        }
    }

    // Menú de transferencia interno o interbancario
    private void transferenciaMenu() {
        System.out.println("\n-- Transferencias --");
        System.out.println("- Saldo disponible: " + saldo);
        System.out.println("1) Transferencia interna");
        System.out.println("2) Transferencia interbancaria");
        System.out.print("Seleccione una opción: ");
        String tipo = scanner.nextLine().trim();
        if (tipo.equals("1")) {
            interna();
        } else if (tipo.equals("2")) {
            interbancaria();
        } else {
            System.out.println("Opción no válida.");
        }
    }
//Transferencia de un mismo banco a otro
    private void interna() {
        System.out.print("Ingrese No. de tarjeta destino (10-12 dígitos): ");
        String target;
        while (true) {
            target = scanner.nextLine().trim();
            if (target.matches("\\d{10,12}")) break;
            System.out.println("Número inválido. Debe tener entre 10 y 12 dígitos.");
        }
        System.out.print("Ingrese monto a transferir: ");
        double monto = leerDouble();
        if (monto <= 0 || monto > saldo) {
            System.out.println("Monto inválido o saldo insuficiente.");
            return;
        }
        realizarTransfer(target, monto);
    }

    private void interbancaria() {
        System.out.print("Ingrese CLABE objetivo (18 dígitos): ");
        String clabe;
        while (true) {
            clabe = scanner.nextLine().trim();
            if (clabe.matches("\\d{18}")) break;
            System.out.println("CLABE inválida. Debe tener exactamente 18 dígitos.");
        }
        System.out.print("Ingrese monto a transferir: ");
        double monto = leerDouble();
        if (monto <= 0 || monto > saldo) {
            System.out.println("Monto inválido o saldo insuficiente.");
            return;
        }
        realizarTransfer(clabe, monto);
    }

    //Simular transferencia
    private void realizarTransfer(String destino, double monto) {
        System.out.print("Ingrese nombre del beneficiario: ");
        String beneficiario = scanner.nextLine();
        System.out.print("Ingrese concepto de la transferencia: ");
        String concepto = scanner.nextLine();
        saldo -= monto;
        System.out.println("\nTransferencia exitosa:");
        System.out.println("- Destino: " + destino);
        System.out.println("- Beneficiario: " + beneficiario);
        System.out.println("- Monto: " + monto);
        System.out.println("- Concepto: " + concepto);
        System.out.println("- Saldo restante: " + saldo);
            pesoArrays = pesoArrays + 1;
             historialSaldo[vecesRealizadas] = monto;
            historialTipo[vecesRealizadas] = "Transferencia";
            historialFecha[vecesRealizadas] = fecha;
            vecesRealizadas = vecesRealizadas + 1;
    }
    //Metodo que muestra el historial .-.
    public void verMovimientos(){
        System.out.println("** Historial de transacciones **");
        System.out.println("Consulte todos los movimientos que ha realizado usted.");
        System.out.println("------------------------------------------");
        for(int i = 0; i < historialFecha.length; i++){
             System.out.println("- Fecha: " + historialFecha[i]);
             System.out.println("- Tipo : " + historialTipo[i]);
             System.out.println("- Cantidad: " + historialSaldo[i]);
        }
    }



    // Cambio de NIP con validación de 4 dígitos
    public void cambiarNIP() {
        int intentos = 3;
        while (true) {
            System.out.print("\nIngrese el nuevo NIP (4 dígitos): ");
            String n1 = scanner.nextLine().trim();
            System.out.print("Confirme el nuevo NIP: ");
            String n2 = scanner.nextLine().trim();
            if (n1.matches("\\d{4}") && n1.equals(n2)) {
                nip = n1;
                modificarNIP();
                break;
            } else {
                System.out.println("Error: ambos NIP deben coincidir y tener 4 dígitos numéricos.");
                intentos = intentos - 1;
            }
            if (intentos == 0){
                System.out.println("Cuenta bloqueada");
                break;
            }
        }
    }

    // leer double seguro
    private double leerDouble() {
        try {
            double val = Double.parseDouble(scanner.nextLine());
            return val;
        } catch (NumberFormatException e) {
            return -1;
        }
    }
}