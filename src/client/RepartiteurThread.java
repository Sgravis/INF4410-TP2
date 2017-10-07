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


  RepartiteurThread(ServerInterface stub) {
    this.stub = stub;
    this.clean_answer = true;
  }

  public void run()
 {
   try {
     for (Operation operation : task) {
       if (operation.getResult() == 0) {
         operation.setResult(stub.Calculer(operation.getOperationName()));
       } else {
         int tampon = stub.Calculer(operation.getOperationName());
         if (operation.checkResults(tampon)) {
           operation.setValidation(true);
         } else {
           operation.setValidation(false);
         }
       }
     }
   } catch (RemoteException e) {
     System.out.println("Erreur: " + e.getMessage());
   }
 }

 public void setTask(ArrayList<Operation> task) {
   this.task = task;
 }
}
