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

  // Booléen concernant l'état du serveur associé au thread.
  private boolean down;




  /**
   * Constructeur du thread.
   *
   * @param stub Stub du serveur avec lequel notre thread va communiquer.
   */
  RepartiteurThread(ServerInterface stub)
  {
    //Initialisation des attributs
    this.stub = stub;
    this.overload = false;
    this.busy=false;
    this.inprogress=true;
    this.down=false;
  }

  public void run()
  {
    loop();
  }

  /**
   * Boucle d'un thread.
   * Tant que le thread peut recevoir des opérations, il passe dans la boucle while.
   * Si le thread n'est pas pas busy, le répartirteur peut lui en envoyer.
   * S'il est busy c'est qu'il vient d'en recevoir qu'il est entrain de communiquer avec le serveur pour traiter la requête.
   */
  private void loop()
  {
    while(inprogress)
    {
     try
     {
      Thread.sleep(0,1);
    }
    catch(Exception e){}
    if (busy)
    {
      traitement();
    }
  }
}

  /**
   * Méthode pour le traitement d'une sous liste d'opérations.
   * On va dans un premier temps envoyer la requête au serveur qui va nous renvoyer une réponse.
   * On va ensuite traiter la réponse qui peut avoir plusieurs forme
   * Si l'on reçoit la chaine "Refus", c'est que nous avons surchargé notre serveur et qu'il n'a pas assez de ressources théoriques pour nous répondre
   * Si l'on reçoit une chaine sérialisée, on la désérialise et on traite le résultat de chaque opération.
   * On gère également le cas ou le serveur serait coupé au milieu d'une requête avec une levée de RemoteException
   */
  private void traitement()
  {
   String tampon = "";
   try
   {
     this.serial_string = Integer.toString(this.task.size());
     for (Operation operation : this.task) //sérialisation des taches a effectuer
     {
       this.serial_string += "&" + operation.getOperationName() + ":" + operation.getOperande();
     }
     tampon = stub.Calculer(this.serial_string); //envoie des taches au serveur
     try
     {
       Thread.sleep(0,1);
     }
     catch(Exception e){}
     if (!tampon.equals("refus")) //si le serveur n'est pas surchargé : ajout des resultats de chaque opération
     {

       this.results = tampon.split("&");
       if (this.results.length == this.task.size())
       {
         for (int i = 0; i < this.results.length; i++)
         {
           this.task.get(i).setResult(Integer.parseInt(this.results[i]));
           this.task.get(i).setValidation(); //appel de la routine de validation 
           try
           {
             Thread.sleep(0,1);
           }
           catch(Exception e){}
         }
       }
     }
     else //si le serveur est surchargé
     {
       this.overload = true;

     }
   }
   catch (RemoteException e)
   {
     System.out.println("Probleme du cote du serveur : plus d'envoie a ce serveur."+Thread.currentThread().getName());
     this.down=true;

   }
   for (int i = 0; i < this.task.size(); i++) //passe l'etat des opérations a "traitées"
   {
     this.task.get(i).setTreatment(true);
   }
   this.busy=false; //le serveur est a nouveau disponible aux calculs
 }

  /**
   * Accesseur du booléen de progression de la tache globale.
   * @param Boolean value progression de la tache globale (il reste des opératiohs ou non).
   */
  public void setInprogress(Boolean value)
  {
    this.inprogress=value;
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
   * Accesseur du booléen de l'état du serveur
   * @return L'état du serveur (s'il répond ou s'il a été coupé brusquement au milieu d'une tache)
   */
  public Boolean getDown()
  {
    return this.down;
  }

  /**
   * Accesseur de la liste de tache d'opérations à faire par notre thread.
   * @param ArrayList<Operation> task liste des opérations à faire.
   */
  public void setTask(ArrayList<Operation> task)
  {
    System.out.println(task.size());
    this.task = task;
    this.busy=true;
  }

  /**
   * Accesseur du booléen de surcharge du serveur.
   * @return l'état de surcharge du serveur.
   */
  public Boolean getOverload()
  {
   return this.overload;
 }

  /**
   * Accesseur du booléen de surcharge du serveur
   * @param Boolean reset réinitialisation du booléen de surcharge
   */
  public void setOverload(Boolean reset)
  {
   this.overload = reset;
 }
}
