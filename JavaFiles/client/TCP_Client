package client;

import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.file.*;
import java.util.Scanner;

public class TCP_Client {

    public static Return_c readFile() throws IOException {
        String port1 = "";
        String port2 = "";
        String hostname1 = "";
        String hostname2 = "";

        Scanner sc;

        try {
            File file = new File("config_client.txt");
            sc = new Scanner(file);
            port1 = sc.nextLine();
            port2 = sc.nextLine();
            hostname1 = sc.nextLine();
            hostname2 = sc.nextLine();
            sc.close();

        } catch (FileNotFoundException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }

        Scanner sc_ = new Scanner(System.in);
        String op = "";
        while (op.equals("")) {
            System.out.print("Do you want to configure addresses and ports of servers? (y/n) ");
            try {
                op = sc_.nextLine();
                if (!(op.equalsIgnoreCase("y")) && !(op.equalsIgnoreCase("n"))) {
                    op = "";
                    System.out.println("Invalid option!");
                }
                System.out.print("\n");
            } catch (java.util.InputMismatchException e) {
                System.out.println("Invalid option!");
                op = "";
            }
        }

        if (op.equalsIgnoreCase("y")) {
            int op_ = -1;
            while (op_ == -1) {
                System.out.println("Which server do you want to change port and address?");
                System.out.println("\t1-Primary");
                System.out.println("\t2-Secondary");
                System.out.print("\nOption: ");
                try {
                    op_ = sc_.nextInt();
                    if (op_ < 0 || op_ > 2) {
                        op_ = -1;
                        System.out.println("Invalid option!");
                    }
                    System.out.print("\n");
                } catch (java.util.InputMismatchException e) {
                    System.out.println("Invalid option!");
                    op_ = -1;
                }
            }

            System.out.print("Adress: ");
            if (op_ == 1) {
                hostname1 = "";
                while(hostname1.isEmpty()) hostname1 = sc_.nextLine();
            }
            else {
                hostname2 = "";
                while(hostname2.isEmpty()) hostname2 = sc_.nextLine();
            }
            System.out.print("Port: ");
            if (op_ == 1) {
                port1 = "";
                while(port1.isEmpty()) port1 = sc_.nextLine();
            }
            else {
                port2 = "";
                while(port2.isEmpty()) port2 = sc_.nextLine();
            }

            String res = port1 + "\n" + port2 + "\n" + hostname1 + "\n" + hostname2;


            String p = System.getProperty("user.dir") + "\\config_client.txt";

            Path fileName = Path.of(p);
            Files.writeString(fileName, res);


        }

        int serversocket1 = Integer.parseInt(port1);
        int serversocket2 = Integer.parseInt(port2);

        return new Return_c(port1, hostname1, hostname2, serversocket1, port2, serversocket2);
    }

    public static int funcs_cl(Socket s, String hostname1, int serversocket1, String hostname2, int serversocket2) {
        System.out.println("SOCKET=" + s);

        try {
            // 2o passo
            DataInputStream in = new DataInputStream(s.getInputStream());
            DataOutputStream out = new DataOutputStream(s.getOutputStream());

            int l = 0;
            String username = "";
            String password = "";
            String directory_server = "";
            String directory_local = System.getProperty("user.dir");
            Scanner sc = new Scanner(System.in);

            while (l == 0) {
                System.out.print("Username: ");
                username = sc.nextLine();
                System.out.print("Password: ");
                password = sc.nextLine();

                // WRITE INTO THE SOCKET
                out.writeUTF(username);
                out.writeUTF(password);

                directory_server = in.readUTF();

                if (directory_server.equals("Invalid user")) {
                    System.out.println("Invalid User!\n");
                }
                else {
                    l = 1;
                    System.out.println("Welcome " + username + "!\n");
                    System.out.println("Current directory:" + directory_server);
                }
            }

            while (true) {

                int option = -1;

                System.out.println("---------------------------- MENU ----------------------------");
                System.out.println("1 - Change user password");
                System.out.println("2 - List files at the current directory of the server");
                System.out.println("3 - Change directory of the server");
                System.out.println("4 - List files at the current local directory");
                System.out.println("5 - Change local directory");
                System.out.println("6 - Download file from server");
                System.out.println("7 - Upload file to server");
                System.out.println("0 - Exit");

                while (option == -1) {
                    System.out.print("\nOption: ");
                    try {
                        sc = new Scanner(System.in);
                        option = sc.nextInt();
                        if (option < 0 || option > 7) {
                            option = -1;
                            System.out.println("Invalid option!");
                        }
                        System.out.print("\n");
                    } catch (java.util.InputMismatchException e) {
                        System.out.println("Invalid option!");
                        option = -1;
                    }
                }

                if (option > 1) option++;

                out.writeUTF(Integer.toString(option));

                switch (option) {
                    case 1 -> {
                        System.out.print("Confirm your password: ");
                        String current_p = "";
                        while (current_p.isEmpty()) current_p = sc.nextLine();
                        out.writeUTF(current_p);

                        if (in.readUTF().equals("Continue")) {
                            String new_p = "";
                            System.out.print("New password: ");
                            while (new_p.isEmpty()) new_p = sc.nextLine();
                            out.writeUTF(new_p);
                            if (in.readUTF().equals("Success")) {
                                System.out.println("Password changed successfully.\n");
                                return 1;
                            } else {
                                System.out.println("Something got wrong with your password change :(\n");
                            }
                        }
                        else System.out.println("Wrong password.");

                    }

                    case 3 -> {
                        String res = in.readUTF();
                        while (!(res.equals("file.getFileName().toString()"))) {
                            System.out.println(res);
                            res = in.readUTF();
                        }
                    }

                    case 4 -> {
                        String res = in.readUTF();
                        if (res.equals("No options available")) {
                            System.out.println("No options available to change directory.\n");
                        }

                        else if (res.equals("Go back")){
                            String op = "";
                            while (op.equals("")) {
                                System.out.print("You can only move back. Do you want? (y/n)");
                                try {
                                    sc = new Scanner(System.in);
                                    op = sc.nextLine();
                                    if (!(op.equalsIgnoreCase("y")) && !(op.equalsIgnoreCase("n"))) {
                                        op = "";
                                        System.out.println("Invalid option!");
                                    }
                                    System.out.print("\n");
                                } catch (java.util.InputMismatchException e) {
                                    System.out.println("Invalid option!");
                                    op = "";
                                }
                            }
                            out.writeUTF(op.toLowerCase());
                            if (op.toLowerCase().equals("y")) {
                                String new_d = in.readUTF();
                                System.out.println("Directory changed to: " + new_d);
                            }
                        }

                        else if (res.equals("Front") || res.equals("Both")) {

                            String possible = in.readUTF();
                            String n = in.readUTF();
                            int n_ = Integer.parseInt(n);

                            int op = -1;
                            while (op == -1) {
                                System.out.println("Possibilities to move:");
                                System.out.println(possible);
                                System.out.println("\tOr you can just stay where you are.");
                                System.out.println("\t\t0 - Dont change");
                                System.out.print("\nOption: ");
                                try {
                                    sc = new Scanner(System.in);
                                    op = sc.nextInt();
                                    if (op < 0 || op > n_) {
                                        op = -1;
                                        System.out.println("Invalid option!");
                                    }
                                    System.out.print("\n");
                                } catch (java.util.InputMismatchException e) {
                                    System.out.println("Invalid option!");
                                    op = -1;
                                }
                            }

                            out.writeUTF(String.valueOf(op));

                            if (op != 0) {
                                String new_d = in.readUTF();
                                System.out.println("Directory changed to: " + new_d);
                            }
                        }

                    }

                    case 5 -> {
                        Path path = Paths.get(directory_local);
                        try (DirectoryStream<Path> stream = Files.newDirectoryStream(path)) {
                            for (Path file : stream) {
                                System.out.println(file.getFileName().toString());
                            }
                        } catch (IOException | DirectoryIteratorException x) {
                            System.err.println(x);
                        }
                    }

                    case 6 -> {

                        File[] directories_f = new File(directory_local).listFiles(File::isDirectory);

                        String[] dir_b = directory_local.split("\\\\");

                        if ((directories_f.length == 0) && (dir_b.length == 1)) {
                            System.out.println("No options available.");
                        }

                        else if ((directories_f.length == 0) && (dir_b.length > 1)) {
                            String op = "";
                            while (op.equals("")) {
                                System.out.print("You can only move back. Do you want? (y/n)");
                                try {
                                    sc = new Scanner(System.in);
                                    op = sc.nextLine();
                                    if (!(op.equalsIgnoreCase("y")) && !(op.equalsIgnoreCase("n"))) {
                                        op = "";
                                        System.out.println("Invalid option!");
                                    }
                                    System.out.println("");
                                } catch (java.util.InputMismatchException e) {
                                    System.out.println("Invalid option!");
                                    op = "";
                                }
                            }

                            if (op.equals("y")) {
                                String[] arr_dir = directory_local.split("\\\\");
                                int n = arr_dir.length;
                                StringBuilder new_dir = new StringBuilder();
                                for (int i=0; i<n-1; i++) {
                                    new_dir.append(arr_dir[i]);
                                    if (i < n-2) new_dir.append("\\");
                                }
                                directory_local = new_dir.toString();
                                System.out.println("Current local directory: " + directory_local);
                            }
                        }

                        else if ((directories_f.length != 0) && (dir_b.length == 1)) {

                            int op = -1;
                            while (op == -1) {
                                System.out.println("You are in your home directory. You can only move to the following subdirectories:");
                                for (int i = 0; i < directories_f.length; i++) {
                                    System.out.println("\t" + (i+1) + "-" + directories_f[i].getName());
                                }
                                System.out.println("Or you can just stay where you are.");
                                System.out.println("\t0 - Dont change");
                                System.out.print("\nOption: ");
                                try {
                                    sc = new Scanner(System.in);
                                    op = sc.nextInt();
                                    if (op < 0 || op > directories_f.length) {
                                        op = -1;
                                        System.out.println("Invalid option!");
                                    }
                                    System.out.print("\n");
                                } catch (java.util.InputMismatchException e) {
                                    System.out.println("Invalid option!");
                                    op = -1;
                                }
                            }

                            if (op != 0) {
                                directory_local = directory_local + directories_f[op - 1].getName();
                                System.out.println("Current local directory: " + directory_local);
                            }
                        }

                        else if ((directories_f.length != 0) && (dir_b.length > 1)) {

                            int op = -1;
                            while (op == -1) {
                                System.out.println("You are in a subdirectory. You can move to the following subdirectories:");
                                for (int i = 0; i < directories_f.length; i++) {
                                    System.out.println("\t" + (i+1) + "-" + directories_f[i].getName());
                                    if (i == directories_f.length -1) {
                                        System.out.println("You can also go back:");
                                        System.out.println("\t" + (i+2) + "-Go back");
                                        System.out.println("Or stay where you are");
                                        System.out.println("\t0-Dont move");
                                    }
                                }
                                System.out.print("\nOption: ");
                                try {
                                    sc = new Scanner(System.in);
                                    op = sc.nextInt();
                                    if (op < 0 || op > directories_f.length + 1) {
                                        op = -1;
                                        System.out.println("Invalid option!");
                                    }
                                    System.out.print("\n");
                                } catch (java.util.InputMismatchException e) {
                                    System.out.println("Invalid option!");
                                    op = -1;
                                }
                            }

                            if (op != 0) {
                                if (op == directories_f.length + 1) {
                                    String[] arr_dir = directory_local.split("\\\\");
                                    int n = arr_dir.length;
                                    StringBuilder new_dir = new StringBuilder();
                                    for (int i=0; i<n-1; i++) {
                                        new_dir.append(arr_dir[i]);
                                        if (i < n-2) new_dir.append("\\");
                                    }
                                    directory_local = new_dir.toString();
                                }
                                else {
                                    directory_local = directory_local + "\\" + directories_f[op - 1].getName();
                                }
                                System.out.println("Current local directory: " + directory_local);
                            }
                        }
                    }

                    case 7, 8 -> files(hostname1, serversocket1, username, directory_local, option);

                    case 0 -> {
                        return 0;
                    }
                }
            }
        }

        catch(EOFException e) {
            System.out.println("EOF:" + e);
        } catch(IOException e) {
            System.out.println("IO:" + e);
            client(hostname1, serversocket1, hostname2, serversocket2);
        }

        return 0;
    }

    public static int client(String hostname1, int serversocket1, String hostname2, int serversocket2) {
        // 1o passo - criar socket
        int res = 0;

        try (Socket s = new Socket(hostname1, serversocket1)) {
            res = funcs_cl(s, hostname1, serversocket1, hostname2, serversocket2);

        } catch (UnknownHostException e) {
            try (Socket s = new Socket(hostname2, serversocket2)) {
                res = funcs_cl(s, hostname2, serversocket2, hostname1, serversocket1);

            } catch (IOException ex) {
                ex.printStackTrace();
            }
        } catch (EOFException e) {
            try (Socket s = new Socket(hostname2, serversocket2)) {
                res = funcs_cl(s, hostname2, serversocket2, hostname1, serversocket1);

            } catch (IOException ex) {
                ex.printStackTrace();
            }
        } catch (IOException e) {
            try (Socket s = new Socket(hostname2, serversocket2)) {
                res = funcs_cl(s, hostname2, serversocket2, hostname1, serversocket1);

            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        return res;
    }

    private static void files(String hostname, int serversocket, String username, String directory_local, int option) {
        try (Socket sf = new Socket(hostname, serversocket + 1)) {
            System.out.println("FILE SOCKET=" + sf);

            // 2o passo
            DataInputStream inf = new DataInputStream(sf.getInputStream());
            DataOutputStream outf = new DataOutputStream(sf.getOutputStream());

            outf.writeUTF(username);

            if (option == 7) {
                if (inf.readUTF().equals("Nothing")) {
                    System.out.println("There are no files in this directory.");
                }
                else {
                    String poss = inf.readUTF();
                    int n_ = Integer.parseInt(inf.readUTF());

                    int op = -1;
                    while (op == -1) {
                        System.out.println("Which file do you want to download?");
                        System.out.println(poss);
                        System.out.println("\t0 - None");
                        System.out.print("\nOption: ");
                        try {
                            Scanner sc = new Scanner(System.in);
                            op = sc.nextInt();
                            if (op < 0 || op > n_) {
                                op = -1;
                                System.out.println("Invalid option!");
                            }
                            System.out.print("\n");
                        } catch (java.util.InputMismatchException e) {
                            System.out.println("Invalid option!");
                            op = -1;
                        }
                    }

                    outf.writeUTF(String.valueOf(op));

                    if (op != 0) {
                        String name_f = inf.readUTF();

                        FileOutputStream fos = new FileOutputStream(directory_local + "/" + name_f);

                        int size_piece = 0;
                        long totalsize = inf.readLong();

                        byte[] buffer = new byte[1024]; // size of each piece received - 1KB
                        while (totalsize > 0 && (size_piece = inf.read(buffer, 0, (int)Math.min(buffer.length, totalsize))) != -1) {
                            fos.write(buffer,0,size_piece);
                            totalsize -= size_piece;
                        }

                        fos.close();

                        System.out.println("File downloaded.");
                    }
                }
            }

            else {
                File[] files = new File(directory_local).listFiles(File::isFile);

                if (files.length == 0) {
                    outf.writeUTF("Nothing");
                    System.out.println("There are no files in this directory to upload.");
                }
                else {
                    int op = -1;

                    while (op == -1) {
                        System.out.println("Which file do you want to upload?");
                        for (int i=0; i<files.length; i++) {
                            System.out.println("\t" + (i+1) + "-" + files[i].getName());
                        }
                        System.out.println("\t0 - None");
                        System.out.print("\nOption: ");
                        try {
                            Scanner sc = new Scanner(System.in);
                            op = sc.nextInt();
                            if (op < 0 || op > files.length) {
                                op = -1;
                                System.out.println("Invalid option!");
                            }
                            System.out.print("\n");
                        } catch (java.util.InputMismatchException e) {
                            System.out.println("Invalid option!");
                            op = -1;
                        }
                    }

                    outf.writeUTF(String.valueOf(op));

                    if (op != 0) {
                        outf.writeUTF(files[op-1].getName());

                        // read the file and make it ready to send
                        FileInputStream fis = new FileInputStream(files[op-1]);
                        outf.writeLong(files[op-1].length());

                        byte[] buffer = new byte[1024]; // size of each piece - 1KB
                        while ((fis.read(buffer))!=-1){
                            outf.write(buffer,0,buffer.length);
                            outf.flush();   // to avoid trash in outf
                        }
                        fis.close();

                        System.out.println(inf.readUTF());
                    }
                }
            }



        }

        catch (UnknownHostException e) {
            System.out.println("Sock:" + e.getMessage());
        } catch (EOFException e) {
            System.out.println("EOF:" + e.getMessage());
        } catch (IOException e) {
            System.out.println("IO:" + e.getMessage());
        }
    }


    private static void update_add(String addr, String port) throws IOException {
        StringBuilder res = new StringBuilder();
        res.append(port).append("\n").append(addr);

        Path fileName = Path.of(System.getProperty("user.dir") + "/config_client.txt");
        Files.writeString(fileName, res);
    }

    public static void main(String[] args) throws IOException {
        int res = 1;

        Return_c ret = new Return_c("", "", "", 0, "", 0);

        while (res == 1) {
            ret = readFile();
            res = client(ret.hostname1, ret.serversocket1, ret.hostname2, ret.serversocket2);
        }

    }

}
