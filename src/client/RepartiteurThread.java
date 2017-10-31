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

  private ServerInterface stub;
  private Boolean overload;
  private ArrayList<Operation> task;
  private String serial_string;
  private boolean busy;
  private boolean inprogress;
  private String[] results;
  private boolean down;


  RepartiteurThread(ServerInterface stub) {


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
    private void traitement()
    {
      String tampon = "";
      try
      {
        this.serial_string = Integer.toString(this.task.size());
        for (Operation operation : this.task)
        {
          this.serial_string += "&" + operation.getOperationName() + ":" + operation.getOperande();
        }
        tampon = stub.Calculer(this.serial_string);
        if (!tampon.equals("refus"))
        {
          this.results = tampon.split("&");
          if (this.results.length == this.task.size())
          {
           try
           {
            Thread.sleep(0,1);
            }
           catch(Exception e){}
            for (int i = 0; i < this.results.length; i++)
             {
               this.task.get(i).setResult(Integer.parseInt(this.results[i]));
               this.task.get(i).setValidation();
             }
          }
          
        }else
        {
          System.out.println("Il faut baisser le taux");
         this.overload = true;

        }
      }catch (RemoteException e)
      {
       System.out.println("Probleme du cote du serveur : plus d'envoie a ce serveur."+Thread.currentThread().getName());
       this.down=true;

      }
      for (int i = 0; i < this.task.size(); i++)
      {
           this.task.get(i).setTreatment(true);
      }
      this.busy=false;
    }

    public void setInprogress(Boolean value)
    {
      this.inprogress=value;
    }

    public Boolean getBusy()
    {
      return this.busy;
    }

    public Boolean getDown()
    {
      return this.down;
    }
    public void setTask(ArrayList<Operation> task)
    {
      System.out.println(task.size());
      this.task = task;
      this.busy=true;
    }
    public Boolean getOverload()
    {
       return this.overload;
    }
    public void setOverload(Boolean value)
    {
       this.overload=value;
    }
 }
