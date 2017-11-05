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



	public static void main(String[] args)
	{

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
	public Server(int capacite, int taux_malveillance)
	{
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
		if (HitRateCalculation(Integer.parseInt(operation_liste[0]))) //si le serveur n'est pas surchargé
		{
			for (int i = 1; i <= Integer.parseInt(operation_liste[0]); i++) //calcul pour chaque operation
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
		}
		else //serveur surchargé
		{
			this.resultat = "refus";
		}
		return this.resultat;
	}

	/**
	 * calcul de l'opération pell
	 * @param  int valeur        opérande associé
	 * @return     Résultat de l'opération
	 */
	private int pell(int valeur)
	{
			if(random(100) >= taux_malveillance) //resultat correct
			{
				return Operations.pell(valeur); 
			}
			else //resultat erroné
			{
				return random(10000000);
			}
	}

	/**
	 * calcul de l'opération prime
	 * @param  int valeur        opérande associé
	 * @return     Résultat de l'opération
	 */
	private int prime(int valeur)
	{

			if(random(100) >= taux_malveillance) //resultat correct
			{
				return Operations.prime(valeur);
			}
			else //resultat erroné 
			{
				return random(10000000);
			}
	}

	/**
	 * Méthode de calcul d'un nombre aléatoire permettant la simulation de surcharge de ressources ou le taux de malveillance.
	 * @param  int border        borne supérieure de la randomisation
	 * @return     nombre aléatoire entre 0 et border
	 */
	private int random(int border)
	{
		return (new Random().nextInt(border));
	}

	/**
	 * Méthode permettant de simuler la surcharge théoriques d'un serveur en calculant le taux en fonction de la capacité et du nombre d'opérations envoyés.
	 * Le serveur est en surcharge quand ce taux est supérieur au nombre aléatoire généré entre 0 et 100.
	 * @param  int number_operations nombre d'opérations envoyées au serveur par le thread
	 * @return     Booléen de validation de la tache envoyée au serveur.
	 */
	private Boolean HitRateCalculation(int number_operations)
	{
		float taux=(((float)number_operations - (float)this.capacite)/((float)this.capacite*5))*100;
		float chance = random(100);
		return (taux < chance);
	}
}
