package server;

import shared.ServerInterface;
import server.Operations;
import java.rmi.ConnectException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.*;
import java.io.*;


import shared.ServerInterface;

public class Server implements ServerInterface {

	// Capacité de notre serveur.
	private int capacite;

	// taux de malveillance théorique de notre serveur .
	// Représente les mauvaises réponses qu'il peut nous renvoyer.
	private int taux_malveillance;

	// Résultat sérialisé de l'opération que le serveur vient de traiter.
	private String resultat;


	public static void main(String[] args) {

		int capacite;
		int taux_malveillance;
		if (args.length != 2)
		{
			System.out.println("Erreur : Nombre d'arguments incorrect \n\tUsage : ./server Capacité Taux_Fiabilité\n");
		}
		else
		{
			if ((Integer.parseInt(args[0]) > 0) && (Integer.parseInt(args[1]) <= 100) && (Integer.parseInt(args[1]) >= 0))
			{
				capacite = Integer.parseInt(args[0]);
				taux_malveillance = Integer.parseInt(args[1]);
				Server server = new Server(capacite, taux_malveillance);
				server.run();
			}
		}
	}

	/**
	 * Constructeur du serveur.
	 * @param  int capacite         Capacité du serveur.
	 * @param  int taux_malveillance Taux de malveillance du serveur.
	 * @return     Serveur
	 */
	public Server(int capacite, int taux_malveillance) {
		super();
		this.capacite = capacite;
		this.taux_malveillance = taux_malveillance;
		this.resultat = "";
	}

	private void run()
	{
		if (System.getSecurityManager() == null)
		{
			System.setSecurityManager(new SecurityManager());
		}
		try
		{
			ServerInterface stub = (ServerInterface) UnicastRemoteObject.exportObject(this, 0);
			Registry registry = LocateRegistry.getRegistry(5000);
			registry.rebind("server", stub);
			System.out.println("Server ready.");
		}
		catch (ConnectException e)
		{
			System.err.println("Impossible de se connecter au registre RMI. Est-ce que rmiregistry est lancé ?");
			System.err.println();
			System.err.println("Erreur: " + e.getMessage());
		}
		catch (Exception e)
		{
			System.err.println("Erreur: " + e.getMessage());
		}
	}

	/**
	 * Méthode pour le traitement d'une liste d'opérations.
	 * @param  String          operation_string opérations à faire sous forme sérialisée.
	 * @return                 résultat des opérations sous forme sérialisée.
	 * @throws RemoteException concernant l'appel aux RPC.
	 */
	public String Calculer(String operation_string) throws RemoteException
	{
		this.resultat = "";
		String[] operation_liste = operation_string.split("&");
		//if (HitRateCalculation(Integer.parseInt(operation_liste[0]))) {
			for (int i = 1; i <= Integer.parseInt(operation_liste[0]); i++)
			{
				String[] operation = operation_liste[i].split(":");
				if (operation[0].equals("pell"))
				{
					this.resultat += Integer.toString((pell(Integer.parseInt(operation[1])))%4000) + "&";
				}
				else if (operation[0].equals("prime"))
				{
					this.resultat += Integer.toString((prime(Integer.parseInt(operation[1])))%4000) + "&";
				}
				else
				{
					continue; //En cas d'opération inconnue..
				}
			}
		//} else {
			//this.resultat = null;
		//}
		return this.resultat;
	}

	private int pell(int valeur)
	{
			if(random(100) >= taux_malveillance)
			{
				return Operations.pell(valeur);
			}
			else
			{
				return random(100000);
			}
	}


	private int prime(int valeur)
	{

			if(random(100) >= taux_malveillance)
			{
				return Operations.prime(valeur);
			}
			else
			{
				return random(100000);
			}
	}


	private int random(int border)
	{
		return (new Random().nextInt(border));
	}

	private Boolean HitRateCalculation(int number_operations) {
			return (random(((number_operations - this.capacite)/(this.capacite*5))*100)) > (((number_operations - this.capacite)/(this.capacite*5))*100);
	}
}
