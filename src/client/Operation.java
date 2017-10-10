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

  Operation(String name, int operande, int validation) {
    this.operation_name = name;
    this.operande = operande;
    this.validation = validation;
    this.solved = false;
    this.result = 0;
  }

  public Boolean isSolved() {
    return this.solved;
  }

  public void Validate() {
    if (this.validation == 0) {
      this.solved = true;
    }
  }

  public String getOperationName() {
    return this.operation_name;
  }

  public int getOperande() {
    return this.operande;
  }

  public int getValidation() {
    return this.validation;
  }

  public void setValidation(Boolean same_result) {
    if (same_result){
      this.validation--;
    } else if (this.validation < 3){
      this.validation++;
    }
  }

  public void setResult(int result) {
    this.result = result;
    this.possible_results.add(result);
  }

  public int getResult() {
    return this.result;
  }

  public void checkResults(int result) {
    for (Integer possibleresult : possible_results) {
      if (possibleresult == result) {
        setResult(result);
      }
    }
  }
}
