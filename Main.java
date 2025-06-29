import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class Main {
    private static Scanner scanner = new Scanner(System.in);
    private static List<Cliente> clientes = new ArrayList<>();
    private static List<Empleado> empleados = new ArrayList<>();
    private static List<Administrador> admins = new ArrayList<>();
    private static Gerente gerente = new Gerente("Alejandro", "G001");
    private static Map<String, Prestamo> prestamos = new HashMap<>();
    private static Map<String, List<Transaccion>> transacciones = new HashMap<>();
    private static List<Cuenta> cuentas = new ArrayList<>();

    public static void main(String[] args) {
        inicializarDatos();
        mostrarMenuPrincipal();
    }

    private static void inicializarDatos() {
        // Inicialización de usuarios
        empleados.add(new Empleado("Luis", "E001", encriptarContraseña("empleado1")));
        empleados.add(new Empleado("Carmen", "E002", encriptarContraseña("empleado2")));
        
        admins.add(new Administrador("Sofia", "A001", encriptarContraseña("admin1")));
        admins.add(new Administrador("Jorge", "A002", encriptarContraseña("admin2")));
        admins.add(new Administrador("Ana", "A003", encriptarContraseña("admin3")));
        
        gerente.setContraseña(encriptarContraseña("gerente123"));

        // Creación de clientes iniciales
        crearClienteInicial("Daniela", 17, "menor de edad", "1234", 1000.0);
        crearClienteInicial("Marco", 21, "becario", "4321", 2500.0);
        crearClienteInicial("Teresa", 30, "empresa", "9999", 15000.0);
    }

    private static void crearClienteInicial(String nombre, int edad, String tipoTarjeta, String nip, double saldoInicial) {
        Cliente cliente = Cliente.crearNuevoCliente(nombre, edad, tipoTarjeta);
        if (cliente != null) {
            String numeroCuenta = generarNumeroCuenta();
            cliente.asignarCuentaYPin(numeroCuenta, nip);
            cliente.saldo = saldoInicial;
            clientes.add(cliente);
            
            // Crear cuenta asociada
            Cuenta cuenta = new Cuenta(numeroCuenta, saldoInicial, "Cuenta Corriente");
            cuentas.add(cuenta);
            transacciones.put(numeroCuenta, new ArrayList<>());
        }
    }

    private static String generarNumeroCuenta() {
        Random random = new Random();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 16; i++) {
            sb.append(random.nextInt(10));
        }
        return sb.toString();
    }

    private static void mostrarMenuPrincipal() {
        while (true) {
            System.out.println("\n==== BIENVENIDO AL BANCO UPV ====");
            System.out.println("1) Cliente");
            System.out.println("2) Empleado");
            System.out.println("3) Administrador");
            System.out.println("4) Gerente");
            System.out.println("5) Salir");
            System.out.print("Opción: ");
            String op = scanner.nextLine();

            switch (op) {
                case "1": autenticarCliente(); break;
                case "2": autenticarEmpleado(); break;
                case "3": autenticarAdministrador(); break;
                case "4": autenticarGerente(); break;
                case "5": 
                    System.out.println("¡Hasta pronto!");
                    return;
                default: System.out.println("Opción inválida.");
            }
        }
    }

    // Métodos de autenticación mejorados
    private static void autenticarCliente() {
        System.out.print("Número de cuenta: ");
        String cuenta = scanner.nextLine();
        System.out.print("NIP: ");
        String nip = scanner.nextLine();

        Cliente cliente = buscarCliente(cuenta);
        if (cliente != null && cliente.verificarNIP(nip)) {
            cliente.iniciarSesion();
        } else {
            System.out.println("Credenciales incorrectas.");
        }
    }

    private static void autenticarEmpleado() {
        System.out.print("ID de empleado: ");
        String id = scanner.nextLine();
        System.out.print("Contraseña: ");
        String contraseña = scanner.nextLine();

        Empleado empleado = buscarEmpleado(id);
        if (empleado != null && empleado.verificarContraseña(encriptarContraseña(contraseña))) {
            mostrarMenuEmpleado(empleado);
        } else {
            System.out.println("Credenciales incorrectas.");
        }
    }

    private static void autenticarAdministrador() {
        System.out.print("ID de administrador: ");
        String id = scanner.nextLine();
        System.out.print("Contraseña: ");
        String contraseña = scanner.nextLine();

        Administrador admin = buscarAdministrador(id);
        if (admin != null && admin.verificarContraseña(encriptarContraseña(contraseña))) {
            mostrarMenuAdministrador(admin);
        } else {
            System.out.println("Credenciales incorrectas.");
        }
    }

    private static void autenticarGerente() {
        System.out.print("ID de gerente: ");
        String id = scanner.nextLine();
        System.out.print("Contraseña: ");
        String contraseña = scanner.nextLine();

        if (gerente.getId().equals(id) && gerente.verificarContraseña(encriptarContraseña(contraseña))) {
            mostrarMenuGerente();
        } else {
            System.out.println("Credenciales incorrectas.");
        }
    }

    // Menús mejorados para cada tipo de usuario
    private static void mostrarMenuEmpleado(Empleado empleado) {
        while (true) {
            System.out.println("\n== MENÚ EMPLEADO ==");
            System.out.println("1) Registrar nuevo cliente");
            System.out.println("2) Realizar depósito");
            System.out.println("3) Realizar retiro");
            System.out.println("4) Procesar préstamo");
            System.out.println("5) Ver información de cliente");
            System.out.println("6) Salir");
            System.out.print("Opción: ");
            String op = scanner.nextLine();

            switch (op) {
                case "1":
                    empleado.registrarNuevoCliente(clientes, cuentas, transacciones);
                    break;
                case "2":
                    empleado.realizarDeposito(clientes, transacciones);
                    break;
                case "3":
                    empleado.realizarRetiro(clientes, transacciones);
                    break;
                case "4":
                    empleado.procesarPrestamo(clientes, prestamos);
                    break;
                case "5":
                    empleado.verInfoCliente(clientes);
                    break;
                case "6":
                    return;
                default:
                    System.out.println("Opción inválida.");
            }
        }
    }

    private static void mostrarMenuAdministrador(Administrador admin) {
        while (true) {
            System.out.println("\n== MENÚ ADMINISTRADOR ==");
            System.out.println("1) Gestionar empleados");
            System.out.println("2) Gestionar clientes");
            System.out.println("3) Ver reportes");
            System.out.println("4) Salir");
            System.out.print("Opción: ");
            String op = scanner.nextLine();

            switch (op) {
                case "1":
                    admin.gestionarEmpleados(empleados);
                    break;
                case "2":
                    admin.gestionarClientes(clientes, cuentas);
                    break;
                case "3":
                    admin.generarReportes(clientes, empleados, prestamos, transacciones);
                    break;
                case "4":
                    return;
                default:
                    System.out.println("Opción inválida.");
            }
        }
    }

    private static void mostrarMenuGerente() {
        while (true) {
            System.out.println("\n== MENÚ GERENTE ==");
            System.out.println("1) Gestionar administradores");
            System.out.println("2) Gestionar empleados");
            System.out.println("3) Gestionar clientes");
            System.out.println("4) Ver reportes completos");
            System.out.println("5) Configurar parámetros del banco");
            System.out.println("6) Salir");
            System.out.print("Opción: ");
            String op = scanner.nextLine();

            switch (op) {
                case "1":
                    gerente.gestionarAdministradores(admins);
                    break;
                case "2":
                    gerente.gestionarEmpleados(empleados);
                    break;
                case "3":
                    gerente.gestionarClientes(clientes, cuentas);
                    break;
                case "4":
                    gerente.generarReportesCompletos(clientes, empleados, admins, prestamos, transacciones);
                    break;
                case "5":
                    gerente.configurarParametrosBanco();
                    break;
                case "6":
                    return;
                default:
                    System.out.println("Opción inválida.");
            }
        }
    }

    // Métodos de búsqueda
    private static Cliente buscarCliente(String cuenta) {
        for (Cliente c : clientes) {
            if (c.getNumeroCuenta().equals(cuenta)) {
                return c;
            }
        }
        return null;
    }

    private static Empleado buscarEmpleado(String id) {
        for (Empleado e : empleados) {
            if (e.getId().equals(id)) {
                return e;
            }
        }
        return null;
    }

    private static Administrador buscarAdministrador(String id) {
        for (Administrador a : admins) {
            if (a.getId().equals(id)) {
                return a;
            }
        }
        return null;
    }

    // Método de encriptación
    private static String encriptarContraseña(String contraseña) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(contraseña.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : hash) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }

    // Clases internas mejoradas
    static class PersonaBanco {
        protected String nombre;
        protected String id;
        protected String contraseña;

        public PersonaBanco(String nombre, String id, String contraseña) {
            this.nombre = nombre;
            this.id = id;
            this.contraseña = contraseña;
        }

        public String getNombre() { return nombre; }
        public String getId() { return id; }
        
        public boolean verificarContraseña(String contraseñaEncriptada) {
            return this.contraseña.equals(contraseñaEncriptada);
        }
        
        public void setContraseña(String nuevaContraseña) {
            this.contraseña = nuevaContraseña;
        }
    }

    static class Cliente extends PersonaBanco {
        private String numeroCuenta;
        private String nip;
        private double saldo;
        private String tipoTarjeta;
        private List<Cuenta> cuentas = new ArrayList<>();
        private List<Prestamo> prestamos = new ArrayList<>();
        private List<Transaccion> historial = new ArrayList<>();
        private List<Inversion> inversiones = new ArrayList<>();

        public Cliente(String nombre, String id, String contraseña, String numeroCuenta, String nip) {
            super(nombre, id, contraseña);
            this.numeroCuenta = numeroCuenta;
            this.nip = nip;
            this.saldo = 0;
        }

        public static Cliente crearNuevoCliente(String nombre, int edad, String tipoTarjeta) {
            tipoTarjeta = tipoTarjeta.toLowerCase();
            if (tipoTarjeta.equals("menor de edad") && edad >= 18) {
                System.out.println("Solo menores pueden tener esa tarjeta.");
                return null;
            }
            if ((tipoTarjeta.equals("becario") || tipoTarjeta.equals("estudiantil")) && edad > 25) {
                System.out.println("Tarjeta permitida solo a menores de 26.");
                return null;
            }
            if (tipoTarjeta.equals("empresa") && edad < 18) {
                System.out.println("Tarjeta empresarial requiere mayoría de edad.");
                return null;
            }
            
            String numeroCuenta = generarNumeroCuenta();
            String nip = String.format("%04d", new Random().nextInt(10000));
            return new Cliente(nombre, "C" + clientes.size() + 1, encriptarContraseña(nip), numeroCuenta, nip);
        }

        public void asignarCuentaYPin(String cuenta, String pin) {
            this.numeroCuenta = cuenta;
            this.nip = pin;
        }

        public boolean verificarNIP(String intento) {
            return nip.equals(intento);
        }

        public String getNumeroCuenta() { return numeroCuenta; }
        public String getNip() { return nip; }

        public void iniciarSesion() {
            System.out.println("\nBienvenido, " + nombre + "!");
            while (true) {
                mostrarMenuCliente();
                String opcion = scanner.nextLine();
                
                switch (opcion) {
                    case "1": realizarDeposito(); break;
                    case "2": realizarRetiro(); break;
                    case "3": realizarTransferencia(); break;
                    case "4": cambiarNIP(); break;
                    case "5": verHistorial(); break;
                    case "6": gestionarInversiones(); break;
                    case "7": solicitarPrestamo(); break;
                    case "8": verCuentas(); break;
                    case "9": verPrestamos(); break;
                    case "10": 
                        System.out.println("Cerrando sesión...");
                        return;
                    default:
                        System.out.println("Opción inválida.");
                }
            }
        }

        private void mostrarMenuCliente() {
            System.out.println("\n=== MENÚ CLIENTE ===");
            System.out.println("1) Depositar");
            System.out.println("2) Retirar");
            System.out.println("3) Transferir");
            System.out.println("4) Cambiar NIP");
            System.out.println("5) Ver historial");
            System.out.println("6) Inversiones");
            System.out.println("7) Solicitar préstamo");
            System.out.println("8) Mis cuentas");
            System.out.println("9) Mis préstamos");
            System.out.println("10) Salir");
            System.out.print("Opción: ");
        }

        private void realizarDeposito() {
            System.out.print("Monto a depositar: ");
            double monto = Double.parseDouble(scanner.nextLine());
            if (monto > 0) {
                saldo += monto;
                Transaccion t = new Transaccion("Depósito", monto, numeroCuenta, numeroCuenta);
                historial.add(t);
                System.out.println("Depósito exitoso. Nuevo saldo: $" + saldo);
            } else {
                System.out.println("Monto inválido.");
            }
        }

        private void realizarRetiro() {
            System.out.print("Monto a retirar: ");
            double monto = Double.parseDouble(scanner.nextLine());
            if (monto > 0 && monto <= saldo) {
                saldo -= monto;
                Transaccion t = new Transaccion("Retiro", monto, numeroCuenta, numeroCuenta);
                historial.add(t);
                System.out.println("Retiro exitoso. Nuevo saldo: $" + saldo);
            } else {
                System.out.println("Monto inválido o saldo insuficiente.");
            }
        }

        private void realizarTransferencia() {
            System.out.print("Número de cuenta destino: ");
            String cuentaDestino = scanner.nextLine();
            System.out.print("Monto a transferir: ");
            double monto = Double.parseDouble(scanner.nextLine());
            
            if (monto > 0 && monto <= saldo) {
                Cliente destino = buscarCliente(cuentaDestino);
                if (destino != null) {
                    saldo -= monto;
                    destino.saldo += monto;
                    
                    Transaccion tOrigen = new Transaccion("Transferencia enviada", monto, numeroCuenta, cuentaDestino);
                    Transaccion tDestino = new Transaccion("Transferencia recibida", monto, numeroCuenta, cuentaDestino);
                    
                    historial.add(tOrigen);
                    destino.historial.add(tDestino);
                    
                    System.out.println("Transferencia exitosa. Nuevo saldo: $" + saldo);
                } else {
                    System.out.println("Cuenta destino no encontrada.");
                }
            } else {
                System.out.println("Monto inválido o saldo insuficiente.");
            }
        }

        private void cambiarNIP() {
            System.out.print("Nuevo NIP (4 dígitos): ");
            String nuevo = scanner.nextLine();
            if (nuevo.matches("\\d{4}")) {
                nip = nuevo;
                System.out.println("NIP cambiado exitosamente.");
            } else {
                System.out.println("El NIP debe tener exactamente 4 dígitos.");
            }
        }

        private void verHistorial() {
            System.out.println("\n=== HISTORIAL DE TRANSACCIONES ===");
            if (historial.isEmpty()) {
                System.out.println("No hay transacciones registradas.");
            } else {
                for (Transaccion t : historial) {
                    System.out.println(t);
                }
            }
        }

        private void gestionarInversiones() {
            while (true) {
                System.out.println("\n=== GESTIÓN DE INVERSIONES ===");
                System.out.println("1) Crear inversión");
                System.out.println("2) Ver mis inversiones");
                System.out.println("3) Liquidar inversión");
                System.out.println("4) Volver");
                System.out.print("Opción: ");
                
                String op = scanner.nextLine();
                switch (op) {
                    case "1": crearInversion(); break;
                    case "2": verInversiones(); break;
                    case "3": liquidarInversion(); break;
                    case "4": return;
                    default: System.out.println("Opción inválida.");
                }
            }
        }

        private void crearInversion() {
            System.out.println("\n=== NUEVA INVERSIÓN ===");
            System.out.print("Monto a invertir: ");
            double monto = Double.parseDouble(scanner.nextLine());
            
            if (monto <= 0 || monto > saldo) {
                System.out.println("Monto inválido o saldo insuficiente.");
                return;
            }
            
            System.out.println("Plazos disponibles:");
            System.out.println("1) 30 días (2% interés)");
            System.out.println("2) 60 días (3.5% interés)");
            System.out.println("3) 90 días (5% interés)");
            System.out.print("Seleccione plazo: ");
            
            int plazo = Integer.parseInt(scanner.nextLine());
            double interes = 0;
            int dias = 0;
            
            switch (plazo) {
                case 1: 
                    interes = 0.02;
                    dias = 30;
                    break;
                case 2:
                    interes = 0.035;
                    dias = 60;
                    break;
                case 3:
                    interes = 0.05;
                    dias = 90;
                    break;
                default:
                    System.out.println("Opción inválida.");
                    return;
            }
            
            saldo -= monto;
            Inversion inversion = new Inversion(monto, interes, dias);
            inversiones.add(inversion);
            
            System.out.println("Inversión creada exitosamente:");
            System.out.println("Monto: $" + monto);
            System.out.println("Plazo: " + dias + " días");
            System.out.println("Interés estimado: $" + (monto * interes));
        }

        private void verInversiones() {
            System.out.println("\n=== MIS INVERSIONES ===");
            if (inversiones.isEmpty()) {
                System.out.println("No tiene inversiones activas.");
            } else {
                for (int i = 0; i < inversiones.size(); i++) {
                    System.out.println((i+1) + ") " + inversiones.get(i));
                }
            }
        }

        private void liquidarInversion() {
            verInversiones();
            if (inversiones.isEmpty()) return;
            
            System.out.print("Seleccione inversión a liquidar (0 para cancelar): ");
            int op = Integer.parseInt(scanner.nextLine()) - 1;
            
            if (op >= 0 && op < inversiones.size()) {
                Inversion inv = inversiones.get(op);
                double ganancia = inv.getMonto() * inv.getInteres();
                saldo += inv.getMonto() + ganancia;
                
                System.out.println("Inversión liquidada:");
                System.out.println("Capital devuelto: $" + inv.getMonto());
                System.out.println("Ganancia: $" + ganancia);
                System.out.println("Total recibido: $" + (inv.getMonto() + ganancia));
                
                inversiones.remove(op);
            }
        }

        private void solicitarPrestamo() {
            System.out.println("\n=== SOLICITUD DE PRÉSTAMO ===");
            System.out.print("Monto solicitado: ");
            double monto = Double.parseDouble(scanner.nextLine());
            
            if (monto <= 0) {
                System.out.println("Monto inválido.");
                return;
            }
            
            System.out.println("Plazos disponibles:");
            System.out.println("1) 6 meses (10% interés)");
            System.out.println("2) 12 meses (8% interés)");
            System.out.println("3) 24 meses (6% interés)");
            System.out.print("Seleccione plazo: ");
            
            int plazo = Integer.parseInt(scanner.nextLine());
            double interes = 0;
            int meses = 0;
            
            switch (plazo) {
                case 1: 
                    interes = 0.10;
                    meses = 6;
                    break;
                case 2:
                    interes = 0.08;
                    meses = 12;
                    break;
                case 3:
                    interes = 0.06;
                    meses = 24;
                    break;
                default:
                    System.out.println("Opción inválida.");
                    return;
            }
            
            double pagoMensual = (monto + (monto * interes)) / meses;
            
            System.out.println("\nResumen del préstamo:");
            System.out.println("Monto: $" + monto);
            System.out.println("Plazo: " + meses + " meses");
            System.out.println("Tasa de interés: " + (interes * 100) + "%");
            System.out.println("Pago mensual estimado: $" + pagoMensual);
            System.out.print("¿Desea solicitar este préstamo? (s/n): ");
            
            if (scanner.nextLine().equalsIgnoreCase("s")) {
                Prestamo prestamo = new Prestamo(monto, interes, meses, numeroCuenta);
                prestamos.add(prestamo);
                saldo += monto;
                
                System.out.println("Préstamo aprobado. El monto ha sido depositado en su cuenta.");
                System.out.println("Nuevo saldo: $" + saldo);
            } else {
                System.out.println("Solicitud cancelada.");
            }
        }

        private void verCuentas() {
            System.out.println("\n=== MIS CUENTAS ===");
            System.out.println("Cuenta principal:");
            System.out.println("Número: " + numeroCuenta);
            System.out.println("Saldo: $" + saldo);
            System.out.println("Tipo: " + tipoTarjeta);
            
            if (!cuentas.isEmpty()) {
                System.out.println("\nOtras cuentas:");
                for (Cuenta c : cuentas) {
                    System.out.println(c);
                }
            }
        }

        private void verPrestamos() {
            System.out.println("\n=== MIS PRÉSTAMOS ===");
            if (prestamos.isEmpty()) {
                System.out.println("No tiene préstamos activos.");
            } else {
                for (Prestamo p : prestamos) {
                    System.out.println(p);
                }
            }
        }
    }

    static class Empleado extends PersonaBanco {
        public Empleado(String nombre, String id, String contraseña) {
            super(nombre, id, contraseña);
        }

        public void registrarNuevoCliente(List<Cliente> clientes, List<Cuenta> cuentas, Map<String, List<Transaccion>> transacciones) {
            System.out.println("\n=== REGISTRO DE NUEVO CLIENTE ===");
            System.out.print("Nombre completo: ");
            String nombre = scanner.nextLine();
            System.out.print("Edad: ");
            int edad = Integer.parseInt(scanner.nextLine());
            System.out.print("Tipo de cuenta (ahorro/corriente/empresarial): ");
            String tipoCuenta = scanner.nextLine();
            
            Cliente nuevoCliente = Cliente.crearNuevoCliente(nombre, edad, tipoCuenta);
            if (nuevoCliente != null) {
                clientes.add(nuevoCliente);
                
                // Crear cuenta asociada
                Cuenta cuenta = new Cuenta(nuevoCliente.getNumeroCuenta(), 0, tipoCuenta);
                cuentas.add(cuenta);
                transacciones.put(nuevoCliente.getNumeroCuenta(), new ArrayList<>());
                
                System.out.println("Cliente registrado exitosamente:");
                System.out.println("Número de cuenta: " + nuevoCliente.getNumeroCuenta());
                System.out.println("NIP temporal: " + nuevoCliente.getNip());
            }
        }

        public void realizarDeposito(List<Cliente> clientes, Map<String, List<Transaccion>> transacciones) {
            System.out.println("\n=== DEPÓSITO A CUENTA ===");
            System.out.print("Número de cuenta: ");
            String cuenta = scanner.nextLine();
            System.out.print("Monto: ");
            double monto = Double.parseDouble(scanner.nextLine());
            
            Cliente cliente = buscarCliente(cuenta);
            if (cliente != null && monto > 0) {
                cliente.saldo += monto;
                Transaccion t = new Transaccion("Depósito empleado", monto, "VENTANILLA", cuenta);
                transacciones.get(cuenta).add(t);
                
                System.out.println("Depósito realizado. Nuevo saldo: $" + cliente.saldo);
            } else {
                System.out.println("Cuenta no encontrada o monto inválido.");
            }
        }

        public void realizarRetiro(List<Cliente> clientes, Map<String, List<Transaccion>> transacciones) {
            System.out.println("\n=== RETIRO DE CUENTA ===");
            System.out.print("Número de cuenta: ");
            String cuenta = scanner.nextLine();
            System.out.print("Monto: ");
            double monto = Double.parseDouble(scanner.nextLine());
            
            Cliente cliente = buscarCliente(cuenta);
            if (cliente != null && monto > 0 && monto <= cliente.saldo) {
                cliente.saldo -= monto;
                Transaccion t = new Transaccion("Retiro empleado", monto, cuenta, "VENTANILLA");
                transacciones.get(cuenta).add(t);
                
                System.out.println("Retiro realizado. Nuevo saldo: $" + cliente.saldo);
            } else {
                System.out.println("Cuenta no encontrada, monto inválido o saldo insuficiente.");
            }
        }

        public void procesarPrestamo(List<Cliente> clientes, Map<String, Prestamo> prestamos) {
            System.out.println("\n=== PROCESAMIENTO DE PRÉSTAMO ===");
            System.out.print("Número de cuenta del cliente: ");
            String cuenta = scanner.nextLine();
            
            Cliente cliente = buscarCliente(cuenta);
            if (cliente == null) {
                System.out.println("Cliente no encontrado.");
                return;
            }
            
            System.out.print("Monto del préstamo: ");
            double monto = Double.parseDouble(scanner.nextLine());
            System.out.print("Plazo en meses: ");
            int meses = Integer.parseInt(scanner.nextLine());
            System.out.print("Tasa de interés (%): ");
            double interes = Double.parseDouble(scanner.nextLine()) / 100;
            
            double pagoMensual = (monto + (monto * interes)) / meses;
            
            System.out.println("\nResumen del préstamo:");
            System.out.println("Cliente: " + cliente.getNombre());
            System.out.println("Monto: $" + monto);
            System.out.println("Plazo: " + meses + " meses");
            System.out.println("Tasa de interés: " + (interes * 100) + "%");
            System.out.println("Pago mensual estimado: $" + pagoMensual);
            System.out.print("¿Aprobar este préstamo? (s/n): ");
            
            if (scanner.nextLine().equalsIgnoreCase("s")) {
                Prestamo prestamo = new Prestamo(monto, interes, meses, cuenta);
                prestamos.put(cuenta, prestamo);
                cliente.saldo += monto;
                
                System.out.println("Préstamo aprobado. El monto ha sido depositado en la cuenta del cliente.");
            } else {
                System.out.println("Préstamo rechazado.");
            }
        }

        public void verInfoCliente(List<Cliente> clientes) {
            System.out.println("\n=== INFORMACIÓN DE CLIENTE ===");
            System.out.print("Número de cuenta o nombre: ");
            String busqueda = scanner.nextLine();
            
            for (Cliente c : clientes) {
                if (c.getNumeroCuenta().equals(busqueda) || c.getNombre().equalsIgnoreCase(busqueda)) {
                    System.out.println("\nInformación del cliente:");
                    System.out.println("Nombre: " + c.getNombre());
                    System.out.println("Número de cuenta: " + c.getNumeroCuenta());
                    System.out.println("Tipo de cuenta: " + c.tipoTarjeta);
                    System.out.println("Saldo actual: $" + c.saldo);
                    return;
                }
            }
            
            System.out.println("Cliente no encontrado.");
        }
    }

    static class Administrador extends PersonaBanco {
        public Administrador(String nombre, String id, String contraseña) {
            super(nombre, id, contraseña);
        }

        public void gestionarEmpleados(List<Empleado> empleados) {
            while (true) {
                System.out.println("\n=== GESTIÓN DE EMPLEADOS ===");
                System.out.println("1) Ver todos los empleados");
                System.out.println("2) Agregar empleado");
                System.out.println("3) Eliminar empleado");
                System.out.println("4) Modificar empleado");
                System.out.println("5) Volver");
                System.out.print("Opción: ");
                
                String op = scanner.nextLine();
                switch (op) {
                    case "1": verEmpleados(empleados); break;
                    case "2": agregarEmpleado(empleados); break;
                    case "3": eliminarEmpleado(empleados); break;
                    case "4": modificarEmpleado(empleados); break;
                    case "5": return;
                    default: System.out.println("Opción inválida.");
                }
            }
        }

        private void verEmpleados(List<Empleado> empleados) {
            System.out.println("\n=== LISTA DE EMPLEADOS ===");
            for (Empleado e : empleados) {
                System.out.println("ID: " + e.getId() + " | Nombre: " + e.getNombre());
            }
        }

        private void agregarEmpleado(List<Empleado> empleados) {
            System.out.println("\n=== NUEVO EMPLEADO ===");
            System.out.print("Nombre: ");
            String nombre = scanner.nextLine();
            System.out.print("Contraseña: ");
            String contraseña = scanner.nextLine();
            
            String id = "E" + (empleados.size() + 1);
            empleados.add(new Empleado(nombre, id, encriptarContraseña(contraseña)));
            
            System.out.println("Empleado agregado con ID: " + id);
        }

        private void eliminarEmpleado(List<Empleado> empleados) {
            verEmpleados(empleados);
            System.out.print("\nID del empleado a eliminar: ");
            String id = scanner.nextLine();
            
            Iterator<Empleado> it = empleados.iterator();
            while (it.hasNext()) {
                Empleado e = it.next();
                if (e.getId().equals(id)) {
                    it.remove();
                    System.out.println("Empleado eliminado.");
                    return;
                }
            }
            
            System.out.println("Empleado no encontrado.");
        }

        private void modificarEmpleado(List<Empleado> empleados) {
            verEmpleados(empleados);
            System.out.print("\nID del empleado a modificar: ");
            String id = scanner.nextLine();
            
            for (Empleado e : empleados) {
                if (e.getId().equals(id)) {
                    System.out.print("Nuevo nombre (actual: " + e.getNombre() + "): ");
                    String nombre = scanner.nextLine();
                    System.out.print("Nueva contraseña: ");
                    String contraseña = scanner.nextLine();
                    
                    if (!nombre.isEmpty()) e.nombre = nombre;
                    if (!contraseña.isEmpty()) e.setContraseña(encriptarContraseña(contraseña));
                    
                    System.out.println("Empleado modificado.");
                    return;
                }
            }
            
            System.out.println("Empleado no encontrado.");
        }

        public void gestionarClientes(List<Cliente> clientes, List<Cuenta> cuentas) {
            while (true) {
                System.out.println("\n=== GESTIÓN DE CLIENTES ===");
                System.out.println("1) Ver todos los clientes");
                System.out.println("2) Buscar cliente");
                System.out.println("3) Bloquear cuenta");
                System.out.println("4) Volver");
                System.out.print("Opción: ");
                
                String op = scanner.nextLine();
                switch (op) {
                    case "1": verClientes(clientes); break;
                    case "2": buscarCliente(clientes); break;
                    case "3": bloquearCuenta(clientes); break;
                    case "4": return;
                    default: System.out.println("Opción inválida.");
                }
            }
        }

        private void verClientes(List<Cliente> clientes) {
            System.out.println("\n=== LISTA DE CLIENTES ===");
            for (Cliente c : clientes) {
                System.out.println("Cuenta: " + c.getNumeroCuenta() + " | Nombre: " + c.getNombre() + 
                                 " | Saldo: $" + c.saldo);
            }
        }

        private void buscarCliente(List<Cliente> clientes) {
            System.out.print("\nNúmero de cuenta o nombre: ");
            String busqueda = scanner.nextLine();
            
            for (Cliente c : clientes) {
                if (c.getNumeroCuenta().equals(busqueda) || c.getNombre().equalsIgnoreCase(busqueda)) {
                    System.out.println("\nInformación del cliente:");
                    System.out.println("Nombre: " + c.getNombre());
                    System.out.println("Número de cuenta: " + c.getNumeroCuenta());
                    System.out.println("Tipo de cuenta: " + c.tipoTarjeta);
                    System.out.println("Saldo actual: $" + c.saldo);
                    System.out.println("NIP: " + c.getNip());
                    return;
                }
            }
            
            System.out.println("Cliente no encontrado.");
        }

        private void bloquearCuenta(List<Cliente> clientes) {
            System.out.print("\nNúmero de cuenta a bloquear: ");
            String cuenta = scanner.nextLine();
            
            for (Cliente c : clientes) {
                if (c.getNumeroCuenta().equals(cuenta)) {
                    System.out.print("¿Está seguro de bloquear esta cuenta? (s/n): ");
                    if (scanner.nextLine().equalsIgnoreCase("s")) {
                        // En un sistema real, aquí se marcaría la cuenta como bloqueada
                        System.out.println("Cuenta bloqueada exitosamente.");
                    }
                    return;
                }
            }
            
            System.out.println("Cuenta no encontrada.");
        }

        public void generarReportes(List<Cliente> clientes, List<Empleado> empleados, 
                                  Map<String, Prestamo> prestamos, Map<String, List<Transaccion>> transacciones) {
            System.out.println("\n=== REPORTES ===");
            System.out.println("1) Reporte de clientes");
            System.out.println("2) Reporte de transacciones");
            System.out.println("3) Reporte de préstamos");
            System.out.println("4) Volver");
            System.out.print("Opción: ");
            
            String op = scanner.nextLine();
            switch (op) {
                case "1": 
                    System.out.println("\n=== REPORTE DE CLIENTES ===");
                    System.out.println("Total de clientes: " + clientes.size());
                    double totalDepositos = clientes.stream().mapToDouble(c -> c.saldo).sum();
                    System.out.println("Total de depósitos: $" + totalDepositos);
                    break;
                    
                case "2":
                    System.out.println("\n=== REPORTE DE TRANSACCIONES ===");
                    long totalTransacciones = transacciones.values().stream().mapToLong(List::size).sum();
                    System.out.println("Total de transacciones: " + totalTransacciones);
                    break;
                    
                case "3":
                    System.out.println("\n=== REPORTE DE PRÉSTAMOS ===");
                    System.out.println("Total de préstamos activos: " + prestamos.size());
                    double totalPrestamos = prestamos.values().stream().mapToDouble(Prestamo::getMonto).sum();
                    System.out.println("Monto total en préstamos: $" + totalPrestamos);
                    break;
                    
                case "4": return;
                default: System.out.println("Opción inválida.");
            }
        }
    }

    static class Gerente extends PersonaBanco {
        public Gerente(String nombre, String id) {
            super(nombre, id, "");
        }

        public void gestionarAdministradores(List<Administrador> admins) {
            while (true) {
                System.out.println("\n=== GESTIÓN DE ADMINISTRADORES ===");
                System.out.println("1) Ver administradores");
                System.out.println("2) Agregar administrador");
                System.out.println("3) Eliminar administrador");
                System.out.println("4) Modificar administrador");
                System.out.println("5) Volver");
                System.out.print("Opción: ");
                
                String op = scanner.nextLine();
                switch (op) {
                    case "1": verAdministradores(admins); break;
                    case "2": agregarAdministrador(admins); break;
                    case "3": eliminarAdministrador(admins); break;
                    case "4": modificarAdministrador(admins); break;
                    case "5": return;
                    default: System.out.println("Opción inválida.");
                }
            }
        }

        private void verAdministradores(List<Administrador> admins) {
            System.out.println("\n=== LISTA DE ADMINISTRADORES ===");
            for (Administrador a : admins) {
                System.out.println("ID: " + a.getId() + " | Nombre: " + a.getNombre());
            }
        }

        private void agregarAdministrador(List<Administrador> admins) {
            System.out.println("\n=== NUEVO ADMINISTRADOR ===");
            System.out.print("Nombre: ");
            String nombre = scanner.nextLine();
            System.out.print("Contraseña: ");
            String contraseña = scanner.nextLine();
            
            String id = "A" + (admins.size() + 1);
            admins.add(new Administrador(nombre, id, encriptarContraseña(contraseña)));
            
            System.out.println("Administrador agregado con ID: " + id);
        }

        private void eliminarAdministrador(List<Administrador> admins) {
            verAdministradores(admins);
            System.out.print("\nID del administrador a eliminar: ");
            String id = scanner.nextLine();
            
            Iterator<Administrador> it = admins.iterator();
            while (it.hasNext()) {
                Administrador a = it.next();
                if (a.getId().equals(id)) {
                    it.remove();
                    System.out.println("Administrador eliminado.");
                    return;
                }
            }
            
            System.out.println("Administrador no encontrado.");
        }

        private void modificarAdministrador(List<Administrador> admins) {
            verAdministradores(admins);
            System.out.print("\nID del administrador a modificar: ");
            String id = scanner.nextLine();
            
            for (Administrador a : admins) {
                if (a.getId().equals(id)) {
                    System.out.print("Nuevo nombre (actual: " + a.getNombre() + "): ");
                    String nombre = scanner.nextLine();
                    System.out.print("Nueva contraseña: ");
                    String contraseña = scanner.nextLine();
                    
                    if (!nombre.isEmpty()) a.nombre = nombre;
                    if (!contraseña.isEmpty()) a.setContraseña(encriptarContraseña(contraseña));
                    
                    System.out.println("Administrador modificado.");
                    return;
                }
            }
            
            System.out.println("Administrador no encontrado.");
        }

        public void gestionarEmpleados(List<Empleado> empleados) {
            while (true) {
                System.out.println("\n=== GESTIÓN DE EMPLEADOS ===");
                System.out.println("1) Ver todos los empleados");
                System.out.println("2) Agregar empleado");
                System.out.println("3) Eliminar empleado");
                System.out.println("4) Modificar empleado");
                System.out.println("5) Volver");
                System.out.print("Opción: ");
                
                String op = scanner.nextLine();
                switch (op) {
                    case "1": verEmpleados(empleados); break;
                    case "2": agregarEmpleado(empleados); break;
                    case "3": eliminarEmpleado(empleados); break;
                    case "4": modificarEmpleado(empleados); break;
                    case "5": return;
                    default: System.out.println("Opción inválida.");
                }
            }
        }

        private void verEmpleados(List<Empleado> empleados) {
            System.out.println("\n=== LISTA DE EMPLEADOS ===");
            for (Empleado e : empleados) {
                System.out.println("ID: " + e.getId() + " | Nombre: " + e.getNombre());
            }
        }

        private void agregarEmpleado(List<Empleado> empleados) {
            System.out.println("\n=== NUEVO EMPLEADO ===");
            System.out.print("Nombre: ");
            String nombre = scanner.nextLine();
            System.out.print("Contraseña: ");
            String contraseña = scanner.nextLine();
            
            String id = "E" + (empleados.size() + 1);
            empleados.add(new Empleado(nombre, id, encriptarContraseña(contraseña)));
            
            System.out.println("Empleado agregado con ID: " + id);
        }

        private void eliminarEmpleado(List<Empleado> empleados) {
            verEmpleados(empleados);
            System.out.print("\nID del empleado a eliminar: ");
            String id = scanner.nextLine();
            
            Iterator<Empleado> it = empleados.iterator();
            while (it.hasNext()) {
                Empleado e = it.next();
                if (e.getId().equals(id)) {
                    it.remove();
                    System.out.println("Empleado eliminado.");
                    return;
                }
            }
            
            System.out.println("Empleado no encontrado.");
        }

        private void modificarEmpleado(List<Empleado> empleados) {
            verEmpleados(empleados);
            System.out.print("\nID del empleado a modificar: ");
            String id = scanner.nextLine();
            
            for (Empleado e : empleados) {
                if (e.getId().equals(id)) {
                    System.out.print("Nuevo nombre (actual: " + e.getNombre() + "): ");
                    String nombre = scanner.nextLine();
                    System.out.print("Nueva contraseña: ");
                    String contraseña = scanner.nextLine();
                    
                    if (!nombre.isEmpty()) e.nombre = nombre;
                    if (!contraseña.isEmpty()) e.setContraseña(encriptarContraseña(contraseña));
                    
                    System.out.println("Empleado modificado.");
                    return;
                }
            }
            
            System.out.println("Empleado no encontrado.");
        }

        public void gestionarClientes(List<Cliente> clientes, List<Cuenta> cuentas) {
            while (true) {
                System.out.println("\n=== GESTIÓN DE CLIENTES ===");
                System.out.println("1) Ver todos los clientes");
                System.out.println("2) Buscar cliente");
                System.out.println("3) Bloquear cuenta");
                System.out.println("4) Eliminar cliente");
                System.out.println("5) Volver");
                System.out.print("Opción: ");
                
                String op = scanner.nextLine();
                switch (op) {
                    case "1": verClientes(clientes); break;
                    case "2": buscarCliente(clientes); break;
                    case "3": bloquearCuenta(clientes); break;
                    case "4": eliminarCliente(clientes, cuentas); break;
                    case "5": return;
                    default: System.out.println("Opción inválida.");
                }
            }
        }

        private void verClientes(List<Cliente> clientes) {
            System.out.println("\n=== LISTA DE CLIENTES ===");
            for (Cliente c : clientes) {
                System.out.println("Cuenta: " + c.getNumeroCuenta() + " | Nombre: " + c.getNombre() + 
                                 " | Saldo: $" + c.saldo);
            }
        }

        private void buscarCliente(List<Cliente> clientes) {
            System.out.print("\nNúmero de cuenta or nombre: ");
            String busqueda = scanner.nextLine();
            
            for (Cliente c : clientes) {
                if (c.getNumeroCuenta().equals(busqueda) || c.getNombre().equalsIgnoreCase(busqueda)) {
                    System.out.println("\nInformación del cliente:");
                    System.out.println("Nombre: " + c.getNombre());
                    System.out.println("Número de cuenta: " + c.getNumeroCuenta());
                    System.out.println("Tipo de cuenta: " + c.tipoTarjeta);
                    System.out.println("Saldo actual: $" + c.saldo);
                    System.out.println("NIP: " + c.getNip());
                    return;
                }
            }
            
            System.out.println("Cliente no encontrado.");
        }

        private void bloquearCuenta(List<Cliente> clientes) {
            System.out.print("\nNúmero de cuenta a bloquear: ");
            String cuenta = scanner.nextLine();
            
            for (Cliente c : clientes) {
                if (c.getNumeroCuenta().equals(cuenta)) {
                    System.out.print("¿Está seguro de bloquear esta cuenta? (s/n): ");
                    if (scanner.nextLine().equalsIgnoreCase("s")) {
                        // En un sistema real, aquí se marcaría la cuenta como bloqueada
                        System.out.println("Cuenta bloqueada exitosamente.");
                    }
                    return;
                }
            }
            
            System.out.println("Cuenta no encontrada.");
        }

        private void eliminarCliente(List<Cliente> clientes, List<Cuenta> cuentas) {
            System.out.print("\nNúmero de cuenta a eliminar: ");
            String cuenta = scanner.nextLine();
            
            Iterator<Cliente> it = clientes.iterator();
            while (it.hasNext()) {
                Cliente c = it.next();
                if (c.getNumeroCuenta().equals(cuenta)) {
                    System.out.print("¿Está seguro de eliminar este cliente? (s/n): ");
                    if (scanner.nextLine().equalsIgnoreCase("s")) {
                        it.remove();
                        
                        // Eliminar cuenta asociada
                        cuentas.removeIf(cu -> cu.getNumeroCuenta().equals(cuenta));
                        
                        System.out.println("Cliente eliminado exitosamente.");
                    }
                    return;
                }
            }
            
            System.out.println("Cliente no encontrado.");
        }

        public void generarReportesCompletos(List<Cliente> clientes, List<Empleado> empleados, 
                                           List<Administrador> admins, Map<String, Prestamo> prestamos, 
                                           Map<String, List<Transaccion>> transacciones) {
            System.out.println("\n=== REPORTES COMPLETOS ===");
            System.out.println("1) Reporte general");
            System.out.println("2) Reporte financiero");
            System.out.println("3) Reporte de personal");
            System.out.println("4) Volver");
            System.out.print("Opción: ");
            
            String op = scanner.nextLine();
            switch (op) {
                case "1": 
                    System.out.println("\n=== REPORTE GENERAL ===");
                    System.out.println("Total de clientes: " + clientes.size());
                    System.out.println("Total de empleados: " + empleados.size());
                    System.out.println("Total de administradores: " + admins.size());
                    System.out.println("Total de préstamos activos: " + prestamos.size());
                    break;
                    
                case "2":
                    System.out.println("\n=== REPORTE FINANCIERO ===");
                    double totalDepositos = clientes.stream().mapToDouble(c -> c.saldo).sum();
                    System.out.println("Total de depósitos: $" + totalDepositos);
                    
                    double totalPrestamos = prestamos.values().stream().mapToDouble(Prestamo::getMonto).sum();
                    System.out.println("Total de préstamos: $" + totalPrestamos);
                    
                    long totalTransacciones = transacciones.values().stream().mapToLong(List::size).sum();
                    System.out.println("Total de transacciones: " + totalTransacciones);
                    break;
                    
                case "3":
                    System.out.println("\n=== REPORTE DE PERSONAL ===");
                    System.out.println("Gerente: " + this.getNombre());
                    System.out.println("\nAdministradores:");
                    admins.forEach(a -> System.out.println("- " + a.getNombre()));
                    System.out.println("\nEmpleados:");
                    empleados.forEach(e -> System.out.println("- " + e.getNombre()));
                    break;
                    
                case "4": return;
                default: System.out.println("Opción inválida.");
            }
        }

        public void configurarParametrosBanco() {
            System.out.println("\n=== CONFIGURACIÓN DEL BANCO ===");
            System.out.println("1) Cambiar contraseña de gerente");
            System.out.println("2) Configurar tasas de interés");
            System.out.println("3) Volver");
            System.out.print("Opción: ");
            
            String op = scanner.nextLine();
            switch (op) {
                case "1":
                    System.out.print("Nueva contraseña: ");
                    String nuevaContraseña = scanner.nextLine();
                    this.setContraseña(encriptarContraseña(nuevaContraseña));
                    System.out.println("Contraseña actualizada.");
                    break;
                    
                case "2":
                    System.out.println("\nConfiguración de tasas de interés:");
                    System.out.println("Esta funcionalidad estaría conectada a la base de datos");
                    System.out.println("en un sistema real para modificar las tasas.");
                    break;
                    
                case "3": return;
                default: System.out.println("Opción inválida.");
            }
        }
    }

    // Clases de modelo adicionales
    static class Cuenta {
        private String numeroCuenta;
        private double saldo;
        private String tipo;
        private LocalDate fechaCreacion;

        public Cuenta(String numeroCuenta, double saldo, String tipo) {
            this.numeroCuenta = numeroCuenta;
            this.saldo = saldo;
            this.tipo = tipo;
            this.fechaCreacion = LocalDate.now();
        }

        public String getNumeroCuenta() { return numeroCuenta; }
        public double getSaldo() { return saldo; }
        public String getTipo() { return tipo; }
        public LocalDate getFechaCreacion() { return fechaCreacion; }

        @Override
        public String toString() {
            return "Cuenta: " + numeroCuenta + " | Tipo: " + tipo + " | Saldo: $" + saldo;
        }
    }

    static class Prestamo {
        private double monto;
        private double interes;
        private int plazoMeses;
        private String cuentaAsociada;
        private LocalDate fechaAprobacion;
        private boolean aprobado;

        public Prestamo(double monto, double interes, int plazoMeses, String cuentaAsociada) {
            this.monto = monto;
            this.interes = interes;
            this.plazoMeses = plazoMeses;
            this.cuentaAsociada = cuentaAsociada;
            this.fechaAprobacion = LocalDate.now();
            this.aprobado = true;
        }

        public double getMonto() { return monto; }
        public double getInteres() { return interes; }
        public int getPlazoMeses() { return plazoMeses; }
        public String getCuentaAsociada() { return cuentaAsociada; }
        public LocalDate getFechaAprobacion() { return fechaAprobacion; }
        public boolean isAprobado() { return aprobado; }

        @Override
        public String toString() {
            return "Préstamo: $" + monto + " | Interés: " + (interes * 100) + "% | " +
                   "Plazo: " + plazoMeses + " meses | Fecha: " + fechaAprobacion;
        }
    }

    static class Transaccion {
        private String tipo;
        private double monto;
        private String cuentaOrigen;
        private String cuentaDestino;
        private LocalDate fecha;

        public Transaccion(String tipo, double monto, String cuentaOrigen, String cuentaDestino) {
            this.tipo = tipo;
            this.monto = monto;
            this.cuentaOrigen = cuentaOrigen;
            this.cuentaDestino = cuentaDestino;
            this.fecha = LocalDate.now();
        }

@Override
public String toString() {
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    return fecha.format(formatter) + " | " + tipo + " | $" + monto + 
          " | Origen: " + cuentaOrigen + " | Destino: " + cuentaDestino;
}
    }

    static class Inversion {
        private double monto;
        private double interes;
        private int dias;
        private LocalDate fechaInicio;

        public Inversion(double monto, double interes, int dias) {
            this.monto = monto;
            this.interes = interes;
            this.dias = dias;
            this.fechaInicio = LocalDate.now();
        }

        public double getMonto() { return monto; }
        public double getInteres() { return interes; }
        public int getDias() { return dias; }
        public LocalDate getFechaInicio() { return fechaInicio; }

        @Override
        public String toString() {
            return "Inversión: $" + monto + " | Interés: " + (interes * 100) + "% | " +
                   "Plazo: " + dias + " días | Fecha inicio: " + fechaInicio;
        }
    }
}