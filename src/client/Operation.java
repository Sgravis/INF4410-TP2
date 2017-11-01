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

  // Entier pour stocker le résultat de l'opération.
  private int result;

  //Liste des résultats possible pour notre opération en cas de vérification (mode non sécurisé).
  private ArrayList<Integer> possible_results;

  // Booléen gardant l'état de l'opération (traitée ou non). Il permet au répartiteur de savoir s'il doit traiter les résultats.
  private Boolean treatment;


  /**
   * Constructeur de la classe opération.
   *
   * @param name nom de l'opération : Pell ou Prime.
   * @param operande opérande lié à l'opération.
   * @param validation mode sécurisé ou mode non-sécurisé.
  */
  Operation(String name, int operande, int validation)
  {
    this.possible_results = new ArrayList<Integer>();
    this.operation_name = name;
    this.operande = operande;
    this.validation = validation;
    this.solved = false;
    this.result = 0;
    this.treatment = false;
  }

  /**
  * Accesseur du booléen de résolution de l'opération.
  * @return Le résultat est traité ou non.
  */
  public Boolean isSolved()
  {
    return this.solved;
  }

  /**
   * Accesseur pour le type de l'opération.
   * @return Le type de l'opération : Pell ou Prime.
   */
  public  String getOperationName()
  {
    return this.operation_name;
  }

  /**
   * Accesseur pour l'opérande lié à l'opération.
   * @return L'opérande de l'opération.
   */
  public int getOperande()
  {
    return this.operande;
  }

  /**
   * Accesseur pour la validation de l'opération.
   * Vérifie les différents résultats présent dans le tableau de résultats possibles.
   * Si le Répartirteur est en mode sécurisé, la méthode met juste la première case du tableau dans le résutlat et valide l'opération.
   * Si le Répartirteur est en mode non sécurisé, la méthode va regarder dans le tableau pour voir si deux résutlats sont identiques.
   * Dans ce cas la, il validera l'opération et mettra le bon résultat dans la variable adéquate.
   */
   public void setValidation()
   {
     //  if(!solved){
     if(validation == 1)
     {
       this.result=this.possible_results.get(0);
       this.solved=true;
     }
     else
     {
       for(int i = 0; i < possible_results.size(); i++)
       {
         for (int j = i + 1; j < possible_results.size(); j++)
         {
           if (possible_results.get(i).equals(possible_results.get(j)))
           {
             this.result = possible_results.get(i);
             this.solved = true;
           }
         }
       }
     }
   }

  /**
   * Accesseur pour le résultat de l'opération.
   * @param int result  Résultat de notre opération.
   */
  public void setResult(int result) {
    this.possible_results.add(result);
  }

  /**
   * Accesseur pour le résultat de l'opération
   * @return  Renvoie le résultat de l'opération.
   */
  public int getResult() {
    return this.result;
  }

  /**
   * Accesseur pour l'état de traitement de l'opération.
   * @return L'état de traitement de l'opération.
   */
  public Boolean getTreatment(){
    return this.treatment;
  }

  /**
   * Accesseur pour l'état de traitement de l'opération.
   * @param Boolean value État de traitement de l'opération.
   */
  public void setTreatment(Boolean value)
  {
    this.treatment=value;
  }
}
