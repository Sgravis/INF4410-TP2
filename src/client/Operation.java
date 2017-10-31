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

public class Operation {

  // Nom de l'opération : Pell ou Prime
  private String operation_name;

  // Opérande associé à l'opération
  private int operande;

  // Booléen permettant la vérification de la résolution de l'opération.
  private Boolean solved;

  // Entier permettant de savoir si l'on se trouve en mode sécurisé ou non.
  private int validation;

  //
  private int result;
  private ArrayList<Integer> possible_results;
  private Boolean treatment;

  Operation(String name, int operande, int validation) {
    this.possible_results = new ArrayList<Integer>();
    this.operation_name = name;
    this.operande = operande;
    this.validation = validation;
    this.solved = false;
    this.result = 0;
    this.treatment = false;
  }

  public Boolean isSolved() {
    return this.solved;
  }

  public  String getOperationName() {
    return this.operation_name;
  }

  public int getOperande() {
    return this.operande;
  }

  public void setValidation() {
  //  if(!solved){
    if(validation == 1)
    {
      this.result=this.possible_results.get(0);
      this.solved=true;
    }
    else
    {
      for(int i = 0; i < possible_results.size(); i++) { 
       for (int j = i + 1; j < possible_results.size(); j++) {
        if (possible_results.get(i).equals(possible_results.get(j)))
        {
          this.result = possible_results.get(i);
          this.solved = true;
        }

       }
    }
  }
}

  public void setResult(int result) {
    this.possible_results.add(result);
  }

  public int getResult() {
    return this.result;
  }
  public Boolean getTreatment(){
    return this.treatment;
  }
  public void setTreatment(Boolean value)
  {
    this.treatment=value;
  }
}
