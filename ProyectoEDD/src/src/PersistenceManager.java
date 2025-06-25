/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package src;

import java.io.*;

public class PersistenceManager {
    public static void saveContacts(CircularDoubleLinkedList<Contacto> contacts, String filePath) throws IOException {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filePath))) {
            oos.writeObject(contacts);
        }
    }

    public static CircularDoubleLinkedList<Contacto> loadContacts(String filePath) throws IOException, ClassNotFoundException {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filePath))) {
            return (CircularDoubleLinkedList<Contacto>) ois.readObject();
        }
    }
}