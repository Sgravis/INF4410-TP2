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



  RepartiteurThread(ServerInterface stub) {


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
        if (!tampon.equals(null))
        {
          this.results = tampon.split("&");
          for (int i = 0; i < this.results.length; i++)
          {
             this.task.get(i).setResult(Integer.parseInt(this.results[i]));
             this.task.get(i).setValidation();
          }
        }else
        {
         this.overload = true;
        }
        for (Operation o : task)
        {
          System.out.println(o.getResult());
        }
      }catch (RemoteException e)
      {
       System.out.println("Erreur: " + e.getMessage());
      }
      System.out.println("fin du traitement");
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

    public void setTask(ArrayList<Operation> task)
    {
      this.task = task;
      this.busy=true;
    }

 }
