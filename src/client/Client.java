package client;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.rmi.AccessException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.*;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import shared.ServerInterface;

public class Client {
  private int _mode;
  private ArrayList<Operation> operations_stack; //operations a faire
  private ArrayList<Operation> in_progress_operations_stack; //operations en traitement
  private HashMap<RepartiteurThread, Integer> threads;
  private int result;

	public static void main(String[] args) {
    HashMap<String,Integer> socket = new HashMap<String, Integer>();
    HashMap<HashMap<String,Integer>,Integer> servers = new HashMap<HashMap<String,Integer>,Integer>();//hashmap contenant les socket serveur et leur capacité

    if (args.length != 2 && (Integer.parseInt(args[1]) != 0 || Integer.parseInt(args[1]) != 1)) {
      System.out.println("Erreur : Nombre d'arguments incorrect \n\tUsage : ./client Fichier  Mode d'execution:(S = 0/NS = 1)\n");
    }
    else
    {
      try (BufferedReader lines = new BufferedReader(new FileReader("src/client/Servers.txt"))) {
        String line;
        while ((line = lines.readLine()) != null) {
          socket.put(line.split(":")[0], Integer.parseInt(line.split(":")[1]));
          servers.put(socket, Integer.parseInt(line.split(":")[2]));
        }
      }
      catch (IOException e)
      {
        System.out.println("Erreur: " + e.getMessage());
      }
      Client client = new Client(servers, args[0], Integer.parseInt(args[1]));
      client.run();
    }
	}


	public Client(HashMap<HashMap<String, Integer>, Integer> servers, String file, int mode) {
		super();
    this._mode = mode;
		if (System.getSecurityManager() == null)
		{
			System.setSecurityManager(new SecurityManager());
		}
    FileToArray(file);
   for (HashMap<String,Integer> socket : servers.keySet())
   {
	 	threads.put(new RepartiteurThread(loadServerStub(socket)), servers.get(socket));
   }
   for (RepartiteurThread thread : threads.keySet()) {
     for (int i=0 ; i<3; i++) {
       setCapacity(thread, true);
      }
    }
	}

	private void run() {
    ExecutorService task_executor = Executors.newFixedThreadPool(threads.size());
    
    System.out.println(this.result);
	}

	private ServerInterface loadServerStub(HashMap<String, Integer> socket) {
		ServerInterface stub = null;

		try
		{

			Registry registry = LocateRegistry.getRegistry(socket.keySet().toArray()[0].toString(), socket.get(socket.keySet().toArray()[0]));
			stub = (ServerInterface) registry.lookup("server");
		}
		catch (NotBoundException e)
		{

			System.out.println("Erreur: Le nom '" + e.getMessage()
					+ "' n'est pas défini dans le registre.");
		}
		catch (AccessException e)
		{

			System.out.println("Erreur: " + e.getMessage());
		}
		catch (RemoteException e)
		{

			System.out.println("Erreur: " + e.getMessage());
		}
		return stub;
	}

  private void FileToArray(String file) {
    try (BufferedReader lines = new BufferedReader(new FileReader("Operations/" + file))) {
      String line;
      while ((line = lines.readLine()) != null) {
        if (this._mode == 0)
          this.operations_stack.add(new Operation(line.split(" ")[0],Integer.parseInt(line.split(" ")[1]), 1));
        else if (this._mode == 1)
          this.operations_stack.add(new Operation(line.split(" ")[0],Integer.parseInt(line.split(" ")[1]), 2));
      }
    }
    catch (IOException e)
    {
      System.out.println("Erreur: " + e.getMessage());
    }
  }

  private void setCapacity(RepartiteurThread thread, Boolean uprate) {
    if (uprate){
      threads.put(thread, threads.get(thread) + threads.get(thread));
    } else {
      threads.put(thread, threads.get(thread) - threads.get(thread));
    }

  }
}
