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

  // Type de sécurité pour notre Répartiteur : 0 = sécurisé / 1 = non sécurisé.
  private int _mode;

  // Liste contenant les opérations à répartir entre nos différents clients.
  private ArrayList<Operation> operations_stack;

  // Liste contenant les opérations en cours de traitement par nos serveurs.
  private List<Operation> in_progress_operations_stack;

  // Hashmap contenant chaque threads (1 par serveur) avec la capacité associée
  private HashMap<RepartiteurThread, Integer> threads;

  // HashMap contenant chaque thread et sa liste d'opération en cours.
  private HashMap<RepartiteurThread, ArrayList<Operation>> thread_op;

  // Résultat final de notre fichier d'opérations.
  private int result;

	public static void main(String[] args)
  {
    //hashmap contenant les socket serveur et leur capacité.
    HashMap<HashMap<String,Integer>,Integer> servers = new HashMap<HashMap<String,Integer>,Integer>();

    if (args.length != 2 && (Integer.parseInt(args[1]) != 0 || Integer.parseInt(args[1]) != 1)) 
    {
      System.out.println("Erreur : Nombre d'arguments incorrect \n\tUsage : ./client Fichier  Mode d'execution:(S = 0/NS = 1)\n");
    }
    else
    {
      try (BufferedReader lines = new BufferedReader(new FileReader("src/client/Servers.txt"))) 
      {
        String line;
        while ((line = lines.readLine()) != null) 
        {
          HashMap<String,Integer> socket = new HashMap<String, Integer>();
          socket.put(line.split(":")[0], Integer.parseInt(line.split(":")[1]));
          servers.put(socket,Integer.parseInt(line.split(":")[2]));
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

  /**
   * Constructeur du Répartirteur
   * @param  HashMap<HashMap<String, Integer>, Integer>  servers  HashMap des serveurs disponibles ainsi que leur capacité respective.
   * @param  String                  file          Fichier contenant la liste d'opérations à effectuer.
   * @param  int                     mode          Mode de sécurité de notre répartirteur.
   * @return                         Répartirteur.
   */
	public Client(HashMap<HashMap<String, Integer>, Integer> servers, String file, int mode)
  {
    //Initialisation des attributs
		super();
    this._mode = mode;
    this.operations_stack = new ArrayList<Operation>();
    this.in_progress_operations_stack = Collections.synchronizedList(new ArrayList<Operation>());
    this.threads = new HashMap<RepartiteurThread, Integer>();
    this.thread_op = new HashMap<RepartiteurThread,ArrayList<Operation>>();
    this.result = 0;


		if (System.getSecurityManager() == null)
		{
			System.setSecurityManager(new SecurityManager());
		}

    //Création de le pile d'opérations.
    FileToArray(file);

    //Création des threads
    for (HashMap<String, Integer> socket : servers.keySet())
    {
      RepartiteurThread t = new RepartiteurThread(loadServerStub(socket));
      threads.put(t, servers.get(socket));
      thread_op.put(t, new ArrayList<Operation>());
      t.start();
    }
	}

	private void run()
  {
    long start = System.nanoTime();

    ArrayList<Operation> task;
    while(!operations_stack.isEmpty() || !in_progress_operations_stack.isEmpty())
    {

      //Boucle d'envoie de tache aux threads
      for (RepartiteurThread thread : threads.keySet())
      {
        if(!thread.getBusy() && !thread.getDown()) //Si le thread n'est pas occupé et n'est pas coupé, on peut lui envoyer des taches
        {
          //ajustement de sa capacité en fonction de sa réponse precedente
          if(thread.getOverload()) //on diminue sa capacité si il est surchargé
          {
             setCapacity(thread,false);
          }
          else //on baisse sa capacité si il ne l'est pas
          {
             setCapacity(thread,true);
          }
          if(!operations_stack.isEmpty()) //si la pile d'operations n'est pas vide on lui en envoie des taches restantes
          {
            task=thread_op.get(thread);
            task.clear();

            for (int i = 0; i < threads.get(thread); i++) //on lui envoie autant de tâche que sa capacité l'autorise.
            {
              if(!operations_stack.isEmpty())
              {
                 task.add(operations_stack.get(0));
                 in_progress_operations_stack.add(operations_stack.get(0)); //ajout de la tache a la pile d'opération en cours
                 operations_stack.remove(operations_stack.get(0)); //suppression de la tache de la pile d'opération.
              }
            }
            thread.setTask(task); 
          }
        }
      }


      //Traitement de la pile d'opération : On vérifie la résolution des opérations en cours de traitement.
      Iterator it = in_progress_operations_stack.iterator();
      while (it.hasNext())
      {
        Operation operation_inprogress =(Operation)it.next();
        if (operation_inprogress.getTreatment()) //L'opération a été traitée par un serveur
        {
          if(operation_inprogress.isSolved()) //Si celle ci est resolue, on ajoute son resultat
          {
            this.result =(this.result+  operation_inprogress.getResult())%4000;
          }
          else //sinon, on la remet dans la pile d'opération a traiter.
          {
            operation_inprogress.setTreatment(false);
            operations_stack.add(operation_inprogress);

          }

          it.remove();
        }

      }

    }
      System.out.println("result:"+this.result); //affichage du resultat
      long end = System.nanoTime();
      System.out.println("Temps écoulé appel RMI distant: "
          + ((end - start)*0.000001) + " ms");
      for (RepartiteurThread thread : threads.keySet())
      {
       thread.setInprogress(false);
      }
  }

  /**
   * Méthode permettant de récupérer le stub associé à notre serveur.
   * @param HashMap<String, Integer>  socket Ip et port d'écoute du rmiregistry associé au serveur.
   * @return                 Stub associé au serveur.
   */
	private ServerInterface loadServerStub(HashMap<String, Integer> socket)
  {
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

  /**
   * Méthode qui récupère toutes les opérations du fichier pour les mettre dans la pile adéquate.
   * @param String file fichier à traiter.
   */
  private void FileToArray(String file)
  {
    try (BufferedReader lines = new BufferedReader(new FileReader("Operations/" + file)))
    {
      String line;
      while ((line = lines.readLine()) != null)
      {
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

  /**
   * Accesseur sur la capacité de chaque serveur.
   * Cette méthode permet de modifier le nombre d'opérations envoyées à un serveur en fonction de sa surcharge
   * @param RepartiteurThread thread thread associé au serveur dont on veut modifier le nombre d'opérations à envoyer
   * @param Boolean           uprate valeur de la modification : true pour augmenter & false pour diminuer
   */
  private void setCapacity(RepartiteurThread thread, Boolean uprate)
  {
    if (uprate)
    {
      threads.put(thread, threads.get(thread) + 2);
    }
    else
    {
      threads.put(thread, threads.get(thread) - 2);
    }
    thread.setOverload(false);
  }
}
