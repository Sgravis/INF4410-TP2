package shared;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.*;

import java.io.*;



public interface ServerInterface extends Remote {

	public int Calculer(String operation_string) throws RemoteException;

}
