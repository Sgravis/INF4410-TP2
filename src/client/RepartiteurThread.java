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
  private Boolean clean_answer;
  private ArrayList<Operation> task;
  private String serial_string;
  private String[] results;


  RepartiteurThread(ServerInterface stub) {
    this.stub = stub;
    this.clean_answer = true;
    this.results = null;
    this.task = task;
  }

  public void run()
 {
   String tampon = "";
   try {
     this.results = null;
     this.serial_string = Integer.toString(this.task.size());
     for (Operation operation : this.task) {
       this.serial_string += "&" + operation.getOperationName() + ":" + operation.getOperande();
     }
     tampon = stub.Calculer(this.serial_string);
     if (!tampon.equals(null)) {
       this.results = tampon.split("&");
       for (int i = 0; i < this.results.length; i++) {
           this.task.get(i).setResult(Integer.parseInt(this.results[i]));
           this.task.get(i).setValidation();
       }
     } else {
       this.clean_answer = false;
     }

   } catch (RemoteException e) {
     System.out.println("Erreur: " + e.getMessage());
   }
 }

}
