package service;

import java.sql.SQLException;
import java.util.List;

public interface iservice1<T> {
    public void ajouter(T t) throws SQLException;
    public void modifier(T t) throws SQLException;
    public void supprimer(int id) throws SQLException;
    public List<T> afficher() throws SQLException;
    //public List<T> chercher (String refW) throws SQLException;
    //public List<T> filtrer(String s) throws SQLException;
}