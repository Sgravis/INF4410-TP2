package client;

import java.io.*;
import java.lang.*;
import java.rmi.ConnectException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.*;
import java.util.ArrayList;
import server.Operations;
import shared.ServerInterface;

public class RepartiteurThread extends Thread {

  // Stub du serveur avec lequel notre thread va communiquer.
  private ServerInterface stub;

  // Booléen permettant de savoir si le calcul s'est déroulé sans erreurs ou non.
  private Boolean overload;

  // Liste des opérations que doit faire notre thread.
  private ArrayList<Operation> task;

  // Chaine sérialisée contenant les opérations à faire qui sera envoyé au serveur.
  private String serial_string;

  // Booléen concernant l'état du thread (s'il est disponible ou non).
  private boolean busy;

  // Booléen permettant de savoir s'il reste des opérations à effectuer.
  // Tant que la pile d'opération n'est pas vide, on continue l'éxécution du thread.
  private boolean inprogress;

  // Liste des résultats de nos opérations.
  private String[] results;


  /**
   * Constructeur du thread.
   *
   * @param stub Stub du serveur avec lequel notre thread va communiquer.
   */
  RepartiteurThread(ServerInterface stub)
  {
    this.stub = stub;
    this.overload = false;
    this.busy=false;
    this.inprogress=true;
  }

  public void run()
  {
    while(inprogress)
    {
      if (busy)
      {
        traitement();
      }
    }
  }

  /**
   * Méthode pour le traitement d'une sous liste d'opérations.
   *
   */
  private void traitement()
  {

    // Envoie de la requête au serveur et récupération du résultat.
    String tampon = "";
    try
    {
      this.serial_string = Integer.toString(this.task.size());
      for (Operation operation : this.task)
      {
        this.serial_string += "&" + operation.getOperationName() + ":" + operation.getOperande();
      }
      tampon = stub.Calculer(this.serial_string);
      if (!tampon.equals(null))
      {
        this.results = tampon.split("&");
        // traitement des résultats reçus dans chaque instance d'opération.
        for (int i = 0; i < this.results.length; i++)
        {
           this.task.get(i).setResult(Integer.parseInt(this.results[i]));
           this.task.get(i).setValidation();
        }
      }
      else
      {
       this.overload = true;
      }
      for (Operation o : task)
      {
        System.out.println(o.getResult());
      }
    }
    catch (RemoteException e)
    {
     System.out.println("Erreur: " + e.getMessage());
    }
    this.busy=false;
  }

  /**
   * Accesseur du booléen de progression de la tache globale.
   * @param Boolean value progression de la tache globale (il reste des opératiohs ou non).
   */
  public void setInprogress(Boolean value)
  {
    this.inprogress = value;
  }

  /**
   * Accesseur du booléen d'état du thread
   * @return l'état actuel du thread.
   */
  public Boolean getBusy()
  {
    return this.busy;
  }

  /**
   * Accesseur de la liste de tache d'opérations à faire par notre thread.
   * @param ArrayList<Operation> task liste des opérations à faire.
   */
  public void setTask(ArrayList<Operation> task)
  {
    this.task = task;
    this.busy=true;
  }
 }
