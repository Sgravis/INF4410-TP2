package client;

import shared.ServerInterface;
import java.io.*;
import java.rmi.AccessException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;


public class Client {
	private ServerInterface ServerStub = null;

	public static void main(String[] args) {
		String Hostname = "127.0.0.1";
		Client client = new Client(Hostname);
		client.run();
	}


	public Client(String Hostame) {
		super();

		if (System.getSecurityManager() == null)
		{
			System.setSecurityManager(new SecurityManager());
		}

		ServerStub = loadServerStub(Hostame);


	}

	private void run() {
		try
		{
			System.out.println(ServerStub.Calculer("4&pell:5&prime:10&pell:13&prime:6500"));
		}
		catch (RemoteException e)
		{
			System.out.println("Erreur: " + e.getMessage());
		}
	}

	private ServerInterface loadServerStub(String hostname) {
		ServerInterface stub = null;
		try
		{
			Registry registry = LocateRegistry.getRegistry(hostname);
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
}
