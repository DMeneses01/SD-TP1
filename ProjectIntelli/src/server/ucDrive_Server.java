// TCPServer2.java: Multithreaded server
package server;

import java.net.*;
import java.nio.file.DirectoryIteratorException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.io.*;
import java.util.*;

import static server.ucDrive_Server.clients;

// Para comunicar com os clientes
public class ucDrive_Server {

    public static ArrayList<Client_Class> clients = new ArrayList<>();

    public static void main(String[] args) throws UnknownHostException {

        String thisTCPport = "";
        String thisUDPport = "";
        String otheraddress = "";
        String otherUDPport = "";
        String server_home = "";
        String thisfilePort = "";
        String otherfilePort = "";
        int n_pings = 0;
        int time_pings = 0;

        try {
            System.out.println("Server on.");
            String p = System.getProperty("user.dir") + "\\config_server1.txt";
            File file = new File(p);
            Scanner sc = new Scanner(file);

            sc.nextLine(); //thisaddress
            thisTCPport = sc.nextLine();
            thisUDPport = sc.nextLine();
            thisfilePort = sc.nextLine();
            server_home = sc.nextLine();
            otheraddress = sc.nextLine();
            sc.nextLine(); //otherTCPport
            otherUDPport = sc.nextLine();
            otherfilePort = sc.nextLine();
            sc.nextLine(); //otherserverhome
            n_pings = Integer.parseInt(sc.nextLine());
            time_pings = Integer.parseInt(sc.nextLine());

            sc.close();

        } catch (FileNotFoundException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }

        int serverPort = Integer.parseInt(thisTCPport);
        int UDPPort = Integer.parseInt(thisUDPport);
        int filePort = Integer.parseInt(thisfilePort);


        while (true) {
            try (DatagramSocket aSocket = new DatagramSocket(UDPPort)) {

                System.out.println("PRIMARY");

                try {
                    Scanner sc = new Scanner(new File(System.getProperty("user.dir") + "\\clients1.txt"));
                    String[] c;
                    String line;

                    while (sc.hasNextLine()) {
                        line = sc.nextLine();
                        c = line.split("/", 7);

                        if (c.length == 7) {
                            clients.add(new Client_Class(c[0], c[1], server_home + "\\" + c[6], c[2], c[3], c[4], c[5]));
                        } else {
                            clients.add(new Client_Class(c[0], c[1], server_home + "\\" + c[0], c[2], c[3], c[4], c[5]));
                        }
                    }
                    sc.close();

                } catch (FileNotFoundException e) {
                    System.out.println("An error occurred.");
                    e.printStackTrace();
                }

                try (ServerSocket listenSocket = new ServerSocket(serverPort)) {

                    //try (DatagramSocket fileSocket = new DatagramSocket(filePort)) {

                    System.out.println("Socket Datagram à escuta no porto " + UDPPort);
                    new server.UDPConnection(aSocket);

                    while (true) {
                        Socket clientSocket = listenSocket.accept(); // BLOQUEANTE
                        System.out.println("CLIENT_SOCKET (created at accept())=" + clientSocket);
                        new server.Connection(clientSocket, aSocket, otheraddress, otherUDPport);
                    }
                    //}

                } catch (IOException e) {
                    System.out.println("Listen:" + e.getMessage());
                }

            } catch (SocketException e) {
                System.out.println("SECONDARY");

                try (DatagramSocket dsf = new DatagramSocket(Integer.parseInt(otherfilePort))) {
                    new server.FileUDPSecondary(otheraddress, Integer.parseInt(otherfilePort), dsf);

                    int count = 0;

                    InetAddress ia = InetAddress.getByName(otheraddress);
                    try (DatagramSocket ds = new DatagramSocket()) {
                        int timeout = 1000;
                        int lost = 0;
                        ds.setSoTimeout(timeout);
                        while (lost < n_pings) {
                            try {
                                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                                DataOutputStream dos = new DataOutputStream(baos);
                                dos.writeInt(count++);
                                byte[] buf = baos.toByteArray();

                                DatagramPacket dp = new DatagramPacket(buf, buf.length, ia, Integer.parseInt(otherUDPport));
                                ds.send(dp);

                                byte[] rbuf = new byte[1024];
                                DatagramPacket dr = new DatagramPacket(rbuf, rbuf.length);

                                ds.receive(dr);
                                lost = 0;
                                ByteArrayInputStream bais = new ByteArrayInputStream(rbuf, 0, dr.getLength());
                                DataInputStream dis = new DataInputStream(bais);
                                int n = dis.readInt();
                                System.out.println("Got: " + n + ".");
                            } catch (SocketTimeoutException ste) {
                                lost++;
                                System.out.println("Failed heartbeats: " + lost);
                            } catch (IOException ex) {
                                ex.printStackTrace();
                            }
                            Thread.sleep(time_pings);
                        }
                        dsf.close();
                    } catch (SocketException | InterruptedException ex) {
                        ex.printStackTrace();
                    }
                } catch (SocketException ex) {
                    ex.printStackTrace();
                }


                System.out.println("Socket: " + e.getMessage());
            }
        }

    }
}


class FileUDPSecondary extends Thread {
    String oaddress;
    int oport;
    DatagramSocket ds;

    public FileUDPSecondary(String otheraddress, int otherport, DatagramSocket dsr) {
        oaddress = otheraddress;
        oport = otherport;
        ds = dsr;
        this.start();
    }

    //=============================
    public synchronized void run() {

        while (!(ds.isClosed())) {
            try {
                byte[] rbuf = new byte[1024];
                DatagramPacket dr = new DatagramPacket(rbuf, rbuf.length);
                if(!(ds.isClosed())) {
                    ds.receive(dr);
                    String dis = new String(dr.getData(), 0, dr.getLength());

                    StringBuilder newp = new StringBuilder();
                    String[] arrays = dis.split("\\\\", 0);
                    for (int i=0; i<arrays.length; i++) {
                        if (i!=0)   newp.append("\\");
                        if (arrays[i].equals("server2")) {
                            newp.append("server");
                        }
                        else if (arrays[i].equals("clients2.txt")) {
                            newp.append("clients1.txt");
                        }
                        else {
                            newp.append(arrays[i]);
                        }
                    }

                    System.out.println(dis);
                    System.out.println("Path: " + newp);

                    ds.receive(dr);
                    String dis1 = new String(dr.getData(), 0, dr.getLength());
                    long size = Long.parseLong(dis1);
                    System.out.println("Size: " + size);


                    FileOutputStream fos = new FileOutputStream(String.valueOf(newp));

                    ds.setSoTimeout(1000);

                    int quant = 0;

                    byte[] buffer = new byte[1024];
                    DatagramPacket drr = new DatagramPacket(buffer, buffer.length);
                    while (size > 0) {
                        try {
                            ds.receive(drr);
                            byte[] received = drr.getData();

                            if (size > 1023)     quant = 1024;
                            else quant = (int) size;

                            byte[] piecefile = new byte[quant];

                            System.arraycopy(received, 0, piecefile, 0, quant);

                            fos.write(piecefile, 0, quant);
                            size -= quant;


                            String res = "OK";
                            byte[] buf1 = res.getBytes();
                            DatagramPacket dp = new DatagramPacket(buf1, buf1.length, drr.getAddress(), drr.getPort());
                            ds.send(dp);
                        }

                        catch (SocketException e1) {
                            System.out.println("FALHOU");
                            String res = "NOT OK";
                            byte[] buf1 = res.getBytes();
                            DatagramPacket dp = new DatagramPacket(buf1, buf1.length, drr.getAddress(), drr.getPort());
                            ds.send(dp);
                        }

                    }

                    System.out.println(size);

                    fos.close();


                }
            } catch (IOException ignored) {
            }
        }
    }
}


class UDPConnection extends Thread {
    DatagramSocket UDPSocket;

    public UDPConnection(DatagramSocket aSocket) {
        UDPSocket = aSocket;
        this.start();
    }

    //=============================
    public void run() {
        while (true) {
            byte[] buf = new byte[1024];
            try {
                DatagramPacket dp = new DatagramPacket(buf, buf.length);

                UDPSocket.receive(dp);

                ByteArrayInputStream bais = new ByteArrayInputStream(buf, 0, dp.getLength());
                DataInputStream dis = new DataInputStream(bais);
                int count = dis.readInt();

                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                DataOutputStream dos = new DataOutputStream(baos);
                dos.writeInt(count);
                byte[] resp = baos.toByteArray();
                DatagramPacket dpresp = new DatagramPacket(resp, resp.length, dp.getAddress(), dp.getPort());
                UDPSocket.send(dpresp);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}

class Connection extends Thread {
    DataInputStream in;
    DataOutputStream out;
    Socket clientSocket;
    DatagramSocket UDPSocket;
    String oaddress;
    int oport;
    boolean sem_file = false;

    public synchronized void semfWait() throws InterruptedException {
        while (sem_file) {
            wait();
        }
        sem_file = true;
    }

    public synchronized void semfNotify() {
        sem_file = false;
    }


    public Connection(Socket aClientSocket, DatagramSocket aSocket, String otheraddress, String otherport) {
        try {
            clientSocket = aClientSocket;
            UDPSocket = aSocket;
            oaddress = otheraddress;
            oport = Integer.parseInt(otherport);
            in = new DataInputStream(clientSocket.getInputStream());
            out = new DataOutputStream(clientSocket.getOutputStream());
            this.start();
        } catch (IOException e) {
            System.out.println("Connection:" + e.getMessage());
        }
    }

    //=============================
    public void run() {

        String thisTCPport = "";
        String server_home = "";
        String thisfilePort = "";
        String otherfilePort = "";
        String otheraddress = "";

        try {
            System.out.println("Server2 on.");
            String p = System.getProperty("user.dir") + "\\config_server1.txt";
            File file = new File(p);
            Scanner sc = new Scanner(file);

            sc.nextLine(); //thisaddress
            thisTCPport = sc.nextLine();
            sc.nextLine(); //thisUDPport
            thisfilePort = sc.nextLine();
            server_home = sc.nextLine();
            otheraddress = sc.nextLine(); //otheraddress
            sc.nextLine(); //otherTCPport
            sc.nextLine(); //otherUDPport
            otherfilePort = sc.nextLine();
            sc.close();

        } catch (FileNotFoundException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }

        try {

            int logged = 0;
            String username = "";
            String password = "";
            Client_Class client = new Client_Class("", "", "", "", "", "", "");

            while (logged == 0) {
                username = in.readUTF();
                password = in.readUTF();

                for (Client_Class c : clients) {
                    if ((c.username.equals(username)) && (c.password.equals(password))) {
                        client = c;
                        out.writeUTF(c.path);
                        System.out.println("CLient " + username + " logged.\n");
                        logged = 1;
                        break;
                    }
                }

                if (logged == 0)
                    out.writeUTF("Invalid user");
            }


            while (true) {
                String option = in.readUTF();

                switch (option) {
                    case "1" -> {
                        int is = 0;
                        String cur_p = in.readUTF();
                        for (Client_Class c : clients) {
                            if ((c.username.equals(username)) && (c.password.equals(cur_p))) {
                                is = 1;
                                out.writeUTF("Continue");
                                break;
                            }
                        }

                        if (is == 1) {
                            String new_p = in.readUTF();

                            for (Client_Class c : clients) {
                                if (c.username.equals(username)) {
                                    c.password = new_p;
                                    break;
                                }
                            }

                            semfWait();
                            update_clients(otheraddress, Integer.parseInt(otherfilePort), server_home);
                            semfNotify();
                            out.writeUTF("Success");
                        } else out.writeUTF("Wrong");
                    }

                    case "3" -> {
                        String dir = client.path;
                        Path path = Paths.get(dir);
                        try (DirectoryStream<Path> stream = Files.newDirectoryStream(path)) {
                            for (Path file : stream) {
                                out.writeUTF(file.getFileName().toString());
                            }
                            out.writeUTF("file.getFileName().toString()");
                        } catch (IOException | DirectoryIteratorException x) {
                            System.err.println(x);
                        }
                    }

                    case "4" -> {
                        String dir = client.path;

                        File[] directories = new File(dir).listFiles(File::isDirectory);

                        System.out.println(dir);
                        System.out.println(server_home + "\\" + username);


                        if ((directories.length == 0) && (dir.equals(server_home + "\\" + username))) {
                            out.writeUTF("No options available");
                        } else if ((directories.length == 0) && (!(dir.equals(server_home + "\\" + username)))) {
                            out.writeUTF("Go back");
                            String op = in.readUTF();
                            if (op.equals("y")) {
                                String[] arr_dir = client.path.split("\\\\", 0);
                                int n = arr_dir.length;
                                StringBuilder new_dir = new StringBuilder();
                                for (int i=0; i<n-1; i++) {
                                    new_dir.append(arr_dir[i]);
                                    if (i < n-2) new_dir.append("\\");
                                }

                                for (Client_Class c : clients) {
                                    if (c.username.equals(username)) {
                                        c.path = new_dir.toString();
                                        client.path = c.path;
                                        break;
                                    }
                                }

                                out.writeUTF(new_dir.toString());
                                semfWait();
                                update_clients(otheraddress, Integer.parseInt(otherfilePort), server_home);
                                semfNotify();
                            }
                        } else if ((directories.length != 0) && (dir.equals(server_home + "\\" + username))) {
                            out.writeUTF("Front");
                            StringBuilder possible = new StringBuilder();
                            possible.append("\tYou are in your home directory. You can only move to the following subdirectories:\n");
                            for (int i = 0; i < directories.length; i++) {
                                possible.append("\t\t").append(i + 1).append("-").append(directories[i].getName());
                                if (i < directories.length - 1)
                                    possible.append("\n");
                            }
                            out.writeUTF(possible.toString());
                            String n = String.valueOf(directories.length);
                            out.writeUTF(n);

                            String op = in.readUTF();
                            int op_ = Integer.parseInt(op);

                            if (op_ != 0) {

                                for (Client_Class c : clients) {
                                    if (c.username.equals(username)) {
                                        c.path = c.path + "\\" + directories[op_ - 1].getName();
                                        client.path = c.path;
                                        out.writeUTF(c.path);
                                        break;
                                    }
                                }

                                semfWait();
                                update_clients(otheraddress, Integer.parseInt(otherfilePort), server_home);
                                semfNotify();
                            }
                        } else if ((directories.length != 0) && (!dir.equals(server_home + "\\" + username))) {
                            out.writeUTF("Both");

                            StringBuilder possible = new StringBuilder();
                            possible.append("\tYou are in a subdirectory of your home directory. You can move to the following subdirectories:\n");
                            for (int i = 0; i < directories.length; i++) {
                                possible.append("\t\t").append(i + 1).append("-").append(directories[i].getName());
                                if (i < directories.length - 1)
                                    possible.append("\n");
                                else {
                                    possible.append("\n\tYou can also go back:\n");
                                    possible.append("\t\t").append(i + 2).append("-").append("Go back");
                                }

                            }
                            out.writeUTF(possible.toString());
                            String n = String.valueOf((directories.length) + 1);
                            out.writeUTF(n);

                            String op = in.readUTF();
                            int op_ = Integer.parseInt(op);

                            if (op_ != 0) {
                                if (op_ == directories.length + 1) {
                                    String[] arr_dir = client.path.split("\\\\", 0);
                                    int n_ = arr_dir.length;
                                    StringBuilder new_dir = new StringBuilder();
                                    for (int i=0; i<n_-1; i++) {
                                        new_dir.append(arr_dir[i]);
                                        if (i < n_-2) new_dir.append("\\");
                                    }

                                    for (Client_Class c : clients) {
                                        if (c.username.equals(username)) {
                                            c.path = new_dir.toString();
                                            client.path = c.path;
                                            break;
                                        }
                                    }

                                    out.writeUTF(new_dir.toString());
                                } else {

                                    for (Client_Class c : clients) {
                                        if (c.username.equals(username)) {
                                            c.path = c.path + "\\" + directories[op_ - 1].getName();
                                            client.path = c.path;
                                            out.writeUTF(c.path);
                                            break;
                                        }
                                    }

                                }
                                semfWait();
                                update_clients(otheraddress, Integer.parseInt(otherfilePort), server_home);
                                semfNotify();
                            }

                        }

                    }

                    case "7", "8" -> {
                        int serverPort = Integer.parseInt(thisTCPport);
                        try (ServerSocket listenSocket = new ServerSocket(serverPort + 1)) {
                            //System.out.println("A escuta no porto 6001");
                            //System.out.println("LISTEN SOCKET=" + listenSocket);

                            Socket fileSocket = listenSocket.accept(); // BLOQUEANTE
                            System.out.println("FILE_SOCKET (created at accept())=" + fileSocket);
                            new FileConnection(fileSocket, option, otheraddress, Integer.parseInt(otherfilePort));


                        } catch (IOException e) {
                            System.out.println("Listen:" + e.getMessage());
                        }
                    }

                }
            }

        } catch (EOFException e) {
            System.out.println("EOF:" + e);
        } catch (IOException e) {
            System.out.println("IO:" + e);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    private synchronized void update_clients(String otheraddress, int otherUDPPort, String server_home) throws IOException {
        StringBuilder res = new StringBuilder();
        int i = 0;
        System.out.println("HOME: "+server_home);
        System.out.println(server_home.length());
        for (Client_Class c : clients) {
            if (i != 0) res.append("\n");
            String difpath = (c.path).substring(server_home.length() + 1);
            res.append(c.username).append("/").append(c.password).append("/").append(c.department).append("/").append(c.telephone).append("/").append(c.address).append("/").append(c.cc_expiration).append("/").append(difpath);
            i++;
        }

        String p = System.getProperty("user.dir") + "\\clients1.txt";

        Path fileName = Path.of(p);
        Files.writeString(fileName, res);


        try (DatagramSocket ds = new DatagramSocket()) {
            InetAddress ia = InetAddress.getByName(otheraddress);

            String namef = System.getProperty("user.dir") + "\\clients1.txt";
            byte[] buf = namef.getBytes();
            DatagramPacket dp = new DatagramPacket(buf, buf.length, ia, otherUDPPort);
            ds.send(dp);

            long size = Files.size(Path.of(System.getProperty("user.dir") + "\\clients1.txt"));
            String sizes = String.valueOf(size);
            byte[] buf1 = sizes.getBytes();
            DatagramPacket dp1 = new DatagramPacket(buf1, buf1.length, ia, otherUDPPort);
            ds.send(dp1);


            File real_file = new File(String.valueOf(Path.of(System.getProperty("user.dir") + "\\clients1.txt")));

            FileInputStream fis = null;
            try {
                fis = new FileInputStream(real_file);

            } catch (IOException ioExp) {
                ioExp.printStackTrace();
            }

            byte[] file_array = new byte[1024 * 30];

            int pos = 0;

            int quant = 0;
            int little_array = 1024 * 30;
            fis.read(file_array);

            byte[] piece = new byte[1024]; // size of each piece - 1KB
            while (size > 0) {

                if (little_array <= 0) {
                    fis.read(file_array);
                    little_array = 1024 * 30;
                    pos = 0;
                }

                if (size > 1023)     quant = 1024;
                else quant = (int) size;

                System.arraycopy(file_array, pos * 1024, piece, 0, quant);

                DatagramPacket tosend = new DatagramPacket(piece, piece.length, ia, otherUDPPort);
                ds.send(tosend);

                byte[] res1 = new byte[1000];
                DatagramPacket reply = new DatagramPacket(res1, res1.length);
                ds.receive(reply);
                String res_ = new String(reply.getData(), 0, reply.getLength());
                if (res_.equals("OK")) {
                    pos++;
                    size -= quant;
                    little_array -= quant;
                }

            }


            fis.close();

        } catch (IOException ex) {
            ex.printStackTrace();
        }


    }

}


//= Thread para tratar de cada canal de comunicação com um cliente
class FileConnection extends Thread {

    DataInputStream inf;
    DataOutputStream outf;
    Socket fileSocket;
    String option;
    String otheraddress;
    int otherUDPPort;

    public FileConnection(Socket aClientSocket, String option_, String otheradd, int otherUDP) {
        try {
            fileSocket = aClientSocket;
            inf = new DataInputStream(fileSocket.getInputStream());
            outf = new DataOutputStream(fileSocket.getOutputStream());
            option = option_;
            otheraddress = otheradd;
            otherUDPPort = otherUDP;
            this.start();
        } catch (IOException e) {
            System.out.println("Connection:" + e.getMessage());
        }
    }

    //=============================
    public void run() {
        try {
            String username = inf.readUTF();

            String path = "";
            for (Client_Class c : clients) {
                if (c.username.equals(username)) {
                    path = c.path;
                    break;
                }
            }

            if (option.equals("7")) {
                System.out.println("PATH: " + path);
                File[] files = new File(path).listFiles(File::isFile);

                if (files.length == 0) outf.writeUTF("Nothing");
                else {
                    outf.writeUTF("Fine");
                    StringBuilder res = new StringBuilder();
                    for (int i = 0; i < files.length; i++) {
                        res.append("\t").append(i + 1).append("-").append(files[i].getName());
                        if (i < files.length - 1) res.append("\n");
                    }
                    outf.writeUTF(res.toString());
                    outf.writeUTF(String.valueOf(files.length));

                    int op = Integer.parseInt(inf.readUTF());

                    if (op != 0) {
                        outf.writeUTF(files[op - 1].getName());

                        // read the file and make it ready to send
                        FileInputStream fis = new FileInputStream(files[op - 1]);
                        outf.writeLong(files[op - 1].length());

                        byte[] buffer = new byte[1024]; // size of each piece - 1KB
                        while ((fis.read(buffer)) != -1) {
                            outf.write(buffer, 0, buffer.length);
                            outf.flush();   // to avoid trash in outf
                        }
                        fis.close();

                    }
                }
            } else {
                String maybe = inf.readUTF();

                if (!maybe.equals("Nothing")) {
                    int op = Integer.parseInt(maybe);

                    if (op != 0) {
                        String name_f = inf.readUTF();

                        FileOutputStream fos = new FileOutputStream(path + "\\" + name_f);

                        int size_piece = 0;
                        long totalsize = inf.readLong();

                        byte[] buffer = new byte[1024]; // size of each piece received - 1KB
                        while (totalsize > 0 && (size_piece = inf.read(buffer, 0, (int) Math.min(buffer.length, totalsize))) != -1) {
                            fos.write(buffer, 0, size_piece);
                            totalsize -= size_piece;
                        }

                        fos.close();

                        try (DatagramSocket ds = new DatagramSocket()) {
                            InetAddress ia = InetAddress.getByName(otheraddress);

                            String namef = path + "\\" + name_f;
                            byte[] buf = namef.getBytes();
                            DatagramPacket dp = new DatagramPacket(buf, buf.length, ia, otherUDPPort);
                            ds.send(dp);

                            long size = Files.size(Path.of(path + "\\" + name_f));
                            String sizes = String.valueOf(size);
                            byte[] buf1 = sizes.getBytes();
                            DatagramPacket dp1 = new DatagramPacket(buf1, buf1.length, ia, otherUDPPort);
                            ds.send(dp1);


                            File real_file = new File(String.valueOf(Path.of(path + "\\" + name_f)));

                            FileInputStream fis = null;
                            try {
                                fis = new FileInputStream(real_file);

                            } catch (IOException ioExp) {
                                ioExp.printStackTrace();
                            }

                            byte[] file_array = new byte[1024 * 30];

                            int pos = 0;

                            int quant = 0;
                            int little_array = 1024 * 30;
                            fis.read(file_array);

                            byte[] piece = new byte[1024]; // size of each piece - 1KB
                            while (size > 0) {

                                if (little_array <= 0) {
                                    fis.read(file_array);
                                    little_array = 1024 * 30;
                                    pos = 0;
                                }

                                if (size > 1023)     quant = 1024;
                                else quant = (int) size;

                                System.arraycopy(file_array, pos * 1024, piece, 0, quant);

                                DatagramPacket tosend = new DatagramPacket(piece, piece.length, ia, otherUDPPort);
                                ds.send(tosend);

                                byte[] res = new byte[1000];
                                DatagramPacket reply = new DatagramPacket(res, res.length);
                                ds.receive(reply);
                                String res_ = new String(reply.getData(), 0, reply.getLength());
                                if (res_.equals("OK")) {
                                    pos++;
                                    size -= quant;
                                    little_array -= quant;
                                }

                            }


                            fis.close();

                        } catch (IOException ex) {
                            ex.printStackTrace();
                        }

                        outf.writeUTF("File uploaded.");
                    }
                }

            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}



