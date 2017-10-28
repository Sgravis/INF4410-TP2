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
  private String operation_name;
  private int operande;
  private Boolean solved;
  private int validation;
  private int result;
  private ArrayList<Integer> possible_results;
  private Boolean treatment;

  Operation(String name, int operande, int validation) {
    possible_results = new ArrayList<Integer>();
    this.operation_name = name;
    this.operande = operande;
    this.validation = validation;
    this.solved = false;
    this.result = 0;
    this.treatment =talse;
  }

  public Boolean isSolved() {
    return this.solved;
  }

  public String getOperationName() {
    return this.operation_name;
  }

  public int getOperande() {
    return this.operande;
  }

  public void setValidation() {
    this.treatment=true;
    if(validation == 1)
    {
      this.result=this.possible_results.get(0);
      this.solved=true;
    }
    else
    {
      for(int i = 0; i < possible_results.size(); i++) {
       for (int j = i + 1; j < possible_results.size(); j++) {
        if (possible_results.get(i) == possible_results.get(j)) {
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
